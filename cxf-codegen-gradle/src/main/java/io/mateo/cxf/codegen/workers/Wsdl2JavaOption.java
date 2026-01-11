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
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

/**
 * Options for the {@code wsdl2java} code generation tool.
 */
@Incubating
public interface Wsdl2JavaOption extends Option {

    /**
     * Specifies zero, or more, package names to use for the generated code.
     * <p>
     * Optionally specifies the WSDL namespace to package name mapping.
     * @return package names
     */
    @Input
    @Optional
    ListProperty<String> getPackageNames();

    /**
     * Specifies extra arguments to pass to the command-line code generator.
     * @return extra arguments
     */
    @Input
    @Optional
    ListProperty<String> getExtraArgs();

    /**
     * Specifies
     * <a href= "https://javaee.github.io/jaxb-v2/doc/user-guide/ch04.html">arguments</a>
     * that are passed directly to the XJC processor when using the JAXB data binding.
     * @return xjc arguments
     */
    @Input
    @Optional
    ListProperty<String> getXjcArgs();

    /**
     * Specifies subsequently generated Java class methods to allow for client-side
     * asynchronous calls, similar to {@code enableAsyncMapping} in a JAX-WS binding file.
     * @return async methods
     */
    @Input
    @Optional
    ListProperty<String> getAsyncMethods();

    /**
     * Specifies subsequently generated Java class methods to have wrapper style, similar
     * to {@code enableWrapperStyle} in JAX-WS binding file.
     * @return bare methods
     */
    @Input
    @Optional
    ListProperty<String> getBareMethods();

    /**
     * Specifies subsequently generated Java class methods to enable mime:content mapping,
     * similar to {@code enableMIMEContent} in JAX-WS binding file.
     * @return mime methods
     */
    @Input
    @Optional
    ListProperty<String> getMimeMethods();

    /**
     * Ignore the specified WSDL schema namespace when generating code.
     * <p>
     * Optionally specifies the Java package name used by types described in the excluded
     * namespace(s) using schema-namespace[=java-packagename]
     * @return namespace excludes
     */
    @Input
    @Optional
    ListProperty<String> getNamespaceExcludes();

    /**
     * Enables or disables the loading of the default excludes namespace mapping.
     * <p>
     * If not set, {@code wsdl2java} defaults to {@code true}.
     * @return default excludes namespace indicator
     */
    @Input
    @Optional
    Property<Boolean> getDefaultExcludesNamespace();

    /**
     * Enables or disables the loading of the default namespace package name mapping.
     * <p>
     * If not set, {@code wsdl2java} defaults to {@code true} and <a href=
     * "http://www.w3.org/2005/08/addressing=org.apache.cxf.ws.addressingnamespace">http://www.w3.org/2005/08/addressing</a>
     * package mapping will be enabled.
     * @return default namespace package mapping indicator
     */
    @Input
    @Optional
    Property<Boolean> getDefaultNamespacePackageMapping();

    /**
     * Specifies JAX-WS or JAXB binding files or XMLBeans context files.
     * <p>
     * The values are evaluated as per {@link org.gradle.api.file.Directory#file(String)}
     * from {@link org.gradle.api.file.ProjectLayout#getProjectDirectory()}
     * @return binding files
     */
    @Input
    @Optional
    SetProperty<String> getBindingFiles();

    /**
     * Specifies the value of the {@code @WebServiceClient} annotation's
     * {@code wsdlLocation} property.
     * <p>
     * By default, the {@code wsdl2java} tool will use the value of {@link #getWsdl()}.
     * This may lead to runtime issues such as {@code FileNotFoundException} being thrown.
     * Consider setting this value to a location that your application understands. For
     * example (Kotlin DSL), a classpath location: <pre>
     * tasks.register("example", Wsdl2Java::class) {
     * 	toolOptions {
     * 		wsdlUrl.set(layout.projectDirectory.file("src/main/resources/wsdl/example.wsdl").asFile.absolutePath)
     * 		wsdlLocation.set("classpath:wsdl/example.wsdl")
     *        }
     * }
     * </pre>
     * @return wsdl location
     */
    @Input
    @Optional
    Property<String> getWsdlLocation();

    /**
     * Specifies that the {@code wsdlurl} contains a plain text, new line delimited, list
     * of {@code wsdlurl}s instead of the WSDL itself.
     * @return wsdl list flag
     */
    @Input
    @Optional
    Property<Boolean> getWsdlList();

    /**
     * Specifies the frontend. Currently only supports JAX-WS CXF frontends and a
     * {@code jaxws21} frontend to generate JAX-WS 2.1 compliant code.
     * <p>
     * If not set, {@code wsdl2java} defaults to {@code jaxws}
     * @return frontend
     */
    @Input
    @Optional
    Property<String> getFrontend();

