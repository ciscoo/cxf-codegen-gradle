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
            wsdl.set(file("wsdls/calculatorCopy.wsdl"))
            markGenerated.set(false)
        }
    }
}
