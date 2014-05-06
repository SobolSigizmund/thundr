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
package com.threewks.thundr.view;

/**
 * A base class for 'data' views. Data views essentially expose a representation of a data object.
 * For example, an xml or json view are data views.
 * 
 * @param <Self>
 */
public abstract class DataView<Self extends DataView<Self>> extends BaseView<Self> {
	protected Object output;

	protected DataView(Object output) {
		super();
		this.output = output;
	}

	protected DataView(DataView<?> other) {
		super(other);
		this.output = other.output;
	}

	public Object getOutput() {
		return output;
	}
}
