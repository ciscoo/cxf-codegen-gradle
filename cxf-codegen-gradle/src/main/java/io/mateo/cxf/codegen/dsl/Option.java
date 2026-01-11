/*
 * Copyright 2020-2025 the original author or authors.
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

import org.gradle.api.Incubating;
import org.gradle.api.Named;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;

/**
 * Base option for WSDL to code generation tools.
 */
@Incubating
public interface Option extends Named {

    /**
     * {@inheritDoc}
     */
    @Internal
    @Override
    String getName();

    /**
     * The WSDL to process. The value can either be a direct path to a file on a local system
     * or URL to a remote file.
     * @return wsdl file
     */
    @Input
    Property<String> getWsdl();

    /**
     * Specifies the directory the generated code files are written.
     * <p>
     * If not set, the convention is
     * {@code "build/$name-wsdl2{tool}-generated-sources"}
     * @return output directory
     */
    @OutputDirectory
    DirectoryProperty getOutputDirectory();
}
