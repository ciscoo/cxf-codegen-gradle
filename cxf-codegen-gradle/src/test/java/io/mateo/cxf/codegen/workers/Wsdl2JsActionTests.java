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

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Wsdl2JsActionTests {

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
    void quiet(TestInfo testInfo) {
        var expected = List.of("-d", this.outputDirectory.toString(), "-quiet", this.wsdl.toString());

        var actual = getTestArguments(option -> option.getQuiet().set(true));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void verbose() {
        var expected = List.of("-d", this.outputDirectory.toString(), "-verbose", this.wsdl.toString());

        var actual = getTestArguments(options -> options.getVerbose().set(true));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void verboseAndQuietEnabledResultsInException() {
        assertThatExceptionOfType(InvalidUserDataException.class)
                .isThrownBy(() -> getTestArguments(options -> {
                    options.getQuiet().set(true);
                    options.getVerbose().set(true);
                }))
                .withMessage("Verbose and quite are mutually exclusive; only one can be enabled, not both.");
    }

    @Test
    void validate() {
        var expected = List.of("-d", this.outputDirectory.toString(), "-validate=true", this.wsdl.toString());

        var actual = getTestArguments(options -> options.getValidate().set("true"));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void catalog() {
        var catalog = this.outputDirectory.resolve("example.xml");
        var expected = List.of(
                "-catalog",
                catalog.toAbsolutePath().toString(),
                "-d",
                this.outputDirectory.toString(),
                this.wsdl.toString());

        var actual = getTestArguments(options -> options.getCatalog().set(catalog.toFile()));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void packagePrefixes() {
        var expected = List.of(
                "-p",
                "foo=http://www.example.com/Example/V1/ExampleService",
                "-p",
                "bar=http://www.example.com/Example/V1/ExampleService",
                "-d",
                this.outputDirectory.toString(),
                this.wsdl.toString());

        var actual = getTestArguments(options -> options.getPackagePrefixes()
                .set(List.of(
                        new UriPrefixPair("http://www.example.com/Example/V1/ExampleService", "foo"),
                        new UriPrefixPair("http://www.example.com/Example/V1/ExampleService", "bar"))));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void wsdlVersion() {
        var expected = List.of("-wv", "1.1", "-d", this.outputDirectory.toString(), this.wsdl.toString());

        var actual = getTestArguments(options -> options.getWsdlVersion().set("1.1"));

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void wsdlOnly() {
        var expected = List.of("-d", this.outputDirectory.toString(), this.wsdl.toString());

        var actual = getTestArguments(options -> {});

        assertThat(actual).containsExactlyElementsOf(expected);
    }

    private List<String> getTestArguments(Consumer<Wsdl2JsOption> configurer) {
        Consumer<Wsdl2JsOption> setWsdl =
                option -> option.getWsdl().set(this.wsdl.toAbsolutePath().toString());
        CodegenParameters parameters = createParameters(setWsdl.andThen(configurer));
        parameters.getProjectDirectory().set(this.project.getLayout().getProjectDirectory());
        return new Wsdl2JsActionTests.TestAction(parameters).getArguments();
    }

    private CodegenParameters createParameters(Consumer<Wsdl2JsOption> configurer) {
        var parameters = this.project.getObjects().newInstance(CodegenParameters.class);
        var option = this.project.getObjects().newInstance(Wsdl2JsOption.class, "test");
        // Plugin configures a convention for output directory.
        option.getOutputDirectory().set(this.outputDirectory.toFile());
        configurer.accept(option);
        parameters.getOption().set(option);
        return parameters;
    }

    static class TestAction extends Wsdl2JsAction {

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
