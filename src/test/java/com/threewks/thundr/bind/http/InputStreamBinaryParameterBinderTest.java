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
package com.threewks.thundr.bind.http;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.threewks.thundr.bind.parameter.InputStreamBinaryParameterBinder;
import com.threewks.thundr.http.MultipartFile;
import com.threewks.thundr.introspection.ParameterDescription;

public class InputStreamBinaryParameterBinderTest {

    private InputStreamBinaryParameterBinder binder = new InputStreamBinaryParameterBinder();

    @Test
    public void shouldReturnTrueForWillBindOnInputStreams() {
        assertThat(binder.willBind(new ParameterDescription("data", InputStream.class)), is(true));
        assertThat(binder.willBind(new ParameterDescription("data", BufferedInputStream.class)), is(false));
    }

    @Test
    public void shouldBindByteArrayByReturningInputStream() throws IOException {
        byte[] bytes = new byte[] { 1,  2, 3 };
        byte[] bound = new byte[3];
        binder.bind(new ParameterDescription("data", InputStream[].class), new MultipartFile("test", bytes, "none/none")).read(bound);
        assertThat(bound, is(bytes));
    }

    @Test
    public void shouldHandleNullBytes() throws IOException {
        assertThat(binder.bind(new ParameterDescription("data", InputStream[].class), null), nullValue());
    }
}
