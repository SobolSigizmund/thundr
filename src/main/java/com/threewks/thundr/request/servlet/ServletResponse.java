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
package com.threewks.thundr.request.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.threewks.thundr.http.Cookie;
import com.threewks.thundr.http.StatusCode;
import com.threewks.thundr.request.BaseResponse;
import com.threewks.thundr.request.Response;
import com.threewks.thundr.transformer.TransformerManager;

public class ServletResponse extends BaseResponse implements Response {
	protected HttpServletResponse resp;

	public ServletResponse(TransformerManager transformerManager, HttpServletResponse resp) {
		super(transformerManager);
		this.resp = resp;
	}

	@Override
	public boolean isCommitted() {
		return resp.isCommitted();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return resp.getOutputStream();
	}

	/*
	 * @Override
	 * public Response withContent(InputStream inputStream) throws IOException {
	 * ServletOutputStream outputStream = resp.getOutputStream();
	 * Streams.copy(inputStream, outputStream);
	 * outputStream.flush();
	 * return this;
	 * }
	 */

	@Override
	protected Object getRawResponse() {
		return resp;
	}

	@Override
	protected void setHeaderInternal(String key, List<String> values) {
		boolean first = true;
		for (String value : values) {
			if (first) {
				resp.setHeader(key, value);
			} else {
				resp.addHeader(key, value);
			}
			first = false;
		}
	}

	@Override
	public Response withStatusCode(StatusCode statusCode) {
		resp.setStatus(statusCode.getCode());
		return this;
	}

	@Override
	public Response withStatusMessage(String message) {
		// TODO - v3 - noop
		return this;
	}

	@Override
	public Response withContentType(String contentType) {
		resp.setContentType(contentType);
		return this;
	}

	@Override
	public Response withCookie(Cookie cookie) {
		resp.addCookie(Servlets.toServletCookie(cookie));
		return this;
	}

	@Override
	public Response withCookies(Collection<Cookie> cookies) {
		// @formatter:off
		cookies.stream()
				.map(Servlets::toServletCookie)
				.forEach(cookie -> resp.addCookie(cookie));
		// @formatter:on
		return this;
	}

	@Override
	public Response withCharacterEncoding(String characterEncoding) {
		resp.setCharacterEncoding(characterEncoding);
		return this;
	}

	@Override
	public Response withContentLength(int length) {
		resp.setContentLength((int) length);
		return this;
	}
}
