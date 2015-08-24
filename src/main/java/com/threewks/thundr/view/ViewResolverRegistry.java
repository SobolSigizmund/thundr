/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://3wks.github.io/thundr/
 * Copyright (C) 2014 3wks, <thundr@3wks.com.au>
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
package com.threewks.thundr.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.threewks.thundr.introspection.ClassIntrospector;
import com.threewks.thundr.logger.Logger;
import com.threewks.thundr.request.Request;
import com.threewks.thundr.request.Response;

public class ViewResolverRegistry {
	private Map<Class<?>, ViewResolver<?>> resolvers = new HashMap<Class<?>, ViewResolver<?>>();
	private List<Class<?>> resolversOrder = new ArrayList<Class<?>>();
	private Map<Class<?>, ViewResolver<?>> resolversCache = new WeakHashMap<Class<?>, ViewResolver<?>>();
	private ClassIntrospector classIntrospector = new ClassIntrospector();

	public <T> void addResolver(Class<T> viewResult, ViewResolver<T> resolver) {
		resolvers.put(viewResult, (ViewResolver<T>) resolver);
		resolversOrder.add(viewResult);
		clearResolversCache();
		Logger.info("Added ViewResolver %s for views of %s", resolver, viewResult);
	}

	public void removeResolver(Class<?> viewResult) {
		resolvers.remove(viewResult);
		resolversOrder.removeAll(Collections.singletonList(viewResult));
		clearResolversCache();
	}

	private void clearResolversCache() {
		synchronized (resolversCache) {
			resolversCache.clear();
		}
	}

	public <T> ViewResolver<T> findViewResolver(T viewResult) {
		ViewResolver<T> viewResolver = findViewResolverInCache(viewResult);
		if (viewResolver == null) {
			viewResolver = createAndCacheResolver(viewResult);
		}
		return viewResolver;
	}

	@SuppressWarnings("unchecked")
	protected <T> ViewResolver<T> createAndCacheResolver(T viewResult) {
		Class<?> resultType = getViewResultType(viewResult);
		List<Class<?>> orderedTypes = classIntrospector.listImplementedTypes(resultType);
		synchronized (resolversCache) {
			for (Class<?> type : orderedTypes) {
				for (int i = resolversOrder.size() - 1; i >= 0; i--) {
					Class<?> resolverClass = resolversOrder.get(i);
					Class<?> viewResultClass = type;
					if (resolverClass == viewResultClass) {
						ViewResolver<T> viewResolver = (ViewResolver<T>) resolvers.get(resolverClass);
						resolversCache.put(resultType, viewResolver);
						return viewResolver;
					}
				}
			}
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> ViewResolver<T> findViewResolverInCache(T viewResult) {
		Class<?> type = getViewResultType(viewResult);
		synchronized (resolversCache) {
			return (ViewResolver<T>) resolversCache.get(type);
		}
	}

	private <T> Class<?> getViewResultType(T viewResult) {
		Class<?> type = viewResult == null ? null : viewResult.getClass();
		return type;
	}

	public <R extends Response> R resolve(Request request, R response, Object view) {
		ViewResolver<Object> viewResolver = findViewResolver(view);
		if (viewResolver == null) {
			throw new ViewResolverNotFoundException("No %s is registered for the view result %s - %s", ViewResolver.class.getSimpleName(), view.getClass().getSimpleName(), view);
		}
		viewResolver.resolve(request, response, view);
		return response;
	}
}
