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
import org.apache.cxf.tools.wsdlto.WSDLToJava;
import org.gradle.api.Incubating;
import org.gradle.api.file.RegularFile;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.workers.WorkAction;

/**
 * An action to run the {@code wsdl2java} tool.
 */
@Incubating
public abstract class Wsdl2JavaAction implements WorkAction<CodegenParameters> {

    private static final Logger logger = Logging.getLogger(Wsdl2JavaAction.class);

    @Override
    public void execute() {
        List<String> arguments = getArguments();
        if (logger.isInfoEnabled()) {
            logger.info("Invoking wsdl2java tool with arguments: {}", arguments);
        }
        ToolContext context = new ToolContext();
        try {
            new WSDLToJava(arguments.toArray(String[]::new)).run(context);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException rex) {
                throw rex;
            }
            throw new RuntimeException(ex);
        }
    }

    List<String> getArguments() { // package private for testing
        Wsdl2JavaOption option = (Wsdl2JavaOption) getParameters().getOption().get();
        List<String> arguments = new ArrayList<>();
        if (option.getEncoding().isPresent()) {
            arguments.add("-encoding");
            arguments.add(option.getEncoding().get());
        }
        if (option.getPackageNames().isPresent()) {
            option.getPackageNames().get().forEach(value -> {
                arguments.add("-p");
                arguments.add(value);
            });
        }
        if (option.getNamespaceExcludes().isPresent()) {
            option.getNamespaceExcludes().get().forEach(value -> {
                arguments.add("-nexclude");
                arguments.add(value);
            });
        }
        arguments.add("-d");
        String outputDir = option.getOutputDirectory().getAsFile().get().getAbsolutePath();
        arguments.add(outputDir);
        if (option.getBindingFiles().isPresent()) {
            option.getBindingFiles().get().forEach(binding -> {
                RegularFile bindingFile =
                        getParameters().getProjectDirectory().get().file(binding);
                arguments.add("-b");
                arguments.add(bindingFile.getAsFile().toPath().toAbsolutePath().toString());
            });
        }
        if (option.getFrontend().isPresent()) {
            arguments.add("-fe");
            arguments.add(option.getFrontend().get());
        }
        if (option.getDatabinding().isPresent()) {
            arguments.add("-db");
            arguments.add(option.getDatabinding().get());
        }
        if (option.getWsdlVersion().isPresent()) {
            arguments.add("-wv");
            arguments.add(option.getWsdlVersion().get());
        }
        if (option.getCatalog().isPresent()) {
            arguments.add("-catalog");
            arguments.add(option.getCatalog().get());
        }
        if (option.getExtendedSoapHeaders().isPresent()
                && option.getExtendedSoapHeaders().get()) {
            arguments.add("-exsh");
            arguments.add("true");
        }
        if (option.getNoTypes().isPresent() && option.getNoTypes().get()) {
            arguments.add("-noTypes");
        }
        if (option.getAllowElementRefs().isPresent()
                && option.getAllowElementRefs().get()) {
            arguments.add("-allowElementReferences");
        }
        if (option.getValidateWsdl().isPresent()) {
            arguments.add("-validate=" + option.getValidateWsdl().get());
        }
        if (option.getMarkGenerated().isPresent() && option.getMarkGenerated().get()) {
            arguments.add("-mark-generated");
        }
        if (option.getSuppressGeneratedDate().isPresent()
                && option.getSuppressGeneratedDate().get()) {
            arguments.add("-suppress-generated-date");
        }
        if (option.getDefaultExcludesNamespace().isPresent()) {
            arguments.add("-dex");
            arguments.add(option.getDefaultExcludesNamespace().get().toString());
        }
        if (option.getDefaultNamespacePackageMapping().isPresent()) {
            arguments.add("-dns");
            arguments.add(option.getDefaultNamespacePackageMapping().get().toString());
        }
        if (option.getServiceName().isPresent()) {
            arguments.add("-sn");
            arguments.add(option.getServiceName().get());
        }
        if (option.getFaultSerialVersionUid().isPresent()) {
            arguments.add("-faultSerialVersionUID");
            arguments.add(option.getFaultSerialVersionUid().get());
        }
        if (option.getExceptionSuper().isPresent()) {
            arguments.add("-exceptionSuper");
            arguments.add(option.getExceptionSuper().get());
        }
        if (option.getSeiSuper().isPresent()) {
            option.getSeiSuper().get().forEach(value -> {
                arguments.add("-seiSuper");
                arguments.add(value);
            });
        }
        if (option.getAutoNameResolution().isPresent()
                && option.getAutoNameResolution().get()) {
            arguments.add("-autoNameResolution");
        }
        if (option.getNoAddressBinding().isPresent()
                && option.getNoAddressBinding().get()) {
            arguments.add("-noAddressBinding");
        }
        if (option.getXjcArgs().isPresent()) {
            option.getXjcArgs().get().forEach(value -> arguments.add("-xjc" + value));
        }
        if (option.getExtraArgs().isPresent()) {
            arguments.addAll(option.getExtraArgs().get());
        }
        if (option.getWsdlLocation().isPresent()) {
            arguments.add("-wsdlLocation");
            arguments.add(option.getWsdlLocation().get());
        }
        if (option.getWsdlList().isPresent() && option.getWsdlList().get()) {
            arguments.add("-wsdlList");
        }
        if (option.getVerbose().isPresent() && option.getVerbose().get()) {
            arguments.add("-verbose");
        }
        if (option.getAsyncMethods().isPresent()) {
            List<String> asyncMethods = option.getAsyncMethods().get();
            if (!asyncMethods.isEmpty()) {
                arguments.add("-asyncMethods=" + String.join(",", asyncMethods));
            }
        }
        if (option.getBareMethods().isPresent()) {
            List<String> bareMethods = option.getBareMethods().get();
            if (!bareMethods.isEmpty()) {
                StringBuilder sb = new StringBuilder("-bareMethods");
                sb.append("=");
                boolean first = true;
                for (String value : bareMethods) {
                    if (!first) {
                        sb.append(",");
                    }
                    sb.append(value);
                    first = false;
                }
                arguments.add(sb.toString());
            }
        }
        if (option.getMimeMethods().isPresent()) {
            List<String> mimeMethods = option.getMimeMethods().get();
            if (!mimeMethods.isEmpty()) {
                StringBuilder sb = new StringBuilder("-mimeMethods");
                sb.append("=");
                boolean first = true;
                for (String value : mimeMethods) {
                    if (!first) {
                        sb.append(",");
                    }
                    sb.append(value);
                    first = false;
                }

                arguments.add(sb.toString());
            }
        }
        arguments.add(option.getWsdl().get());
        return List.copyOf(arguments);
    }
}
