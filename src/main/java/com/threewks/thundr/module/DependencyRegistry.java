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
package com.threewks.thundr.module;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.threewks.thundr.injection.Module;

public class DependencyRegistry {
	private Collection<Class<? extends Module>> dependencies = new LinkedHashSet<Class<? extends Module>>();

	public void addDependency(Class<? extends Module> dependency) {
		this.dependencies.add(dependency);
	}

	public Collection<Class<? extends Module>> getDependencies() {
		return dependencies;
	}

	public boolean hasDependency(Class<? extends Module> dependency) {
		return dependencies.contains(dependency);
	}
}
