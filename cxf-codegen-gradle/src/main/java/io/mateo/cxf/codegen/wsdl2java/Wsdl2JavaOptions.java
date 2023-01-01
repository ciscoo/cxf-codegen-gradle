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
package io.mateo.cxf.codegen.wsdl2java;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;

/**
 * Options for the {@code wsdl2java} tool.
 *
 * @see <a href="https://cxf.apache.org/docs/wsdl-to-java.html#WSDLtoJava-Options">WSDL to
 * Java options</a>
 */
public class Wsdl2JavaOptions {

	private final RegularFileProperty wsdl;

	private final ListProperty<String> packageNames;

	private final ListProperty<String> extraArgs;

	private final ListProperty<String> xjcArgs;

	private final ListProperty<String> asyncMethods;

	private final ListProperty<String> bareMethods;

	private final ListProperty<String> mimeMethods;

	private final ListProperty<String> namespaceExcludes;

	private final Property<Boolean> defaultExcludesNamespace;

	private final Property<Boolean> defaultNamespacePackageMapping;

	private final SetProperty<String> bindingFiles;

	private final Property<String> wsdlLocation;

	private final Property<Boolean> wsdlList;

	private final Property<String> frontend;

	private final Property<String> databinding;

	private final Property<String> wsdlVersion;

	private final Property<String> catalog;

	private final Property<Boolean> extendedSoapHeaders;

	private final Property<String> validateWsdl;

	private final Property<Boolean> noTypes;

	private final Property<String> faultSerialVersionUid;

	private final Property<String> exceptionSuper;

	private final ListProperty<String> seiSuper;

	private final Property<Boolean> markGenerated;

	private final Property<Boolean> suppressGeneratedDate;

	private final Property<String> serviceName;

	private final Property<Boolean> autoNameResolution;

	private final Property<Boolean> noAddressBinding;

	private final Property<Boolean> allowElementRefs;

	private final Property<String> encoding;

	private final Property<Boolean> verbose;

	private final DirectoryProperty outputDir;

	public Wsdl2JavaOptions(String taskName, ObjectFactory objects, ProjectLayout layout) {
		this.outputDir = objects.directoryProperty()
				.convention(layout.getBuildDirectory().dir(taskName + "-wsdl2java-generated-sources"));
		this.wsdl = objects.fileProperty();
		this.packageNames = objects.listProperty(String.class);
		this.extraArgs = objects.listProperty(String.class);
		this.xjcArgs = objects.listProperty(String.class);
		this.asyncMethods = objects.listProperty(String.class);
		this.bareMethods = objects.listProperty(String.class);
		this.mimeMethods = objects.listProperty(String.class);
		this.namespaceExcludes = objects.listProperty(String.class);
		this.defaultExcludesNamespace = objects.property(Boolean.class);
		this.defaultNamespacePackageMapping = objects.property(Boolean.class);
		this.bindingFiles = objects.setProperty(String.class);
		this.wsdlLocation = objects.property(String.class);
		this.wsdlList = objects.property(Boolean.class);
		this.frontend = objects.property(String.class);
		this.databinding = objects.property(String.class);
		this.wsdlVersion = objects.property(String.class);
		this.catalog = objects.property(String.class);
		this.extendedSoapHeaders = objects.property(Boolean.class);
		this.validateWsdl = objects.property(String.class);
		this.noTypes = objects.property(Boolean.class);
		this.faultSerialVersionUid = objects.property(String.class);
		this.exceptionSuper = objects.property(String.class);
		this.seiSuper = objects.listProperty(String.class);
		this.markGenerated = objects.property(Boolean.class);
		this.suppressGeneratedDate = objects.property(Boolean.class);
		this.serviceName = objects.property(String.class);
		this.autoNameResolution = objects.property(Boolean.class);
		this.noAddressBinding = objects.property(Boolean.class);
		this.allowElementRefs = objects.property(Boolean.class);
		this.encoding = objects.property(String.class);
		this.verbose = objects.property(Boolean.class);
	}

	/**
	 * WSDL file to process.
	 * @return wsdl file
	 */
	@InputFile
	@PathSensitive(PathSensitivity.RELATIVE)
	@SkipWhenEmpty
	public RegularFileProperty getWsdl() {
		return this.wsdl;
	}

