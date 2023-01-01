/*
 * Copyright 2020-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mateo.cxf.codegen.junit;

import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayNameGenerator;

/**
 * {@code DisplayNameGenerator} to generate test names that can be safely used for task
 * registration, delegating to the <em>standard</em> display generator for class name
 * generation.
 */
public final class TaskNameGenerator implements DisplayNameGenerator {

	private static final DisplayNameGenerator STANDARD_DISPLAY_NAME_GENERATOR = new Standard();

	public TaskNameGenerator() {
	}

	@Override
	public String generateDisplayNameForClass(Class<?> testClass) {
		return STANDARD_DISPLAY_NAME_GENERATOR.generateDisplayNameForClass(testClass);
	}

	@Override
	public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
		return STANDARD_DISPLAY_NAME_GENERATOR.generateDisplayNameForNestedClass(nestedClass);
	}

	@Override
	public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
		return testMethod.getName();
	}

}
