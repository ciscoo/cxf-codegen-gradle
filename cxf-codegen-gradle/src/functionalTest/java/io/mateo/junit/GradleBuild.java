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
package io.mateo.junit;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;

/**
 * A {@code GradleBuild} is used to run a Gradle build using {@link GradleRunner}.
 * <p>
 * Heavily based on the Spring Boot implementation by Andy Wilkinson.
 */
public class GradleBuild {

	private Path projectDir;

	private Path script;

	private Path settings;

	private String gradleVersion;

	private GradleDsl dsl;

	void before() {
		try {
			this.projectDir = Files.createTempDirectory("gradle-");
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	void after() {
		this.script = null;
		try {
			deleteDirectory(this.projectDir);
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	public GradleBuild script(Path script) {
		this.script = script;
		if (!script.toString().endsWith(this.dsl.getExtension())) {
			this.script = script.resolveSibling(script.getFileName() + this.dsl.getExtension());
		}
		return this;
	}

	public GradleBuild settings(Path settings) {
		this.settings = settings;
		if (!settings.endsWith(this.dsl.getExtension())) {
			this.settings = settings.resolveSibling(settings.getFileName() + this.dsl.getExtension());
		}
		return this;
	}

	public BuildResult build(String... arguments) {
		try {
			return prepareRunner(arguments).build();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public BuildResult buildAndFail(String... arguments) {
		try {
			return prepareRunner(arguments).buildAndFail();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public GradleRunner prepareRunner(String... arguments) {
		String scriptContent;
		try {
			scriptContent = Files.readString(this.script, StandardCharsets.UTF_8);
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}

		try {
			Files.writeString(this.projectDir.resolve("build" + this.dsl.getExtension()), scriptContent,
					StandardCharsets.UTF_8);
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}

		Path settingsContent = getSettingsContent();

		try {
			Files.copy(settingsContent, this.projectDir.resolve(settingsContent.getFileName()),
					StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
			final Path functionalTestResources = Path.of("src", "functionalTest", "resources");
			copyDirectoryToDirectory(functionalTestResources.resolve("wsdls"), this.projectDir);
			copyDirectory(functionalTestResources.resolve("test-support"), this.projectDir);
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
		// https://github.com/gradle/gradle/issues/23348
		GradleRunner gradleRunner = GradleRunner.create()
			.withProjectDir(this.projectDir.toFile())
			.withPluginClasspath();
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

	public Path getProjectDir() {
		return this.projectDir;
	}

	public GradleDsl getDsl() {
		return this.dsl;
	}

	private void copyDirectory(Path source, Path target) throws IOException {
		Files.walkFileTree(source, new SimpleFileVisitor<>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Objects.requireNonNull(file);
				Files.copy(file, target.resolve(file.getFileName()), StandardCopyOption.COPY_ATTRIBUTES,
						StandardCopyOption.REPLACE_EXISTING);
				return FileVisitResult.CONTINUE;
			}

		});

	}

	private void copyDirectoryToDirectory(Path source, Path target) throws IOException {
		Files.walkFileTree(source, new SimpleFileVisitor<>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				Files.createDirectories(target.resolve(dir.getFileName()));
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Objects.requireNonNull(file);
				Files.copy(file, target.resolve(file.getParent().getFileName().resolve(file.getFileName())),
						StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private void deleteDirectory(Path target) throws IOException {
		Files.walkFileTree(target, new SimpleFileVisitor<>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(Objects.requireNonNull(file));
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(Objects.requireNonNull(dir));
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private Path getSettingsContent() {
		Path scriptPathName = this.settings != null ? this.settings : Path.of("settings" + this.dsl.getExtension());
		Path parentPath = Path.of("").toAbsolutePath();
		return parentPath.resolve(Path.of("src", "functionalTest", "resources", "io", "mateo", "cxf", "codegen"))
			.resolve(scriptPathName);
	}

}
