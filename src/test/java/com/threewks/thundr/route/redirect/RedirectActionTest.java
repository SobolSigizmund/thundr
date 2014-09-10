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
package com.threewks.thundr.route.redirect;

import static com.atomicleopard.expressive.Expressive.map;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import com.threewks.thundr.route.redirect.Redirect;

public class RedirectActionTest {
	private Map<String, String> emptyPathVars = Collections.emptyMap();

	@Test
	public void shouldCreateSimpleRedirect() {
		Redirect redirectAction = new Redirect("/path/to/file.html");
		assertThat(redirectAction.getRedirectTo(emptyPathVars), is("/path/to/file.html"));
	}

	@Test
	public void shouldRedirectWithPathVar() {
		Map<String, String> pathVars = map("to", "destination");
		Redirect redirectAction = new Redirect("/path/{to}/file.html");
		assertThat(redirectAction.getRedirectTo(pathVars), is("/path/destination/file.html"));
	}

	@Test
	public void shouldRedirectWithMissingPathVar() {
		Map<String, String> pathVars = map("to", "destination");
		Redirect redirectAction = new Redirect("/path/{to}/{file}.html");
		assertThat(redirectAction.getRedirectTo(pathVars), is("/path/destination/.html"));
	}

	@Test
	public void shouldRedirectWithExtraPathVars() {
		Map<String, String> pathVars = map("to", "destination", "other", "whatever");
		Redirect redirectAction = new Redirect("http://google.com?q={to}");
		assertThat(redirectAction.getRedirectTo(pathVars), is("http://google.com?q=destination"));
	}

	@Test
	public void shouldHaveUsefulToString() {
		assertThat(new Redirect("http://google.com?q={to}").toString(), is("Redirect:http://google.com?q={to}"));
	}
}
