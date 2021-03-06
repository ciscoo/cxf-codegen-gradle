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
package io.mateo.junit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.gradle.util.GradleVersion;
import org.junit.jupiter.api.Assertions;
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

	private static final List<String> DEFAULT_GRADLE_VERSIONS = List.of("5.5.1", "5.6.4", "6.0.1", "6.1.1", "6.2.2",
			"6.3", "6.4.1", "6.5.1", "6.6.1", "6.7.1", "6.8.3", "6.9", "7.0.2", "current");

	private final List<String> gradleVersions;

	public GradleCompatibilityExtension() {
		// Attempt to reduce amount of work done in CI.
		// TODO: Drop support for older versions of Gradle.
		if (Boolean.parseBoolean(System.getProperty("firstSix"))) {
			this.gradleVersions = List.of("5.5.1", "5.6.4", "6.0.1", "6.1.1", "6.2.2", "6.3");
		}
		else {
			this.gradleVersions = List.of("6.4.1", "6.5.1", "6.6.1", "6.7.1", "6.8.3", "6.9", "7.0.2", "current");
		}
		System.err.println("Testing against the following Gradle versions " + this.gradleVersions);
		Assertions.assertTrue(DEFAULT_GRADLE_VERSIONS.containsAll(this.gradleVersions),
				"GradleCompatibilityExtension version defaults mismatch");
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

	private static final class GradleVersionTestTemplateInvocationContext implements TestTemplateInvocationContext {

		private final GradleVersionDsl gradleVersionDsl;

		GradleVersionTestTemplateInvocationContext(GradleVersionDsl gradleVersionDsl) {
			this.gradleVersionDsl = gradleVersionDsl;
		}

		@Override
		public String getDisplayName(int invocationIndex) {
			if (this.gradleVersionDsl.getVersion().equals("current")) {
				return String.format("Gradle %s - %s ", GradleVersion.current().getVersion(),
						this.gradleVersionDsl.getDsl().getName());
			}
			return String.format("Gradle %s - %s", this.gradleVersionDsl.getVersion(),
					this.gradleVersionDsl.getDsl().getName());
		}

		@Override
		public List<Extension> getAdditionalExtensions() {
			GradleBuild gradleBuild = new GradleBuild().dsl(this.gradleVersionDsl.getDsl());
			if (!this.gradleVersionDsl.getVersion().equals("current")) {
				gradleBuild.gradleVersion(this.gradleVersionDsl.getVersion());
			}
			return List.of(new GradleBuildExtension(gradleBuild));
		}

	}

}
