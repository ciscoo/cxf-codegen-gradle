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

import io.mateo.junit.GradleBuild;
import io.mateo.junit.GradleCompatibility;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.TestTemplate;

@GradleCompatibility
class IsolatedProjectsFunctionalTests {

    @TestTemplate
    void isolatedProjectsCompatibility(GradleBuild gradleBuild) {
        GradleRunner runner = gradleBuild
                .useConfigurationCache()
                .prepareRunner("-Dorg.gradle.unsafe.isolated-projects=true", "calculator");

        BuildResult initialResult = runner.build();
        BuildResult finalResult = runner.build();

        assertThat(initialResult.getOutput()).contains("Isolated projects is an incubating feature");
        assertThat(finalResult.getOutput())
                .contains("Isolated projects is an incubating feature")
                .contains("Reusing configuration cache.");
    }

    @TestTemplate
    void isolatedProjectsWorkersCompatibility(GradleBuild gradleBuild) {
        GradleRunner runner = gradleBuild
                .useConfigurationCache()
                .prepareRunner(
                        "-Dorg.gradle.unsafe.isolated-projects=true",
                        "-Pio.mateo.cxf-codegen.workers=true",
                        "wsdl2java");

        BuildResult initialResult = runner.build();
        BuildResult finalResult = runner.build();

        assertThat(initialResult.getOutput()).contains("Isolated projects is an incubating feature");
        assertThat(finalResult.getOutput())
                .contains("Isolated projects is an incubating feature")
                .contains("Reusing configuration cache.")
                .contains("Task :wsdl2java UP-TO-DATE");
    }
}
