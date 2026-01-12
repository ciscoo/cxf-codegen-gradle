/*
 * Copyright 2020-2025 the original author or authors.
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
package io.mateo.cxf.codegen.workers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Wsdl2JavaActionTests {

    private Project project;

    private Path outputDirectory;

    private Path wsdl;

    @BeforeAll
    void beforeAll(@TempDir Path projectDir, @TempDir Path wsdl) {
        this.project =
                ProjectBuilder.builder().withProjectDir(projectDir.toFile()).build();
        this.outputDirectory =
                this.project.getLayout().getBuildDirectory().get().getAsFile().toPath();
        this.wsdl = wsdl;
    }

    @Test
    void packageNames() {
        List<String> expected = List.of(
                "-p",
                "com.example.foo",
                "-p",
                "com.example.bar",
                "-d",
                this.outputDirectory.toString(),
                this.wsdl.toAbsolutePath().toUri().toString());

        List<String> actual =
                getTestArguments(option -> option.getPackageNames().set(List.of("com.example.foo", "com.example.bar")));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void namespaceExcludes() {
        List<String> expected = List.of(
                "-nexclude",
                "foo",
                "-nexclude",
                "bar",
                "-d",
                this.outputDirectory.toString(),
                this.wsdl.toAbsolutePath().toUri().toString());

        List<String> actual =
                getTestArguments(option -> option.getNamespaceExcludes().set(List.of("foo", "bar")));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void bindingFiles() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-b",
                this.project
                        .getLayout()
                        .getProjectDirectory()
                        .file("foo")
                        .getAsFile()
                        .toPath()
                        .toAbsolutePath()
                        .toString(),
                "-b",
                this.project
                        .getLayout()
                        .getProjectDirectory()
                        .file("bar")
                        .getAsFile()
                        .toPath()
                        .toAbsolutePath()
                        .toString(),
                this.wsdl.toAbsolutePath().toUri().toString());

        List<String> actual =
                getTestArguments(option -> option.getBindingFiles().set(List.of("foo", "bar")));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void frontend() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-fe",
                "foo",
                this.wsdl.toAbsolutePath().toUri().toString());

        List<String> actual = getTestArguments(option -> option.getFrontend().set("foo"));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void databinding() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-db",
                "foo",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getDatabinding().set("foo"));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void wsdlVersion() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-wv",
                "foo",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getWsdlVersion().set("foo"));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void catalog() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-catalog",
                "foo",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual = getTestArguments(option -> option.getCatalog().set("foo"));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void extendedSoapHeaders() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-exsh",
                "true",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getExtendedSoapHeaders().set(true));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void noTypes() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-noTypes",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual = getTestArguments(option -> option.getNoTypes().set(true));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void allowElementRefs() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-allowElementReferences",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getAllowElementRefs().set(true));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void validateWsdl() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-validate=foo",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getValidateWsdl().set("foo"));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void markGenerated() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-mark-generated",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getMarkGenerated().set(true));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void suppressGeneratedDate() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-suppress-generated-date",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getSuppressGeneratedDate().set(true));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void defaultExcludesNamespace() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-dex",
                "true",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getDefaultExcludesNamespace().set(true));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void defaultNamespacePackingMapping() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-dns",
                "true",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual = getTestArguments(
                option -> option.getDefaultNamespacePackageMapping().set(true));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void serviceName() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-sn",
                "foo",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getServiceName().set("foo"));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void faultSerial() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-faultSerialVersionUID",
                "1234",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getFaultSerialVersionUid().set("1234"));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void exceptionSuper() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-exceptionSuper",
                UncheckedIOException.class.toString(),
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getExceptionSuper().set(UncheckedIOException.class.toString()));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void seiSuper() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-seiSuper",
                "foo",
                "-seiSuper",
                "bar",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getSeiSuper().set(List.of("foo", "bar")));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void autoNameResolution() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-autoNameResolution",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getAutoNameResolution().set(true));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void noAddressBinding() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-noAddressBinding",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getNoAddressBinding().set(true));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void xjcArgs() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-xjc-Xts",
                "-xjc-Xwsdlextension",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getXjcArgs().set(List.of("-Xts", "-Xwsdlextension")));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void extraArgs() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-client",
                "-all",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getExtraArgs().set(List.of("-client", "-all")));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void wsdlLocation() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-wsdlLocation",
                "foo",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getWsdlLocation().set("foo"));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void wsdlList() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-wsdlList",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getWsdlList().set(true));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void encoding() {
        List<String> expected = List.of(
                "-encoding",
                "UTF-8",
                "-d",
                this.outputDirectory.toString(),
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getEncoding().set("UTF-8"));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void verbose() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-verbose",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual = getTestArguments(option -> option.getVerbose().set(true));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void asyncMethods() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-asyncMethods=foo,bar",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getAsyncMethods().set(List.of("foo", "bar")));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void bareMethods() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-bareMethods=foo,bar",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getBareMethods().set(List.of("foo", "bar")));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void mimeMethods() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                "-mimeMethods=foo,bar",
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual =
                getTestArguments(option -> option.getMimeMethods().set(List.of("foo", "bar")));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void wsdlOnly() {
        List<String> expected = List.of(
                "-d",
                this.outputDirectory.toString(),
                this.wsdl.toAbsolutePath().toUri().toString());

        Iterable<String> actual = getTestArguments(option -> {});

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void wsdlUrlOnly() {
        List<String> expected = List.of("-d", this.outputDirectory.toString(), "https://example.com/example?wsdl");

        Iterable<String> actual = getTestArguments(option -> option.getWsdl().set("https://example.com/example?wsdl"));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void wsdlMissingResultsInFailure() {
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> getTestArguments(option -> option.getWsdl().unset()))
                .withMessage("Cannot query the value of property 'wsdl' because it has no value available.");
    }

    private List<String> getTestArguments(Consumer<Wsdl2JavaOption> configurer) {
        Consumer<Wsdl2JavaOption> setWsdl =
                option -> option.getWsdl().set(this.wsdl.toAbsolutePath().toString());
        CodegenParameters parameters = createParameters(setWsdl.andThen(configurer));
        parameters.getProjectDirectory().set(this.project.getLayout().getProjectDirectory());
        return new TestAction(parameters).getArguments();
    }

    private CodegenParameters createParameters(Consumer<Wsdl2JavaOption> configurer) {
        var parameters = this.project.getObjects().newInstance(CodegenParameters.class);
        var option = this.project.getObjects().newInstance(Wsdl2JavaOption.class, "test");
        // Plugin configures a convention for output directory.
        option.getOutputDirectory().set(this.outputDirectory.toFile());
        configurer.accept(option);
        parameters.getOption().set(option);
        return parameters;
    }

    static class TestAction extends Wsdl2JavaAction {

        private final CodegenParameters parameters;

        public TestAction(CodegenParameters parameters) {
            this.parameters = parameters;
        }

        @Override
        public CodegenParameters getParameters() {
            return this.parameters;
        }
    }
}
