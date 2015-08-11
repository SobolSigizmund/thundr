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
package com.threewks.thundr.request;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.threewks.thundr.http.ContentType;
import com.threewks.thundr.http.Cookie;
import com.threewks.thundr.route.HttpMethod;
import com.threewks.thundr.test.mock.servlet.MockHttpServletRequest;

public interface Request {

	/**
	 * Gets the raw underlying request, or null if the given type cannot be converted from the underlying request
	 * 
	 * @param type
	 * @return
	 */
	public <T> T getRawRequest(Class<T> type);

	/**
	 * Returns a unique id for this request, useful for request correlation.
	 */
	public UUID getId();

	public String getContentTypeString();

	public ContentType getContentType();

	public String getCharacterEncoding();

	public String getHeader(String name);

	// TODO - v3 - Should provide a mechanism for getting headers and parameters ignoring case
	// public String getHeaderStrict(String name);

	public List<String> getHeaders(String name);

	public Map<String, List<String>> getAllHeaders();

	public String getParameter(String name);

	public List<String> getParameters(String name);

	public Map<String, List<String>> getAllParameters();

	public int getContentLength();

	public HttpMethod getMethod();

	public boolean isA(HttpMethod method);

	/**
	 * Returns the path of the request
	 * 
	 * That is given <code>https://www.domain.com/path/to/resource#anchor?k=v</code> this method will
	 * return <code>/path/to/resource</code>
	 * 
	 * @return
	 */
	public String getRequestPath();

	public List<Cookie> getCookies();

	public Cookie getCookie(String name);

	public List<Cookie> getCookies(String name);

	public Map<String, List<Cookie>> getAllCookies();

	// TODO - v3 - how should you be able to read the content, reader, channel, stream?
	public BufferedReader getReader();

	// TODO - v3 - it makes sense for this stuff to be in the request itself, but requires some
	// re-engineering.
	/**
	 * Get the route for this request
	 * 
	 * @return
	 * 
	 *         public Route getRoute();
	 * 
	 * 
	 *         Path variables are the dynamic portion of the request path as defined in a thundr route.
	 *         This method will return the value of the given path variable for this request.
	 * 
	 * @param name
	 * @return
	 *         public String getPathVariable(String name);
	 * 
	 *         Path variables are the dynamic portion of the request path as defined in a thundr route.
	 *         This method will return all the path variables as a map.
	 * 
	 * @param name
	 * @return
	 *         public Map<String, String> getAllPathVariables();
	 */

}
