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
package io.mateo.cxf.codegen.workers;

import java.util.ArrayList;
import java.util.List;
import org.apache.cxf.tools.common.ToolContext;
import org.apache.cxf.tools.wsdlto.javascript.WSDLToJavaScript;
import org.gradle.api.Incubating;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.workers.WorkAction;

/**
 * An action to run the {@code wsdl2js} tool.
 */
@Incubating
public abstract class Wsdl2JsAction implements WorkAction<CodegenParameters> {

    private static final Logger logger = Logging.getLogger(Wsdl2JsAction.class);

    @Override
    public void execute() {
        List<String> arguments = getArguments();
        if (logger.isInfoEnabled()) {
            logger.info("Invoking wsdl2js tool with arguments: {}", arguments);
        }
        ToolContext context = new ToolContext();
        try {
            new WSDLToJavaScript(arguments.toArray(String[]::new)).run(context);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException rex) {
                throw rex;
            }
            throw new RuntimeException(ex);
        }
    }

    List<String> getArguments() { // package private for testing
        Wsdl2JsOption option = (Wsdl2JsOption) getParameters().getOption().get();
        List<String> arguments = new ArrayList<>();
        if (option.getWsdlVersion().isPresent()) {
            arguments.add("-wv");
            arguments.add(option.getWsdlVersion().get());
        }
        if (option.getPackagePrefixes().isPresent()) {
            for (UriPrefixPair uriPrefixPair : option.getPackagePrefixes().get()) {
                arguments.add("-p");
                arguments.add("%s=%s".formatted(uriPrefixPair.getPrefix(), uriPrefixPair.getUri()));
            }
        }
        if (option.getCatalog().isPresent()) {
            arguments.add("-catalog");
            arguments.add(option.getCatalog()
                    .map(it -> it.getAsFile().toPath().toAbsolutePath().toString())
                    .get());
        }
        arguments.add("-d");
        arguments.add(option.getOutputDirectory().get().getAsFile().getAbsolutePath());
        if (option.getValidate().isPresent()) {
            arguments.add("-validate=" + option.getValidate().get());
        }
        if (option.getVerbose().isPresent() && option.getQuiet().isPresent()) {
            throw new InvalidUserDataException(
                    "Verbose and quite are mutually exclusive; only one can be enabled, not both.");
        }
        if (option.getVerbose().isPresent() && option.getVerbose().get()) {
            arguments.add("-verbose");
        }
        if (option.getQuiet().isPresent() && option.getQuiet().get()) {
            arguments.add("-quiet");
        }
        arguments.add(option.getWsdl().get());
        return arguments;
    }
}
