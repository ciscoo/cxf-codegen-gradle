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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.mateo.cxf.codegen.dsl.CxfCodegenExtension;
import io.mateo.cxf.codegen.internal.GeneratedVersionAccessor;
import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java;

import io.mateo.cxf.codegen.wsdl2js.Wsdl2Js;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.util.GradleVersion;

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
		CxfCodegenExtension extension = createExtension(project);
		NamedDomainObjectProvider<Configuration> cxfCodegenConfiguration = createConfiguration(project, extension);
		configureWsdl2JavaTaskConventions(project, cxfCodegenConfiguration);
		configureWsdl2JsTaskConventions(project, cxfCodegenConfiguration);
		addToSourceSet(project);
		registerAggregateTask(project);
	}

	private CxfCodegenExtension createExtension(Project project) {
		CxfCodegenExtension extension = project.getExtensions()
			.create(CxfCodegenExtension.EXTENSION_NAME, CxfCodegenExtension.class);
		extension.getCxfVersion().convention(GeneratedVersionAccessor.CXF_VERSION);
		return extension;
	}

	private void configureWsdl2JsTaskConventions(Project project,
			NamedDomainObjectProvider<Configuration> cxfCodegenConfiguration) {
		project.getTasks().withType(Wsdl2Js.class).configureEach(task -> {
			task.getMainClass().set(WSDL2JS_TOOL_MAIN_CLASS);
			task.setClasspath(cxfCodegenConfiguration.get());
			task.setGroup(WSDL2JS_GROUP);
			task.setDescription("Generates JavaScript sources for '" + task.getName() + "'");
			task.getWsdl2JsOptions()
				.getOutputDir()
				.convention(project.getLayout().getBuildDirectory().dir(task.getName() + "-wsdl2js-generated-sources"));
		});
	}

	private void configureWsdl2JavaTaskConventions(Project project,
			NamedDomainObjectProvider<Configuration> cxfCodegenConfiguration) {
		project.getTasks().withType(Wsdl2Java.class).configureEach(task -> {
			task.getMainClass().set(WSDL2JAVA_TOOL_MAIN_CLASS);
			task.setClasspath(cxfCodegenConfiguration.get());
			task.setGroup(WSDL2JAVA_GROUP);
			task.setDescription("Generates Java sources for '" + task.getName() + "'");
			task.getWsdl2JavaOptions()
				.getOutputDir()
				.convention(
						project.getLayout().getBuildDirectory().dir(task.getName() + "-wsdl2java-generated-sources"));
			task.getAddToMainSourceSet().convention(true);
		});
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
				evaluated.getExtensions()
					.configure(SourceSetContainer.class, sourceSets -> sourceSets.named(SourceSet.MAIN_SOURCE_SET_NAME,
							main -> main.getJava().srcDir(wsdl2Java.getWsdl2JavaOptions().getOutputDir())));
			}
		}));
	}

	private NamedDomainObjectProvider<Configuration> createConfiguration(Project project,
			CxfCodegenExtension extension) {
		return project.getConfigurations().register(CXF_CODEGEN_CONFIGURATION_NAME, configuration -> {
			configuration.setVisible(false);
			configuration.setCanBeConsumed(false);
			configuration.setCanBeResolved(true);
			configuration.setDescription("Classpath for CXF Codegen.");
			configuration.getDependencies().addAllLater(createDependencies(project, extension));
		});
	}

	private Provider<List<Dependency>> createDependencies(Project project, CxfCodegenExtension extension) {
		// Avoid cast exception: FlatMapProvider to CollectionProviderInternal
		if (GradleVersion.current().compareTo(GradleVersion.version("7.4")) < 0) {
			String cxfVersion = extension.getCxfVersion().get();
			return createDependenciesProvider(project, cxfVersion);
		}
		return extension.getCxfVersion().flatMap(cxfVersion -> createDependenciesProvider(project, cxfVersion));
	}

	private ListProperty<Dependency> createDependenciesProvider(Project project, String cxfVersion) {
		ListProperty<Dependency> dependencies = project.getObjects().listProperty(Dependency.class);
		addDependencies(dependencies::add, project.getDependencies(), cxfVersion);
		return dependencies;
	}

	private void addDependencies(Consumer<Dependency> adderFn, DependencyHandler dependencyHandler, String cxfVersion) {
		// Same dependencies defined in cxf-codegen-plugin's POM.
		adderFn.accept(dependencyHandler.create("org.apache.cxf:cxf-core:" + cxfVersion));
		adderFn.accept(dependencyHandler.create("org.apache.cxf:cxf-tools-common:" + cxfVersion));
		adderFn.accept(dependencyHandler.create("org.apache.cxf:cxf-tools-wsdlto-core:" + cxfVersion));
		adderFn.accept(dependencyHandler.create("org.apache.cxf:cxf-tools-wsdlto-databinding-jaxb:" + cxfVersion));
		adderFn.accept(dependencyHandler.create("org.apache.cxf:cxf-tools-wsdlto-frontend-jaxws:" + cxfVersion));

		// The Maven plugin excludes cxf-rt-frontend-simple, so exclude it here as well.
		ModuleDependency dependency = (ModuleDependency) dependencyHandler
			.create("org.apache.cxf:cxf-tools-wsdlto-frontend-javascript:" + cxfVersion);
		Map<String, String> excludeProperties = new HashMap<>();
		excludeProperties.put("group", "org.apache.cxf");
		excludeProperties.put("module", "cxf-rt-frontend-simple");
		dependency.exclude(excludeProperties);
		adderFn.accept(dependency);
	}

}
