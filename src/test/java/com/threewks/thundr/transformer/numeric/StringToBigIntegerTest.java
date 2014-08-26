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
package com.threewks.thundr.transformer.numeric;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.junit.Test;

public class StringToBigIntegerTest {
	private StringToBigInteger transformer = new StringToBigInteger();

	@Test
	public void shouldTransform() {
		assertThat(transformer.from(null), is(nullValue()));
		assertThat(transformer.from("0"), is(BigInteger.ZERO));
		assertThat(transformer.from("1"), is(BigInteger.ONE));
		assertThat(transformer.from("10"), is(BigInteger.TEN));
		assertThat(transformer.from("1234"), is(new BigInteger("1234")));
		assertThat(transformer.from("10000000000000000000000000000000000012"), is(new BigInteger("10000000000000000000000000000000000012")));
		assertThat(transformer.from("-1000000000000000000000000000000000012"), is(new BigInteger("-1000000000000000000000000000000000012")));
	}
}
