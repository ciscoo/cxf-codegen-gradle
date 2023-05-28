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

dependencies {
    cxfCodegen("org.apache.cxf:cxf-rt-databinding-jibx:3.1.18") // <1>
}

// ...

tasks.register("calculator", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(layout.projectDirectory.file("calculator.wsdl").asFile.toPath().toAbsolutePath().toUri().toString())
        extraArgs.addAll(listOf("-databinding", "jibx")) // <2>
    }
}
// end::code[]
