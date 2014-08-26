/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://www.3wks.com.au/thundr
 * Copyright (C) 2013 3wks, <thundr@3wks.com.au>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.threewks.thundr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atomicleopard.expressive.Cast;
import com.atomicleopard.expressive.Expressive;
import com.threewks.thundr.configuration.ConfigurationModule;
import com.threewks.thundr.http.Header;
import com.threewks.thundr.http.RequestThreadLocal;
import com.threewks.thundr.injection.InjectionContextImpl;
import com.threewks.thundr.injection.Module;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.logger.Logger;
import com.threewks.thundr.module.Modules;
import com.threewks.thundr.module.ModulesModule;
import com.threewks.thundr.route.HttpMethod;
import com.threewks.thundr.route.RouteResolverException;
import com.threewks.thundr.route.Router;
import com.threewks.thundr.route.RouterModule;
import com.threewks.thundr.transformer.TransformerModule;
import com.threewks.thundr.view.ViewResolver;
import com.threewks.thundr.view.ViewResolverNotFoundException;
import com.threewks.thundr.view.ViewResolverRegistry;

public class ThundrServlet extends HttpServlet {
	private static final long serialVersionUID = -7179293239117252585L;
	private static final String POST = "POST";
	private static final String HEAD = "HEAD";
	private UpdatableInjectionContext injectionContext;
	private Modules modules;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			long start = System.currentTimeMillis();
			ServletContext servletContext = config.getServletContext();
			injectionContext = initInjectionContext(servletContext);
			modules = initModules(injectionContext);
			debugRoutes(injectionContext);
			Logger.info("Started up in %dms", System.currentTimeMillis() - start);
		} catch (RuntimeException e) {
			throw new ServletException("Failed to initialse thundr: " + e.getMessage(), e);
		}
	}

	private void debugRoutes(UpdatableInjectionContext injectionContext) {
		Router routes = injectionContext.get(Router.class);
		if (routes == null || routes.isEmpty()) {
			Logger.warn("No routes are configured for this application.");
		}
		if (Logger.willDebug()) {
			Logger.debug("Loaded routes: \n%s", routes.listRoutes());
		}
	}

	protected Modules initModules(UpdatableInjectionContext injectionContext) {
		Modules modules = new Modules();
		injectionContext.inject(modules).as(Modules.class);

		for (Class<? extends Module> module : getBaseModules()) {
			modules.addModule(module);
		}
		modules.runStartupLifecycle(injectionContext);
		return modules;
	}

	protected List<Class<? extends Module>> getBaseModules() {
		List<Class<? extends Module>> baseModules = new ArrayList<Class<? extends Module>>();
		baseModules.add(ConfigurationModule.class);
		baseModules.add(ModulesModule.class);
		baseModules.add(TransformerModule.class);
		baseModules.add(RouterModule.class);
		return baseModules;
	}

	/**
	 * This extension point allows overriding servlets to change the basic configuration of a thundr application.
	 * Generally this involves bootstrapping a configuration solution, loading modules and adding any routes.
	 * 
	 * @param servletContext
	 * @return
	 */
	protected UpdatableInjectionContext initInjectionContext(ServletContext servletContext) {
		UpdatableInjectionContext injectionContext = new InjectionContextImpl();
		injectionContext.inject(servletContext).as(ServletContext.class);
		servletContext.setAttribute("injectionContext", injectionContext);
		return injectionContext;
	}

	protected void applyRoute(final HttpMethod routeType, final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		final ViewResolverRegistry viewResolverRegistry = injectionContext.get(ViewResolverRegistry.class);
		String requestPath = req.getRequestURI();
		try {
			Logger.debug("Invoking path %s", requestPath);
			RequestThreadLocal.set(req, resp);
			Router routes = injectionContext.get(Router.class);
			final Object viewResult = routes.invoke(requestPath, routeType, req, resp);
			if (viewResult != null) {
				resolveView(req, resp, viewResolverRegistry, viewResult);
			}
		} catch (Exception e) {
			if (Cast.is(e, RouteResolverException.class)) {
				// unwrap ActionException if it is one
				e = (Exception) Cast.as(e, RouteResolverException.class).getCause();
			}
			if (Cast.is(e, ViewResolverNotFoundException.class)) {
				// if there was an error finding a view resolver, propogate this
				throw (ViewResolverNotFoundException) e;
			}
			if (!resp.isCommitted()) {
				ViewResolver<Exception> viewResolver = viewResolverRegistry.findViewResolver(e);
				if (viewResolver != null) {
					viewResolver.resolve(req, resp, e);
				}
			}
		} finally {
			RequestThreadLocal.clear();
		}
	}

	private void resolveView(final HttpServletRequest req, final HttpServletResponse resp, final ViewResolverRegistry viewResolverRegistry, final Object viewResult) {
		ViewResolver<Object> viewResolver = viewResolverRegistry.findViewResolver(viewResult);
		if (viewResolver == null) {
			throw new ViewResolverNotFoundException("No %s is registered for the view result %s - %s", ViewResolver.class.getSimpleName(), viewResult.getClass().getSimpleName(), viewResult);
		}
		viewResolver.resolve(req, resp, viewResult);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean handled = customService(req, resp);
		if (!handled) {
			String method = determineMethod(req);
			HttpMethod routeType = HttpMethod.from(method);
			if (routeType != null) {
				applyRoute(routeType, req, resp);
			} else if (HEAD.equals(method)) {
				doHead(req, resp);
			} else {
				// thundr doesnt deal with these
				resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Request method '" + method + "' not implemented.");
			}
		}
	}

	/**
	 * Given a request, determines the method (i.e. GET, PUT, POST etc)
	 * 
	 * @param req
	 * @return
	 */
	protected String determineMethod(HttpServletRequest req) {
		String method = req.getMethod();
		if (POST.equalsIgnoreCase(method)) {
			String methodOverride = getHeaderCaseInsensitive(req, Header.XHttpMethodOverride);
			String methodOverride2 = getParameterCaseInsensitive(req, "_method");

			if (methodOverride != null) {
				method = methodOverride;
			}
			if (methodOverride2 != null) {
				method = methodOverride2;
			}
		}
		return method;
	}

	/**
	 * A custom extensionpoint which allows overriding servlets to handle requests/route types that thundr currently does not.
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	protected boolean customService(HttpServletRequest req, HttpServletResponse resp) {
		return false;
	}

	/*
	 * This method is here so that the basic servlet HEAD functionality continues to work;
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		applyRoute(HttpMethod.GET, req, resp);
	}

	@SuppressWarnings("unchecked")
	protected String getParameterCaseInsensitive(HttpServletRequest req, String parameterName) {
		Iterable<String> iterable = Expressive.<String> iterable(req.getParameterNames());
		for (String parameter : iterable) {
			if (parameterName.equalsIgnoreCase(parameter)) {
				return req.getParameter(parameter);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected String getHeaderCaseInsensitive(HttpServletRequest req, String headerName) {
		Iterable<String> iterable = Expressive.<String> iterable(req.getHeaderNames());
		for (String header : iterable) {
			if (headerName.equalsIgnoreCase(header)) {
				return req.getHeader(header);
			}
		}
		return null;
	}

	@Override
	public void destroy() {
		modules.runStopLifecycle(injectionContext);
		super.destroy();
	}
}