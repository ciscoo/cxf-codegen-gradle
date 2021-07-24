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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;

/**
 * {@link Plugin} for code generation from WSDLs using Apache CXF.
 */
public class CxfCodegenPlugin implements Plugin<Project> {

	/**
	 * Name of the {@link Configuration} where dependencies are used for code generation.
	 */
	public static final String CXF_CODEGEN_CONFIGURATION_NAME = "cxfCodegen";

	/**
	 * Name of the extension contributed by this plugin.
	 */
	public static final String CXF_CODEGEN_EXTENSION_NAME = "cxfCodegen";

	/**
	 * Task name to execute all {@link Wsdl2JavaTask} tasks.
	 */
	public static final String WSDL2JAVA_TASK_NAME = "wsdl2java";

	/**
	 * Group name that all {@link Wsdl2JavaTask} tasks belong to.
	 */
	public static final String WSDL2JAVA_GROUP = "wsdl2java";

	/**
	 * Update documentation when updating version.
	 */
	static final String DEFAULT_CXF_VERSION = "3.4.4";

	@Override
	public void apply(Project project) {
		NamedDomainObjectProvider<Configuration> configuration = createConfiguration(project);
		project.getExtensions().create(CXF_CODEGEN_EXTENSION_NAME, CxfCodegenExtension.class, project, configuration);
		registerAggregateTask(project);
	}

	private void registerAggregateTask(Project project) {
		project.getTasks().register(WSDL2JAVA_TASK_NAME, (task) -> {
			task.setDependsOn(project.getTasks().withType(Wsdl2JavaTask.class));
			task.setGroup(WSDL2JAVA_GROUP);
			task.setDescription("Runs all wsdl2java tasks");
		});
	}

	private NamedDomainObjectProvider<Configuration> createConfiguration(Project project) {
		return project.getConfigurations().register(CXF_CODEGEN_CONFIGURATION_NAME, (configuration) -> {
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
