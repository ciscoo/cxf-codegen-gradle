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
package io.mateo.cxf.codegen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.mateo.cxf.codegen.dsl.CxfCodegenExtension;
import io.mateo.cxf.codegen.internal.GeneratedVersionAccessor;
import io.mateo.cxf.codegen.junit.TaskNameGenerator;
import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java;
import io.mateo.cxf.codegen.wsdl2js.Wsdl2Js;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.gradle.api.Describable;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

@DisplayNameGeneration(TaskNameGenerator.class)
class CxfCodegenPluginTests {

    private Project project;

    @BeforeEach
    void setUp() {
        this.project = ProjectBuilder.builder().build();
        this.project.getPlugins().apply("io.mateo.cxf-codegen");
    }

    @Test
    void configurationCreated() {
        Configuration configuration =
                this.project.getConfigurations().findByName(CxfCodegenPlugin.CXF_CODEGEN_CONFIGURATION_NAME);

        assertThat(configuration).isNotNull();
        assertThat(configuration.isVisible()).isFalse();
        assertThat(configuration.isCanBeConsumed()).isFalse();
        assertThat(configuration.isCanBeResolved()).isTrue();
        assertThat(configuration.getDescription()).isEqualTo("Classpath for CXF Codegen.");
    }

    @Test
    void defaultDependencies() {
        Spec<Dependency> spec = dependency -> dependency.getName().equals("cxf-tools-wsdlto-frontend-javascript");
        List<String> expectedDependencies = List.of(
                "slf4j-nop",
                "cxf-core",
                "cxf-tools-common",
                "cxf-tools-wsdlto-core",
                "cxf-tools-wsdlto-databinding-jaxb",
                "cxf-tools-wsdlto-frontend-jaxws",
                "cxf-tools-wsdlto-frontend-javascript");
        var expectedVersions = List.of("2.0.17", "4.1.3", "4.1.3", "4.1.3", "4.1.3", "4.1.3", "4.1.3");

        Configuration configuration =
                this.project.getConfigurations().getByName(CxfCodegenPlugin.CXF_CODEGEN_CONFIGURATION_NAME);

        assertThat(configuration.getDependencies()).isNotEmpty();
        assertThat(configuration.getDependencies())
                .extracting(Dependency::getName)
                .containsExactlyElementsOf(expectedDependencies);
        assertThat(configuration.getDependencies())
                .extracting(Dependency::getVersion)
                .isEqualTo(expectedVersions);
        assertThat(configuration.getDependencies().matching(spec))
                .singleElement()
                .asInstanceOf(InstanceOfAssertFactories.type(ModuleDependency.class))
                .satisfies((dependency) -> {
                    assertThat(dependency.getExcludeRules())
                            .singleElement()
                            .extracting((excludeRule) -> excludeRule.getGroup() + ":" + excludeRule.getModule())
                            .isEqualTo("org.apache.cxf:cxf-rt-frontend-simple");
                });
    }

    @Test
    void configureWsdl2JavaDefaults(TestInfo testInfo) {
        project.getTasks().register(testInfo.getDisplayName(), Wsdl2Java.class);

        project.getTasks().withType(Wsdl2Java.class).all(wsdl2Java -> {
            Set<Path> outputs = wsdl2Java.getOutputs().getFiles().getFiles().stream()
                    .map(it -> it.toPath().toAbsolutePath())
                    .collect(Collectors.toUnmodifiableSet());
            assertThat(outputs).hasSize(1);
            assertThat(outputs.iterator().next())
                    .endsWithRaw(Path.of("build", wsdl2Java.getName() + "-wsdl2java-generated-sources"));
            assertThat(wsdl2Java.getMainClass().get()).isEqualTo("org.apache.cxf.tools.wsdlto.WSDLToJava");
            // Can not resolve configuration in unit tests, so assert on error message.
            assertThatCode(() -> wsdl2Java.getClasspath().getFiles())
                    .hasMessageContaining("configuration ':cxfCodegen'");
            assertThat(wsdl2Java.getGroup()).isEqualTo(CxfCodegenPlugin.WSDL2JAVA_GROUP);
            assertThat(wsdl2Java.getDescription())
                    .isEqualTo(String.format("Generates Java sources for '%s'", testInfo.getDisplayName()));
            assertThat(wsdl2Java.getArgumentProviders())
                    .singleElement()
                    .extracting(it -> it.getClass().getSimpleName())
                    .isEqualTo("Wsdl2JavaArgumentProvider");
            assertThat(wsdl2Java.getAddToMainSourceSet().get()).isTrue();
            assertThat(this
                            .<org.gradle.api.internal.tasks.execution.DescribingAndSpec<
                                            ? super org.gradle.api.internal.TaskInternal>>
                                    uncheckedCast(wsdl2Java.getOnlyIf())
                            .getSpecs())
                    .singleElement()
                    .asInstanceOf(InstanceOfAssertFactories.type(Describable.class))
                    .extracting(Describable::getDisplayName)
                    .isEqualTo("Task is enabled");
        });
    }

