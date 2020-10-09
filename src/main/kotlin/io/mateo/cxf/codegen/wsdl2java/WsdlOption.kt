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
package io.mateo.cxf.codegen.wsdl2java

import javax.inject.Inject
import org.gradle.api.GradleException
import org.gradle.api.Named
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

open class WsdlOption @Inject constructor(
    private val name: String,
    objects: ObjectFactory,
    private val layout: ProjectLayout
) : Option, Named {

    override val wsdl: RegularFileProperty = objects.fileProperty()

    override val packageNames: ListProperty<String> = objects.listProperty(String::class.java)

    override val extraArgs: ListProperty<String> = objects.listProperty(String::class.java)

    override val xjcArgs: ListProperty<String> = objects.listProperty(String::class.java)

    override val asyncMethods: ListProperty<String> = objects.listProperty(String::class.java)

    override val bareMethods: ListProperty<String> = objects.listProperty(String::class.java)

    override val mimeMethods: ListProperty<String> = objects.listProperty(String::class.java)

    override val namespaceExcludes: ListProperty<String> = objects.listProperty(String::class.java)

    override val defaultExcludesNamespace: Property<Boolean> = objects.property(Boolean::class.java)

    override val defaultNamespacePackageMapping: Property<Boolean> = objects.property(Boolean::class.java)

    override val bindingFiles: SetProperty<String> = objects.setProperty(String::class.java)

    override val wsdlLocation: Property<String> = objects.property(String::class.java)

    override val wsdlList: Property<Boolean> = objects.property(Boolean::class.java)

    override val frontend: Property<String> = objects.property(String::class.java)

    override val databinding: Property<String> = objects.property(String::class.java)

    override val wsdlVersion: Property<String> = objects.property(String::class.java)

    override val catalog: Property<String> = objects.property(String::class.java)

    override val extendedSoapHeaders: Property<Boolean> = objects.property(Boolean::class.java)

    override val validateWsdl: Property<String> = objects.property(String::class.java)

    override val noTypes: Property<Boolean> = objects.property(Boolean::class.java)

    override val faultSerialVersionUid: Property<String> = objects.property(String::class.java)

    override val exceptionSuper: Property<String> = objects.property(String::class.java)

    override val seiSuper: ListProperty<String> = objects.listProperty(String::class.java)

    override val markGenerated: Property<Boolean> = objects.property(Boolean::class.java)

    override val suppressGeneratedData: Property<Boolean> = objects.property(Boolean::class.java)

    override val serviceName: Property<String> = objects.property(String::class.java)

    override val autoNameResolution: Property<Boolean> = objects.property(Boolean::class.java)

    override val noAddressBinding: Property<Boolean> = objects.property(Boolean::class.java)

    override val allowElementRefs: Property<Boolean> = objects.property(Boolean::class.java)

    override val verbose: Property<Boolean> = objects.property(Boolean::class.java)

    override val outputDir: DirectoryProperty = objects.directoryProperty().convention(layout.buildDirectory.dir("/generated-sources"))

    override fun getName() = name

    override fun toString() = StringBuilder(128).apply {
        append("WSDL: ").append(wsdl.get().asFile.toURI()).append('\n')
        append("OutputDir: ").append(outputDir.get().asFile.absolutePath).append('\n')
        append("Extraargs: ").append(extraArgs.get()).append('\n').append('\n')
        append("XJCargs: ").append(xjcArgs.get()).append('\n')
        append("Packagenames: ").append(packageNames.get()).append('\n')
    }.toString()

    fun generateArgs(): List<String> {
        val command = mutableListOf<String>()
        if (packageNames.isPresent) {
            packageNames.get().forEach {
                command.add("-p")
                command.add(it)
            }
        }
        if (namespaceExcludes.isPresent) {
            namespaceExcludes.get().forEach {
                command.add("-nexclude")
                command.add(it)
            }
        }
        command.add("-d")
        command.add(outputDir.get().asFile.absolutePath) // always set by convention
        if (bindingFiles.isPresent) {
            bindingFiles.get().forEach {
                val bindingFile = layout.projectDirectory.file(it)
                command.add("-b")
                command.add(bindingFile.asFile.toURI().toString())
            }
        }
        if (frontend.isPresent) {
            command.add("-fe")
            command.add(frontend.get())
        }
        if (databinding.isPresent) {
            command.add("-db")
            command.add(databinding.get())
        }
        if (wsdlVersion.isPresent) {
            command.add("-wv")
            command.add(wsdlVersion.get())
        }
        if (catalog.isPresent) {
            command.add("-catalog")
            command.add(catalog.get())
        }
        if (extendedSoapHeaders.isPresent && extendedSoapHeaders.get()) {
            command.add("-exsh")
            command.add("true")
        }
        if (noTypes.isPresent && noTypes.get()) {
            command.add("-noTypes")
        }
        if (allowElementRefs.isPresent && allowElementRefs.get()) {
            command.add("-allowElementReferences")
        }
        if (validateWsdl.isPresent) {
            command.add("-validate=${validateWsdl.get()}")
        }
        if (markGenerated.isPresent && markGenerated.get()) {
            command.add("-mark-generated")
        }
        if (suppressGeneratedData.isPresent && suppressGeneratedData.get()) {
            command.add("-suppress-generated-date")
        }
        if (defaultExcludesNamespace.isPresent) {
            command.add("-dex")
            command.add(defaultExcludesNamespace.get().toString())
        }
        if (defaultNamespacePackageMapping.isPresent) {
            command.add("-dns")
            command.add(defaultNamespacePackageMapping.get().toString())
        }
        if (serviceName.isPresent) {
            command.add("-sn")
            command.add(serviceName.get())
        }
        if (faultSerialVersionUid.isPresent) {
            command.add("-faultSerialVersionUID")
            command.add(faultSerialVersionUid.get())
        }
        if (exceptionSuper.isPresent) {
            command.add("-exceptionSuper")
            command.add(exceptionSuper.get())
        }
        if (seiSuper.isPresent) {
            seiSuper.get().forEach {
                command.add("-seiSuper")
                command.add(it)
            }
        }
        if (autoNameResolution.isPresent && autoNameResolution.get()) {
            command.add("-autoNameResolution")
        }
        if (noAddressBinding.isPresent && noAddressBinding.get()) {
            command.add("-noAddressBinding")
        }
        if (xjcArgs.isPresent) {
            xjcArgs.get().forEach {
                command.add("-xjc$it")
            }
        }
        if (extraArgs.isPresent) {
            command.addAll(extraArgs.get())
        }
        if (wsdlLocation.isPresent) {
            command.add("-wsdlLocation")
            command.add(wsdlLocation.get())
        }
        if (wsdlList.isPresent && wsdlList.get()) {
            command.add("-wsdlList")
        }
        if (verbose.isPresent && verbose.get()) {
            command.add("-verbose")
        }
        if (asyncMethods.isPresent && asyncMethods.get().isNotEmpty()) {
            val sb = StringBuilder("-asyncMethods").apply {
                append("=")
                var first = true
                for (value in asyncMethods.get()) {
                    if (!first) {
                        append(",")
                    }
                    append(value)
                    first = false
                }
            }
            command.add(sb.toString())
        }
        if (bareMethods.isPresent && bareMethods.get().isNotEmpty()) {
            val sb = StringBuilder("-bareMethods").apply {
                append("=")
                var first = true
                for (value in bareMethods.get()) {
                    if (!first) {
                        append(",")
                    }
                    append(value)
                    first = false
                }
            }
            command.add(sb.toString())
        }
        if (mimeMethods.isPresent && mimeMethods.get().isNotEmpty()) {
            val sb = StringBuilder("-mimeMethods").apply {
                append("=")
                var first = true
                for (value in mimeMethods.get()) {
                    if (!first) {
                        append(",")
                    }
                    append(value)
                    first = false
                }
            }
            command.add(sb.toString())
        }
        if (!wsdl.isPresent) {
            throw GradleException("'wsdl' property is not present")
        }
        command.add(wsdl.get().asFile.toURI().toString())
        return command
    }
}
