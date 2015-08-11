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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;

import com.threewks.thundr.introspection.ParameterDescription;
import com.threewks.thundr.request.mock.MockRequest;
import com.threewks.thundr.request.mock.MockResponse;
import com.threewks.thundr.test.mock.servlet.MockHttpServletRequest;
import com.threewks.thundr.test.mock.servlet.MockHttpServletResponse;

public class ServletRequestClassBinderTest {
	private ServletRequestClassBinder binder;
	private MockHttpServletRequest servletRequest = new MockHttpServletRequest();
	private MockHttpServletResponse servletResponse = new MockHttpServletResponse();
	private MockRequest request = new MockRequest().withRawRequest(servletRequest);
	private MockResponse response = new MockResponse().withRawResponse(servletResponse);
	private HttpSession session;
	private Map<String, String> pathVariables;
	private Map<ParameterDescription, Object> parameterDescriptions;

	@Before
	public void before() {
		binder = new ServletRequestClassBinder();
		parameterDescriptions = new LinkedHashMap<ParameterDescription, Object>();
		pathVariables = new HashMap<String, String>();
		session = servletRequest.getSession();
	}

	@Test
	public void shouldBindToHttpServletRequestParam() {
		ParameterDescription httpServletRequestParameter = new ParameterDescription("request", HttpServletRequest.class);
		parameterDescriptions.put(httpServletRequestParameter, null);

		binder.bindAll(parameterDescriptions, request, response, pathVariables);
		assertThat(parameterDescriptions.get(httpServletRequestParameter), is((Object) servletRequest));
	}

	@Test
	public void shouldBindToServletRequestParam() {
		ParameterDescription servletRequestParameter = new ParameterDescription("request", ServletRequest.class);
		parameterDescriptions.put(servletRequestParameter, null);

		binder.bindAll(parameterDescriptions, request, response, pathVariables);
		assertThat(parameterDescriptions.get(servletRequestParameter), is((Object) servletRequest));
	}

	@Test
	public void shouldBindToHttpServletResponseParam() {
		ParameterDescription httpServletResponseParameter = new ParameterDescription("response", HttpServletResponse.class);
		parameterDescriptions.put(httpServletResponseParameter, null);

		binder.bindAll(parameterDescriptions, request, response, pathVariables);
		assertThat(parameterDescriptions.get(httpServletResponseParameter), is((Object) servletResponse));
	}

	@Test
	public void shouldBindToServletResponseParam() {
		ParameterDescription servletResponseParameter = new ParameterDescription("response", ServletResponse.class);
		parameterDescriptions.put(servletResponseParameter, null);

		binder.bindAll(parameterDescriptions, request, response, pathVariables);
		assertThat(parameterDescriptions.get(servletResponseParameter), is((Object) servletResponse));
	}

	@Test
	public void shouldBindToHttpSessionParam() {
		ParameterDescription sessionParameter = new ParameterDescription("session", HttpSession.class);
		parameterDescriptions.put(sessionParameter, null);

		binder.bindAll(parameterDescriptions, request, response, pathVariables);
		assertThat(parameterDescriptions.get(sessionParameter), is((Object) session));
	}

	@Test
	public void shouldNotBindToRequestClassesWhenNotNull() {
		ParameterDescription servletRequestParameter = new ParameterDescription("request", ServletRequest.class);
		ParameterDescription servletResponseParameter = new ParameterDescription("response", ServletResponse.class);
		ParameterDescription sessionParameter = new ParameterDescription("session", HttpSession.class);

		parameterDescriptions.put(servletRequestParameter, "string");
		parameterDescriptions.put(servletResponseParameter, "string");
		parameterDescriptions.put(sessionParameter, "string");

		binder.bindAll(parameterDescriptions, request, response, pathVariables);
		assertThat(parameterDescriptions.get(servletRequestParameter), is((Object) "string"));
		assertThat(parameterDescriptions.get(servletResponseParameter), is((Object) "string"));
		assertThat(parameterDescriptions.get(sessionParameter), is((Object) "string"));
	}

}
