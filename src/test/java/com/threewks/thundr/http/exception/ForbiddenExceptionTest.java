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
package com.threewks.thundr.http.exception;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.threewks.thundr.http.StatusCode;

public class ForbiddenExceptionTest {

	@Test
	public void shouldFormatAndRetainReason() {
		ForbiddenException forbiddenException = new ForbiddenException("String %s", "format");
		assertThat(forbiddenException.getMessage(), is("String format"));
		assertThat(forbiddenException.getCause(), is(nullValue()));
		assertThat(forbiddenException.getStatus(), is(StatusCode.Forbidden));
	}

	@Test
	public void shouldFormatAndRetainCauseAndReason() {
		Exception exception = new Exception("cause");
		ForbiddenException forbiddenException = new ForbiddenException(exception, "String %s", "format");
		assertThat(forbiddenException.getMessage(), is("String format"));
		assertThat(forbiddenException.getCause(), is((Throwable) exception));
		assertThat(forbiddenException.getStatus(), is(StatusCode.Forbidden));
	}
}
