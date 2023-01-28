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

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.mateo.junit.GradleBuild;
import io.mateo.junit.GradleCompatibilityExtension;

import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

class PluginConfigurationFunctionalTests {

	@RegisterExtension
	static GradleCompatibilityExtension extension = new GradleCompatibilityExtension("current");

	@TestTemplate
	void managingDependencyVersions(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.script(scriptFor("managing-dependency-versions")).build();

		// Do not test Gradle's functionality of dependency resolution.
		// Just tests that we are able to do it.
		assertThat(result.getOutput()).contains("BUILD SUCCESSFUL");
	}

	@TestTemplate
	void pluginExtension(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.script(scriptFor("plugin-extension")).build();

		assertThat(result.getOutput()).contains("BUILD SUCCESSFUL");
	}

	String scriptFor(String name) {
		final Path rootDir = Path.of("").toAbsolutePath().getParent();
		Path scriptPath = rootDir
				.resolve(Path.of("documentation", "src", "docs", "gradle", "plugin-configuration", name));
		return scriptPath.toString();
	}

}
