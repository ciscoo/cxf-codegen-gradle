/*
 * Copyright 2020 the original author or authors.
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

import org.gradle.api.JavaVersion;
import org.gradle.util.GradleVersion;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * {@link Extension} that runs {@linkplain TestTemplate templated tests} against
 * multiple versions of Gradle for each DSL. Test classes using the extension
 * must have test methods that have a parameter of type {@link GradleBuild}.
 * <p>
 * Heavily based on the Spring Boot extension by Andy Wilkinson.
 */
public final class GradleCompatibilityExtension implements TestTemplateInvocationContextProvider {

	private static List<String> GRADLE_VERSIONS;

	static {
		JavaVersion javaVersion = JavaVersion.current();
		if (javaVersion.isCompatibleWith(JavaVersion.VERSION_15)
				|| javaVersion.isCompatibleWith(JavaVersion.VERSION_14)) {
			GRADLE_VERSIONS = List.of("6.3", "6.4.1", "6.5.1", "6.6.1", "default");
		} else {
			GRADLE_VERSIONS = List.of("5.5.1", "5.6.4", "6.0.1", "6.1.1", "6.2.2", "6.3", "6.4.1", "6.5.1", "6.6.1",
					"default");
		}
	}

	public GradleCompatibilityExtension() {
	}

	public GradleCompatibilityExtension(String... versions) {
		withVersions(versions);
	}

	static void withVersions(String... versions) {
		GRADLE_VERSIONS = List.of(versions);
	}

	@Override
	public boolean supportsTestTemplate(ExtensionContext context) {
		return true;
	}

	@Override
	public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
		List<GradleVersionDsl> versionToDsl = new ArrayList<>();
		GRADLE_VERSIONS.forEach((version) -> {
			versionToDsl.add(new GradleVersionDsl(version, GradleDsl.GROOVY));
			versionToDsl.add(new GradleVersionDsl(version, GradleDsl.KOTLIN));
		});
		return versionToDsl.stream().map(GradleVersionTestTemplateInvocationContext::new);
	}

	private static final class GradleVersionTestTemplateInvocationContext implements TestTemplateInvocationContext {

		private final GradleVersionDsl gradleVersionDsl;

		GradleVersionTestTemplateInvocationContext(GradleVersionDsl gradleVersionDsl) {
			this.gradleVersionDsl = gradleVersionDsl;
		}

		@Override
		public String getDisplayName(int invocationIndex) {
			if (this.gradleVersionDsl.getVersion().equals("default")) {
				return String.format("Gradle %s - %s ", GradleVersion.current().getVersion(),
						this.gradleVersionDsl.getDsl().getName());
			}
			return String.format("Gradle %s - %s", this.gradleVersionDsl.getVersion(),
					this.gradleVersionDsl.getDsl().getName());
		}

		@Override
		public List<Extension> getAdditionalExtensions() {
			GradleBuild gradleBuild = new GradleBuild().dsl(this.gradleVersionDsl.getDsl());
			if (!this.gradleVersionDsl.getVersion().equals("default")) {
				gradleBuild.gradleVersion(this.gradleVersionDsl.getVersion());
			}
			return List.of(new GradleBuildExtension(gradleBuild));
		}

	}
}