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

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * A {@code GradleBuild} is used to run a Gradle build using
 * {@link GradleRunner}.
 * <p>
 * Heavily based on the Spring Boot implementation by Andy Wilkinson.
 */
public class GradleBuild {

	private File projectDir;

	private String script;

	private String gradleVersion;

	private GradleDsl dsl;

	void before() {
		try {
			this.projectDir = Files.createTempDirectory("gradle-").toFile();
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	void after() {
		this.script = null;
		try {
			FileUtils.deleteDirectory(this.projectDir);
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	public GradleBuild script(String script) {
		this.script = script;
		return this;
	}

	public BuildResult build(String... arguments) {
		try {
			return prepareRunner(arguments).build();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public BuildResult buildAndFail(String... arguments) {
		try {
			return prepareRunner(arguments).buildAndFail();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public GradleRunner prepareRunner(String... arguments) throws IOException {
		String scriptContent = FileUtils.readFileToString(new File(this.script), StandardCharsets.UTF_8);
		FileUtils.writeStringToFile(new File(this.projectDir, "build%s".formatted(this.dsl.getExtension())),
				scriptContent, StandardCharsets.UTF_8);
		List<String> settingsLines = List.of("startParameter.showStacktrace = ShowStacktrace.ALWAYS_FULL",
				"startParameter.warningMode = WarningMode.All");
		FileUtils.writeLines(new File(this.projectDir, "settings.gradle"), StandardCharsets.UTF_8.name(),
				settingsLines);
		FileUtils.copyDirectoryToDirectory(new File("src/functionalTest/resources/wsdls"), this.projectDir);
		GradleRunner gradleRunner = GradleRunner.create().withProjectDir(this.projectDir).withPluginClasspath();
		gradleRunner.withDebug(true);
		if (this.gradleVersion != null) {
			gradleRunner.withGradleVersion(this.gradleVersion);
		}
		return gradleRunner.withArguments(List.of(arguments));
	}

	public GradleBuild gradleVersion(String version) {
		this.gradleVersion = version;
		return this;
	}

	public GradleBuild dsl(GradleDsl dsl) {
		this.dsl = dsl;
		return this;
	}

	public File getProjectDir() {
		return this.projectDir;
	}

	public GradleDsl getDsl() {
		return this.dsl;
	}
}