	/**
	 * Specifies the directory the generated code files are written.
	 * <p>
	 * If not set, the convention is
	 * {@code "$buildDir/$taskName-wsdl2java-generated-sources"}
	 * @return output directory
	 */
	@OutputDirectory
	@Optional
	public DirectoryProperty getOutputDir() {
		return this.outputDir;
	}

	/**
	 * Specifies zero, or more, package names to use for the generated code.
	 * <p>
	 * Optionally specifies the WSDL namespace to package name mapping.
	 * @return package names
	 */
	@Input
	@Optional
	public ListProperty<String> getPackageNames() {
		return this.packageNames;
	}

	/**
	 * Specifies extra arguments to pass to the command-line code generator.
	 * @return extra arguments
	 */
	@Input
	@Optional
	public ListProperty<String> getExtraArgs() {
		return this.extraArgs;
	}

	/**
	 * Specifies
	 * <a href= "https://javaee.github.io/jaxb-v2/doc/user-guide/ch04.html">arguments</a>
	 * that are passed directly to the XJC processor when using the JAXB data binding.
	 * @return xjc arguments
	 */
	@Input
	@Optional
	public ListProperty<String> getXjcArgs() {
		return this.xjcArgs;
	}

	/**
	 * Specifies subsequently generated Java class methods to allow for client-side
	 * asynchronous calls, similar to {@code enableAsyncMapping} in a JAX-WS binding file.
	 * @return async methods
	 */
	@Input
	@Optional
	public ListProperty<String> getAsyncMethods() {
		return this.asyncMethods;
	}

	/**
	 * Specifies subsequently generated Java class methods to have wrapper style, similar
	 * to {@code enableWrapperStyle} in JAX-WS binding file.
	 * @return bare methods
	 */
	@Input
	@Optional
	public ListProperty<String> getBareMethods() {
		return this.bareMethods;
	}

	/**
	 * Specifies subsequently generated Java class methods to enable mime:content mapping,
	 * similar to {@code enableMIMEContent} in JAX-WS binding file.
	 * @return mime methods
	 */
	@Input
	@Optional
	public ListProperty<String> getMimeMethods() {
		return this.mimeMethods;
	}

	/**
	 * Ignore the specified WSDL schema namespace when generating code.
	 * <p>
	 * Optionally specifies the Java package name used by types described in the excluded
	 * namespace(s) using schema-namespace[=java-packagename]
	 * @return namespace excludes
	 */
	@Input
	@Optional
	public ListProperty<String> getNamespaceExcludes() {
		return this.namespaceExcludes;
	}

	/**
	 * Enables or disables the loading of the default excludes namespace mapping.
	 * <p>
	 * If not set, {@code wsdl2java} defaults to {@code true}.
	 * @return default excludes namespace indicator
	 */
	@Input
	@Optional
	public Property<Boolean> getDefaultExcludesNamespace() {
		return this.defaultExcludesNamespace;
	}

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
	public Property<Boolean> getDefaultNamespacePackageMapping() {
		return this.defaultNamespacePackageMapping;
	}

	/**
	 * Specifies JAX-WS or JAXB binding files or XMLBeans context files.
	 * <p>
	 * The values are evaluated as per {@link org.gradle.api.file.Directory#file(String)}
	 * from {@link org.gradle.api.file.ProjectLayout#getProjectDirectory()}
	 * @return binding files
	 */
	@Input
	@Optional
	public SetProperty<String> getBindingFiles() {
		return this.bindingFiles;
	}

	/**
	 * Specifies the value of the {@code @WebServiceClient} annotation's
	 * {@code wsdlLocation} property.
	 * @return wsdl location
	 */
	@Input
	@Optional
	public Property<String> getWsdlLocation() {
		return this.wsdlLocation;
	}

	/**
	 * Specifies that the {@code wsdlurl} contains a plain text, new line delimited, list
	 * of {@code wsdlurl}s instead of the WSDL itself.
	 * @return wsdl list flag
	 */
	@Input
	@Optional
	public Property<Boolean> getWsdlList() {
		return this.wsdlList;
	}

	/**
	 * Specifies the frontend. Currently only supports JAX-WS CXF frontends and a
	 * {@code jaxws21} frontend to generate JAX-WS 2.1 compliant code.
	 * <p>
	 * If not set, {@code wsdl2java} defaults to {@code jaxws}
	 * @return frontend
	 */
	@Input
	@Optional
	public Property<String> getFrontend() {
		return this.frontend;
	}

