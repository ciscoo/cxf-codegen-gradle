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
            wsdl.set(file("wsdls/calculator.wsdl").toPath().toAbsolutePath().toString())
            markGenerated.set(true)
        }
    }
}
