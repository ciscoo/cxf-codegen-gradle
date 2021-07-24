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
package io.mateo.cxf.codegen;

import static io.mateo.cxf.codegen.CxfCodegenPlugin.WSDL2JAVA_GROUP;

import javax.inject.Inject;

import io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask;
import io.mateo.cxf.codegen.wsdl2java.WsdlOption;

import org.gradle.api.*;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;

/**
 * Extension to configure WSDL sources.
 */
public class CxfCodegenExtension {

	private final NamedDomainObjectContainer<WsdlOption> wsdl2java;

	private final Project project;

	private final NamedDomainObjectProvider<Configuration> configuration;

	@Inject
	public CxfCodegenExtension(ObjectFactory objects, Project project,
			NamedDomainObjectProvider<Configuration> configuration) {
		this.wsdl2java = objects.domainObjectContainer(WsdlOption.class);
		this.project = project;
		this.configuration = configuration;
	}

	/**
	 * Returns the Java sources used for code generation.
	 * @return sources for Java code generation
	 */
	public NamedDomainObjectContainer<WsdlOption> getWsdl2java() {
		return this.wsdl2java;
	}

	/**
	 * Configures the WSDL sources to use for code generation.
	 * @param configure action or closure to configure the WSDL sources with
	 */
	public void wsdl2java(Action<? super NamedDomainObjectContainer<WsdlOption>> configure) {
		configure.execute(this.wsdl2java);

		this.addToSourceSet();
		this.registerCodegenTasks();
	}

	private void addToSourceSet() {
		this.project.getPluginManager().withPlugin("java-base", (plugin) -> {
			this.wsdl2java.all((option) -> {
				this.project.getExtensions().configure(SourceSetContainer.class, (sourceSets) -> {
					sourceSets.named(SourceSet.MAIN_SOURCE_SET_NAME, (main) -> {
						main.getJava().srcDir(this.project.provider(() -> option.getOutputDir().getAsFile()));
					});
				});
			});
		});
	}

	@SuppressWarnings("deprecation") // setMain()
	private void registerCodegenTasks() {
		this.wsdl2java.all((option) -> {
			String name = option.getName().substring(0, 1).toUpperCase() + option.getName().substring(1);
			String taskName = "wsdl2java" + name;
			TaskProvider<Wsdl2JavaTask> task;
			try {
				task = this.project.getTasks().named(taskName, Wsdl2JavaTask.class);
			}
			catch (UnknownTaskException e) {
				task = this.project.getTasks().register(taskName, Wsdl2JavaTask.class);
			}
			task.configure((it) -> {
				it.getOutputs().dir(option.getOutputDir().get());
				it.getSource().value(option.getWsdl());

				try {
					it.getMainClass().set("org.apache.cxf.tools.wsdlto.WSDLToJava");
				}
				catch (NoSuchMethodError ignored) {
					// < Gradle 6.4
					it.setMain("org.apache.cxf.tools.wsdlto.WSDLToJava");
				}
				it.setClasspath(this.configuration.get());
				it.setGroup(WSDL2JAVA_GROUP);
				it.setDescription("Generates Java sources for '" + option.getName() + "'");
				it.setArgs(option.generateArgs());
			});
		});
	}

}
