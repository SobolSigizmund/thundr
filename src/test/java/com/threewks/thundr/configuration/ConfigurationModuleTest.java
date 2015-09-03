/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://3wks.github.io/thundr/
 * Copyright (C) 2015 3wks, <thundr@3wks.com.au>
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
package com.threewks.thundr.configuration;

import static com.atomicleopard.expressive.Expressive.map;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.threewks.thundr.injection.InjectionContextImpl;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.test.TestSupport;

public class ConfigurationModuleTest {

	private ConfigurationModule configuration = new ConfigurationModule();
	private UpdatableInjectionContext injectionContext = new InjectionContextImpl();
	private PropertiesLoader propertiesLoader = mock(PropertiesLoader.class);

	@Before
	public void before() {
		TestSupport.setField(configuration, "propertiesLoader", propertiesLoader);
	}

	@After
	public void after() {
		Environment.set(null);
	}

	@Test
	public void shouldInjectPropertiesIntoInjectionContext() {
		Map<String, String> properties = map("property1", "property 1 value", "property2", "property 2 value");
		setUpProperties(properties);
		configuration.initialise(injectionContext);
		assertThat(injectionContext.get(String.class, "property1"), is("property 1 value"));
		assertThat(injectionContext.get(String.class, "property2"), is("property 2 value"));
	}

	@Test
	public void shouldInjectEnvironmentSpecificPropertiesIntoInjectionContext() {
		Environment.set("dev");
		Map<String, String> properties = map("property1%my-application-id", "property 1 value", "property2%dev", "property 2 value");
		setUpProperties(properties);
		configuration.initialise(injectionContext);
		assertThat(injectionContext.get(String.class, "property2"), is("property 2 value"));
		assertThat(injectionContext.contains(String.class, "property1"), is(false));
		assertThat(injectionContext.contains(String.class, "property1%my-application-id"), is(true));
	}

	@Test
	public void shouldPreferEnvironmentSpecificPropertiesOverBaseProperties() {
		Environment.set("dev");
		Map<String, String> properties = map("property1%dev", "property 1 development value", "property1", "property 1 base value");
		setUpProperties(properties);
		configuration.initialise(injectionContext);
		assertThat(injectionContext.get(String.class, "property1"), is("property 1 development value"));
	}

	private void setUpProperties(Map<String, String> value) {
		when(propertiesLoader.load(anyString())).thenReturn(value);
	}
}