    @Test
    void addsToMainSourceSetForWsdl2JavaTasks(TestInfo testInfo) {
        project.getPluginManager().apply("java");
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        SourceDirectorySet java =
                sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).getJava();
        int expectedSize = java.getSrcDirs().size() + 1;

        project.getTasks().register(testInfo.getDisplayName(), Wsdl2Java.class);

        project.afterEvaluate(evaluated -> {
            assertThat(java.getSrcDirs()).hasSize(expectedSize);
            List<String> paths = java.getSrcDirs().stream()
                    .map(it -> it.toPath().toAbsolutePath().toString())
                    .collect(Collectors.toList());
            String outputDir = Path.of(
                            evaluated
                                    .getLayout()
                                    .getBuildDirectory()
                                    .getAsFile()
                                    .get()
                                    .getAbsolutePath(),
                            testInfo.getDisplayName() + "-wsdl2java-generated-sources")
                    .toFile()
                    .getAbsolutePath();
            assertThat(paths).contains(outputDir);
        });
    }

    @Test
    void doesNotAddToMainSourceSetForWsdl2JavaTasksWhenConfiguredFalse(TestInfo testInfo) {
        project.getPluginManager().apply("java");
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        SourceDirectorySet java =
                sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).getJava();
        int expectedSize = java.getSrcDirs().size();

        project.getTasks().register(testInfo.getDisplayName(), Wsdl2Java.class, wsdl2java -> wsdl2java
                .getAddToMainSourceSet()
                .set(false));

        assertThat(java.getSrcDirs()).hasSize(expectedSize);

        List<String> paths = java.getSrcDirs().stream()
                .map(it -> it.toPath().toAbsolutePath().toString())
                .collect(Collectors.toList());
        String outputDir = Path.of(
                        project.getLayout()
                                .getBuildDirectory()
                                .getAsFile()
                                .get()
                                .getAbsolutePath(),
                        testInfo.getDisplayName() + "-wsdl2java-generated-sources")
                .toFile()
                .getAbsolutePath();
        assertThat(paths).isNotEmpty().doesNotContain(outputDir);
    }

    @SuppressWarnings("unchecked")
    @Test
    void aggregateTaskWillRunWsdl2JavaTaskTypes() {
        var a = project.getTasks().register("a", Wsdl2Java.class);
        var b = project.getTasks().register("b", Wsdl2Java.class);

        Task wsdl2java = project.getTasks().getByName(CxfCodegenPlugin.WSDL2JAVA_TASK_NAME);
        assertThat(wsdl2java.getDependsOn()).satisfies(dependencies -> assertThat(dependencies)
                .singleElement()
                .asInstanceOf(InstanceOfAssertFactories.type(TaskCollection.class))
                .satisfies(tasks -> {
                    assertThat(tasks).containsExactlyInAnyOrder(a.get(), b.get());
                }));
    }

    @Test
    void configureWsdl2JsDefaults(TestInfo testInfo) {
        project.getTasks().register(testInfo.getDisplayName(), Wsdl2Js.class);

        project.getTasks().withType(Wsdl2Js.class).all(wsdl2Js -> {
            Set<Path> outputs = wsdl2Js.getOutputs().getFiles().getFiles().stream()
                    .map(it -> it.toPath().toAbsolutePath())
                    .collect(Collectors.toSet());
            assertThat(outputs).hasSize(1);
            assertThat(outputs.iterator().next())
                    .endsWithRaw(Path.of("build", wsdl2Js.getName() + "-wsdl2js-generated-sources"));
            assertThat(wsdl2Js.getMainClass().get())
                    .isEqualTo("org.apache.cxf.tools.wsdlto.javascript.WSDLToJavaScript");
            // Can not resolve configuration in unit tests, so assert on error message.
            assertThatCode(() -> wsdl2Js.getClasspath().getFiles()).hasMessageContaining("configuration ':cxfCodegen'");
            assertThat(wsdl2Js.getGroup()).isEqualTo(CxfCodegenPlugin.WSDL2JS_GROUP);
            assertThat(wsdl2Js.getDescription())
                    .isEqualTo(String.format("Generates JavaScript sources for '%s'", testInfo.getDisplayName()));
            assertThat(wsdl2Js.getArgumentProviders())
                    .singleElement()
                    .extracting(it -> it.getClass().getSimpleName())
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

    @Test
    void extensionCreatedWithDefaults() {
        var extension = this.project.getExtensions().findByType(CxfCodegenExtension.class);

        assertThat(extension).isNotNull().satisfies(ext -> assertThat(
                        ext.getCxfVersion().get())
                .isEqualTo(GeneratedVersionAccessor.CXF_VERSION));
    }

    @org.junitpioneer.jupiter.SetSystemProperty(key = "GRADLE_VERSION_OVERRIDE", value = "8.10")
    @Test
    void warnForDeprecated() throws IllegalAccessException, NoSuchFieldException {
        var message = new java.util.concurrent.atomic.AtomicReference<String>();
        var logger = (org.gradle.internal.logging.slf4j.OutputEventListenerBackedLogger)
                org.slf4j.LoggerFactory.getLogger(CxfCodegenPlugin.class);

        var contextField = getField(org.gradle.internal.logging.slf4j.OutputEventListenerBackedLogger.class, "context");
        var context =
                (org.gradle.internal.logging.slf4j.OutputEventListenerBackedLoggerContext) contextField.get(logger);
        context.setOutputEventListener(
                logEvent -> message.getAndSet(((org.gradle.internal.logging.events.LogEvent) logEvent).getMessage()));

        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("io.mateo.cxf-codegen");

        assertThat(message.get())
                .isEqualTo("Support for Gradle versions less than 8.11 is deprecated. You are using Gradle 8.10.1.");
    }

    @org.junitpioneer.jupiter.SetSystemProperty(key = "GRADLE_VERSION_OVERRIDE", value = "8.11")
    @Test
    void noWarningForDeprecated() throws IllegalAccessException, NoSuchFieldException {
        var message = new java.util.concurrent.atomic.AtomicReference<String>();
        var logger = (org.gradle.internal.logging.slf4j.OutputEventListenerBackedLogger)
                org.slf4j.LoggerFactory.getLogger(CxfCodegenPlugin.class);

        var contextField = getField(org.gradle.internal.logging.slf4j.OutputEventListenerBackedLogger.class, "context");
        var context =
                (org.gradle.internal.logging.slf4j.OutputEventListenerBackedLoggerContext) contextField.get(logger);
        context.setOutputEventListener(
                logEvent -> message.getAndSet(((org.gradle.internal.logging.events.LogEvent) logEvent).getMessage()));

        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("io.mateo.cxf-codegen");

        assertThat(message.get()).isNull();
    }

    private Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        if (Modifier.isPrivate(field.getModifiers())) {
            field.setAccessible(true);
        }
        return field;
    }

    @SuppressWarnings("unchecked")
    public <T> T uncheckedCast(Object object) {
        return (T) object;
    }
}
