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
package com.threewks.thundr.view.string;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import jodd.util.StringPool;

public class StringViewTest {
	@Test
	public void shouldRetainSpecifiedContent() {
		assertThat(new StringView("Content").content().toString(), is("Content"));
	}

	@Test
	public void shouldHaveContentMatchingFormattedArguments() {
		assertThat(new StringView("Format %s %d", "value", 15).content().toString(), is("Format value 15"));
	}

	@Test
	public void shouldHaveDefaultContentTypeOfTextPlainAndCharEncondingUTF8() {
		assertThat(new StringView("").getContentType(), is("text/plain"));
		assertThat(new StringView("", "value").getContentType(), is("text/plain"));
		assertThat(new StringView("").getCharacterEncoding(), is(StringPool.UTF_8));
		assertThat(new StringView("", "value").getCharacterEncoding(), is(StringPool.UTF_8));
	}

	@Test
	public void shouldHaveSpecifiedContentType() {
		StringView view = new StringView("content").withContentType("application/json");
		assertThat(view.content(), is((CharSequence) "content"));
		assertThat(view.getContentType(), is("application/json"));
	}

	@Test
	public void shouldHaveToStringShowingContents() {
		assertThat(new StringView("Content\r\r\n\tIs here ").toString(), is("Content\r\r\n\tIs here "));
	}

	@Test
	public void shouldBeAbleToSetExtendedValuesDirectly() {
		StringView view = new StringView("view content");
		assertThat(view.getContentType(), is("text/plain"));
		assertThat(view.getCharacterEncoding(), is("UTF-8"));
		assertThat(view.getHeader("header"), is(nullValue()));
		assertThat(view.getCookie("cookie"), is(nullValue()));

		view.withContentType("content/type").withCharacterEncoding("UTF-16").withHeader("header", "value1").withCookie("cookie", "value2");

		assertThat(view.getContentType(), is("content/type"));
		assertThat(view.getCharacterEncoding(), is("UTF-16"));
		assertThat(view.getHeader("header"), is("value1"));
		assertThat(view.getCookie("cookie"), is(notNullValue()));
	}
}
