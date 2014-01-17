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
package com.threewks.thundr.view.jsp;

import static com.atomicleopard.expressive.Expressive.map;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

public class JspViewTest {
	@Test
	public void shouldSaveViewPathAndHaveEmptyModel() {
		JspView view = new JspView("/WEB-INF/jsp/path/view.jsp");
		assertThat(view.getView(), is("/WEB-INF/jsp/path/view.jsp"));
		assertThat(view.getModel(), is(notNullValue()));
		assertThat(view.getModel().isEmpty(), is(true));
	}

	@Test
	public void shouldSaveViewPathAndModel() {
		Map<String, Object> model = map("input", 1);
		JspView view = new JspView("/WEB-INF/jsp/path/view.jsp", model);
		assertThat(view.getView(), is("/WEB-INF/jsp/path/view.jsp"));
		assertThat(view.getModel(), is(notNullValue()));
		assertThat(view.getModel().get("input"), is((Object) 1));
	}

	@Test
	public void shouldReturnViewPathRelativeToWebInfAndForJspWhenPartialViewNameGiven() {
		assertThat(new JspView("view").getView(), is("/WEB-INF/jsp/view.jsp"));
		assertThat(new JspView("view.jsp").getView(), is("/WEB-INF/jsp/view.jsp"));
		assertThat(new JspView("path/view.jsp").getView(), is("/WEB-INF/jsp/path/view.jsp"));
		assertThat(new JspView("path/view").getView(), is("/WEB-INF/jsp/path/view.jsp"));
	}

	@Test
	public void shouldReturnViewNameForToString() {
		assertThat(new JspView("/WEB-INF/jsp/view.jsp").toString(), is("/WEB-INF/jsp/view.jsp"));
		assertThat(new JspView("view").toString(), is("view (/WEB-INF/jsp/view.jsp)"));
		assertThat(new JspView("view.jsp").toString(), is("view.jsp (/WEB-INF/jsp/view.jsp)"));
		assertThat(new JspView("path/view.jsp").toString(), is("path/view.jsp (/WEB-INF/jsp/path/view.jsp)"));
		assertThat(new JspView("path/view").toString(), is("path/view (/WEB-INF/jsp/path/view.jsp)"));
	}

	@Test
	public void shouldBeAbleToSetExtendedValuesDirectly() {
		JspView view = new JspView("view");
		assertThat(view.getContentType(), is("text/html"));
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
