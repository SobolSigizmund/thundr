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
package com.threewks.thundr.bind.parameter;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.threewks.thundr.introspection.ParameterDescription;
import com.threewks.thundr.transformer.TransformerManager;

import jodd.util.ReflectUtil;

/**
 * Binds to an array.
 * 
 * Supports two types of array - indexed and unindexed.
 * An indexed array looks like this:
 * list[0]=value
 * list[1]=value
 * 
 * and unindexed array looks like this:
 * list=value,value
 * 
 */
public class ArrayParameterBinder<T> implements ParameterBinder<T[]> {
	private static final Pattern indexPattern = Pattern.compile("\\[(\\d+)\\]");

	public T[] bind(ParameterBinderRegistry binders, ParameterDescription parameterDescription, RequestDataMap pathMap, TransformerManager transformerManager) {
		String[] entryForParameter = pathMap.get(parameterDescription.name());
		boolean isIndexed = entryForParameter == null || entryForParameter.length == 0;
		return isIndexed ? createIndexed(binders, parameterDescription, pathMap, transformerManager) : createUnindexed(binders, parameterDescription, pathMap, transformerManager);
	}

	// TODO - Is there a discrepency between how unindexed and indexed entities are created?
	// createUnindexed uses the TransformerManager directly, but createIndexed uses the ParameterBinderRegistry?
	@SuppressWarnings("unchecked")
	private T[] createUnindexed(ParameterBinderRegistry binders, ParameterDescription parameterDescription, RequestDataMap pathMap, TransformerManager transformerManager) {
		String[] entries = pathMap.get(parameterDescription.name());
		// a special case of a single empty string entry we'll equate to null
		if (entries == null || entries.length == 1 && (entries[0] == null || "".equals(entries[0]))) {
			return null;
		}
		Type type = parameterDescription.getArrayType();
		Class<T> clazz = ReflectUtil.toClass(type);
		T[] arrayParameter = createArray(entries.length, clazz);
		for (int i = 0; i < entries.length; i++) {
			String entry = entries[i];
			arrayParameter[i] = transformerManager.transform(String.class, clazz, entry);
		}
		return arrayParameter;
	}

	@SuppressWarnings("unchecked")
	private T[] createIndexed(ParameterBinderRegistry binders, ParameterDescription parameterDescription, RequestDataMap pathMap, TransformerManager transformerManager) {
		pathMap = pathMap.pathMapFor(parameterDescription.name());
		Set<String> uniqueChildren = pathMap.uniqueChildren();
		if (uniqueChildren.size() == 0) {
			return null;
		}

		Map<Integer, String> keyToIndex = new HashMap<Integer, String>();
		int highestIndex = 0;
		for (String string : uniqueChildren) {
			Matcher matcher = indexPattern.matcher(string);
			if (!matcher.matches()) {
				throw new IllegalArgumentException(String.format("Cannot bind %s%s - not a valid array index", parameterDescription.name(), string));
			}
			String indexString = matcher.group(1);
			int index = Integer.parseInt(indexString);
			keyToIndex.put(index, string);
			highestIndex = Math.max(highestIndex, index);
		}
		highestIndex += 1;

		Type type = parameterDescription.getArrayType();
		Class<T> clazz = ReflectUtil.toClass(type);
		T[] arrayParameter = createArray(highestIndex, clazz);
		for (int i = 0; i < highestIndex; i++) {
			String key = keyToIndex.get(i);
			if (key != null) {
				ParameterDescription parameter = new ParameterDescription(key, type);
				T listEntry = (T) binders.createFor(parameter, pathMap);
				arrayParameter[i] = listEntry;
			}
		}
		return arrayParameter;
	}

	@Override
	public boolean willBind(ParameterDescription parameterDescription, TransformerManager transformerManager) {
		return parameterDescription.classType().isArray();
	}

	@SuppressWarnings("unchecked")
	private T[] createArray(int size, Class<T> clazz) {
		return (T[]) Array.newInstance(clazz, size);
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}
}
