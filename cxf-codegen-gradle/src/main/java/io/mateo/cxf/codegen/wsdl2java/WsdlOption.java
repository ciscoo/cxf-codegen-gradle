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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.gradle.api.GradleException;
import org.gradle.api.Named;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

/**
 * Default implementation for options for the {@code wsdl2java} command.
 *
 * @deprecated since 1.0.0 for removal in 1.1.0 in favor of
 * {@link io.mateo.cxf.codegen.wsdl2java.Wsdl2Java} tasks
 */
@Deprecated
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

	private final Property<Boolean> suppressGeneratedDate;

	private final Property<String> serviceName;

	private final Property<Boolean> autoNameResolution;

	private final Property<Boolean> noAddressBinding;

	private final Property<Boolean> allowElementRefs;

	private final Property<String> encoding;

	private final Property<Boolean> verbose;

	private final DirectoryProperty outputDir;

	private final ProjectLayout layout;

	@Inject
	public WsdlOption(String name, ObjectFactory objects, ProjectLayout layout) {
		this.name = name;
		this.layout = layout;
		this.outputDir = objects.directoryProperty()
				.convention(layout.getBuildDirectory().dir("generated-sources/cxf/" + name));
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
	public Property<Boolean> getSuppressGeneratedDate() {
		return this.suppressGeneratedDate;
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
	public Property<String> getEncoding() {
		return this.encoding;
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

	/**
	 * Generate arguments for {@code wsdl2java}.
	 * @return arguments
	 */
	public List<String> generateArgs() {
		List<String> command = new ArrayList<>();
		if (this.encoding.isPresent()) {
			command.add("-encoding");
			command.add(this.encoding.get());
		}
		if (this.packageNames.isPresent()) {
			this.packageNames.get().forEach((value) -> {
				command.add("-p");
				command.add(value);
			});
		}
		if (this.namespaceExcludes.isPresent()) {
			this.namespaceExcludes.get().forEach((value) -> {
				command.add("-nexclude");
				command.add(value);
			});
		}
		command.add("-d");
		command.add(this.outputDir.get().getAsFile().getAbsolutePath()); // always set by
																			// convention
		if (this.bindingFiles.isPresent()) {
			this.bindingFiles.get().forEach((value) -> {
				RegularFile bindingFile = this.layout.getProjectDirectory().file(value);
				command.add("-b");
				command.add(bindingFile.getAsFile().toURI().toString());
			});
		}
		if (this.frontend.isPresent()) {
			command.add("-fe");
			command.add(this.frontend.get());
		}
		if (this.databinding.isPresent()) {
			command.add("-db");
			command.add(this.databinding.get());
		}
		if (this.wsdlVersion.isPresent()) {
			command.add("-wv");
			command.add(this.wsdlVersion.get());
		}
		if (this.catalog.isPresent()) {
			command.add("-catalog");
			command.add(this.catalog.get());
		}
		if (this.extendedSoapHeaders.isPresent() && this.extendedSoapHeaders.get()) {
			command.add("-exsh");
			command.add("true");
		}
		if (this.noTypes.isPresent() && this.noTypes.get()) {
			command.add("-noTypes");
		}
		if (this.allowElementRefs.isPresent() && this.allowElementRefs.get()) {
			command.add("-allowElementReferences");
		}
		if (this.validateWsdl.isPresent()) {
			command.add("-validate=" + this.validateWsdl.get());
		}
		if (this.markGenerated.isPresent() && this.markGenerated.get()) {
			command.add("-mark-generated");
		}
		if (this.suppressGeneratedDate.isPresent() && this.suppressGeneratedDate.get()) {
			command.add("-suppress-generated-date");
		}
		if (this.defaultExcludesNamespace.isPresent()) {
			command.add("-dex");
			command.add(this.defaultExcludesNamespace.get().toString());
		}
		if (this.defaultNamespacePackageMapping.isPresent()) {
			command.add("-dns");
			command.add(this.defaultNamespacePackageMapping.get().toString());
		}
		if (this.serviceName.isPresent()) {
			command.add("-sn");
			command.add(this.serviceName.get());
		}
		if (this.faultSerialVersionUid.isPresent()) {
			command.add("-faultSerialVersionUID");
			command.add(this.faultSerialVersionUid.get());
		}
		if (this.exceptionSuper.isPresent()) {
			command.add("-exceptionSuper");
			command.add(this.exceptionSuper.get());
		}
		if (this.seiSuper.isPresent()) {
			this.seiSuper.get().forEach((value) -> {
				command.add("-seiSuper");
				command.add(value);
			});
		}
		if (this.autoNameResolution.isPresent() && this.autoNameResolution.get()) {
			command.add("-autoNameResolution");
		}
		if (this.noAddressBinding.isPresent() && this.noAddressBinding.get()) {
			command.add("-noAddressBinding");
		}
		if (this.xjcArgs.isPresent()) {
			this.xjcArgs.get().forEach((value) -> {
				command.add("-xjc" + value);
			});
		}
		if (this.extraArgs.isPresent()) {
			command.addAll(this.extraArgs.get());
		}
		if (this.wsdlLocation.isPresent()) {
			command.add("-wsdlLocation");
			command.add(this.wsdlLocation.get());
		}
		if (this.wsdlList.isPresent() && this.wsdlList.get()) {
			command.add("-wsdlList");
		}
		if (this.verbose.isPresent() && this.verbose.get()) {
			command.add("-verbose");
		}
		if (this.asyncMethods.isPresent() && !this.asyncMethods.get().isEmpty()) {
			StringBuilder sb = new StringBuilder("-asyncMethods");
			sb.append("=");
			boolean first = true;
			for (String value : this.asyncMethods.get()) {
				if (!first) {
					sb.append(",");
				}
				sb.append(value);
				first = false;
			}
			command.add(sb.toString());
		}
		if (this.bareMethods.isPresent() && !this.bareMethods.get().isEmpty()) {
			StringBuilder sb = new StringBuilder("-bareMethods");
			sb.append("=");
			boolean first = true;
			for (String value : this.bareMethods.get()) {
				if (!first) {
					sb.append(",");
				}
				sb.append(value);
				first = false;
			}
			command.add(sb.toString());
		}
		if (this.mimeMethods.isPresent() && !this.mimeMethods.get().isEmpty()) {
			StringBuilder sb = new StringBuilder("-mimeMethods");
			sb.append("=");
			boolean first = true;
			for (String value : this.mimeMethods.get()) {
				if (!first) {
					sb.append(",");
				}
				sb.append(value);
				first = false;
			}

			command.add(sb.toString());
		}
		if (!this.wsdl.isPresent()) {
			throw new GradleException("'wsdl' property is not present");
		}
		command.add(this.wsdl.get().getAsFile().toURI().toString());
		return command;
	}

}
