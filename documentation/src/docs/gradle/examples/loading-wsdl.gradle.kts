// tag::code[]
import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java
// end::code[]

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

// tag::code[]

val myConfiguration: Configuration by configurations.creating // <1>

dependencies {
    myConfiguration("org.apache.cxf:cxf-testutils:3.5.3") // <2>
}

val copyArtifact = tasks.register("copyArtifact", Copy::class) { // <3>
    from(myConfiguration) {
        include {
            it.name.startsWith("cxf-testutils")
        }
    }
    into(layout.buildDirectory.dir("copied-artifact"))
}

val extractWsdl = tasks.register("extractWsdl") { // <4>
    val wsdlFile = layout.buildDirectory.file("extracted.wsdl")
    inputs.files(copyArtifact)
    outputs.file(wsdlFile)
    doLast {
        // Assumes single JAR artifact from previous task
        val archive = inputs.files.singleFile.walk().filter { it.isFile }.single()
        val textResource = resources.text.fromArchiveEntry(archive, "wsdl/calculator.wsdl")
        val f = wsdlFile.get().asFile
        f.createNewFile()
        f.writeText(textResource.asString())
    }
}

tasks.register("calculator", Wsdl2Java::class) { // <5>
    inputs.files(extractWsdl)
    toolOptions {
        wsdl.set(extractWsdl.map { it.outputs.files.singleFile.toPath().toAbsolutePath().toUri().toString() })
    }
}
// end::code[]
