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

import io.mateo.cxf.codegen.internal.GeneratedVersionAccessor;
import io.mateo.junit.GradleBuild;
import io.mateo.junit.GradleCompatibilityExtension;
import java.nio.file.Path;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

class PluginConfigurationFunctionalTests {

    @RegisterExtension
    static GradleCompatibilityExtension extension = new GradleCompatibilityExtension("current");

    @TestTemplate
    void extension(GradleBuild gradleBuild) {
        var result = gradleBuild.script(scriptFor("extension")).build("verify");

        assertThat(result.getOutput()).contains("Configured CXF version = " + GeneratedVersionAccessor.CXF_VERSION);
    }

    Path scriptFor(String name) {
        final Path rootDir = Path.of("").toAbsolutePath().getParent();
        return rootDir.resolve(Path.of("documentation", "src", "docs", "gradle", "plugin-configuration", name));
    }
}
