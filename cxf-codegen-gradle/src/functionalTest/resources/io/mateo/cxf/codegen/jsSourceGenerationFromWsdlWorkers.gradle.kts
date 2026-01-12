import io.mateo.cxf.codegen.workers.Wsdl2JsOption

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

cxfCodegen {
    options {
        register<Wsdl2JsOption>("calculator") {
            wsdl = layout.projectDirectory.file("wsdls/calculator.wsdl").asFile.toPath().toAbsolutePath().toString()
        }
    }
}
