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

import java.io.File;
import java.io.IOException;

import io.mateo.junit.GradleBuild;
import io.mateo.junit.GradleCompatibility;
import io.mateo.junit.GradleDsl;

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.TestTemplate;

@GradleCompatibility
class IncrementalBuildFunctionalTests {

	private void overwriteBuildSpec(GradleBuild build, String newBuildFileBase) throws IOException {
		if (build.getDsl() == GradleDsl.GROOVY) {
			FileUtils.copyFile(
					new File("src/functionalTest/resources/io/mateo/cxf/codegen", newBuildFileBase + ".gradle"),
					new File(build.getProjectDir(), "build.gradle"));
		}
		else {
			FileUtils.copyFile(
					new File("src/functionalTest/resources/io/mateo/cxf/codegen", newBuildFileBase + ".gradle.kts"),
					new File(build.getProjectDir(), "build.gradle.kts"));
		}

	}

	@TestTemplate
	void separateXsd(GradleBuild gradleBuild) {
		GradleRunner runner = gradleBuild.prepareRunner("wsdl2javaCalculator", "-i");
		BuildResult initial = runner.build();
		assertThat(initial.getOutput()).contains("Task ':wsdl2javaCalculator' is not up-to-date");
		BuildResult second = runner.build();
		assertThat(second.getOutput()).contains("Skipping task ':wsdl2javaCalculator' as it is up-to-date.");
		BuildResult third = runner.build();
		assertThat(third.getOutput()).contains("Skipping task ':wsdl2javaCalculator' as it is up-to-date.");
	}

	@TestTemplate
	void generatesJavaFromWsdl(GradleBuild gradleBuild) throws IOException {
		GradleRunner runner = gradleBuild.prepareRunner("wsdl2javaCalculator", "-i");

		BuildResult first = runner.build();
		assertThat(first.getOutput()).contains("Task ':wsdl2javaCalculator' is not up-to-date");
		BuildResult second = runner.build();
		assertThat(second.getOutput()).contains("Skipping task ':wsdl2javaCalculator' as it is up-to-date.");

		// test that changing WSDLOption.markGenerated triggers a rebuild
		this.overwriteBuildSpec(gradleBuild, "generatesJavaFromWsdlChangedMarkGenerated");
		BuildResult third = runner.build();
		assertThat(third.getOutput()).contains("Task ':wsdl2javaCalculator' is not up-to-date");
		BuildResult fourth = runner.build();
		assertThat(fourth.getOutput()).contains("Skipping task ':wsdl2javaCalculator' as it is up-to-date.");

		// test that changing WsdlOption.wsdl triggers a rebuild
		this.overwriteBuildSpec(gradleBuild, "generatesJavaFromWsdlChangedWsdl");
		BuildResult fifth = runner.build();
		assertThat(fifth.getOutput()).contains("Task ':wsdl2javaCalculator' is not up-to-date");
		BuildResult sixth = runner.build();
		assertThat(sixth.getOutput()).contains("Skipping task ':wsdl2javaCalculator' as it is up-to-date.");
	}

}