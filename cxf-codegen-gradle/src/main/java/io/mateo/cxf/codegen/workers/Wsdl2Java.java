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
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Incubating;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.JavaForkOptions;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;
import org.jspecify.annotations.Nullable;

/**
 * Generates Java sources from WSDL files using the Apache CXF {@code wsdl2java} tool.
 * Code generation is performed in an isolated worker process for each WSDL file.
 */
@Incubating
@CacheableTask
public abstract class Wsdl2Java extends DefaultTask {

    private @Nullable List<Action<? super JavaForkOptions>> forkOptionsActions;

    /**
     * Classpath for the {@code wsdl2java} code generation tool.
     * @return the classpath
     */
    @Classpath
    public abstract ConfigurableFileCollection getWsdl2JavaClasspath();

    /**
     * List of options for the {@code wsdl2java} tool.
     * @return the options
     */
    @Nested
    public abstract ListProperty<Wsdl2JavaOption> getOptions();

    /**
     * Returns the list of actions to configure the Java fork options used when executing the {@code wsdl2java} tool.
     * @return the actions, never null.
     */
    @Internal
    public List<Action<? super JavaForkOptions>> getForkOptionsActions() {
        return this.forkOptionsActions != null ? this.forkOptionsActions : List.of();
    }

    /**
     * Adds an action to configure the Java fork options used when executing the {@code wsdl2java} tool.
     * @param action the action to configure the fork options
     */
    public void forkOptions(Action<? super JavaForkOptions> action) {
        if (this.forkOptionsActions == null) {
            this.forkOptionsActions = new ArrayList<>();
        }
        this.forkOptionsActions.add(action);
    }

    @Inject
    public abstract WorkerExecutor getWorkerExecutor();

    @Inject
    public abstract ProjectLayout getLayout();

    @TaskAction
    public void generate() {
        WorkQueue workQueue = getWorkerExecutor().processIsolation(spec -> {
            spec.getClasspath().from(getWsdl2JavaClasspath());
            for (Action<? super JavaForkOptions> action : getForkOptionsActions()) {
                spec.forkOptions(action);
            }
        });
        for (Wsdl2JavaOption option : getOptions().get()) {
            workQueue.submit(Wsdl2JavaAction.class, params -> {
                params.getOption().set(option);
                params.getProjectDirectory().set(getLayout().getProjectDirectory());
            });
        }
    }
}
