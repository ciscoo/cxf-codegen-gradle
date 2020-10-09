/*
 * Copyright 2020 the original author or authors.
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
package io.mateo.cxf.codegen.wsdl2java

import io.mateo.cxf.codegen.GenericWsdlOption
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

interface Option : GenericWsdlOption {

    /**
     * WSDL file to process.
     */
    val wsdl: RegularFileProperty

    /**
     * Specifies package names to use for the generated code.
     */
    val packageNames: ListProperty<String>

    /**
     * Specifies extra arguments to pass to the command-line code generator.
     */
    val extraArgs: ListProperty<String>

    /**
     * Specifies [arguments](https://javaee.github.io/jaxb-v2/doc/user-guide/ch04.html) that are
     * passed directly to the XJC processor when using the JAXB data binding.
     */
    val xjcArgs: ListProperty<String>

    /**
     * Specifies subsequently generated Java class methods to allow for client-side asynchronous
     * calls, similar to `enableAsyncMapping` in a JAX-WS binding file.
     */
    val asyncMethods: ListProperty<String>

    /**
     * Specifies subsequently generated Java class methods to have wrapper style, similar to
     * `enableWrapperStyle` in JAX-WS binding file.
     */
    val bareMethods: ListProperty<String>

    /**
     * Specifies subsequently generated Java class methods to enable mime:content mapping, similar to
     * `enableMIMEContent` in JAX-WS binding file.
     */
    val mimeMethods: ListProperty<String>

    /**
     * Ignore the specified WSDL schema namespace when generating code.
     *
     * Optionally specifies the Java package name used by types described in the excluded namespace(s)
     * using schema-namespace[=java-packagename]
     */
    val namespaceExcludes: ListProperty<String>

    /**
     * Enables or disables the loading of the default excludes namespace mapping.
     *
     * If not set, `wsdl2java` defaults to `true`.
     */
    val defaultExcludesNamespace: Property<Boolean>

    /**
     * Enables or disables the loading of the default namespace package name mapping.
     *
     * If not set, `wsdl2java` defaults to `true` and
     * [http://www.w3.org/2005/08/addressing=org.apache.cxf.ws.addressingnamespace](http://www.w3.org/2005/08/addressing)
     * package mapping will be enabled.
     */
    val defaultNamespacePackageMapping: Property<Boolean>

    /**
     * Specifies JAX-WS or JAXB binding files or XMLBeans context files.
     *
     * The values are evaluated as per [org.gradle.api.file.DirectoryProperty.file] from [org.gradle.api.file.ProjectLayout.getBuildDirectory].
     */
    val bindingFiles: SetProperty<String>

    /**
     * Specifies the value of the `@WebServiceClient` annotation's `wsdlLocation` property.
     */
    val wsdlLocation: Property<String>

    /**
     * Specifies that the `wsdlurl` contains a plain text, new line delimited, list of `wsdlurl`s
     * instead of the WSDL itself.
     */
    val wsdlList: Property<Boolean>

    /**
     * Specifies the frontend. Currently supports *JAX-WS* *CXF* frontends.
     *
     * If not set, `wsdl2java` defaults to `jaxws`
     */
    val frontend: Property<String>

    /**
     * Specifies the databinding.
     *
     * If not set, `wsdl2java` defaults to `jaxb`; currently supports JAXB, XMLBeans, SDO (sdo-static
     * and sdo-dynamic), and JiBX.
     */
    val databinding: Property<String>

    /**
     * Specifies the WSDL version.
     *
     * If not set, `wsdl2java` defaults to `WSDL1.1`; currently supports only WSDL1.1 version.
     */
    val wsdlVersion: Property<String>

    /**
     * Specify catalog file to map the imported WSDL/schema
     */
    val catalog: Property<String>

    /**
     * Enables or disables processing of implicit SOAP headers (i.e. SOAP headers defined in the
     * `wsdl:binding`, but not `wsdl:portType` section.) Processing the SOAP headers requires the SOAP
     * binding JARs available on the classpath which was not the default in CXF 2.4.x and older. You
     * may need to add a dependency to `cxf-rt-binding-soap` for this flag to work.
     *
     * If not set, `wsdl2java` defaults to `false`.
     */
    val extendedSoapHeaders: Property<Boolean>

    /**
     * Enables validating the WSDL before generating the code.
     */
    val validateWsdl: Property<String>

    /**
     * Enables or disables generation of the type classes.
     *
     * If not set, `wsdl2java` defaults to `false`.
     */
    val noTypes: Property<Boolean>

    /**
     * Specifies how to generate serial version UID of fault exceptions.
     *
     * Use one of the following:
     *
     * * `NONE`
     * * `TIMESTAMP`
     * * `FQCN`
     * * a specific number
     *
     * If not set, `wsdl2java` defaults to `NONE`.
     */
    val faultSerialVersionUid: Property<String>

    /**
     * Specifies the superclass for fault beans generated from `wsdl:fault` elements
     *
     * If not set, `wsdl2java` defaults to "[ java.lang.Exception] [java.lang.Exception]".
     */
    val exceptionSuper: Property<String>

    /**
     * Specifies the superinterfaces to use for generated SEIs.
     */
    val seiSuper: ListProperty<String>

    /**
     * Enables or disables adding the [`@Generated`][javax.annotation.processing.Generated]
     * annotation to classes generated.
     */
    val markGenerated: Property<Boolean>

    /**
     * Enables or disables writing the current timestamp in the generated file (since CXF version
     * 3.2.2).
     */
    val suppressGeneratedData: Property<Boolean>

    /**
     * Specifies the WSDL service name to use for the generated code.
     */
    val serviceName: Property<String>

    /**
     * Enables or disables automatically resolving naming conflicts without requiring the use of
     * binding customizations.
     */
    val autoNameResolution: Property<Boolean>

    /**
     * For compatibility with CXF 2.0, this flag directs the code generator to generate the older CXF
     * proprietary WS-Addressing types instead of the JAX-WS 2.1 compliant WS-Addressing types.
     */
    val noAddressBinding: Property<Boolean>

    /**
     * Enables or disables disregarding the rule given in section 2.3.1.2(v) of the JAX-WS 2.2
     * specification disallowing element references when using wrapper-style mapping.
     */
    val allowElementRefs: Property<Boolean>

    /**
     * Enables or disables verbosity.
     */
    val verbose: Property<Boolean>
}
