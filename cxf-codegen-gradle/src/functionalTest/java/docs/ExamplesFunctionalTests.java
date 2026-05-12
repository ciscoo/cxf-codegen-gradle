/*
 * Copyright 2020-2025 the original author or authors.
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

import io.mateo.junit.GradleBuild;
import io.mateo.junit.GradleCompatibilityExtension;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

class ExamplesFunctionalTests {

    @RegisterExtension
    static GradleCompatibilityExtension extension = new GradleCompatibilityExtension();

    @TestTemplate
    void jaxwsBindingFile(GradleBuild gradleBuild) {
        doTestForScript("jaxws-binding-file", gradleBuild);
    }

    @TestTemplate
    void jaxwsBindingFileWorkers(GradleBuild gradleBuild) {
        doTestForScript("jaxws-binding-file-workers", gradleBuild, "-Pio.mateo.cxf-codegen.workers=true");
    }

    @TestTemplate
    void specifyDataBinding(GradleBuild gradleBuild) {
        doTestForScript("data-binding", gradleBuild);
    }

    @TestTemplate
    void specifyDataBindingWorkers(GradleBuild gradleBuild) {
        doTestForScript("data-binding-workers", gradleBuild, "-Pio.mateo.cxf-codegen.workers=true");
    }

    @TestTemplate
    void serviceName(GradleBuild gradleBuild) {
        doTestForScript("service-name", gradleBuild);
    }

    @TestTemplate
    void serviceNameWorkers(GradleBuild gradleBuild) {
        doTestForScript("service-name-workers", gradleBuild, "-Pio.mateo.cxf-codegen.workers=true");
    }

    @TestTemplate
    void loadFromArtifact(GradleBuild gradleBuild) {
        doTestForScript("loading-wsdl", gradleBuild);
    }

    @TestTemplate
    void loadFromArtifactWorkers(GradleBuild gradleBuild) {
        doTestForScript("loading-wsdl-workers", gradleBuild, "-Pio.mateo.cxf-codegen.workers=true");
    }

    @TestTemplate
    void usingXjcExtensions(GradleBuild gradleBuild) {
        doTestForScript("using-xjc-extensions", gradleBuild);
    }

    @TestTemplate
    void usingXjcExtensionsWorkers(GradleBuild gradleBuild) {
        doTestForScript("using-xjc-extensions-workers", gradleBuild, "-Pio.mateo.cxf-codegen.workers=true");
    }

    private void doTestForScript(String name, GradleBuild gradleBuild, String... additionalArgs) {
        final var rootDir = Path.of("").toAbsolutePath().getParent();
        final var scriptPath = rootDir.resolve(Path.of("documentation", "src", "docs", "gradle", "examples", name));

        var args = Stream.concat(Stream.of(additionalArgs), Stream.of("wsdl2java")).toArray(String[]::new);
        var runner = gradleBuild.script(scriptPath).prepareRunner(args);

        var result = runner.build();

        assertThat(result.task(":wsdl2java"))
                .isNotNull()
                .extracting(BuildTask::getOutcome)
                .isEqualTo(TaskOutcome.SUCCESS);
    }
}
