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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import io.mateo.junit.GradleBuild;
import io.mateo.junit.GradleCompatibility;
import io.mateo.junit.GradleDsl;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.TestTemplate;

@GradleCompatibility
class IncrementalBuildFunctionalTests {

	@TestTemplate
	void generatesJavaSourcesWhenSeparateXsdIsUsed(GradleBuild gradleBuild) {
		runTest(gradleBuild);
	}

	@TestTemplate
	void wsdlFileChangesCausesTaskToBeOutdated(GradleBuild gradleBuild) throws IOException {
		GradleRunner runner = gradleBuild.prepareRunner("calculator", "-i");
		BuildResult result = runner.build();

		assertThat(result.getOutput())
				.contains("Task ':calculator' is not up-to-date because:\n" + "  No history is available.");

		// Simulate changes to the file by adding a new line.
		Path calculatorWsdlPath = Path.of("wsdls", "calculator.wsdl");
		Path wsdl = gradleBuild.getProjectDir().resolve(calculatorWsdlPath);
		BufferedWriter writer = Files.newBufferedWriter(wsdl, StandardOpenOption.APPEND);
		writer.newLine();
		writer.close();

		result = runner.build();

		assertThat(result.getOutput()).contains("Task ':calculator' is not up-to-date because",
				"Input property 'wsdl2JavaOptions.wsdl'");
	}

	@TestTemplate
	void wsdlOptionChangesCausesTaskToBeOutdated(GradleBuild gradleBuild) throws IOException {
		GradleRunner runner = gradleBuild.prepareRunner("calculator", "-i");

		BuildResult first = runner.build();
		assertThat(first.getOutput()).contains("Task ':calculator' is not up-to-date");
		BuildResult second = runner.build();
		assertThat(second.getOutput()).contains("Skipping task ':calculator' as it is up-to-date.");

		// Changing wsdlOptions.markGenerated to true triggers a rebuild
		this.overwriteBuildSpec(gradleBuild, "wsdlOptionChangesCausesTaskToBeOutdatedMarkGenerated");
		BuildResult third = runner.build();
		assertThat(third.getOutput()).contains("Task ':calculator' is not up-to-date");
		BuildResult fourth = runner.build();
		assertThat(fourth.getOutput()).contains("Skipping task ':calculator' as it is up-to-date.");
	}

	void runTest(GradleBuild gradleBuild) {
		GradleRunner runner = gradleBuild.prepareRunner("calculator", "-i");

		BuildResult initialResult = runner.build();
		BuildResult secondResult = runner.build();

		assertThat(initialResult.getOutput()).contains("Task ':calculator' is not up-to-date");
		assertThat(secondResult.getOutput()).contains("Skipping task ':calculator' as it is up-to-date.");
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

	void overwriteBuildSpec(GradleBuild build, String newBuildFileBase) throws IOException {
		final Path buildScriptPath = Path.of("src", "functionalTest", "resources", "io", "mateo", "cxf", "codegen");
		if (build.getDsl() == GradleDsl.GROOVY) {
			Files.copy(buildScriptPath.resolve(Path.of(newBuildFileBase + ".gradle")),
					build.getProjectDir().resolve("build.gradle"), StandardCopyOption.COPY_ATTRIBUTES,
					StandardCopyOption.REPLACE_EXISTING);
		}
		else {
			Files.copy(buildScriptPath.resolve(Path.of(newBuildFileBase + ".gradle.kts")),
					build.getProjectDir().resolve("build.gradle.kts"), StandardCopyOption.COPY_ATTRIBUTES,
					StandardCopyOption.REPLACE_EXISTING);
		}

	}

}
