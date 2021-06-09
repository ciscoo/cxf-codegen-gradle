plugins {
    id("io.mateo.cxf-codegen") version "1.0.0-rc.3"
    id("io.micronaut.application") version "9.9.9-SNAPSHOT"
}

repositories {
    mavenCentral()
    // https://github.com/grails/grails-core/issues/11825
    maven {
        url = uri("https://repo.grails.org/artifactory/core")
    }
}

dependencies {
    cxfCodegen("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    cxfCodegen("jakarta.annotation:jakarta.annotation-api:1.3.5")
}

application {
    mainClass.set("com.example.Example")
}

cxfCodegen {
    wsdl2java {
        register("gps") {
            wsdl.set(file("wsdls/calculator.wsdl"))
        }
    }
}
