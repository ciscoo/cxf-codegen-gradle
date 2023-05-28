import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    id("io.mateo.cxf-codegen")
    id("io.micronaut.application") version "3.1.1"
}

repositories {
    mavenCentral()
}

application {
    // This does not need to exist for tests purposes.
    mainClass.set("io.mateo.Main")
}

tasks.register("calculator", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(file("wsdls/calculator.wsdl").toPath().toAbsolutePath().toUri().toString())
    }
}
