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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.mateo.cxf.codegen.junit.TaskNameGenerator;
import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java;

import io.mateo.cxf.codegen.wsdl2js.Wsdl2Js;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.gradle.api.DefaultTask;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.ProjectConfigurationException;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(TaskNameGenerator.class)
class CxfCodegenPluginTests {

	private Project project;

	@BeforeEach
	void setUp() {
		this.project = ProjectBuilder.builder().build();
		this.project.getPlugins().apply("io.mateo.cxf-codegen");
	}

	@SuppressWarnings("deprecation")
	@Test
	void extensionCreated() {
		Object extension = this.project.getExtensions().findByName(CxfCodegenPlugin.CXF_CODEGEN_EXTENSION_NAME);

		assertThat(extension).isInstanceOf(CxfCodegenExtension.class);
	}

	@Test
	void configurationCreated() {
		Configuration configuration = this.project.getConfigurations()
				.findByName(CxfCodegenPlugin.CXF_CODEGEN_CONFIGURATION_NAME);

		assertThat(configuration).isNotNull();
		assertThat(configuration.isVisible()).isFalse();
		assertThat(configuration.isCanBeConsumed()).isFalse();
		assertThat(configuration.isCanBeResolved()).isTrue();
		assertThat(configuration.getDescription()).isEqualTo("Classpath for CXF Codegen.");
	}

	@Test
	void defaultDependencies() {
		Spec<Dependency> spec = (dependency) -> dependency.getName().equals("cxf-tools-wsdlto-frontend-javascript");
		List<String> expectedDependencies = List.of("cxf-core", "cxf-tools-common", "cxf-tools-wsdlto-core",
				"cxf-tools-wsdlto-databinding-jaxb", "cxf-tools-wsdlto-frontend-jaxws",
				"cxf-tools-wsdlto-frontend-javascript");

		Configuration configuration = this.project.getConfigurations()
				.getByName(CxfCodegenPlugin.CXF_CODEGEN_CONFIGURATION_NAME);

		assertThat(configuration.getDependencies()).isNotEmpty();
		assertThat(configuration.getDependencies()).extracting(Dependency::getName)
				.containsExactlyElementsOf(expectedDependencies);
		assertThat(configuration.getDependencies()).extracting(Dependency::getVersion)
				.allMatch((version) -> version.equals(CxfCodegenPlugin.DEFAULT_CXF_VERSION));
		assertThat(configuration.getDependencies().matching(spec)).singleElement()
				.asInstanceOf(InstanceOfAssertFactories.type(ModuleDependency.class)).satisfies((dependency) -> {
					assertThat(dependency.getExcludeRules()).singleElement()
							.extracting((excludeRule) -> excludeRule.getGroup() + ":" + excludeRule.getModule())
							.isEqualTo("org.apache.cxf:cxf-rt-frontend-simple");
				});
	}

	@SuppressWarnings("deprecation")
	@Test
	void registersTasksForExtension(@TempDir File temp) {
		project.getExtensions().configure(CxfCodegenExtension.class, (cxfCodegen) -> {
			cxfCodegen.wsdl2java((wsdl2Java) -> {
				wsdl2Java.register("foo", (foo) -> foo.getWsdl().set(temp));
				wsdl2Java.register("bar", (bar) -> bar.getWsdl().set(temp));
			});
		});
		TaskContainer tasks = project.getTasks();

		assertThat(tasks.withType(io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask.class)).hasSize(2);
		assertThat(tasks.getByName("wsdl2javaFoo"))
				.asInstanceOf(InstanceOfAssertFactories.type(io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask.class))
				.satisfies((task) -> wsdl2javaTaskAssertions(task, "foo"));
		assertThat(tasks.getByName("wsdl2javaBar"))
				.asInstanceOf(InstanceOfAssertFactories.type(io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask.class))
				.satisfies((task) -> wsdl2javaTaskAssertions(task, "bar"));
		assertThat(tasks.withType(Delete.class).matching(it -> it.getName().toLowerCase().contains("wsdl2java")))
				.hasSize(2);
		assertThat(tasks.getByName("cleanWsdl2javaFoo")).asInstanceOf(InstanceOfAssertFactories.type(Delete.class))
				.satisfies((task) -> wsdl2javaDeleteTaskAssertions(task,
						(io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask) tasks.getByName("wsdl2javaFoo")));
		assertThat(tasks.getByName("cleanWsdl2javaBar")).asInstanceOf(InstanceOfAssertFactories.type(Delete.class))
				.satisfies((task) -> wsdl2javaDeleteTaskAssertions(task,
						(io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask) tasks.getByName("wsdl2javaBar")));
		assertThat(tasks.findByName(CxfCodegenPlugin.WSDL2JAVA_TASK_NAME)).isNotNull().satisfies((wsdl2java) -> {
			assertThat(wsdl2java).isInstanceOf(DefaultTask.class);
			assertThat(wsdl2java.getDependsOn()).hasSize(2);
			assertThat(wsdl2java.getGroup()).isEqualTo(CxfCodegenPlugin.WSDL2JAVA_GROUP);
			assertThat(wsdl2java.getDescription()).isEqualTo("Runs all wsdl2java tasks");
		});
	}

