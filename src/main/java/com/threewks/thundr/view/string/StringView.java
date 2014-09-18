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

import java.io.UnsupportedEncodingException;

import com.threewks.thundr.http.ContentType;
import com.threewks.thundr.view.BaseView;
import com.threewks.thundr.view.View;

import jodd.util.StringPool;

public class StringView extends BaseView<StringView> implements View {
	private String content;

	public StringView(String content) {
		this.content = content;
		withContentType(ContentType.TextPlain.value());
		withCharacterEncoding(StringPool.UTF_8);
	}

	public StringView(String format, Object... args) {
		this(String.format(format, args));
	}

	public String content() {
		return content;
	}

	public String toString() {
		return content.toString();
	}

	public byte[] contentBytes() throws UnsupportedEncodingException {
		String encoding = getCharacterEncoding();
		byte[] bytes = content.toString().getBytes(encoding);
		return bytes;
	}
}
