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
package io.mateo.cxf.codegen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.File;
import java.util.List;
import java.util.Set;

import io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CxfCodegenPluginTests {

	private Project project;

	@BeforeEach
	void setUp() {
		this.project = ProjectBuilder.builder().build();
		this.project.getPlugins().apply("io.mateo.cxf-codegen");
	}

	@Test
	void extensionCreated() {
		Object extension = this.project.getExtensions().findByName(CxfCodegenPlugin.CXF_CODEGEN_EXTENSION_NAME);

		assertThat(extension).isNotNull();
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

	@Test
	void registersTasks(@TempDir File temp) {
		project.getExtensions().configure(CxfCodegenExtension.class, (cxfCodegen) -> {
			cxfCodegen.getWsdl2java().register("foo", (foo) -> foo.getWsdl().set(temp));
			cxfCodegen.getWsdl2java().register("bar", (bar) -> bar.getWsdl().set(temp));
		});
		TaskContainer tasks = project.getTasks();

		assertThat(tasks.withType(Wsdl2JavaTask.class)).hasSize(2);
		assertThat(tasks.getByName("wsdl2javaFoo")).asInstanceOf(InstanceOfAssertFactories.type(Wsdl2JavaTask.class))
				.satisfies((task) -> wsdl2javaTaskAssertions(task, "foo"));
		assertThat(tasks.getByName("wsdl2javaBar")).asInstanceOf(InstanceOfAssertFactories.type(Wsdl2JavaTask.class))
				.satisfies((task) -> wsdl2javaTaskAssertions(task, "bar"));
		assertThat(tasks.findByName(CxfCodegenPlugin.WSDL2JAVA_TASK_NAME)).isNotNull().satisfies((wsdl2java) -> {
			assertThat(wsdl2java).isInstanceOf(DefaultTask.class);
			assertThat(wsdl2java.getDependsOn()).hasSize(2);
			assertThat(wsdl2java.getGroup()).isEqualTo(CxfCodegenPlugin.WSDL2JAVA_GROUP);
			assertThat(wsdl2java.getDescription()).isEqualTo("Runs all wsdl2java tasks");
		});
	}

	@Test
	void addsToSourceSet(@TempDir File temp) {
		project.getPluginManager().apply("java");
		SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
		SourceDirectorySet java = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).getJava();
		int expectedSize = java.getSrcDirs().size() + 1;

		project.getExtensions().configure(CxfCodegenExtension.class, (cxfCodegen) -> {
			cxfCodegen.getWsdl2java().register("foo", (foo) -> foo.getWsdl().set(temp));
		});

		assertThat(java.getSrcDirs().size()).isEqualTo(expectedSize);
	}

	private static void wsdl2javaTaskAssertions(Wsdl2JavaTask wsdl2Java, String sourceName) {
		Set<File> outputs = wsdl2Java.getOutputs().getFiles().getFiles();
		assertThat(outputs).hasSize(1);
		assertThat(outputs.iterator().next().toPath().endsWith("build/generated-sources"));
		assertThat(wsdl2Java.getMain()).isEqualTo("org.apache.cxf.tools.wsdlto.WSDLToJava");
		// Can not resolve configuration in unit tests, so assert on error message.
		assertThatCode(() -> wsdl2Java.getClasspath().getFiles()).hasMessageContaining("configuration ':cxfCodegen'");
		assertThat(wsdl2Java.getGroup()).isEqualTo(CxfCodegenPlugin.WSDL2JAVA_GROUP);
		assertThat(wsdl2Java.getDescription()).isEqualTo(String.format("Generates Java sources for '%s'", sourceName));
		assertThat(wsdl2Java.getArgs()).hasSize(3);
	}

}
