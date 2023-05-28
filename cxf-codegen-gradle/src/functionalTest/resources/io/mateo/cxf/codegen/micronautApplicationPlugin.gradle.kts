plugins {
    id("io.mateo.cxf-codegen")
    id("io.micronaut.application") version "1.5.1"
}

repositories {
    mavenCentral()
    // https://github.com/grails/grails-core/issues/11825
    maven {
        url = uri("https://repo.grails.org/grails/core")
    }
}

application {
    // This does not need to exist for tests purposes.
    mainClass.set("io.mateo.Main")
}

cxfCodegen {
    wsdl2java {
        register("calculator") {
            wsdl.set(file("wsdls/calculator.wsdl").toPath().toAbsolutePath().toUri().toString())
            markGenerated.set(false)
        }
    }
}
