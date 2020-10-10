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
package io.mateo.cxf.codegen.wsdl2java;

import org.gradle.api.Named;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

import javax.inject.Inject;
import java.util.List;

/**
 * Default implementation for options for the {@code wsdl2java} command.
 */
public class WsdlOption implements Option, Named {

	private final String name;

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

	private final Property<Boolean> suppressGeneratedData;

	private final Property<String> serviceName;

	private final Property<Boolean> autoNameResolution;

	private final Property<Boolean> noAddressBinding;

	private final Property<Boolean> allowElementRefs;

	private final Property<Boolean> verbose;

	private final DirectoryProperty outputDir;

	@Inject
	public WsdlOption(String name, ObjectFactory objects, ProjectLayout layout) {
		this.name = name;
		this.outputDir = objects.directoryProperty().convention(layout.getBuildDirectory().dir("/generated-sources"));
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
		this.suppressGeneratedData = objects.property(Boolean.class);
		this.serviceName = objects.property(String.class);
		this.autoNameResolution = objects.property(Boolean.class);
		this.noAddressBinding = objects.property(Boolean.class);
		this.allowElementRefs = objects.property(Boolean.class);
		this.verbose = objects.property(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RegularFileProperty getWsdl() {
		return this.wsdl;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListProperty<String> getPackageNames() {
		return this.packageNames;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListProperty<String> getExtraArgs() {
		return this.extraArgs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListProperty<String> getXjcArgs() {
		return this.xjcArgs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListProperty<String> getAsyncMethods() {
		return this.asyncMethods;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListProperty<String> getBareMethods() {
		return this.bareMethods;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListProperty<String> getMimeMethods() {
		return this.mimeMethods;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListProperty<String> getNamespaceExcludes() {
		return this.namespaceExcludes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<Boolean> getDefaultExcludesNamespace() {
		return this.defaultExcludesNamespace;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<Boolean> getDefaultNamespacePackageMapping() {
		return this.defaultNamespacePackageMapping;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SetProperty<String> getBindingFiles() {
		return this.bindingFiles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<String> getWsdlLocation() {
		return this.wsdlLocation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<Boolean> getWsdlList() {
		return this.wsdlList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<String> getFrontend() {
		return this.frontend;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<String> getDatabinding() {
		return this.databinding;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<String> getWsdlVersion() {
		return this.wsdlVersion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<String> getCatalog() {
		return this.catalog;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<Boolean> getExtendedSoapHeaders() {
		return this.extendedSoapHeaders;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<String> getValidateWsdl() {
		return this.validateWsdl;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<Boolean> getNoTypes() {
		return this.noTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<String> getFaultSerialVersionUid() {
		return this.faultSerialVersionUid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<String> getExceptionSuper() {
		return this.exceptionSuper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListProperty<String> getSeiSuper() {
		return this.seiSuper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<Boolean> getMarkGenerated() {
		return this.markGenerated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<Boolean> getSuppressGeneratedData() {
		return this.suppressGeneratedData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<String> getServiceName() {
		return this.serviceName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<Boolean> getAutoNameResolution() {
		return this.autoNameResolution;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<Boolean> getNoAddressBinding() {
		return this.noAddressBinding;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<Boolean> getAllowElementRefs() {
		return this.allowElementRefs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Property<Boolean> getVerbose() {
		return this.verbose;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DirectoryProperty getOutputDir() {
		return this.outputDir;
	}

	public List<String> generateArgs() {

		return null;
	}
}
