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

dependencies {
    cxfCodegen("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    cxfCodegen("jakarta.annotation:jakarta.annotation-api:1.3.5")
}

// tag::code[]

// ...

tasks.register("calculator", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(layout.projectDirectory.file("calculator.wsdl"))
        bindingFiles.add(layout.projectDirectory.file("async-binding.xml").asFile.absolutePath) // <1>
    }
}
// end::code[]
