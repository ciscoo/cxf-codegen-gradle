plugins {
    java
    id("io.mateo.cxf-codegen")
}

cxfCodegen {
    wsdl2java {
        register("calculator")
    }
}
