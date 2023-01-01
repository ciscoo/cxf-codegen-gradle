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
package io.mateo.cxf.codegen;

import org.gradle.api.file.DirectoryProperty;

/**
 * Base interface for WSDL options.
 *
 * @deprecated since 1.0.0 for removal in 1.1.0 in favor of
 * {@link io.mateo.cxf.codegen.wsdl2java.Wsdl2Java} tasks
 */
@Deprecated
public interface GenericWsdlOption {

	/**
	 * Specifies the directory the generated code files are written.
	 * <p>
	 * If not set, the convention is {@code "${project.buildDir}/generated-sources"}
	 * @return output directory
	 */
	DirectoryProperty getOutputDir();

}
