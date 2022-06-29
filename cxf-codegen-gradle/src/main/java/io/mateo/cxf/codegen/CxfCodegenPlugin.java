/*
 * Copyright 2020-2022 the original author or authors.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java;

import org.gradle.api.InvalidUserDataException;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskCollection;

/**
 * {@link Plugin} for code generation from WSDLs using Apache CXF.
 */
public class CxfCodegenPlugin implements Plugin<Project> {

	private static final String VALID_CONTAINER_NAME_REGEX = "[A-Za-z0-9_\\-.]+";

	private static final Pattern CONTAINER_NAME_PATTERN = Pattern.compile(VALID_CONTAINER_NAME_REGEX);

	private static final String WSDL2JAVA_TOOL_MAIN_CLASS = "org.apache.cxf.tools.wsdlto.WSDLToJava";

	/**
	 * Name of the {@link Configuration} where dependencies are used for code generation.
	 */
	public static final String CXF_CODEGEN_CONFIGURATION_NAME = "cxfCodegen";

	/**
	 * Name of the extension contributed by this plugin.
	 */
	public static final String CXF_CODEGEN_EXTENSION_NAME = "cxfCodegen";

	/**
	 * Task name to execute all {@link io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask} and
	 * {@link Wsdl2Java} tasks.
	 */
	public static final String WSDL2JAVA_TASK_NAME = "wsdl2java";

	/**
	 * Group name that all {@link io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask} and
	 * {@link Wsdl2Java} tasks belong to.
	 */
	public static final String WSDL2JAVA_GROUP = "wsdl2java";

	/**
	 * Update documentation when updating version.
	 */
	static final String DEFAULT_CXF_VERSION = "3.5.3";

	@SuppressWarnings("deprecation")
	@Override
	public void apply(Project project) {
		CxfCodegenExtension extension = project.getExtensions().create(CXF_CODEGEN_EXTENSION_NAME,
				CxfCodegenExtension.class);
		NamedDomainObjectProvider<Configuration> cxfCodegenConfiguration = createConfiguration(project);
		configureWsdl2JavaTaskConventions(project, cxfCodegenConfiguration);
		registerCodegenTasks(project, extension, cxfCodegenConfiguration);
		addToSourceSet(project, extension);
		registerAggregateTask(project);
		validateWsdlContainerWhenComplete(project, extension);
	}

	@SuppressWarnings("deprecation") // setMain()
	private void configureWsdl2JavaTaskConventions(Project project,
			NamedDomainObjectProvider<Configuration> cxfCodegenConfiguration) {
		project.getTasks().withType(Wsdl2Java.class).configureEach(task -> {
			try {
				task.getMainClass().set(WSDL2JAVA_TOOL_MAIN_CLASS);
			}
			catch (NoSuchMethodError ignored) {
				// < Gradle 6.4
				task.setMain(WSDL2JAVA_TOOL_MAIN_CLASS);
			}
			task.setClasspath(cxfCodegenConfiguration.get());
			task.setGroup(WSDL2JAVA_GROUP);
			task.setDescription("Generates Java sources for '" + task.getName() + "'");
		});
	}

