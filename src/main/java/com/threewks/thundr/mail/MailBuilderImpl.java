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
package com.threewks.thundr.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MailBuilderImpl implements MailBuilder {
	private Mailer mailer;
	private String subject;
	private Map<String, String> from = new HashMap<String, String>();
	private Map<String, String> replyTo = new HashMap<String, String>();
	private Map<String, String> to = new HashMap<String, String>();
	private Map<String, String> cc = new HashMap<String, String>();
	private Map<String, String> bcc = new HashMap<String, String>();
	private Object body;
	private List<Attachment> attachments = new ArrayList<Attachment>();

	public MailBuilderImpl(Mailer mailer) {
		this.mailer = mailer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T body() {
		return (T) body;
	}

	@Override
	public <T> MailBuilder body(T view) {
		this.body = view;
		return this;
	}

	@Override
	public void send() {
		mailer.send(this);
	}

	@Override
	public MailBuilder subject(String subject) {
		this.subject = subject;
		return this;
	}

	@Override
	public MailBuilder from(String emailAddress) {
		return from(emailAddress, null);
	}

	@Override
	public MailBuilder from(String emailAddress, String name) {
		this.from = Collections.singletonMap(emailAddress, name);
		return this;
	}

	@Override
	public MailBuilder to(String emailAddress) {
		return to(emailAddress, null);
	}

	@Override
	public MailBuilder to(String emailAddress, String name) {
		return to(Collections.singletonMap(emailAddress, name));
	}

	@Override
	public MailBuilder to(Map<String, String> to) {
		this.to.putAll(to);
		return this;
	}

	@Override
	public MailBuilder cc(String emailAddress) {
		return cc(emailAddress, null);
	}

	@Override
	public MailBuilder cc(String emailAddress, String name) {
		return cc(Collections.singletonMap(emailAddress, name));
	}

	@Override
	public MailBuilder cc(Map<String, String> cc) {
		this.cc.putAll(cc);
		return this;
	}

	@Override
	public MailBuilder bcc(String emailAddress) {
		return bcc(emailAddress, null);
	}

	@Override
	public MailBuilder bcc(String emailAddress, String name) {
		return bcc(Collections.singletonMap(emailAddress, name));
	}

	@Override
	public MailBuilder bcc(Map<String, String> bcc) {
		this.bcc.putAll(bcc);
		return this;
	}

	@Override
	public MailBuilder replyTo(String email) {
		return replyTo(email, null);
	}

	@Override
	public MailBuilder replyTo(String email, String name) {
		this.replyTo = Collections.singletonMap(email, name);
		return this;
	}

	@Override
	public Map.Entry<String, String> from() {
		return from.isEmpty() ? null : from.entrySet().iterator().next();
	}

	@Override
	public Map<String, String> to() {
		return new HashMap<String, String>(this.to);
	}

	@Override
	public Map<String, String> cc() {
		return new HashMap<String, String>(this.cc);
	}

	@Override
	public Map<String, String> bcc() {
		return new HashMap<String, String>(bcc);
	}

	@Override
	public String subject() {
		return subject;
	}

	@Override
	public Map.Entry<String, String> replyTo() {
		return replyTo.isEmpty() ? null : replyTo.entrySet().iterator().next();
	}

	@Override
	public MailBuilder attach(Attachment attachment) {
		attachments.add(attachment);
		return this;
	}

	@Override
	public List<Attachment> attachments() {
		return Collections.unmodifiableList(attachments);
	}
}
