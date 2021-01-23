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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.UncheckedIOException;
import java.util.*;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WsdlOptionTests {

	private WsdlOption option;

	private File temp;

	private File outputDir;

	@BeforeEach
	void setUp(@TempDir File temp) {
		Project project = ProjectBuilder.builder().withProjectDir(temp).build();
		this.temp = temp;
		this.outputDir = project.getLayout().getBuildDirectory().dir("generated-sources").get().getAsFile();
		this.option = new WsdlOption("sample", project.getObjects(), project.getLayout());
		this.option.getWsdl().set(this.temp);
	}

	@Test
	void packageNames() {
		this.option.getPackageNames().set(List.of("com.example.foo", "com.example.bar"));
		List<String> expected = List.of("-p", "com.example.foo", "-p", "com.example.bar", "-d",
				this.outputDir.getAbsolutePath(), this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void namespaceExcludes() {
		this.option.getNamespaceExcludes().set(List.of("foo", "bar"));
		List<String> expected = List.of("-nexclude", "foo", "-nexclude", "bar", "-d", this.outputDir.getAbsolutePath(),
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void bindingFiles() {
		this.option.getBindingFiles().set(List.of("foo", "bar"));
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-b",
				new File(this.temp, "foo").toURI().toString(), "-b", new File(this.temp, "bar").toURI().toString(),
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void frontend() {
		this.option.getFrontend().set("foo");
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-fe", "foo",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void databinding() {
		this.option.getDatabinding().set("foo");
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-db", "foo",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlVersion() {
		this.option.getWsdlVersion().set("foo");
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-wv", "foo",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void catalog() {
		this.option.getCatalog().set("foo");
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-catalog", "foo",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void extendedSoapHeaders() {
		this.option.getExtendedSoapHeaders().set(true);
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-exsh", "true",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void noTypes() {
		this.option.getNoTypes().set(true);
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-noTypes",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void allowElementRefs() {
		this.option.getAllowElementRefs().set(true);
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-allowElementReferences",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void validateWsdl() {
		this.option.getValidateWsdl().set("foo");
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-validate=foo",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void markGenerated() {
		this.option.getMarkGenerated().set(true);
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-mark-generated",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void suppressGeneratedDate() {
		this.option.getSuppressGeneratedDate().set(true);
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-suppress-generated-date",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void defaultExcludesNamespace() {
		this.option.getDefaultExcludesNamespace().set(true);
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-dex", "true",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void defaultNamespacePackingMapping() {
		this.option.getDefaultNamespacePackageMapping().set(true);
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-dns", "true",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void serviceName() {
		this.option.getServiceName().set("foo");
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-sn", "foo",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void faultSerial() {
		this.option.getFaultSerialVersionUid().set("1234");
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-faultSerialVersionUID", "1234",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void exceptionSuper() {
		this.option.getExceptionSuper().set(UncheckedIOException.class.toString());
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-exceptionSuper",
				UncheckedIOException.class.toString(), this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void seiSuper() {
		this.option.getSeiSuper().set(List.of("foo", "bar"));
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-seiSuper", "foo", "-seiSuper", "bar",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void autoNameResolution() {
		this.option.getAutoNameResolution().set(true);
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-autoNameResolution",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void noAddressBinding() {
		this.option.getNoAddressBinding().set(true);
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-noAddressBinding",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void xjcArgs() {
		this.option.getXjcArgs().set(List.of("-Xts", "-Xwsdlextension"));
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-xjc-Xts", "-xjc-Xwsdlextension",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void extraArgs() {
		this.option.getExtraArgs().set(List.of("-client", "-all"));
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-client", "-all",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlLocation() {
		this.option.getWsdlLocation().set("foo");
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-wsdlLocation", "foo",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlList() {
		this.option.getWsdlList().set(true);
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-wsdlList",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void verbose() {
		this.option.getVerbose().set(true);
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-verbose",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void asyncMethods() {
		this.option.getAsyncMethods().set(List.of("foo", "bar"));
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-asyncMethods=foo,bar",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void bareMethods() {
		this.option.getBareMethods().set(List.of("foo", "bar"));
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-bareMethods=foo,bar",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void mimeMethods() {
		this.option.getMimeMethods().set(List.of("foo", "bar"));
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), "-mimeMethods=foo,bar",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlOnly() {
		List<String> expected = List.of("-d", this.outputDir.getAbsolutePath(), this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

}