	@SuppressWarnings("deprecation")
	private void validateWsdlContainerWhenComplete(Project project, CxfCodegenExtension extension) {
		project.afterEvaluate(evaluated -> {
			for (io.mateo.cxf.codegen.wsdl2java.WsdlOption wsdlOption : extension.getWsdl2java()) {
				final String name = wsdlOption.getName();
				if (!CONTAINER_NAME_PATTERN.matcher(name).matches()) {
					throw new InvalidUserDataException(
							"Name '" + name + "' is not valid for the cxfCodegen container. Must match match regex "
									+ VALID_CONTAINER_NAME_REGEX);
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void registerAggregateTask(Project project) {
		TaskCollection<io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask> wsdl2JavaTasks = project.getTasks()
				.withType(io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask.class);
		TaskCollection<Wsdl2Java> wsdl2Javas = project.getTasks().withType(Wsdl2Java.class);
		project.getTasks().register(WSDL2JAVA_TASK_NAME, task -> {
			task.dependsOn(wsdl2JavaTasks, wsdl2Javas);
			task.setGroup(WSDL2JAVA_GROUP);
			task.setDescription("Runs all wsdl2java tasks");
		});
	}

	@SuppressWarnings("deprecation")
	private void addToSourceSet(Project project, CxfCodegenExtension extension) {
		// @formatter:off
		project.getPluginManager().withPlugin("java-base", plugin ->
			extension.getWsdl2java().all(option -> project.getExtensions().configure(SourceSetContainer.class,
					sourceSets -> sourceSets.named(SourceSet.MAIN_SOURCE_SET_NAME,
							main -> main.getJava().srcDir(project.provider(() -> option.getOutputDir().getAsFile())))))

		);
		// @formatter:on
		project.afterEvaluate(evaluated -> evaluated.getTasks().withType(Wsdl2Java.class).all(wsdl2Java -> {
			if (wsdl2Java.getAddToMainSourceSet().get()) {
				evaluated.getExtensions().configure(SourceSetContainer.class,
						sourceSets -> sourceSets.named(SourceSet.MAIN_SOURCE_SET_NAME,
								main -> main.getJava().srcDir(wsdl2Java.getWsdl2JavaOptions().getOutputDir())));
			}
		}));
	}

	@SuppressWarnings("deprecation") // setMain()
	private void registerCodegenTasks(Project project, CxfCodegenExtension extension,
			NamedDomainObjectProvider<Configuration> configuration) {
		extension.getWsdl2java().all(option -> {
			String name = option.getName().substring(0, 1).toUpperCase() + option.getName().substring(1);
			project.getTasks().register("cleanWsdl2java" + name, Delete.class,
					task -> task.delete(option.getOutputDir()));
			project.getTasks().register(WSDL2JAVA_TASK_NAME + name, io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask.class,
					task -> {
						task.getOutputs().dir(option.getOutputDir().get());
						task.getInputs().file(option.getWsdl().get());
						try {
							task.getMainClass().set(WSDL2JAVA_TOOL_MAIN_CLASS);
						}
						catch (NoSuchMethodError ignored) {
							// < Gradle 6.4
							task.setMain(WSDL2JAVA_TOOL_MAIN_CLASS);
						}
						task.setClasspath(configuration.get());
						task.setGroup(WSDL2JAVA_GROUP);
						task.setDescription("Generates Java sources for '" + option.getName() + "'");
						task.setArgs(option.generateArgs());
					});
		});
	}

	private NamedDomainObjectProvider<Configuration> createConfiguration(Project project) {
		return project.getConfigurations().register(CXF_CODEGEN_CONFIGURATION_NAME, configuration -> {
			configuration.setVisible(false);
			configuration.setCanBeConsumed(false);
			configuration.setCanBeResolved(true);
			configuration.setDescription("Classpath for CXF Codegen.");
			configuration.getDependencies().addAll(createDependencies(project));
		});
	}

	private List<Dependency> createDependencies(Project project) {
		DependencyHandler dependencyHandler = project.getDependencies();
		List<Dependency> dependencies = new ArrayList<>();

		// Same dependencies defined in cxf-codegen-plugin's POM.
		dependencies.add(dependencyHandler.create("org.apache.cxf:cxf-core:" + DEFAULT_CXF_VERSION));
		dependencies.add(dependencyHandler.create("org.apache.cxf:cxf-tools-common:" + DEFAULT_CXF_VERSION));
		dependencies.add(dependencyHandler.create("org.apache.cxf:cxf-tools-wsdlto-core:" + DEFAULT_CXF_VERSION));
		dependencies.add(
				dependencyHandler.create("org.apache.cxf:cxf-tools-wsdlto-databinding-jaxb:" + DEFAULT_CXF_VERSION));
		dependencies
				.add(dependencyHandler.create("org.apache.cxf:cxf-tools-wsdlto-frontend-jaxws:" + DEFAULT_CXF_VERSION));

		// The Maven plugin excludes cxf-rt-frontend-simple, so exclude it here as well.
		ModuleDependency dependency = (ModuleDependency) dependencyHandler
				.create("org.apache.cxf:cxf-tools-wsdlto-frontend-javascript:" + DEFAULT_CXF_VERSION);
		Map<String, String> excludeProperties = new HashMap<>();
		excludeProperties.put("group", "org.apache.cxf");
		excludeProperties.put("module", "cxf-rt-frontend-simple");
		dependency.exclude(excludeProperties);
		dependencies.add(dependency);

		return dependencies;
	}

}
