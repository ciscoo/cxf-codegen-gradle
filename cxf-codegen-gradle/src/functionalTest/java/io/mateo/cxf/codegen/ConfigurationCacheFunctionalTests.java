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
class ConfigurationCacheFunctionalTests {

    @TestTemplate
    void configurationCacheCompatibility(GradleBuild gradleBuild) {
        GradleRunner runner = gradleBuild.useConfigurationCache().prepareRunner("--configuration-cache", "calculator");

        BuildResult initialResult = runner.build();
        BuildResult finalResult = runner.build();

        assertThat(initialResult.getOutput())
                .satisfiesAnyOf(
                        output -> assertThat(output).contains("no configuration cache is available"),
                        output -> assertThat(output).contains("no cached configuration is available"));
        assertThat(finalResult.getOutput()).contains("Reusing configuration cache.");
    }

    @TestTemplate
    void configurationCacheWorkersCompatibility(GradleBuild gradleBuild) {
        GradleRunner runner = gradleBuild
                .useConfigurationCache()
                .prepareRunner("--configuration-cache", "-Pio.mateo.cxf-codegen.workers=true", "wsdl2java");

        BuildResult initialResult = runner.build();
        BuildResult finalResult = runner.build();

        assertThat(initialResult.getOutput()).contains("no cached configuration is available for tasks: wsdl2java");
        assertThat(finalResult.getOutput())
                .contains("Reusing configuration cache.")
                .contains("Task :wsdl2java UP-TO-DATE");
    }
}
