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

import static com.atomicleopard.expressive.Expressive.map;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.threewks.thundr.bind.parameter.ParameterBinderRegistry;
import com.threewks.thundr.introspection.ParameterDescription;
import com.threewks.thundr.test.mock.servlet.MockHttpServletRequest;
import com.threewks.thundr.test.mock.servlet.MockHttpServletResponse;
import com.threewks.thundr.transformer.TransformerManager;

public class CookieBinderTest {
	private CookieBinder binder;
	private MockHttpServletRequest req = new MockHttpServletRequest();
	private HttpServletResponse resp = new MockHttpServletResponse();
	private Map<String, String> pathVariables = map();
	Map<ParameterDescription, Object> bindings = map();
	private ParameterBinderRegistry parameterBinderRegistry;
	
	@Before
	public void before() {
		parameterBinderRegistry = new ParameterBinderRegistry(TransformerManager.createWithDefaults());
		ParameterBinderRegistry.addDefaultBinders(parameterBinderRegistry);
		binder = new CookieBinder(parameterBinderRegistry);
	}

	@Test
	public void shouldBindCookieMatchingParameterName() {
		ParameterDescription varParam = new ParameterDescription("var", String.class);
		bindings.put(varParam, null);
		req.cookie("var", "expected");
		binder.bindAll(bindings, req, resp, pathVariables);

		assertThat(bindings.get(varParam), is((Object) "expected"));
	}

	@Test
	public void shouldOnlyBindCookieWhenNoBindingAlreadyMade() {
		ParameterDescription varParam = new ParameterDescription("var", String.class);
		bindings.put(varParam, "original");
		req.cookie("var", "overridden");
		binder.bindAll(bindings, req, resp, pathVariables);

		assertThat(bindings.get(varParam), is((Object) "original"));
	}

	@Test
	public void shouldSupportTypeBindingByRelyingOnHttpBinder() {
		ParameterDescription varParam = new ParameterDescription("var", Integer.class);
		bindings.put(varParam, null);
		req.cookie("var", "123");
		binder.bindAll(bindings, req, resp, pathVariables);

		assertThat(bindings.get(varParam), is((Object) 123));
	}

	@Test
	public void shouldBindMultipleCookies() {
		ParameterDescription varParam1 = new ParameterDescription("var1", String.class);
		ParameterDescription varParam2 = new ParameterDescription("var2", String.class);
		ParameterDescription varParam3 = new ParameterDescription("var3", String.class);
		bindings.put(varParam1, null);
		bindings.put(varParam2, null);
		bindings.put(varParam3, null);
		req.cookie("var1", "first");
		req.cookie("var2", "second");
		req.cookie("var3", "third");
		binder.bindAll(bindings, req, resp, pathVariables);

		assertThat(bindings.get(varParam1), is((Object) "first"));
		assertThat(bindings.get(varParam2), is((Object) "second"));
		assertThat(bindings.get(varParam3), is((Object) "third"));
	}

	@Test
	public void shouldBindFirstMatchedCookieWhenMultipleCookiesWithSameNameExistMultipleCookies() {
		ParameterDescription varParam = new ParameterDescription("var", String.class);
		bindings.put(varParam, null);
		req.cookie("var", "first");
		req.cookie("var", "second");
		req.cookie("var", "third");
		binder.bindAll(bindings, req, resp, pathVariables);

		assertThat(bindings.get(varParam), is((Object) "first"));
	}

	@Test
	public void shouldBindToCookieTypeWithMatchingCookie() {
		ParameterDescription varParam = new ParameterDescription("var", Cookie.class);
		bindings.put(varParam, null);

		Cookie expected = new Cookie("var", "expected");
		req.cookie(expected);

		binder.bindAll(bindings, req, resp, pathVariables);

		assertThat(bindings.get(varParam), is(sameInstance((Object) expected)));
	}

	@Test
	public void shouldNotBindAnythingWhenNothingToBind() {
		ParameterDescription varParam1 = new ParameterDescription("cookie", Cookie.class);
		ParameterDescription varParam2 = new ParameterDescription("cookieValue", String.class);
		bindings.put(varParam1, null);
		bindings.put(varParam2, null);

		binder.bindAll(bindings, req, resp, pathVariables);

		assertThat(bindings.get(varParam1), is(nullValue()));
		assertThat(bindings.get(varParam2), is(nullValue()));
	}

	@Test
	public void shouldNotBindIfAllVariablesAlreadyBound() {
		ParameterDescription varParam1 = new ParameterDescription("cookie", Cookie.class);
		ParameterDescription varParam2 = new ParameterDescription("cookieValue", String.class);
		Cookie existingCookie = new Cookie("cookie", "existingValue1");
		bindings.put(varParam1, existingCookie);
		bindings.put(varParam2, "existingValue2");

		Cookie requestCookie = new Cookie("cookie", "newValue");
		Cookie requestCookieValue = new Cookie("cookieValue", "newValue2");
		req.cookie(requestCookie);
		req.cookie(requestCookieValue);

		binder.bindAll(bindings, req, resp, pathVariables);

		assertThat(bindings.get(varParam1), is((Object) existingCookie));
		assertThat(bindings.get(varParam2), is((Object) "existingValue2"));
	}
	
