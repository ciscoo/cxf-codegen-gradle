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
package io.mateo.cxf.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import io.mateo.junit.GradleBuild;
import io.mateo.junit.GradleCompatibilityExtension;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

class ConfigurationCacheFunctionalTests {

	@RegisterExtension
	static GradleCompatibilityExtension gradleCompatibilityExtension = new GradleCompatibilityExtension("6.6.1",
			"6.7.1", "6.8.3", "6.9.3", "7.4.2", "7.5.1", "7.6", "current");

	@TestTemplate
	void configurationCache(GradleBuild gradleBuild) {
		GradleRunner runner = gradleBuild.prepareRunner("--configuration-cache", "wsdl2javaCalculator");

		BuildResult initialResult = runner.build();
		BuildResult finalResult = runner.build();

		assertThat(initialResult.getOutput()).contains("no configuration cache is available");
		assertThat(finalResult.getOutput()).contains("Reusing configuration cache.");
	}

	@TestTemplate
	void configurationCacheCompatibility(GradleBuild gradleBuild) {
		GradleRunner runner = gradleBuild.prepareRunner("--configuration-cache", "calculator");

		BuildResult initialResult = runner.build();
		BuildResult finalResult = runner.build();

		assertThat(initialResult.getOutput()).contains("no configuration cache is available");
		assertThat(finalResult.getOutput()).contains("Reusing configuration cache.");
	}

}
