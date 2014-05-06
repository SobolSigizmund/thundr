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

import java.util.HashMap;
import java.util.Map;

import jodd.util.StringPool;

import org.apache.commons.lang3.StringUtils;

import com.threewks.thundr.http.ContentType;
import com.threewks.thundr.view.TemplateView;
import com.threewks.thundr.view.View;

public class JspView extends TemplateView<JspView> implements View {
	public JspView(String view) {
		this(view, new HashMap<String, Object>());
	}

	public JspView(String view, Map<String, Object> model) {
		super(view, model);
		withContentType(ContentType.TextHtml.value());
		withCharacterEncoding(StringPool.UTF_8);
	}

	@Override
	public String getView() {
		return completeViewName(view);
	}

	@Override
	public String toString() {
		String completeView = completeViewName(view);
		return completeView.equals(view) ? view : String.format("%s (%s)", view, completeView);
	}

	private String completeViewName(String view) {
		if (!StringUtils.startsWith(view, "/")) {
			view = "/WEB-INF/jsp/" + view;
		}
		if (!StringUtils.endsWith(view, ".jsp")) {
			view = view + ".jsp";
		}
		return view;
	}
}
