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
package io.mateo.cxf.codegen.wsdl2js;

import io.mateo.cxf.codegen.junit.TaskNameGenerator;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayNameGeneration(TaskNameGenerator.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Wsdl2JsOptionsTests {

	private Project project;

	private Path projectDir;

	private Path outputDir;

	private Path wsdl;

	@BeforeAll
	void beforeAll(@TempDir Path projectDir, @TempDir Path wsdl) {
		this.projectDir = projectDir;
		this.project = ProjectBuilder.builder().withProjectDir(projectDir.toFile()).build();
		this.outputDir = this.project.getLayout().getBuildDirectory().map(it -> it.getAsFile().toPath()).get();
		this.wsdl = wsdl;
	}

	@AfterEach
	void tearDown(TestInfo testInfo) {
		this.project.getTasks().named(testInfo.getDisplayName(), task -> task.setEnabled(false));
	}

	@Test
	void quiet(TestInfo testInfo) {
		var expected = List.of("-d", this.outputDir.toString(), "-quiet", this.wsdl.toString());

		var actual = createTask(testInfo.getDisplayName(), options -> options.getQuiet().set(true))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void verbose(TestInfo testInfo) {
		var expected = List.of("-d", this.outputDir.toString(), "-verbose", this.wsdl.toString());

		var actual = createTask(testInfo.getDisplayName(), options -> options.getVerbose().set(true))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void verboseAndQuietEnabledResultsInException(TestInfo testInfo) {
		var argumentProviders = createTask(testInfo.getDisplayName(), options -> {
			options.getQuiet().set(true);
			options.getVerbose().set(true);
		}).getArgumentProviders().get(0);

		assertThatExceptionOfType(GradleException.class).isThrownBy(argumentProviders::asArguments)
			.withMessage("Verbose and quite are mutually exclusive; only one can be enabled, not both.");
	}

	@Test
	void validate(TestInfo testInfo) {
		var expected = List.of("-d", this.outputDir.toString(), "-validate=true", this.wsdl.toString());

		var actual = createTask(testInfo.getDisplayName(), options -> options.getValidate().set("true"))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void catalog(TestInfo testInfo) {
		var catalog = this.outputDir.resolve("example.xml");
		var expected = List.of("-catalog", catalog.toAbsolutePath().toString(), "-d", this.outputDir.toString(),
				this.wsdl.toString());

		var actual = createTask(testInfo.getDisplayName(), options -> options.getCatalog().set(catalog.toFile()))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void packagePrefixes(TestInfo testInfo) {
		var expected = List.of("-p", "foo=http://www.example.com/Example/V1/ExampleService", "-p",
				"bar=http://www.example.com/Example/V1/ExampleService", "-d", this.outputDir.toString(),
				this.wsdl.toString());

		var actual = createTask(testInfo.getDisplayName(), options -> options.getPackagePrefixes()
			.set(List.of(new Wsdl2JsOptions.UriPrefixPair("http://www.example.com/Example/V1/ExampleService", "foo"),
					new Wsdl2JsOptions.UriPrefixPair("http://www.example.com/Example/V1/ExampleService", "bar"))))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlVersion(TestInfo testInfo) {
		var expected = List.of("-wv", "1.1", "-d", this.outputDir.toString(), this.wsdl.toString());

		var actual = createTask(testInfo.getDisplayName(), options -> options.getWsdlVersion().set("1.1"))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlOnly(TestInfo testInfo) {
		var expected = List.of("-d", this.outputDir.toString(), this.wsdl.toString());

		var actual = createTask(testInfo.getDisplayName()).getArgumentProviders().get(0).asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	private Wsdl2Js createTask(String taskName, Action<? super Wsdl2JsOptions> configurer) {
		return this.project.getTasks().create(taskName, Wsdl2Js.class, wsdl2java -> wsdl2java.toolOptions(options -> {
			options.getOutputDir().set(this.outputDir.toFile());
			options.getWsdl().set(this.wsdl.toString());
			configurer.execute(options);
		}));
	}

	private Wsdl2Js createTask(String taskName) {
		return this.project.getTasks().create(taskName, Wsdl2Js.class, wsdl2java -> wsdl2java.toolOptions(options -> {
			options.getOutputDir().set(this.outputDir.toFile());
			options.getWsdl().set(this.wsdl.toString());
		}));
	}

}
