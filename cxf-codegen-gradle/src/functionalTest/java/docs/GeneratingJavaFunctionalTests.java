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
package docs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.mateo.junit.GradleBuild;
import io.mateo.junit.GradleCompatibilityExtension;

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

class GeneratingJavaFunctionalTests {

	@RegisterExtension
	static GradleCompatibilityExtension extension = new GradleCompatibilityExtension("current");

	@TestTemplate
	void defaultOptions(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.script(scriptFor("default-options")).build("verify");

		assertThat(result.getOutput()).contains("first markGenerated true").contains("second markGenerated true")
				.contains("third markGenerated true");
	}

	@TestTemplate
	void disableAllLogs(GradleBuild gradleBuild) throws IOException {
		GradleRunner runner = gradleBuild.script(scriptFor("disable-all-logs")).prepareRunner("wsdl2java");
		FileUtils.copyFile(new File("src/functionalTest/resources/emptyLogback.xml"),
				new File(gradleBuild.getProjectDir(), "logback.xml"));
		FileUtils.moveFile(FileUtils.getFile(gradleBuild.getProjectDir(), "wsdls/calculator.wsdl"),
				FileUtils.getFile(gradleBuild.getProjectDir(), "example.wsdl"));
		FileUtils.moveFile(FileUtils.getFile(gradleBuild.getProjectDir(), "wsdls/calculator.xsd"),
				FileUtils.getFile(gradleBuild.getProjectDir(), "example.xsd"));

		BuildResult result = runner.build();

		assertThat(result.getOutput()).doesNotContain("DEBUG org.apache.cxf.common.logging.LogUtils",
				"DEBUG org.apache.cxf.tools.wsdlto.core.PluginLoader", "DEBUG org.apache.velocity");
	}

	@TestTemplate
	void disableLogs(GradleBuild gradleBuild) throws IOException {
		GradleRunner runner = gradleBuild.script(scriptFor("disable-logs")).prepareRunner("wsdl2java");

		BuildResult result = runner.build();

		assertThat(result.getOutput()).doesNotContain("DEBUG org.apache.cxf.common.logging.LogUtils",
				"DEBUG org.apache.cxf.tools.wsdlto.core.PluginLoader").contains("DEBUG org.apache.velocity");
	}

	String scriptFor(String name) {
		final Path rootDir = Paths.get("").toAbsolutePath().getParent();
		Path scriptPath = Paths.get(rootDir.toString(), "documentation", "src", "docs", "gradle", "generating-java",
				name);
		return scriptPath.toString();
	}

}
