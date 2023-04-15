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
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratingJavaScriptFunctionalTests {

	@RegisterExtension
	static GradleCompatibilityExtension extension = new GradleCompatibilityExtension("current");

	@TestTemplate
	void minimalTaskUsage(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.script(scriptFor("minimal-task-usage")).prepareRunner("verify").build();

		assertThat(result.getOutput()).contains(Path.of("path", "to", "example.wsdl").toString());
	}

	@TestTemplate
	void toolOptions(GradleBuild gradleBuild) {
		BuildResult result = gradleBuild.script(scriptFor("tool-options")).prepareRunner("verify").build();

		assertThat(result.getOutput()).contains(Path.of("path", "to", "example.wsdl").toString(),
				Path.of("build", "example-generated-js").toString(),
				Path.of("path", "to", "example-catalog.xml").toString(),
				"packagePrefixes=[UriPrefixPair[uri='https://example.com', prefix='example']]", "verbose=true");
	}

	String scriptFor(String name) {
		final var rootDir = Path.of("").toAbsolutePath().getParent();
		var scriptPath = rootDir
			.resolve(Path.of("documentation", "src", "docs", "gradle", "generating-javascript", name));
		return scriptPath.toString();
	}

}
