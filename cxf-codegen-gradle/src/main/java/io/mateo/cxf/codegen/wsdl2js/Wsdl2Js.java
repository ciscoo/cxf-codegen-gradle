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
package io.mateo.cxf.codegen.wsdl2js;

import java.util.ArrayList;
import java.util.List;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.Nested;
import org.gradle.process.CommandLineArgumentProvider;

/**
 * Generates JavaScript sources from WSDLs.
 */
@CacheableTask
public abstract class Wsdl2Js extends JavaExec {

    public Wsdl2Js() {
        getArgumentProviders().add(new Wsdl2Js.Wsdl2JsArgumentProvider());
    }

    /**
     * Options to configure the {@code wsdl2js} tool.
     * @return tool options
     */
    @Nested
    public abstract Wsdl2JsOptions getWsdl2JsOptions();

    /**
     * Configures the {@code wsdl2js} tool options.
     * @param configurer action or closure to configure tool options
     */
    public void toolOptions(Action<? super Wsdl2JsOptions> configurer) {
        configurer.execute(this.getWsdl2JsOptions());
    }

    private class Wsdl2JsArgumentProvider implements CommandLineArgumentProvider {

        @Override
        public Iterable<String> asArguments() {
            List<String> arguments = new ArrayList<>();
            Wsdl2JsOptions options = Wsdl2Js.this.getWsdl2JsOptions();
            if (options.getWsdlVersion().isPresent()) {
                arguments.add("-wv");
                arguments.add(options.getWsdlVersion().get());
            }
            if (options.getPackagePrefixes().isPresent()) {
                for (Wsdl2JsOptions.UriPrefixPair uriPrefixPair :
                        options.getPackagePrefixes().get()) {
                    arguments.add("-p");
                    arguments.add(String.format("%s=%s", uriPrefixPair.getPrefix(), uriPrefixPair.getUri()));
                }
            }
            if (options.getCatalog().isPresent()) {
                arguments.add("-catalog");
                arguments.add(options.getCatalog()
                        .map(it -> it.getAsFile().toPath().toAbsolutePath().toString())
                        .get());
            }
            arguments.add("-d");
            arguments.add(options.getOutputDir().get().getAsFile().getAbsolutePath());
            if (options.getValidate().isPresent()) {
                arguments.add("-validate=" + options.getValidate().get());
            }
            if (options.getVerbose().isPresent() && options.getQuiet().isPresent()) {
                throw new GradleException(
                        "Verbose and quite are mutually exclusive; only one can be enabled, not both.");
            }
            if (options.getVerbose().isPresent() && options.getVerbose().get()) {
                arguments.add("-verbose");
            }
            if (options.getQuiet().isPresent() && options.getQuiet().get()) {
                arguments.add("-quiet");
            }
            arguments.add(options.getWsdl().get());
            return arguments;
        }
    }
}
