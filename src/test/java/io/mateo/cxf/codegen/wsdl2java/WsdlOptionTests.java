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
package io.mateo.cxf.codegen.wsdl2java;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.UncheckedIOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class WsdlOptionTests {

	private WsdlOption option;

	private File temp;

	@BeforeEach
	void setUp(@TempDir File temp) {
		Project project = ProjectBuilder.builder().withProjectDir(temp).build();
		this.temp = temp;
		this.option = new WsdlOption("sample", project.getObjects(), project.getLayout());
		this.option.getWsdl().set(this.temp);
	}

	@Test
	void packageNames() {
		this.option.getPackageNames().set(Arrays.asList("com.example.foo", "com.example.bar"));
		List<String> expected = Arrays.asList("-p", "com.example.foo", "-p", "com.example.bar", "-d",
				"/generated-sources", this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void namespaceExcludes() {
		this.option.getNamespaceExcludes().set(Arrays.asList("foo", "bar"));
		List<String> expected = Arrays.asList("-nexclude", "foo", "-nexclude", "bar", "-d", "/generated-sources",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void bindingFiles() {
		this.option.getBindingFiles().set(Arrays.asList("foo", "bar"));
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-b",
				new File(this.temp, "foo").toURI().toString(), "-b", new File(this.temp, "bar").toURI().toString(),
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void frontend() {
		this.option.getFrontend().set("foo");
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-fe", "foo", this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void databinding() {
		this.option.getDatabinding().set("foo");
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-db", "foo", this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlVersion() {
		this.option.getWsdlVersion().set("foo");
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-wv", "foo", this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void catalog() {
		this.option.getCatalog().set("foo");
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-catalog", "foo",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void extendedSoapHeaders() {
		this.option.getExtendedSoapHeaders().set(true);
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-exsh", "true",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void noTypes() {
		this.option.getNoTypes().set(true);
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-noTypes", this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void allowElementRefs() {
		this.option.getAllowElementRefs().set(true);
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-allowElementReferences",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void validateWsdl() {
		this.option.getValidateWsdl().set("foo");
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-validate=foo",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void markGenerated() {
		this.option.getMarkGenerated().set(true);
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-mark-generated",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void suppressGeneratedDate() {
		this.option.getSuppressGeneratedDate().set(true);
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-suppress-generated-date",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void defaultExcludesNamespace() {
		this.option.getDefaultExcludesNamespace().set(true);
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-dex", "true", this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void defaultNamespacePackingMapping() {
		this.option.getDefaultNamespacePackageMapping().set(true);
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-dns", "true", this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void serviceName() {
		this.option.getServiceName().set("foo");
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-sn", "foo", this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void faultSerial() {
		this.option.getFaultSerialVersionUid().set("1234");
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-faultSerialVersionUID", "1234",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void exceptionSuper() {
		this.option.getExceptionSuper().set(UncheckedIOException.class.toString());
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-exceptionSuper",
				UncheckedIOException.class.toString(), this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void seiSuper() {
		this.option.getSeiSuper().set(Arrays.asList("foo", "bar"));
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-seiSuper", "foo", "-seiSuper", "bar",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void autoNameResolution() {
		this.option.getAutoNameResolution().set(true);
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-autoNameResolution",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void noAddressBinding() {
		this.option.getNoAddressBinding().set(true);
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-noAddressBinding",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void xjcArgs() {
		this.option.getXjcArgs().set(Arrays.asList("-Xts", "-Xwsdlextension"));
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-xjc-Xts", "-xjc-Xwsdlextension",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void extraArgs() {
		this.option.getExtraArgs().set(Arrays.asList("-client", "-all"));
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-client", "-all",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlLocation() {
		this.option.getWsdlLocation().set("foo");
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-wsdlLocation", "foo",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlList() {
		this.option.getWsdlList().set(true);
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-wsdlList", this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void verbose() {
		this.option.getVerbose().set(true);
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-verbose", this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void asyncMethods() {
		this.option.getAsyncMethods().set(Arrays.asList("foo", "bar"));
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-asyncMethods=foo,bar",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void bareMethods() {
		this.option.getBareMethods().set(Arrays.asList("foo", "bar"));
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-bareMethods=foo,bar",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void mimeMethods() {
		this.option.getMimeMethods().set(Arrays.asList("foo", "bar"));
		List<String> expected = Arrays.asList("-d", "/generated-sources", "-mimeMethods=foo,bar",
				this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}

	@Test
	void wsdlOnly() {
		List<String> expected = Arrays.asList("-d", "/generated-sources", this.temp.toURI().toString());

		List<String> actual = this.option.generateArgs();

		assertThat(actual).containsExactlyElementsOf(expected);
	}
}
