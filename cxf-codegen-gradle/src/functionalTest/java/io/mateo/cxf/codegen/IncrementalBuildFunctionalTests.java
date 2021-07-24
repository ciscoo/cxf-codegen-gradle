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
import java.util.HashMap;
import java.util.Map;

import io.mateo.junit.GradleBuild;
import io.mateo.junit.GradleCompatibility;

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.TestTemplate;

@GradleCompatibility
class IncrementalBuildFunctionalTests {

	@TestTemplate
	void separateXsd(GradleBuild gradleBuild) throws IOException {
		{
			// test that a change in the WSDL referencing another XSD trigger a rebuild
			GradleRunner runner = gradleBuild.prepareRunner("wsdl2javaCalculator", "-i");
			runner.build();

			String addJavaBefore = FileUtils.readFileToString(
					new File(runner.getProjectDir(), "build/generated-sources/org/tempuri/Add.java"), "UTF-8");
			assertThat(addJavaBefore).doesNotContain("protected int intC;");

			FileUtils.copyFile(new File("src/functionalTest/resources/wsdls/calculatorSeparateXsdModified.wsdl"),
					new File(runner.getProjectDir(), "wsdls/calculatorSeparateXsd.wsdl"));

			BuildResult finalResult = runner.build();
			assertThat(finalResult.getOutput()).contains("Task ':wsdl2javaCalculator' is not up-to-date");

			String addJavaAfter = FileUtils.readFileToString(
					new File(runner.getProjectDir(), "build/generated-sources/org/tempuri/Add.java"), "UTF-8");
			assertThat(addJavaAfter).contains("protected int intC;");
		}
		{
			// test that a change just in the referenced XSD triggers a rebuild
			GradleRunner runner = gradleBuild.prepareRunner("wsdl2javaCalculator", "-i");
			runner.build();

			String addJavaBefore = FileUtils.readFileToString(
					new File(runner.getProjectDir(), "build/generated-sources/org/tempuri/Add.java"), "UTF-8");
			assertThat(addJavaBefore).doesNotContain("protected int intC;");

			FileUtils.copyFile(new File("src/functionalTest/resources/wsdls/calculatorModified.xsd"),
					new File(runner.getProjectDir(), "wsdls/calculator.xsd"));

			BuildResult finalResult = runner.build();
			assertThat(finalResult.getOutput()).contains("Task ':wsdl2javaCalculator' is not up-to-date");

			String addJavaAfter = FileUtils.readFileToString(
					new File(runner.getProjectDir(), "build/generated-sources/org/tempuri/Add.java"), "UTF-8");
			assertThat(addJavaAfter).contains("protected int intC;");
		}
	}

	@TestTemplate
	void generatesJavaFromWsdl(GradleBuild gradleBuild) {
		GradleRunner runner = gradleBuild.prepareRunner("wsdl2javaCalculator", "-i");

		BuildResult initialResult = runner.build();
		assertThat(initialResult.getOutput()).contains("Task ':wsdl2javaCalculator' is not up-to-date");
		BuildResult secondResult = runner.build();
		assertThat(secondResult.getOutput()).contains("Skipping task ':wsdl2javaCalculator' as it is up-to-date.");
		Map<String, String> env = new HashMap<>();
		env.put("MARK_GENERATED", "1");
		BuildResult finalResult = runner.withEnvironment(env).build();
		// test that WSDLOption changes actually trigger a rebuild
		assertThat(finalResult.getOutput()).contains("Task ':wsdl2javaCalculator' is not up-to-date");
	}

}
