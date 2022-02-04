/*
 * Copyright 2021 the original author or authors.
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
import java.util.Collections;
import java.util.List;

import org.gradle.api.Action;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.Nested;
import org.gradle.process.CommandLineArgumentProvider;

/**
 * Generates Java sources from WSDLs.
 */
@CacheableTask
public class Wsdl2Java extends JavaExec {

	private final Wsdl2JavaOptions wsdl2JavaOptions;

	private final Property<Boolean> addToMainSourceSet = getProject().getObjects().property(Boolean.class)
			.convention(true);

	public Wsdl2Java() {
		Wsdl2JavaOptions options;
		try {
			options = new Wsdl2JavaOptions(getName(), getObjectFactory(), getProject().getLayout());
		}
		catch (NoSuchMethodError ignored) {
			// < Gradle 6.1
			options = new Wsdl2JavaOptions(getName(), getProject().getObjects(), getProject().getLayout());
		}
		this.wsdl2JavaOptions = options;
		getArgumentProviders().add(new Wsdl2JavaArgumentProvider());
	}

	/**
	 * Options to configure the {@code wsdl2java} tool.
	 * @return tool options
	 */
	@Nested
	public Wsdl2JavaOptions getWsdl2JavaOptions() {
		return this.wsdl2JavaOptions;
	}

	/**
	 * Whether to add the generated Java sources to the
	 * {@value org.gradle.api.tasks.SourceSet#MAIN_SOURCE_SET_NAME} or not.
	 * @return whether to add to source set
	 */
	@Internal
	public Property<Boolean> getAddToMainSourceSet() {
		return this.addToMainSourceSet;
	}

	/**
	 * Configures the {@code wsdl2java} tool options.
	 * @param configurer action or closure to configure tool options
	 */
	public void toolOptions(Action<? super Wsdl2JavaOptions> configurer) {
		configurer.execute(this.wsdl2JavaOptions);
	}

	private class Wsdl2JavaArgumentProvider implements CommandLineArgumentProvider {