	@SuppressWarnings("deprecation")
	@Test
	void addsToSourceSetForTasksCreatedByExtension(@TempDir File temp) {
		project.getPluginManager().apply("java");
		SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
		SourceDirectorySet java = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).getJava();
		int expectedSize = java.getSrcDirs().size() + 1;

		project.getExtensions().configure(CxfCodegenExtension.class, (cxfCodegen) -> {
			cxfCodegen.wsdl2java((wsdl2Java) -> wsdl2Java.register("foo", (foo) -> foo.getWsdl().set(temp)));
		});

		assertThat(java.getSrcDirs()).hasSize(expectedSize);
	}

	@SuppressWarnings("deprecation")
	@ValueSource(strings = { "bad name", "bad:name" })
	@ParameterizedTest
	void badContainerNamesResultInError(String candidate, @TempDir File temp) {
		project.getExtensions().configure(CxfCodegenExtension.class, cxfCodegen -> cxfCodegen
				.wsdl2java(wsdl2Java -> wsdl2Java.register(candidate, it -> it.getWsdl().set(temp))));

		// @formatter:off
		assertThatExceptionOfType(ProjectConfigurationException.class)
				.isThrownBy(() -> project.getAllTasks(false)) // forces project evaluation
				.withCause(new InvalidUserDataException("Name '" + candidate
						+ "' is not valid for the cxfCodegen container. Must match match regex [A-Za-z0-9_\\-.]+"));
		// @formatter:on
	}

	@Test
	void configureWsdl2JavaDefaults(TestInfo testInfo) {
		project.getTasks().register(testInfo.getDisplayName(), Wsdl2Java.class);

		project.getTasks().withType(Wsdl2Java.class).all(wsdl2Java -> {
			Set<File> outputs = wsdl2Java.getOutputs().getFiles().getFiles();
			assertThat(outputs).hasSize(1);
			assertThat(outputs.iterator().next().toPath())
					.endsWithRaw(Paths.get("build/" + wsdl2Java.getName() + "-wsdl2java-generated-sources"));
			assertThat(wsdl2Java.getMainClass().get()).isEqualTo("org.apache.cxf.tools.wsdlto.WSDLToJava");
			// Can not resolve configuration in unit tests, so assert on error message.
			assertThatCode(() -> wsdl2Java.getClasspath().getFiles())
					.hasMessageContaining("configuration ':cxfCodegen'");
			assertThat(wsdl2Java.getGroup()).isEqualTo(CxfCodegenPlugin.WSDL2JAVA_GROUP);
			assertThat(wsdl2Java.getDescription())
					.isEqualTo(String.format("Generates Java sources for '%s'", testInfo.getDisplayName()));
			assertThat(wsdl2Java.getArgumentProviders()).singleElement().extracting(it -> it.getClass().getSimpleName())
					.isEqualTo("Wsdl2JavaArgumentProvider");
			assertThat(wsdl2Java.getAddToMainSourceSet().get()).isTrue();
		});
	}

	@Test
	void addsToMainSourceSetForWsdl2JavaTasks(TestInfo testInfo) {
		project.getPluginManager().apply("java");
		SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
		SourceDirectorySet java = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).getJava();
		int expectedSize = java.getSrcDirs().size() + 1;

		project.getTasks().register(testInfo.getDisplayName(), Wsdl2Java.class);

		project.afterEvaluate(evaluated -> {
			assertThat(java.getSrcDirs()).hasSize(expectedSize);
			List<String> paths = java.getSrcDirs().stream().map(File::getAbsolutePath).collect(Collectors.toList());
			String outputDir = Path.of(evaluated.getBuildDir().getAbsolutePath(),
					testInfo.getDisplayName() + "-wsdl2java-generated-sources").toFile().getAbsolutePath();
			assertThat(paths).contains(outputDir);
		});
	}

	@Test
	void doesNotAddToMainSourceSetForWsdl2JavaTasksWhenConfiguredFalse(TestInfo testInfo) {
		project.getPluginManager().apply("java");
		SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
		SourceDirectorySet java = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).getJava();
		int expectedSize = java.getSrcDirs().size();

		project.getTasks().register(testInfo.getDisplayName(), Wsdl2Java.class,
				wsdl2java -> wsdl2java.getAddToMainSourceSet().set(false));

		assertThat(java.getSrcDirs()).hasSize(expectedSize);

		List<String> paths = java.getSrcDirs().stream().map(File::getAbsolutePath).collect(Collectors.toList());
		String outputDir = Path
				.of(project.getBuildDir().getAbsolutePath(), testInfo.getDisplayName() + "-wsdl2java-generated-sources")
				.toFile().getAbsolutePath();
		assertThat(paths).isNotEmpty().doesNotContain(outputDir);
	}

	@Test
	void aggregateTaskWillRunWsdl2JavaTaskTypes() {
		project.getTasks().register("a", Wsdl2Java.class);
		project.getTasks().register("b", Wsdl2Java.class);

		Task wsdl2java = project.getTasks().getByName(CxfCodegenPlugin.WSDL2JAVA_TASK_NAME);
		assertThat(wsdl2java.getDependsOn()).satisfies(dependencies -> assertThat(dependencies).hasSize(2));
	}

	@Test
	void configureWsdl2JsDefaults(TestInfo testInfo) {
		project.getTasks().register(testInfo.getDisplayName(), Wsdl2Js.class);

		project.getTasks().withType(Wsdl2Js.class).all(wsdl2Js -> {
			Set<File> outputs = wsdl2Js.getOutputs().getFiles().getFiles();
			assertThat(outputs).hasSize(1);
			assertThat(outputs.iterator().next().toPath())
					.endsWithRaw(Path.of("build", wsdl2Js.getName() + "-wsdl2js-generated-sources"));
			assertThat(wsdl2Js.getMainClass().get())
					.isEqualTo("org.apache.cxf.tools.wsdlto.javascript.WSDLToJavaScript");
			// Can not resolve configuration in unit tests, so assert on error message.
			assertThatCode(() -> wsdl2Js.getClasspath().getFiles()).hasMessageContaining("configuration ':cxfCodegen'");
			assertThat(wsdl2Js.getGroup()).isEqualTo(CxfCodegenPlugin.WSDL2JS_GROUP);
			assertThat(wsdl2Js.getDescription())
					.isEqualTo(String.format("Generates JavaScript sources for '%s'", testInfo.getDisplayName()));
			assertThat(wsdl2Js.getArgumentProviders()).singleElement().extracting(it -> it.getClass().getSimpleName())
					.isEqualTo("Wsdl2JsArgumentProvider");
		});
	}

	@SuppressWarnings("unchecked") // TaskCollection cast
	@Test
	void aggregateTaskWillRunWsdl2JsTaskTypes() {
		project.getTasks().register("a", Wsdl2Js.class);
		project.getTasks().register("b", Wsdl2Js.class);

		var wsdl2java = project.getTasks().getByName(CxfCodegenPlugin.WSDL2JS_TASK_NAME);
		assertThat(wsdl2java.getDependsOn()).singleElement().satisfies(candidate -> {
			var dependencies = (TaskCollection<Task>) candidate;
			var taskNames = dependencies.stream().map(Task::toString).collect(Collectors.toList());
			assertThat(taskNames).containsExactlyElementsOf(List.of("task ':a'", "task ':b'"));
		});
	}

	@SuppressWarnings("deprecation")
	private static void wsdl2javaTaskAssertions(io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask wsdl2Java,
			String sourceName) {
		Set<File> outputs = wsdl2Java.getOutputs().getFiles().getFiles();
		assertThat(outputs).hasSize(1);
		assertThat(outputs.iterator().next().toPath())
				.endsWithRaw(Paths.get("build/generated-sources/cxf/" + sourceName));
		assertThat(wsdl2Java.getMainClass().get()).isEqualTo("org.apache.cxf.tools.wsdlto.WSDLToJava");
		// Can not resolve configuration in unit tests, so assert on error message.
		assertThatCode(() -> wsdl2Java.getClasspath().getFiles()).hasMessageContaining("configuration ':cxfCodegen'");
		assertThat(wsdl2Java.getGroup()).isEqualTo(CxfCodegenPlugin.WSDL2JAVA_GROUP);
		assertThat(wsdl2Java.getDescription()).isEqualTo(String.format("Generates Java sources for '%s'", sourceName));
		assertThat(wsdl2Java.getArgs()).hasSize(3);
	}

	@SuppressWarnings("deprecation")
	private void wsdl2javaDeleteTaskAssertions(Delete delete, io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask wsdl2Java) {
		assertThat(delete.getDelete()).as("Delete").hasSize(1);
		File expected = wsdl2Java.getOutputs().getFiles().getSingleFile();
		File actual = ((DirectoryProperty) delete.getDelete().iterator().next()).get().getAsFile();
		assertThat(actual).isEqualTo(expected);
	}

}
