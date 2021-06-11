plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

cxfCodegen {
    wsdl2java {
        register("calculator")
    }
}
