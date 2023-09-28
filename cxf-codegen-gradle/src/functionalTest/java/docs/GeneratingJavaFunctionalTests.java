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
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
	void loggingTask(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.script(scriptFor("logging-task")).prepareRunner("wsdl2java").build();

		assertThat(result.getOutput()).contains("DEBUG org.apache.cxf.common.logging.LogUtils",
				"DEBUG org.apache.cxf.tools.wsdlto.core.PluginLoader", "DEBUG org.apache.velocity");
	}

	@TestTemplate
	void verboseLogging(GradleBuild gradleBuild) {
		// same as loggingTask, just different samples
		var result = gradleBuild.script(scriptFor("verbose-logging")).prepareRunner("wsdl2java").build();

		assertThat(result.getOutput()).contains("DEBUG org.apache.cxf.common.logging.LogUtils",
				"DEBUG org.apache.cxf.tools.wsdlto.core.PluginLoader", "DEBUG org.apache.velocity");
	}

	@TestTemplate
	void minimalTaskUsage(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.script(scriptFor("minimal-task-usage")).prepareRunner("verify").build();

		assertThat(result.getOutput()).contains(Path.of("path", "to", "example.wsdl").toString());
	}

	@TestTemplate
	void toolOptions(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.script(scriptFor("tool-options")).prepareRunner("verify").build();

		assertThat(result.getOutput()).contains(Path.of("path", "to", "example.wsdl").toString(),
				Path.of("build", "generated-java").toString(), "markGenerated=true",
				"packageNames=[com.example, com.foo.bar]", "asyncMethods=[foo, bar]");
	}

	Path scriptFor(String name) {
		final Path rootDir = Path.of("").toAbsolutePath().getParent();
		return rootDir.resolve(Path.of("documentation", "src", "docs", "gradle", "generating-java", name));
	}

}
