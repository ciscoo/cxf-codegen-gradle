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
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesFunctionalTests {

	@RegisterExtension
	static GradleCompatibilityExtension extension = new GradleCompatibilityExtension("current");

	@TestTemplate
	void jaxwsBindingFile(GradleBuild gradleBuild) {
		doTestForScript("jaxws-binding-file", gradleBuild);
	}

	@TestTemplate
	void specifyDataBinding(GradleBuild gradleBuild) {
		doTestForScript("specify-data-binding", gradleBuild);
	}

	@TestTemplate
	void serviceName(GradleBuild gradleBuild) {
		doTestForScript("service-name", gradleBuild);
	}

	@TestTemplate
	void loadFromArtifact(GradleBuild gradleBuild) {
		doTestForScript("loading-wsdl", gradleBuild);
	}

	@TestTemplate
	void usingXjcExtensions(GradleBuild gradleBuild) {
		doTestForScript("using-xjc-extensions", gradleBuild);
	}

	private void doTestForScript(String name, GradleBuild gradleBuild) {
		final var rootDir = Path.of("").toAbsolutePath().getParent();
		final var scriptPath = rootDir.resolve(Path.of("documentation", "src", "docs", "gradle", "examples", name));

		var runner = gradleBuild.script(scriptPath.toString()).prepareRunner("wsdl2java");

		var result = runner.build();

		assertThat(result.task(":wsdl2java").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
	}

}
