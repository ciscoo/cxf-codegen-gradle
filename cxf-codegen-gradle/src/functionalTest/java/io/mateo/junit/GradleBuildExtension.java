/*
 * Copyright 2020-2022 the original author or authors.
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

import java.net.URL;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * {@link Extension} for managing the lifecycle of a {@link GradleBuild}.
 * <p>
 * Inspired by the Spring Boot extension by Andy Wilkinson.
 */
public class GradleBuildExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

	private final GradleBuild gradleBuild;

	public GradleBuildExtension(GradleBuild gradleBuild) {
		this.gradleBuild = gradleBuild;
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		URL buildScript = getBuildScript(context);
		if (buildScript != null) {
			this.gradleBuild.script(buildScript.getFile());
		}
		this.gradleBuild.before();
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		this.gradleBuild.after();
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		return parameterContext.getParameter().getType() == GradleBuild.class;
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		return this.gradleBuild;
	}

	private URL getBuildScript(ExtensionContext context) {
		String name = String.format("%s%s", context.getRequiredTestMethod().getName(),
				this.gradleBuild.getDsl().getExtension());
		return context.getRequiredTestClass().getResource(name);
	}

}
