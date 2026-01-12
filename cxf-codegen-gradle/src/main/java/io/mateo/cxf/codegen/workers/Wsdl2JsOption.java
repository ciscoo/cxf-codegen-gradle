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
package io.mateo.cxf.codegen.workers;

import io.mateo.cxf.codegen.dsl.Option;
import org.gradle.api.Incubating;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

/**
 * Options for the {@code wsdl2java} code generation tool.
 */
@Incubating
public interface Wsdl2JsOption extends Option {

    /**
     * Specifies the WSDL version the tool expects.
     * <p>
     * If not set, {@code wsdl2js} defaults to {@code WSDL1.1}.
     * @return wsdl version
     */
    @Input
    @Optional
    Property<String> getWsdlVersion();

    /**
     * Specifies a mapping between the namespaces used in the WSDL document and the
     * prefixes used in the generated JavaScript.
     * <p>
     * The format is <code>URI=prefix</code>. In other words:
     * <code>[wsdl namespace]=prefix</code>
     * @return package prefixes
     */
    @Input
    @Optional
    ListProperty<UriPrefixPair> getPackagePrefixes();

    /**
     * Specifies the XML catalog to use for resolving imported schemas and WSDL documents.
     * @return catalog
     */
    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    @Optional
    RegularFileProperty getCatalog();

    /**
     * Enables validating the WSDL before generating the code.
     * @return validate WSDL
     */
    @Input
    @Optional
    Property<String> getValidate();

    /**
     * Enables or disables verbosity. When enabled, displays comments during the code
     * generation process.
     * @return verbosity indicator
     */
    @Input
    @Optional
    Property<Boolean> getVerbose();

    /**
     * Enables or disables quiet mode. When enabled, suppresses comments during the code
     * generation process.
     * @return quite mode enablement
     */
    @Input
    @Optional
    Property<Boolean> getQuiet();
}
