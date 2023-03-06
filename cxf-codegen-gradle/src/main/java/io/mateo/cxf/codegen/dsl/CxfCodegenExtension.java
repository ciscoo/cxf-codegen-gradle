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
package io.mateo.cxf.codegen.dsl;

import org.gradle.api.provider.Property;

/**
 * Configuration options for the plugin.
 */
public abstract class CxfCodegenExtension {

	/**
	 * Name of this extension.
	 */
	public static final String EXTENSION_NAME = "cxfCodegen";

	/**
	 * Specify the version of Apache CXF dependencies. The convention is
	 * {@value io.mateo.cxf.codegen.internal.GeneratedVersionAccessor#CXF_VERSION}
	 */
	public abstract Property<String> getCxfVersion();

}