	/**
	 * Specifies the databinding. Currently supports JAXB, XMLBeans, SDO (sdo-static * and
	 * sdo-dynamic), and JiBX.
	 * <p>
	 * If not set, {@code wsdl2java} defaults to {@code jaxb}
	 * @return databinding
	 */
	@Input
	@Optional
	public Property<String> getDatabinding() {
		return this.databinding;
	}

	/**
	 * Specifies the WSDL version.
	 * <p>
	 * If not set, {@code wsdl2java} defaults to {@code WSDL1.1}; currently supports only
	 * WSDL1.1 version.
	 * @return wsdl version
	 */
	@Input
	@Optional
	public Property<String> getWsdlVersion() {
		return this.wsdlVersion;
	}

	/**
	 * Specify catalog file to map the imported WSDL/schema.
	 * @return catalog
	 */
	@Input
	@Optional
	public Property<String> getCatalog() {
		return this.catalog;
	}

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
	public Property<Boolean> getExtendedSoapHeaders() {
		return this.extendedSoapHeaders;
	}

	/**
	 * Enables validating the WSDL before generating the code.
	 * @return validate WSDL
	 */
	@Input
	@Optional
	public Property<String> getValidateWsdl() {
		return this.validateWsdl;
	}

	/**
	 * Enables or disables generation of the type classes.
	 * <p>
	 * If not set, {@code wsdl2java} defaults to {@code false}.
	 * @return no types indicator
	 */
	@Input
	@Optional
	public Property<Boolean> getNoTypes() {
		return this.noTypes;
	}

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
	public Property<String> getFaultSerialVersionUid() {
		return this.faultSerialVersionUid;
	}

	/**
	 * Specifies the superclass for fault beans generated from {@code wsdl:fault} elements
	 * <p>
	 * If not set, {@code wsdl2java} defaults to {@link Exception}.
	 * @return exception superclass
	 */
	@Input
	@Optional
	public Property<String> getExceptionSuper() {
		return this.exceptionSuper;
	}

	/**
	 * Specifies the superinterfaces to use for generated SEIs.
	 * @return super interfaces
	 */
	@Input
	@Optional
	public ListProperty<String> getSeiSuper() {
		return this.seiSuper;
	}

	/**
	 * Enables or disables adding the {@code @Generated} annotation to classes generated.
	 * @return mark generated indicator
	 */
	@Input
	@Optional
	public Property<Boolean> getMarkGenerated() {
		return this.markGenerated;
	}

	/**
	 * Enables or disables writing the current timestamp in the generated file (since CXF
	 * version 3.2.2).
	 * @return suppress generated date indicator
	 */
	@Input
	@Optional
	public Property<Boolean> getSuppressGeneratedDate() {
		return this.suppressGeneratedDate;
	}

	/**
	 * Specifies the WSDL service name to use for the generated code.
	 * @return service name
	 */
	@Input
	@Optional
	public Property<String> getServiceName() {
		return this.serviceName;
	}

	/**
	 * Enables or disables automatically resolving naming conflicts without requiring the
	 * use of binding customizations.
	 * @return auto name resolution indicator
	 */
	@Input
	@Optional
	public Property<Boolean> getAutoNameResolution() {
		return this.autoNameResolution;
	}

	/**
	 * For compatibility with CXF 2.0, this flag directs the code generator to generate
	 * the older CXF proprietary WS-Addressing types instead of the JAX-WS 2.1 compliant
	 * WS-Addressing types.
	 * @return no address binding indicator
	 */
	@Input
	@Optional
	public Property<Boolean> getNoAddressBinding() {
		return this.noAddressBinding;
	}

	/**
	 * Enables or disables disregarding the rule given in section 2.3.1.2(v) of the JAX-WS
	 * 2.2 specification disallowing element references when using wrapper-style mapping.
	 * @return allow element references indicator
	 */
	@Input
	@Optional
	public Property<Boolean> getAllowElementRefs() {
		return this.allowElementRefs;
	}

	/**
	 * Encoding to use for generated sources (since CXF version 2.5.4).
	 * @return encoding
	 */
	@Input
	@Optional
	public Property<String> getEncoding() {
		return this.encoding;
	}

	/**
	 * Enables or disables verbosity.
	 * @return verbosity indicator
	 */
	@Input
	@Optional
	public Property<Boolean> getVerbose() {
		return this.verbose;
	}

}
