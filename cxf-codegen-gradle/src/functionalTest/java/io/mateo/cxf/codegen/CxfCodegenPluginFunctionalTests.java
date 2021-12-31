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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.mateo.junit.GradleBuild;
import io.mateo.junit.GradleCompatibility;

import org.apache.commons.io.FileUtils;
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
				.isEqualTo(TaskOutcome.NO_SOURCE);
	}

	@TestTemplate
	void generatesJavaFromWsdl(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.build("wsdl2javaCalculator");

		assertThat(result.getTasks()).hasSize(1);
		assertThat(result.getTasks().get(0).getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
		assertThat(gradleBuild.getProjectDir()).satisfies((projectDir) -> {
			File generatedSources = FileUtils.getFile(projectDir, "build", "generated-sources", "cxf", "calculator");
			assertThat(generatedSources).exists();
			assertThat(generatedSources).isNotEmptyDirectory();

			File packageDir = FileUtils.getFile(generatedSources, "org", "tempuri");
			assertThat(packageDir).exists();
			assertThat(packageDir).isNotEmptyDirectory();

			File[] sourcesDir = Objects.requireNonNull(packageDir.listFiles(), "sources dir");
			assertThat(sourcesDir).isNotEmpty();
			assertThat(sourcesDir).hasSize(12);

			List<String> generatedFiles = Arrays.stream(sourcesDir).map(File::getName).sorted()
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
			File generatedSources = FileUtils.getFile(projectDir, "build", "calculator-wsdl2java-generated-sources");
			assertThat(generatedSources).exists().isNotEmptyDirectory();

			File packageDir = FileUtils.getFile(generatedSources, "org", "tempuri");
			assertThat(packageDir).exists().isNotEmptyDirectory();

			File[] sourcesDir = Objects.requireNonNull(packageDir.listFiles(), "sources dir");
			assertThat(sourcesDir).isNotEmpty().hasSize(12);

			List<String> generatedFiles = Arrays.stream(sourcesDir).map(File::getName).collect(Collectors.toList());
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
			File generatedSources = FileUtils.getFile(projectDir, "build", "generated-sources", "cxf", "calculator");
			assertThat(generatedSources).exists();
			assertThat(generatedSources).isNotEmptyDirectory();
		});

		BuildResult result = gradleBuild.build("cleanWsdl2javaCalculator");
		assertThat(result.getTasks()).hasSize(1);
		assertThat(result.getTasks().get(0).getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
		assertThat(gradleBuild.getProjectDir()).satisfies((projectDir) -> {
			File generatedSources = FileUtils.getFile(projectDir, "build", "generated-sources", "cxf", "calculator");
			assertThat(generatedSources).doesNotExist();
		});
	}

}
