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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import io.mateo.cxf.codegen.junit.TaskNameGenerator;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;

@DisplayNameGeneration(TaskNameGenerator.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Wsdl2JavaOptionsTests {

	private Project project;

	private Path outputDir;

	private Path wsdl;

	@BeforeAll
	void beforeAll(@TempDir Path projectDir, @TempDir Path wsdl) {
		this.project = ProjectBuilder.builder().withProjectDir(projectDir.toFile()).build();
		this.outputDir = this.project.getLayout().getBuildDirectory().get().getAsFile().toPath();
		this.wsdl = wsdl;
	}

	@AfterEach
	void tearDown(TestInfo testInfo) {
		this.project.getTasks().named(testInfo.getDisplayName(), task -> task.setEnabled(false));
	}

	@Test
	void packageNames(TestInfo testInfo) {
		List<String> expected = List.of("-p", "com.example.foo", "-p", "com.example.bar", "-d",
				this.outputDir.toString(), this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getPackageNames().set(List.of("com.example.foo", "com.example.bar")))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void namespaceExcludes(TestInfo testInfo) {
		List<String> expected = List.of("-nexclude", "foo", "-nexclude", "bar", "-d", this.outputDir.toString(),
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getNamespaceExcludes().set(List.of("foo", "bar")))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void bindingFiles(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-b",
				this.project.getLayout()
					.getProjectDirectory()
					.file("foo")
					.getAsFile()
					.toPath()
					.toAbsolutePath()
					.toString(),
				"-b",
				this.project.getLayout()
					.getProjectDirectory()
					.file("bar")
					.getAsFile()
					.toPath()
					.toAbsolutePath()
					.toString(),
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getBindingFiles().set(List.of("foo", "bar")))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void frontend(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-fe", "foo",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(), options -> options.getFrontend().set("foo"))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void databinding(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-db", "foo",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(), options -> options.getDatabinding().set("foo"))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlVersion(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-wv", "foo",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(), options -> options.getWsdlVersion().set("foo"))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void catalog(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-catalog", "foo",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(), options -> options.getCatalog().set("foo"))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void extendedSoapHeaders(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-exsh", "true",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getExtendedSoapHeaders().set(true))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void noTypes(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-noTypes",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(), options -> options.getNoTypes().set(true))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void allowElementRefs(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-allowElementReferences",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getAllowElementRefs().set(true))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void validateWsdl(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-validate=foo",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(), options -> options.getValidateWsdl().set("foo"))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void markGenerated(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-mark-generated",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(), options -> options.getMarkGenerated().set(true))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void suppressGeneratedDate(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-suppress-generated-date",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getSuppressGeneratedDate().set(true))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void defaultExcludesNamespace(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-dex", "true",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getDefaultExcludesNamespace().set(true))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void defaultNamespacePackingMapping(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-dns", "true",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getDefaultNamespacePackageMapping().set(true))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void serviceName(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-sn", "foo",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(), options -> options.getServiceName().set("foo"))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void faultSerial(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-faultSerialVersionUID", "1234",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getFaultSerialVersionUid().set("1234"))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void exceptionSuper(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-exceptionSuper",
				UncheckedIOException.class.toString(), this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getExceptionSuper().set(UncheckedIOException.class.toString()))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void seiSuper(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-seiSuper", "foo", "-seiSuper", "bar",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getSeiSuper().set(List.of("foo", "bar")))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void autoNameResolution(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-autoNameResolution",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getAutoNameResolution().set(true))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void noAddressBinding(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-noAddressBinding",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getNoAddressBinding().set(true))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void xjcArgs(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-xjc-Xts", "-xjc-Xwsdlextension",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getXjcArgs().set(List.of("-Xts", "-Xwsdlextension")))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void extraArgs(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-client", "-all",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getExtraArgs().set(List.of("-client", "-all")))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlLocation(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-wsdlLocation", "foo",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(), options -> options.getWsdlLocation().set("foo"))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlList(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-wsdlList",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(), options -> options.getWsdlList().set(true))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void encoding(TestInfo testInfo) {
		List<String> expected = List.of("-encoding", "UTF-8", "-d", this.outputDir.toString(),
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(), options -> options.getEncoding().set("UTF-8"))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void verbose(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-verbose",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(), options -> options.getVerbose().set(true))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void asyncMethods(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-asyncMethods=foo,bar",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getAsyncMethods().set(List.of("foo", "bar")))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void bareMethods(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-bareMethods=foo,bar",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getBareMethods().set(List.of("foo", "bar")))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void mimeMethods(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "-mimeMethods=foo,bar",
				this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName(),
				options -> options.getMimeMethods().set(List.of("foo", "bar")))
			.getArgumentProviders()
			.get(0)
			.asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlOnly(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), this.wsdl.toAbsolutePath().toUri().toString());

		Iterable<String> actual = createTask(testInfo.getDisplayName()).getArgumentProviders().get(0).asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlUrlOnly(TestInfo testInfo) {
		List<String> expected = List.of("-d", this.outputDir.toString(), "https://example.com/example?wsdl");

		Iterable<String> actual = createTask(testInfo.getDisplayName(), options -> {
			options.getWsdl().set("https://example.com/example?wsdl");
		}).getArgumentProviders().get(0).asArguments();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlMissingResultsInFailure(TestInfo testInfo) {
		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
			createTaskWithConfiguration(testInfo.getDisplayName(), options -> {
			}).getArgumentProviders().get(0).asArguments();
		}).withMessage("Cannot generate arguments for task 'wsdlMissingResultsInFailure' because 'wsdl' has no value.");
	}

	private Wsdl2Java createTask(String taskName, Action<? super Wsdl2JavaOptions> configurer) {
		return this.project.getTasks().create(taskName, Wsdl2Java.class, wsdl2java -> wsdl2java.toolOptions(options -> {
			options.getOutputDir().set(this.outputDir.toFile());
			options.getWsdl().set(this.wsdl.toAbsolutePath().toUri().toString());
			configurer.execute(options);
		}));
	}

	private Wsdl2Java createTask(String taskName) {
		return this.project.getTasks().create(taskName, Wsdl2Java.class, wsdl2java -> wsdl2java.toolOptions(options -> {
			options.getOutputDir().set(this.outputDir.toFile());
			options.getWsdl().set(this.wsdl.toAbsolutePath().toUri().toString());
		}));
	}

	private Wsdl2Java createTaskWithConfiguration(String taskName, Action<? super Wsdl2JavaOptions> configurer) {
		return this.project.getTasks().create(taskName, Wsdl2Java.class, wsdl2java -> {
			wsdl2java.getWsdl2JavaOptions().getOutputDir().set(this.outputDir.toFile());
			wsdl2java.toolOptions(configurer);
		});
	}

}