    /**
     * Specifies the databinding. Currently supports JAXB, XMLBeans, SDO (sdo-static * and
     * sdo-dynamic), and JiBX.
     * <p>
     * If not set, {@code wsdl2java} defaults to {@code jaxb}
     * @return databinding
     */
    @Input
    @Optional
    Property<String> getDatabinding();

    /**
     * Specifies the WSDL version.
     * <p>
     * If not set, {@code wsdl2java} defaults to {@code WSDL1.1}; currently supports only
     * WSDL1.1 version.
     * @return wsdl version
     */
    @Input
    @Optional
    Property<String> getWsdlVersion();

    /**
     * Specify catalog file to map the imported WSDL/schema.
     * @return catalog
     */
    @Input
    @Optional
    Property<String> getCatalog();

    /**
     * Enables or disables processing of implicit SOAP headers (i.e. SOAP headers defined
     * in the {@code wsdl:binding}, but not {@code wsdl:portType} section.)
     * <p>
     * Processing the SOAP headers requires the SOAP binding JARs available on the
     * classpath which was not the default in CXF 2.4.x and older. You may need to add a
     * dependency to {@code cxf-rt-binding-soap} for this flag to work.
     * <p>
     * If not set, {@code wsdl2java} defaults to {@code false}.
     * @return extended SOAP headers indicator
     */
    @Input
    @Optional
    Property<Boolean> getExtendedSoapHeaders();

    /**
     * Enables validating the WSDL before generating the code.
     * @return validate WSDL
     */
    @Input
    @Optional
    Property<String> getValidateWsdl();

    /**
     * Enables or disables generation of the type classes.
     * <p>
     * If not set, {@code wsdl2java} defaults to {@code false}.
     * @return no types indicator
     */
    @Input
    @Optional
    Property<Boolean> getNoTypes();

    /**
     * Specifies how to generate serial version UID of fault exceptions.
     * <p>
     * Use one of the following:
     * <ul>
     * <li>{@code NONE}</li>
     * <li>{@code TIMESTAMP}</li>
     * <li>{@code FQCN}</li>
     * <li>a specific number</li>
     * </ul>
     * <p>
     * If not set, {@code wsdl2java} defaults to {@code NONE}.
     * @return fault serial version UID
     */
    @Input
    @Optional
    Property<String> getFaultSerialVersionUid();

    /**
     * Specifies the superclass for fault beans generated from {@code wsdl:fault} elements
     * <p>
     * If not set, {@code wsdl2java} defaults to {@link Exception}.
     * @return exception superclass
     */
    @Input
    @Optional
    Property<String> getExceptionSuper();

    /**
     * Specifies the superinterfaces to use for generated SEIs.
     * @return super interfaces
     */
    @Input
    @Optional
    ListProperty<String> getSeiSuper();

    /**
     * Enables or disables adding the {@code @Generated} annotation to classes generated.
     * @return mark generated indicator
     */
    @Input
    @Optional
    Property<Boolean> getMarkGenerated();

    /**
     * Enables or disables writing the current timestamp in the generated file (since CXF
     * version 3.2.2).
     * @return suppress generated date indicator
     */
    @Input
    @Optional
    Property<Boolean> getSuppressGeneratedDate();

    /**
     * Specifies the WSDL service name to use for the generated code.
     * @return service name
     */
    @Input
    @Optional
    Property<String> getServiceName();

    /**
     * Enables or disables automatically resolving naming conflicts without requiring the
     * use of binding customizations.
     * @return auto name resolution indicator
     */
    @Input
    @Optional
    Property<Boolean> getAutoNameResolution();

    /**
     * For compatibility with CXF 2.0, this flag directs the code generator to generate
     * the older CXF proprietary WS-Addressing types instead of the JAX-WS 2.1 compliant
     * WS-Addressing types.
     * @return no address binding indicator
     */
    @Input
    @Optional
    Property<Boolean> getNoAddressBinding();

    /**
     * Enables or disables disregarding the rule given in section 2.3.1.2(v) of the JAX-WS
     * 2.2 specification disallowing element references when using wrapper-style mapping.
     * @return allow element references indicator
     */
    @Input
    @Optional
    Property<Boolean> getAllowElementRefs();

    /**
     * Encoding to use for generated sources (since CXF version 2.5.4).
     * @return encoding
     */
    @Input
    @Optional
    Property<String> getEncoding();

    /**
     * Enables or disables verbosity.
     * @return verbosity indicator
     */
    @Input
    @Optional
    Property<Boolean> getVerbose();
}
