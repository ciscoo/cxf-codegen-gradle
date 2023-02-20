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
package io.mateo.cxf.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mateo.cxf.codegen.internal.GeneratedVersionAccessor;
import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java;

import io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaOptions;
import io.mateo.cxf.codegen.wsdl2js.Wsdl2Js;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskCollection;

/**
 * {@link Plugin} for code generation from WSDLs using Apache CXF.
 */
public class CxfCodegenPlugin implements Plugin<Project> {

	private static final String WSDL2JAVA_TOOL_MAIN_CLASS = "org.apache.cxf.tools.wsdlto.WSDLToJava";

	private static final String WSDL2JS_TOOL_MAIN_CLASS = "org.apache.cxf.tools.wsdlto.javascript.WSDLToJavaScript";

	/**
	 * Name of the {@link Configuration} where dependencies are used for code generation.
	 */
	public static final String CXF_CODEGEN_CONFIGURATION_NAME = "cxfCodegen";

	/**
	 * Task name to execute all {@link Wsdl2Java} tasks.
	 */
	public static final String WSDL2JAVA_TASK_NAME = "wsdl2java";

	/**
	 * Group name that all {@link Wsdl2Java} tasks belong to.
	 */
	public static final String WSDL2JAVA_GROUP = "wsdl2java";

	/**
	 * Task name to execute all {@link Wsdl2Js} tasks.
	 */
	public static final String WSDL2JS_TASK_NAME = "wsdl2js";

	/**
	 * Group name that all {@link Wsdl2Js} tasks belong to.
	 */
	public static final String WSDL2JS_GROUP = "wsdl2js";

	@Override
	public void apply(Project project) {
		NamedDomainObjectProvider<Configuration> cxfCodegenConfiguration = createConfiguration(project);
		configureWsdl2JavaTaskConventions(project, cxfCodegenConfiguration);
		configureWsdl2JsTaskConventions(project, cxfCodegenConfiguration);
		addToSourceSet(project);
		registerAggregateTask(project);
	}

	private void configureWsdl2JsTaskConventions(Project project,
			NamedDomainObjectProvider<Configuration> cxfCodegenConfiguration) {
		project.getTasks().withType(Wsdl2Js.class).configureEach(task -> {
			configureMainClass(task, WSDL2JS_TOOL_MAIN_CLASS);
			task.setClasspath(cxfCodegenConfiguration.get());
			task.setGroup(WSDL2JS_GROUP);
			task.setDescription("Generates JavaScript sources for '" + task.getName() + "'");
		});
	}

	private void configureWsdl2JavaTaskConventions(Project project,
			NamedDomainObjectProvider<Configuration> cxfCodegenConfiguration) {
		project.getTasks().withType(Wsdl2Java.class).configureEach(task -> {
			configureMainClass(task, WSDL2JAVA_TOOL_MAIN_CLASS);
			configureOnlyIf(task);
			task.setClasspath(cxfCodegenConfiguration.get());
			task.setGroup(WSDL2JAVA_GROUP);
			task.setDescription("Generates Java sources for '" + task.getName() + "'");
		});
	}

	@SuppressWarnings("deprecation") // setMain()
	private void configureMainClass(JavaExec javaExec, String mainClass) {
		try {
			javaExec.getMainClass().set(mainClass);
		}
		catch (NoSuchMethodError ignored) {
			// < Gradle 6.4
			javaExec.setMain(mainClass);
		}
	}

	private void configureOnlyIf(Wsdl2Java wsdl2Java) {
		try {
			wsdl2Java.onlyIf("run only if 'wsdl' or 'wsdlUrl' is set", self -> {
				Wsdl2JavaOptions options = ((Wsdl2Java) self).getWsdl2JavaOptions();
				return options.getWsdl().isPresent() || options.getWsdlUrl().isPresent();
			});
		}
		catch (NoSuchMethodError ignored) {
			// < Gradle 7.6
			wsdl2Java.onlyIf(task -> {
				Wsdl2JavaOptions options = ((Wsdl2Java) task).getWsdl2JavaOptions();
				boolean shouldExecute = options.getWsdl().isPresent() || options.getWsdlUrl().isPresent();
				if (!shouldExecute) {
					if (task.getLogger().isInfoEnabled()) {
						task.getLogger().info("run only if 'wsdl' or 'wsdlUrl' is set");
					}
				}
				return shouldExecute;
			});
		}
	}

	@SuppressWarnings("deprecation")
	private void registerAggregateTask(Project project) {
		TaskCollection<Wsdl2Java> wsdl2Javas = project.getTasks().withType(Wsdl2Java.class);
		project.getTasks().register(WSDL2JAVA_TASK_NAME, task -> {
			task.dependsOn(wsdl2Javas);
			task.setGroup(WSDL2JAVA_GROUP);
			task.setDescription("Runs all wsdl2java tasks");
		});
		TaskCollection<Wsdl2Js> wsdl2JsTasks = project.getTasks().withType(Wsdl2Js.class);
		project.getTasks().register(WSDL2JS_TASK_NAME, task -> {
			task.dependsOn(wsdl2JsTasks);
			task.setGroup(WSDL2JS_TASK_NAME);
			task.setDescription("Runs all wsdl2js tasks");
		});
	}

	private void addToSourceSet(Project project) {
		project.afterEvaluate(evaluated -> evaluated.getTasks().withType(Wsdl2Java.class).all(wsdl2Java -> {
			if (wsdl2Java.getAddToMainSourceSet().get()) {
				evaluated.getExtensions().configure(SourceSetContainer.class,
						sourceSets -> sourceSets.named(SourceSet.MAIN_SOURCE_SET_NAME,
								main -> main.getJava().srcDir(wsdl2Java.getWsdl2JavaOptions().getOutputDir())));
			}
		}));
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
		dependencies.add(dependencyHandler.create("org.apache.cxf:cxf-core:" + GeneratedVersionAccessor.CXF_VERSION));
		dependencies.add(
				dependencyHandler.create("org.apache.cxf:cxf-tools-common:" + GeneratedVersionAccessor.CXF_VERSION));
		dependencies.add(dependencyHandler
				.create("org.apache.cxf:cxf-tools-wsdlto-core:" + GeneratedVersionAccessor.CXF_VERSION));
		dependencies.add(dependencyHandler
				.create("org.apache.cxf:cxf-tools-wsdlto-databinding-jaxb:" + GeneratedVersionAccessor.CXF_VERSION));
		dependencies.add(dependencyHandler
				.create("org.apache.cxf:cxf-tools-wsdlto-frontend-jaxws:" + GeneratedVersionAccessor.CXF_VERSION));

		// The Maven plugin excludes cxf-rt-frontend-simple, so exclude it here as well.
		ModuleDependency dependency = (ModuleDependency) dependencyHandler
				.create("org.apache.cxf:cxf-tools-wsdlto-frontend-javascript:" + GeneratedVersionAccessor.CXF_VERSION);
		Map<String, String> excludeProperties = new HashMap<>();
		excludeProperties.put("group", "org.apache.cxf");
		excludeProperties.put("module", "cxf-rt-frontend-simple");
		dependency.exclude(excludeProperties);
		dependencies.add(dependency);

		return dependencies;
	}

}