	@Test
	public void shouldNotBindIfNoCookiesExistingOnTheRequest() {
		ParameterDescription varParam1 = new ParameterDescription("cookie", Cookie.class);
		ParameterDescription varParam2 = new ParameterDescription("cookieValue", String.class);
		bindings.put(varParam1, null);
		bindings.put(varParam2, null);


		binder.bindAll(bindings, req, resp, pathVariables);

		assertThat(bindings.get(varParam1), is(nullValue()));
		assertThat(bindings.get(varParam2), is(nullValue()));
	}

	@Test
	public void shouldOnlyBindValuesNotAlreadyBound() {
		ParameterDescription varParam1 = new ParameterDescription("cookie", Cookie.class);
		ParameterDescription varParam2 = new ParameterDescription("cookieValue", String.class);
		Cookie existingCookie = new Cookie("cookie", "existingValue1");
		bindings.put(varParam1, existingCookie);
		bindings.put(varParam2, null);

		Cookie requestCookie = new Cookie("cookie", "newValue1");
		Cookie requestCookieValue = new Cookie("cookieValue", "newValue2");
		req.cookie(requestCookie);
		req.cookie(requestCookieValue);

		binder.bindAll(bindings, req, resp, pathVariables);

		assertThat(bindings.get(varParam1), is((Object) existingCookie));
		assertThat(bindings.get(varParam2), is((Object) "newValue2"));
	}

	@Test
	public void shouldHandleCoreTypeParamBindings() {
		ParameterDescription param1 = new ParameterDescription("param1", String.class);
		ParameterDescription param2 = new ParameterDescription("param2", int.class);
		ParameterDescription param3 = new ParameterDescription("param3", Integer.class);
		ParameterDescription param4 = new ParameterDescription("param4", double.class);
		ParameterDescription param5 = new ParameterDescription("param5", Double.class);
		ParameterDescription param6 = new ParameterDescription("param6", short.class);
		ParameterDescription param7 = new ParameterDescription("param7", Short.class);
		ParameterDescription param8 = new ParameterDescription("param8", float.class);
		ParameterDescription param9 = new ParameterDescription("param9", Float.class);
		ParameterDescription param10 = new ParameterDescription("param10", long.class);
		ParameterDescription param11 = new ParameterDescription("param11", Long.class);
		ParameterDescription param12 = new ParameterDescription("param12", BigDecimal.class);
		ParameterDescription param13 = new ParameterDescription("param13", BigInteger.class);

		Map<ParameterDescription, Object> parameterDescriptions = map();
		parameterDescriptions.put(param1, null);
		parameterDescriptions.put(param2, null);
		parameterDescriptions.put(param3, null);
		parameterDescriptions.put(param4, null);
		parameterDescriptions.put(param5, null);
		parameterDescriptions.put(param6, null);
		parameterDescriptions.put(param7, null);
		parameterDescriptions.put(param8, null);
		parameterDescriptions.put(param9, null);
		parameterDescriptions.put(param10, null);
		parameterDescriptions.put(param11, null);
		parameterDescriptions.put(param12, null);
		parameterDescriptions.put(param13, null);

		req.cookie("param1", "string-value");
		req.cookie("param2", "2");
		req.cookie("param3", "3");
		req.cookie("param4", "4.0");
		req.cookie("param5", "5.0");
		req.cookie("param6", "6");
		req.cookie("param7", "7");
		req.cookie("param8", "8.8");
		req.cookie("param9", "9.9");
		req.cookie("param10", "10");
		req.cookie("param11", "11");
		req.cookie("param12", "12.00");
		req.cookie("param13", "13");

		binder.bindAll(parameterDescriptions, req, resp, pathVariables);

		assertThat(parameterDescriptions.get(param1), is((Object) "string-value"));
		assertThat(parameterDescriptions.get(param2), is((Object) 2));
		assertThat(parameterDescriptions.get(param3), is((Object) 3));
		assertThat(parameterDescriptions.get(param4), is((Object) 4.0));
		assertThat(parameterDescriptions.get(param5), is((Object) 5.0));
		assertThat(parameterDescriptions.get(param6), is((Object) (short) 6));
		assertThat(parameterDescriptions.get(param7), is((Object) (short) 7));
		assertThat(parameterDescriptions.get(param8), is((Object) 8.8f));
		assertThat(parameterDescriptions.get(param9), is((Object) 9.9f));
		assertThat(parameterDescriptions.get(param10), is((Object) 10L));
		assertThat(parameterDescriptions.get(param11), is((Object) 11L));
		assertThat(parameterDescriptions.get(param12), is((Object) new BigDecimal("12.00")));
		assertThat(parameterDescriptions.get(param13), is((Object) BigInteger.valueOf(13)));
	}
}
