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
package com.threewks.thundr.bind.http;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.threewks.thundr.bind.BindException;
import com.threewks.thundr.bind.Binder;
import com.threewks.thundr.bind.parameter.ParameterBinderRegistry;
import com.threewks.thundr.http.ContentType;
import com.threewks.thundr.http.MultipartFile;
import com.threewks.thundr.introspection.ParameterDescription;
import com.threewks.thundr.util.Streams;

public class MultipartHttpBinder implements Binder {

	private List<ContentType> supportedContentTypes = Arrays.asList(ContentType.MultipartFormData);
	private ServletFileUpload upload = new ServletFileUpload();
	private ParameterBinderRegistry parameterBinderRegistry;

	public MultipartHttpBinder(ParameterBinderRegistry parameterBinderRegistry) {
		super();
		this.parameterBinderRegistry = parameterBinderRegistry;
	}

	@Override
	public void bindAll(Map<ParameterDescription, Object> bindings, HttpServletRequest req, HttpServletResponse resp, Map<String, String> pathVariables) {
		if (ContentType.matchesAny(req.getContentType(), supportedContentTypes) && shouldTryToBind(bindings)) {
			Map<String, List<String>> formFields = new HashMap<String, List<String>>();
			Map<String, MultipartFile> fileFields = new HashMap<String, MultipartFile>();
			extractParameters(req, formFields, fileFields);
			Map<String, String[]> parameterMap = ParameterBinderRegistry.convertListMapToArrayMap(formFields);
			parameterBinderRegistry.bind(bindings, parameterMap, fileFields);
		}
	}

	/**
	 * If all parameters have been bound, we don't need to try to bind. This means we won't consume the stream leaving it in tact to be read in controllers.
	 * 
	 * @param bindings
	 * @return
	 */
	boolean shouldTryToBind(Map<ParameterDescription, Object> bindings) {
		return bindings.values().contains(null);
	}

	void extractParameters(HttpServletRequest req, Map<String, List<String>> formFields, Map<String, MultipartFile> fileFields) {
		try {
			FileItemIterator itemIterator = upload.getItemIterator(req);
			while (itemIterator.hasNext()) {
				FileItemStream item = itemIterator.next();
				InputStream stream = item.openStream();

				String fieldName = item.getFieldName();
				if (item.isFormField()) {
					List<String> existing = formFields.get(fieldName);
					if (existing == null) {
						existing = new LinkedList<String>();
						formFields.put(fieldName, existing);
					}
					existing.add(Streams.readString(stream));
				} else {
					MultipartFile file = new MultipartFile(item.getName(), Streams.readBytes(stream), item.getContentType());
					fileFields.put(fieldName, file);
				}
				stream.close();
			}
		} catch (Exception e) {
			throw new BindException(e, "Failed to bind multipart form data: %s", e.getMessage());
		}
	}
}
