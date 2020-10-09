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
package io.mateo.cxf.codegen

import io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * Gradle plugin for code generation from WSDLs using Apache CXF.
 */
open class CxfCodegenPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(CXF_CODEGEN_EXTENSION_NAME, CxfCodegenExtension::class.java)
        val cxfConfiguration = registerConfiguration(project)
        registerCodegenTasks(project, extension, cxfConfiguration)
        maybeAddToMainSourceSet(project, extension)
        registerAggregateTask(project)
    }

    private fun maybeAddToMainSourceSet(project: Project, extension: CxfCodegenExtension) {
        project.pluginManager.withPlugin("java-base") {
            extension.wsdl2java.all { option ->
                project.extensions.configure(SourceSetContainer::class.java) { sourceSets ->
                    sourceSets.named(SourceSet.MAIN_SOURCE_SET_NAME) { main ->
                        main.java.srcDirs(project.provider { option.outputDir.get().asFile })
                    }
                }
            }
        }
    }

    private fun registerAggregateTask(project: Project) {
        val wsdl2javaTasks = project.tasks.withType(Wsdl2JavaTask::class.java)
        project.tasks.register(WSDL2JAVA_TASK_NAME) {
            it.dependsOn(wsdl2javaTasks)
            it.group = LifecycleBasePlugin.BUILD_GROUP
            it.description = "Runs all wsdl2java tasks"
        }
    }

    private fun registerCodegenTasks(project: Project, extension: CxfCodegenExtension, cxfConfiguration: Configuration) {
        extension.wsdl2java.all { option ->
            project.tasks.register("wsdl2java${option.name.capitalize()}", Wsdl2JavaTask::class.java).configure { task ->
                with(task) {
                    outputs.dir(option.outputDir.get())
                    main = "org.apache.cxf.tools.wsdlto.WSDLToJava"
                    classpath = cxfConfiguration
                    group = LifecycleBasePlugin.BUILD_GROUP
                    description = "Generates Java sources for ${option.name}"
                    val arguments = option.generateArgs()
                    if (project.logger.isInfoEnabled) {
                        project.logger.info("WSDL2Java arguments: $arguments")
                    }
                    args = arguments
                }
            }
        }
    }

    private fun registerConfiguration(project: Project): Configuration {
        return project.configurations.create(CXF_CODEGEN_CONFIGURATION_NAME) {
            with(it) {
                isVisible = false
                isCanBeConsumed = false
                isCanBeResolved = true
                description = "Classpath for CXF Codegen."
                dependencies.addAll(createDependencies(project))
            }
        }
    }

    private fun createDependencies(project: Project): List<Dependency> {
        // Same dependencies defined in cxf-codegen-plugin's POM
        val dependencies = project.dependencies
        return listOf<Dependency>(
                dependencies.create("org.apache.cxf:cxf-core:$DEFAULT_CXF_VERSION"),
                dependencies.create("org.apache.cxf:cxf-tools-common:$DEFAULT_CXF_VERSION"),
                dependencies.create("org.apache.cxf:cxf-tools-wsdlto-core:$DEFAULT_CXF_VERSION"),
                dependencies.create("org.apache.cxf:cxf-tools-wsdlto-databinding-jaxb:$DEFAULT_CXF_VERSION"),
                dependencies.create("org.apache.cxf:cxf-tools-wsdlto-frontend-jaxws:$DEFAULT_CXF_VERSION"),
                dependencies.create("org.apache.cxf:cxf-tools-wsdlto-frontend-javascript:$DEFAULT_CXF_VERSION").apply {
                    this as ModuleDependency
                    exclude(mapOf("group" to "org.apache.cxf", "module" to "cxf-rt-frontend-simple"))
                })
    }

    companion object {

        /**
         * Name of the [Configuration] that contains dependencies for code generation.
         */
        const val CXF_CODEGEN_CONFIGURATION_NAME = "cxfCodegen"

        /**
         * Name of the extension contributed by this plugin.
         */
        const val CXF_CODEGEN_EXTENSION_NAME = "cxfCodegen"

        /**
         * Task name to execute all [Wsdl2JavaTask] tasks.
         */
        const val WSDL2JAVA_TASK_NAME = "wsdl2java"

        private const val DEFAULT_CXF_VERSION = "3.4.0"
    }
}
