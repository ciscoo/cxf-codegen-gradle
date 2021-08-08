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
package io.mateo.cxf.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import io.mateo.junit.GradleBuild;
import io.mateo.junit.GradleCompatibilityExtension;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * Tests for plugins that perform some configuration on
 * {@link org.gradle.api.tasks.JavaExec} task types.
 * <p>
 * Tests against the current version of Gradle and the latest plugin version at the time
 * the test was written. From time to time, upgrade the plugin version.
 */
class AdditionalPluginsFunctionalTests {

	@RegisterExtension
	static GradleCompatibilityExtension extension = new GradleCompatibilityExtension("current");

	@TestTemplate // gh-7
	void micronautApplicationPlugin(GradleBuild gradleBuild) {
		GradleRunner runner = gradleBuild.prepareRunner("wsdl2java", "-i", "-PmicronautVersion=2.0.1");

		BuildResult result = runner.buildAndFail();

		assertThat(result.getOutput()).contains("'wsdl' property is not present");
	}

}
