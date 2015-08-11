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
package com.threewks.thundr.bind.http.request;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.atomicleopard.expressive.Cast;
import com.atomicleopard.expressive.Expressive;
import com.threewks.thundr.bind.Binder;
import com.threewks.thundr.introspection.ParameterDescription;
import com.threewks.thundr.request.Request;
import com.threewks.thundr.request.Response;
import com.threewks.thundr.request.servlet.ServletRequest;
import com.threewks.thundr.request.servlet.ServletResponse;

public class ServletRequestClassBinder implements Binder {
	public static final List<Class<?>> BoundTypes = Expressive.<Class<?>> list(javax.servlet.ServletRequest.class, javax.servlet.ServletResponse.class, HttpServletRequest.class,
			HttpServletResponse.class, HttpSession.class);

	@Override
	public void bindAll(Map<ParameterDescription, Object> bindings, Request req, Response resp, Map<String, String> pathVariables) {
		for (Map.Entry<ParameterDescription, Object> binding : bindings.entrySet()) {
			if (binding.getValue() == null) {
				ParameterDescription parameterDescription = binding.getKey();

				HttpServletRequest servletRequest = req.getRawRequest(HttpServletRequest.class);
				Object value = null;
				if (servletRequest != null) {
					if (parameterDescription.isA(HttpServletRequest.class) || parameterDescription.isA(javax.servlet.ServletRequest.class)) {
						value = servletRequest;
					}
					if (parameterDescription.isA(HttpSession.class)) {
						value = servletRequest.getSession();
					}
				}
				HttpServletResponse servletResponse = resp.getRawResponse(HttpServletResponse.class);
				if (servletResponse != null) {
					if (parameterDescription.isA(HttpServletResponse.class) || parameterDescription.isA(javax.servlet.ServletResponse.class)) {
						value = servletResponse;
					}
				}
				bindings.put(parameterDescription, value);
			}
		}
	}
}
