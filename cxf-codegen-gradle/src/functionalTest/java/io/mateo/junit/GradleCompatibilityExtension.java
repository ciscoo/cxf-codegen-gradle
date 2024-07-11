/*
 * Copyright 2020-2024 the original author or authors.
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
package io.mateo.junit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.gradle.util.GradleVersion;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

/**
 * {@link Extension} that runs {@linkplain TestTemplate templated tests} against multiple
 * versions of Gradle for each DSL. Test classes using the extension must have test
 * methods that have a parameter of type {@link GradleBuild}.
 * <p>
 * Heavily based on the Spring Boot extension by Andy Wilkinson.
 */
public final class GradleCompatibilityExtension implements TestTemplateInvocationContextProvider {

    private final List<String> gradleVersions;

    public GradleCompatibilityExtension() {
        this.gradleVersions = List.of("8.4", "8.5", "8.6", "8.7", "current", "8.9-rc-2");
    }

    public GradleCompatibilityExtension(String... versions) {
        this.gradleVersions = List.of(versions);
    }

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        List<GradleVersionDsl> versionToDsl = new ArrayList<>();
        this.gradleVersions.forEach((version) -> {
            versionToDsl.add(new GradleVersionDsl(version, GradleDsl.GROOVY));
            versionToDsl.add(new GradleVersionDsl(version, GradleDsl.KOTLIN));
        });
        return versionToDsl.stream().map(GradleVersionTestTemplateInvocationContext::new);
    }

    private record GradleVersionTestTemplateInvocationContext(GradleVersionDsl gradleVersionDsl)
            implements TestTemplateInvocationContext {

        @Override
        public String getDisplayName(int invocationIndex) {
            if (this.gradleVersionDsl.version().equals("current")) {
                return String.format(
                        "Gradle %s - %s ",
                        GradleVersion.current().getVersion(),
                        this.gradleVersionDsl.dsl().getName());
            }
            return String.format(
                    "Gradle %s - %s",
                    this.gradleVersionDsl.version(), this.gradleVersionDsl.dsl().getName());
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            GradleBuild gradleBuild = new GradleBuild().dsl(this.gradleVersionDsl.dsl());
            if (!this.gradleVersionDsl.version().equals("current")) {
                gradleBuild.gradleVersion(this.gradleVersionDsl.version());
            }
            return List.of(new GradleBuildExtension(gradleBuild));
        }
    }
}
