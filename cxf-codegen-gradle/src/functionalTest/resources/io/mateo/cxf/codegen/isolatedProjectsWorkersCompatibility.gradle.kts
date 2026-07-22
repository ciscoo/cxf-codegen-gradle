import io.mateo.cxf.codegen.workers.Wsdl2JavaOption

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

cxfCodegen {
    options {
        register<Wsdl2JavaOption>("calculator") {
            wsdl = file("wsdls/calculator.wsdl").toPath().toAbsolutePath().toString()
            bindingFiles.add(file("async-binding.xml").toPath().toAbsolutePath().toString())
        }
    }
}
