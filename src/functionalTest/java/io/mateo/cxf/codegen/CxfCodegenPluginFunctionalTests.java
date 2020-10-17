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
package io.mateo.cxf.codegen;

import io.mateo.junit.GradleBuild;
import io.mateo.junit.GradleCompatibility;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.TestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GradleCompatibility
class CxfCodegenPluginFunctionalTests {

	@TestTemplate
	void failForMissingWsdl(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.buildAndFail("wsdl2javaCalculator");

		assertThat(result.getOutput()).contains("Could not create task ':wsdl2javaCalculator'");
		assertThat(result.getOutput()).contains("'wsdl' property is not present");
	}

}
