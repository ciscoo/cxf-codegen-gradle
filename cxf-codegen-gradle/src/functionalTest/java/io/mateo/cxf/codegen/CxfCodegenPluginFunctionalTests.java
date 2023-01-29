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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import io.mateo.junit.GradleBuild;
import io.mateo.junit.GradleCompatibility;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.TestTemplate;

@GradleCompatibility
class CxfCodegenPluginFunctionalTests {

	@TestTemplate
	void failForMissingWsdl(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.buildAndFail("wsdl2javaCalculator");

		assertThat(result.getTasks()).isEmpty();
	}

	@TestTemplate
	void skipWhenWsdlIsMissing(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.build("calculator");

		assertThat(result.task(":calculator")).isNotNull().extracting(BuildTask::getOutcome)
				.isEqualTo(TaskOutcome.SKIPPED);
	}

	@TestTemplate
	void generatesJavaFromWsdl(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.build("wsdl2javaCalculator");

		assertThat(result.getTasks()).hasSize(1);
		assertThat(result.getTasks().get(0).getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
		assertThat(gradleBuild.getProjectDir()).satisfies((projectDir) -> {
			Path generatedSources = projectDir.resolve(Path.of("build", "generated-sources", "cxf", "calculator"));
			assertThat(generatedSources).exists();
			assertThat(generatedSources).isNotEmptyDirectory();

			Path packageDir = generatedSources.resolve(Path.of("org", "tempuri"));
			assertThat(packageDir).exists();
			assertThat(packageDir).isNotEmptyDirectory();

			List<Path> sourcesDir = Files.list(packageDir).collect(Collectors.toUnmodifiableList());
			assertThat(sourcesDir).isNotEmpty();
			assertThat(sourcesDir).hasSize(12);

			List<String> generatedFiles = sourcesDir.stream().map(Path::getFileName).map(Path::toString).sorted()
					.collect(Collectors.toList());
			List<String> expectedFiles = List.of("Add.java", "AddResponse.java", "Calculator.java",
					"CalculatorSoap.java", "Divide.java", "DivideResponse.java", "Multiply.java",
					"MultiplyResponse.java", "ObjectFactory.java", "Subtract.java", "SubtractResponse.java",
					"package-info.java");

			assertThat(generatedFiles).containsExactlyElementsOf(expectedFiles);
		});
	}

	@TestTemplate
	void javaSourceGenerationFromWsdl(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.build("calculator");

		assertThat(result.task(":calculator")).isNotNull().extracting(BuildTask::getOutcome)
				.isEqualTo(TaskOutcome.SUCCESS);
		assertThat(gradleBuild.getProjectDir()).satisfies(projectDir -> {
			Path generatedSources = projectDir.resolve(Path.of("build", "calculator-wsdl2java-generated-sources"));
			assertThat(generatedSources).exists().isNotEmptyDirectory();

			Path packageDir = generatedSources.resolve(Path.of("org", "tempuri"));
			assertThat(packageDir).exists().isNotEmptyDirectory();

			List<Path> sourcesDir = Files.list(packageDir).collect(Collectors.toUnmodifiableList());
			assertThat(sourcesDir).isNotEmpty().hasSize(12);

			List<String> generatedFiles = sourcesDir.stream().map(Path::getFileName).map(Path::toString).sorted()
					.collect(Collectors.toList());
			List<String> expectedFiles = List.of("Add.java", "AddResponse.java", "Calculator.java",
					"CalculatorSoap.java", "Divide.java", "DivideResponse.java", "Multiply.java",
					"MultiplyResponse.java", "ObjectFactory.java", "Subtract.java", "SubtractResponse.java",
					"package-info.java");

			assertThat(generatedFiles).containsExactlyInAnyOrderElementsOf(expectedFiles);
		});
	}

	@TestTemplate
	void deleteGeneratedJava(GradleBuild gradleBuild) {
		gradleBuild.build("wsdl2javaCalculator");
		assertThat(gradleBuild.getProjectDir()).satisfies((projectDir) -> {
			Path generatedSources = projectDir.resolve(Path.of("build", "generated-sources", "cxf", "calculator"));
			assertThat(generatedSources).exists();
			assertThat(generatedSources).isNotEmptyDirectory();
		});

		BuildResult result = gradleBuild.build("cleanWsdl2javaCalculator");
		assertThat(result.getTasks()).hasSize(1);
		assertThat(result.getTasks().get(0).getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
		assertThat(gradleBuild.getProjectDir()).satisfies((projectDir) -> {
			Path generatedSources = projectDir.resolve(Path.of("build", "generated-sources", "cxf", "calculator"));
			assertThat(generatedSources).doesNotExist();
		});
	}

	@TestTemplate
	void generatedJavaIsNotAddedToMainWhenConfiguredFalse(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.build("verify");

		assertThat(result.getOutput()).contains("Source directories size match: true");
	}

	@TestTemplate
	void jsSourceGenerationFromWsdl(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.build("calculator");

		assertThat(result.task(":calculator")).isNotNull().extracting(BuildTask::getOutcome)
				.isEqualTo(TaskOutcome.SUCCESS);
		assertThat(gradleBuild.getProjectDir()).satisfies(projectDir -> {
			var generatedSources = projectDir.resolve(Path.of("build", "calculator-wsdl2js-generated-sources"));
			assertThat(generatedSources).exists().isNotEmptyDirectory();

			try (var files = Files.list(generatedSources)) {
				var generatedFiles = files.map(Path::getFileName).map(Path::toString).collect(Collectors.toList());
				var expectedFiles = List.of("Calculator.js");

				assertThat(generatedFiles).containsExactlyInAnyOrderElementsOf(expectedFiles);
			}

		});
	}

}
