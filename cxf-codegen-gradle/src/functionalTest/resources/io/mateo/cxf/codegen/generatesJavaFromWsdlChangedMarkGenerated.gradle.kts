plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

cxfCodegen {
    wsdl2java {
        register("calculator") {
            wsdl.set(file("wsdls/calculator.wsdl").toPath().toAbsolutePath().toUri().toString())
            markGenerated.set(true)
        }
    }
}
