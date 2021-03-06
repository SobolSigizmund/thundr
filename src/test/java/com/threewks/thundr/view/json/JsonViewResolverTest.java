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
package com.threewks.thundr.view.json;

import static com.atomicleopard.expressive.Expressive.map;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.threewks.thundr.http.Cookies;
import com.threewks.thundr.test.mock.servlet.MockHttpServletRequest;
import com.threewks.thundr.test.mock.servlet.MockHttpServletResponse;
import com.threewks.thundr.view.ViewResolutionException;

public class JsonViewResolverTest {

	@Rule public ExpectedException thrown = ExpectedException.none();

	private MockHttpServletRequest req = new MockHttpServletRequest();
	private MockHttpServletResponse resp = new MockHttpServletResponse();
	private JsonViewResolver resolver = new JsonViewResolver();

	@Test
	public void shouldResolveByWritingJsonToOutputStream() throws IOException {
		JsonView viewResult = new JsonView(map("key", "value"));
		resolver.resolve(req, resp, viewResult);
		assertThat(resp.status(), is(HttpServletResponse.SC_OK));
		assertThat(resp.content(), is("{\"key\":\"value\"}"));
		assertThat(resp.getCharacterEncoding(), is("UTF-8"));
		assertThat(resp.getContentLength(), is(15));
	}

	@Test
	public void shouldResolveJsonElementByWritingJsonToOutputStreamAsJsonElement() throws IOException {
		JsonElement jsonEl = createJsonElement();
		JsonView viewResult = new JsonView(jsonEl);
		resolver.resolve(req, resp, viewResult);
		assertThat(resp.status(), is(HttpServletResponse.SC_OK));
		assertThat(resp.content(), is("{\"key\":\"value\"}"));
		assertThat(resp.getCharacterEncoding(), is("UTF-8"));
		assertThat(resp.getContentLength(), is(15));
	}

	@Test
	public void shouldThrowViewResolutionExceptionWhenFailedToWriteJsonToOutputStream() throws IOException {
		thrown.expect(ViewResolutionException.class);
		thrown.expectMessage("Failed to generate JSON output for object 'string'");

		resp = spy(resp);
		when(resp.getWriter()).thenThrow(new RuntimeException("fail"));
		JsonView viewResult = new JsonView("string");
		resolver.resolve(req, resp, viewResult);
	}

	@Test
	public void shouldReturnClassNameForToString() {
		assertThat(new JsonViewResolver().toString(), is("JsonViewResolver"));
	}

	@Test
	public void shouldSetJsonContentType() {
		JsonView viewResult = new JsonView(map("key", "value"));
		resolver.resolve(req, resp, viewResult);
		assertThat(resp.getContentType(), is("application/json"));
	}

	@Test
	public void shouldRespectExtendedViewValues() {
		JsonView view = new JsonView(map("key", "value"));
		Cookie cookie = Cookies.build("cookie").withValue("value2").build();
		view.withContentType("content/type").withCharacterEncoding("UTF-16").withHeader("header", "value1").withCookie(cookie);

		resolver.resolve(req, resp, view);
		assertThat(resp.getContentType(), is("content/type"));
		assertThat(resp.getCharacterEncoding(), is("UTF-16"));
		assertThat(resp.<String> header("header"), is("value1"));
		assertThat(resp.getCookies(), hasItem(cookie));
	}

	private JsonElement createJsonElement() {
		Gson gson = new GsonBuilder().create();
		return gson.fromJson("{\"key\":\"value\"}", JsonElement.class);
	}

	@Test
	public void shouldAllowAccessToInternalGsonBuilder() {
		assertThat(resolver.getGsonBuilder(), is(notNullValue()));

		GsonBuilder gsonBuilder = new GsonBuilder();
		resolver = new JsonViewResolver(gsonBuilder);
		assertThat(resolver.getGsonBuilder(), is(sameInstance(gsonBuilder)));
	}

}
