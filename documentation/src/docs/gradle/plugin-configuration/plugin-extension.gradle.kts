plugins {
    java
    id("io.mateo.cxf-codegen")
}

// tag::code[]
cxfCodegen { // <1>
    wsdl2java { // <2>

    }
}
// end::code[]