		@Override
		public Iterable<String> asArguments() {
			List<String> arguments = new ArrayList<>();
			Wsdl2JavaOptions options = Wsdl2Java.this.getWsdl2JavaOptions();
			if (options.getEncoding().isPresent()) {
				arguments.add("-encoding");
				arguments.add(options.getEncoding().get());
			}
			if (options.getPackageNames().isPresent()) {
				options.getPackageNames().get().forEach(value -> {
					arguments.add("-p");
					arguments.add(value);
				});
			}
			if (options.getNamespaceExcludes().isPresent()) {
				options.getNamespaceExcludes().get().forEach(value -> {
					arguments.add("-nexclude");
					arguments.add(value);
				});
			}
			arguments.add("-d");
			String outputDir = options.getOutputDir().getAsFile().get().getAbsolutePath();
			arguments.add(outputDir);
			if (options.getBindingFiles().isPresent()) {
				options.getBindingFiles().get().forEach(binding -> {
					RegularFile bindingFile = Wsdl2Java.this.getProject().getLayout().getProjectDirectory()
							.file(binding);
					arguments.add("-b");
					arguments.add(bindingFile.getAsFile().toURI().toString());
				});
			}
			if (options.getFrontend().isPresent()) {
				arguments.add("-fe");
				arguments.add(options.getFrontend().get());
			}
			if (options.getDatabinding().isPresent()) {
				arguments.add("-db");
				arguments.add(options.getDatabinding().get());
			}
			if (options.getWsdlVersion().isPresent()) {
				arguments.add("-wv");
				arguments.add(options.getWsdlVersion().get());
			}
			if (options.getCatalog().isPresent()) {
				arguments.add("-catalog");
				arguments.add(options.getCatalog().get());
			}
			if (options.getExtendedSoapHeaders().isPresent()
					&& Boolean.TRUE.equals(options.getExtendedSoapHeaders().get())) {
				arguments.add("-exsh");
				arguments.add("true");
			}
			if (options.getNoTypes().isPresent() && Boolean.TRUE.equals(options.getNoTypes().get())) {
				arguments.add("-noTypes");
			}
			if (options.getAllowElementRefs().isPresent() && Boolean.TRUE.equals(options.getAllowElementRefs().get())) {
				arguments.add("-allowElementReferences");
			}
			if (options.getValidateWsdl().isPresent()) {
				arguments.add("-validate=" + options.getValidateWsdl().get());
			}
			if (options.getMarkGenerated().isPresent() && Boolean.TRUE.equals(options.getMarkGenerated().get())) {
				arguments.add("-mark-generated");
			}
			if (options.getSuppressGeneratedDate().isPresent()
					&& Boolean.TRUE.equals(options.getSuppressGeneratedDate().get())) {
				arguments.add("-suppress-generated-date");
			}
			if (options.getDefaultExcludesNamespace().isPresent()) {
				arguments.add("-dex");
				arguments.add(options.getDefaultExcludesNamespace().get().toString());
			}
			if (options.getDefaultNamespacePackageMapping().isPresent()) {
				arguments.add("-dns");
				arguments.add(options.getDefaultNamespacePackageMapping().get().toString());
			}
			if (options.getServiceName().isPresent()) {
				arguments.add("-sn");
				arguments.add(options.getServiceName().get());
			}
			if (options.getFaultSerialVersionUid().isPresent()) {
				arguments.add("-faultSerialVersionUID");
				arguments.add(options.getFaultSerialVersionUid().get());
			}
			if (options.getExceptionSuper().isPresent()) {
				arguments.add("-exceptionSuper");
				arguments.add(options.getExceptionSuper().get());
			}
			if (options.getSeiSuper().isPresent()) {
				options.getSeiSuper().get().forEach(value -> {
					arguments.add("-seiSuper");
					arguments.add(value);
				});
			}
			if (options.getAutoNameResolution().isPresent()
					&& Boolean.TRUE.equals(options.getAutoNameResolution().get())) {
				arguments.add("-autoNameResolution");
			}
			if (options.getNoAddressBinding().isPresent() && Boolean.TRUE.equals(options.getNoAddressBinding().get())) {
				arguments.add("-noAddressBinding");
			}
			if (options.getXjcArgs().isPresent()) {
				options.getXjcArgs().get().forEach(value -> arguments.add("-xjc" + value));
			}
			if (options.getExtraArgs().isPresent()) {
				arguments.addAll(options.getExtraArgs().get());
			}
			if (options.getWsdlLocation().isPresent()) {
				arguments.add("-wsdlLocation");
				arguments.add(options.getWsdlLocation().get());
			}
			if (options.getWsdlList().isPresent() && Boolean.TRUE.equals(options.getWsdlList().get())) {
				arguments.add("-wsdlList");
			}
			if (options.getVerbose().isPresent() && Boolean.TRUE.equals(options.getVerbose().get())) {
				arguments.add("-verbose");
			}
			if (options.getAsyncMethods().isPresent()) {
				List<String> asyncMethods = options.getAsyncMethods().get();
				if (!asyncMethods.isEmpty()) {
					StringBuilder sb = new StringBuilder("-asyncMethods");
					sb.append("=");
					boolean first = true;
					for (String value : asyncMethods) {
						if (!first) {
							sb.append(",");
						}
						sb.append(value);
						first = false;
					}
					arguments.add(sb.toString());
				}
			}
			if (options.getBareMethods().isPresent()) {
				List<String> bareMethods = options.getBareMethods().get();
				if (!bareMethods.isEmpty()) {
					StringBuilder sb = new StringBuilder("-bareMethods");
					sb.append("=");
					boolean first = true;
					for (String value : bareMethods) {
						if (!first) {
							sb.append(",");
						}
						sb.append(value);
						first = false;
					}
					arguments.add(sb.toString());
				}

			}
			if (options.getMimeMethods().isPresent()) {
				List<String> mimeMethods = options.getMimeMethods().get();
				if (!mimeMethods.isEmpty()) {
					StringBuilder sb = new StringBuilder("-mimeMethods");
					sb.append("=");
					boolean first = true;
					for (String value : mimeMethods) {
						if (!first) {
							sb.append(",");
						}
						sb.append(value);
						first = false;
					}

					arguments.add(sb.toString());
				}
			}
			arguments.add(options.getWsdl().getAsFile().get().toURI().toString());
			return Collections.unmodifiableList(arguments);
		}

	}

}
