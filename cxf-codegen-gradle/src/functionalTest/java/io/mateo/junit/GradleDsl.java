/*
 * Copyright 2020-2024 the original author or authors.
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
package io.mateo.junit;

/**
 * DSLs supported by Gradle.
 * <p>
 * Based on the Spring Boot implementation by Andy Wilkinson.
 */
public enum GradleDsl {

	GROOVY("Groovy DSL", ".gradle"),

	KOTLIN("Kotlin DSL", ".gradle.kts");

	private final String name;

	private final String extension;

	GradleDsl(String name, String extension) {
		this.name = name;
		this.extension = extension;
	}

	public String getName() {
		return this.name;
	}

	String getExtension() {
		return this.extension;
	}

}
