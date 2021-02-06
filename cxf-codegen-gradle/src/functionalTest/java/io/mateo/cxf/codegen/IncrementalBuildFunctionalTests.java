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
import io.mateo.junit.GradleCompatibility;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.TestTemplate;

@GradleCompatibility
class IncrementalBuildFunctionalTests {

	@TestTemplate
	void separateXsd(GradleBuild gradleBuild) {
		doTest(gradleBuild);
	}

	@TestTemplate
	void generatesJavaFromWsdl(GradleBuild gradleBuild) {
		doTest(gradleBuild);
	}

	void doTest(GradleBuild gradleBuild) {
		GradleRunner runner = gradleBuild.prepareRunner("wsdl2javaCalculator", "-i");

		BuildResult initialResult = runner.build();
		BuildResult secondResult = runner.build();
		BuildResult finalResult = runner.build();

		assertThat(initialResult.getOutput()).contains("Task ':wsdl2javaCalculator' is not up-to-date");
		assertThat(secondResult.getOutput()).contains("Skipping task ':wsdl2javaCalculator' as it is up-to-date.");
		assertThat(finalResult.getOutput()).contains("Skipping task ':wsdl2javaCalculator' as it is up-to-date.");
	}

}
