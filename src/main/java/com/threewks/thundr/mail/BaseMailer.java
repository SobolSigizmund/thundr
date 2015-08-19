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
package com.threewks.thundr.mail;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.atomicleopard.expressive.Expressive;
import com.threewks.thundr.request.InMemoryResponse;
import com.threewks.thundr.request.RequestContainer;
import com.threewks.thundr.transformer.TransformerManager;
import com.threewks.thundr.view.BasicViewRenderer;
import com.threewks.thundr.view.ViewResolverRegistry;

public abstract class BaseMailer implements Mailer {
	protected ViewResolverRegistry viewResolverRegistry;
	protected RequestContainer requestContainer;

	public BaseMailer(ViewResolverRegistry viewResolverRegistry, RequestContainer requestContainer) {
		this.viewResolverRegistry = viewResolverRegistry;
		this.requestContainer = requestContainer;
	}

	@Override
	public MailBuilder mail() {
		return new MailBuilderImpl(this);
	}

	@Override
	public void send(MailBuilder mailBuilder) {
		String subject = mailBuilder.subject();
		Map.Entry<String, String> from = mailBuilder.from();
		Map.Entry<String, String> replyTo = mailBuilder.replyTo();
		Map<String, String> to = mailBuilder.to();
		Map<String, String> cc = mailBuilder.cc();
		Map<String, String> bcc = mailBuilder.bcc();

		validateFrom(from);
		validateRecipients(to, cc, bcc);

		try {
			sendInternal(from, replyTo, to, cc, bcc, subject, mailBuilder.body(), mailBuilder.attachments());
		} catch (MailException e) {
			throw e;
		} catch (Exception e) {
			throw new MailException(e, "Failed to send an email: %s", e.getMessage());
		}
	}

	protected InMemoryResponse render(Object view) {
		try {
			BasicViewRenderer basicViewRenderer = new BasicViewRenderer(viewResolverRegistry);
			InMemoryResponse response = new InMemoryResponse(getTransformerManager());
			basicViewRenderer.render(requestContainer.getRequest(), response, view);
			return response;
		} catch (Exception e) {
			throw new MailException(e, "Failed to render email part: %s", e.getMessage());
		}
	}

	protected TransformerManager getTransformerManager() {
		return TransformerManager.createWithDefaults();
	}

	protected abstract void sendInternal(Entry<String, String> from, Entry<String, String> replyTo, Map<String, String> to, Map<String, String> cc, Map<String, String> bcc, String subject,
			Object body, List<Attachment> attachments);

	protected void validateRecipients(Map<String, String> to, Map<String, String> cc, Map<String, String> bcc) {
		if (Expressive.isEmpty(to) && Expressive.isEmpty(cc) && Expressive.isEmpty(bcc)) {
			throw new MailException("No recipient (to, cc or bcc) has been set for this email");
		}
	}

	protected void validateFrom(Entry<String, String> from) {
		if (from == null || from.getKey() == null) {
			throw new MailException("No sender has been set for this email");
		}
	}
}
