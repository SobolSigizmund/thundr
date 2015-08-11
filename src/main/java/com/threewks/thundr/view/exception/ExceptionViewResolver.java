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
package com.threewks.thundr.view.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.threewks.thundr.http.StatusCode;
import com.threewks.thundr.logger.Logger;
import com.threewks.thundr.request.Request;
import com.threewks.thundr.request.Response;
import com.threewks.thundr.view.ViewResolutionException;
import com.threewks.thundr.view.ViewResolver;

public class ExceptionViewResolver implements ViewResolver<Throwable> {
	@Override
	public void resolve(Request req, Response resp, Throwable viewResult) {
		List<String> messages = new ArrayList<String>();
		for (Throwable cause = viewResult; cause != null; cause = cause.getCause()) {
			messages.add(cause.getMessage());
		}
		try {
			Throwable exceptionOfInterest = viewResult;
			if (viewResult instanceof ViewResolutionException && viewResult.getCause() != null) {
				exceptionOfInterest = viewResult.getCause();
			}
			StringWriter stringWriter = new StringWriter();
			PrintWriter writer = new PrintWriter(stringWriter);
			for (String message : messages) {
				writer.println(message);
			}
			exceptionOfInterest.printStackTrace(writer);
			writer.flush();
			// @formatter:off
			resp.withStatusCode(StatusCode.InternalServerError)
				.withStatusMessage(stringWriter.toString());
			// @formatter:on
			// TODO - v3 - does sendError trigger the servlet's error handling? If so this will behave differently
			//resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, );
			Logger.error(stringWriter.toString());
		} catch (Exception e) {
			Logger.error("Failed to render an exception view because '%s' - original exception: %s", e.getMessage(), viewResult.getMessage());
			viewResult.printStackTrace();
		}
	}
}
