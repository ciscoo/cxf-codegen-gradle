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
package docs;

import io.mateo.junit.GradleBuild;
import io.mateo.junit.GradleCompatibilityExtension;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratingJavaFunctionalTests {

	@RegisterExtension
	static GradleCompatibilityExtension extension = new GradleCompatibilityExtension("current");

	@TestTemplate
	void defaultToolOptions(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.script(scriptFor("default-tool-options")).build("verify");

		assertThat(result.getOutput()).contains("first markGenerated true")
			.contains("second markGenerated true")
			.contains("third markGenerated true");
	}

	@TestTemplate
	void disableAllTaskLogs(GradleBuild gradleBuild) throws IOException {
		GradleRunner runner = gradleBuild.script(scriptFor("disable-all-task-logs")).prepareRunner("wsdl2java");
		Files.copy(Path.of("src", "functionalTest", "resources", "emptyLogback.xml"),
				gradleBuild.getProjectDir().resolve("logback.xml"), StandardCopyOption.COPY_ATTRIBUTES,
				StandardCopyOption.REPLACE_EXISTING);
		Files.copy(gradleBuild.getProjectDir().resolve(Path.of("wsdls", "calculator.wsdl")),
				gradleBuild.getProjectDir().resolve(Path.of("example.wsdl")), StandardCopyOption.COPY_ATTRIBUTES);
		Files.copy(gradleBuild.getProjectDir().resolve(Path.of("wsdls", "calculator.xsd")),
				gradleBuild.getProjectDir().resolve(Path.of("example.xsd")), StandardCopyOption.COPY_ATTRIBUTES);

		BuildResult result = runner.build();

		assertThat(result.getOutput()).doesNotContain("DEBUG org.apache.cxf.common.logging.LogUtils",
				"DEBUG org.apache.cxf.tools.wsdlto.core.PluginLoader", "DEBUG org.apache.velocity");
	}

	@TestTemplate
	void disableTaskLogs(GradleBuild gradleBuild) {
		GradleRunner runner = gradleBuild.script(scriptFor("disable-task-logs")).prepareRunner("wsdl2java");

		BuildResult result = runner.build();

		assertThat(result.getOutput())
			.doesNotContain("DEBUG org.apache.cxf.common.logging.LogUtils",
					"DEBUG org.apache.cxf.tools.wsdlto.core.PluginLoader")
			.contains("DEBUG org.apache.velocity");
	}

	@TestTemplate
	void javaNine(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.script(scriptFor("java-nine")).prepareRunner("verify").build();

		assertThat(result.getOutput()).contains(List.of("jakarta.xml.ws-api", "jakarta.annotation-api"));
	}

	@TestTemplate
	void jakarta(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.script(scriptFor("jakarta")).prepareRunner("wsdl2java").build();

		assertThat(result.getTasks()).extracting(BuildTask::getOutcome)
			.allMatch(outcome -> outcome == TaskOutcome.SUCCESS);
		assertThat(gradleBuild.getProjectDir()).satisfies(projectDir -> {
			Path generatedSources = projectDir.resolve(Path.of("build", "calculator-wsdl2java-generated-sources"));
			assertThat(generatedSources).exists().isNotEmptyDirectory();

			Path packageDir = generatedSources.resolve(Path.of("org", "tempuri"));
			assertThat(packageDir).exists().isNotEmptyDirectory();

			List<Path> sourcesDir = Files.list(packageDir).collect(Collectors.toUnmodifiableList());
			assertThat(sourcesDir).hasSize(12);

			List<Path> filtered = sourcesDir.stream()
				.filter(it -> it.getFileName().toString().equals("Calculator.java"))
				.collect(Collectors.toUnmodifiableList());
			assertThat(filtered).hasSize(1);
			Path calculatorSource = filtered.get(0);

			List<String> calculatorSourceLines = Files.readAllLines(calculatorSource);

			// Lines 5-8 should start with Jakarta import.
			assertThat(calculatorSourceLines.get(5)).startsWith("import jakarta");
			assertThat(calculatorSourceLines.get(6)).startsWith("import jakarta");
			assertThat(calculatorSourceLines.get(7)).startsWith("import jakarta");
			assertThat(calculatorSourceLines.get(8)).startsWith("import jakarta");
		});
	}

	@TestTemplate
	void loggingTask(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.script(scriptFor("logging-task")).prepareRunner("wsdl2java").build();

		assertThat(result.getOutput()).contains("DEBUG org.apache.cxf.common.logging.LogUtils",
				"DEBUG org.apache.cxf.tools.wsdlto.core.PluginLoader", "DEBUG org.apache.velocity");
	}

	@TestTemplate
	void minimalTaskUsage(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.script(scriptFor("minimal-task-usage")).prepareRunner("verify").build();

		assertThat(result.getOutput()).contains("/path/to/example.wsdl");
	}

	@TestTemplate
	void toolOptions(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.script(scriptFor("tool-options")).prepareRunner("verify").build();

		assertThat(result.getOutput()).contains("/path/to/example.wsdl", "/build/generated-java", "markGenerated=true",
				"packageNames=[com.example, com.foo.bar]", "asyncMethods=[foo, bar]");
	}

	String scriptFor(String name) {
		final Path rootDir = Path.of("").toAbsolutePath().getParent();
		Path scriptPath = rootDir.resolve(Path.of("documentation", "src", "docs", "gradle", "generating-java", name));
		return scriptPath.toString();
	}

}
