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

dependencies {
    cxfCodegen("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    cxfCodegen("jakarta.annotation:jakarta.annotation-api:1.3.5")
}

cxfCodegen {
    wsdl2java {
        register("calculator") {
            wsdl.set(file("wsdls/calculator.wsdl"))
            markGenerated.set(false)
        }
    }
}
