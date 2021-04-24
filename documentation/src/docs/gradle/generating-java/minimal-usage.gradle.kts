plugins {
    java
    id("io.mateo.cxf-codegen")
}

// tag::code[]
cxfCodegen {
    wsdl2java {
        register("example") {
            wsdl.set(file("path/to/example.wsdl"))
        }
    }
}
// end::code[]

tasks.register("verify") {
    doLast {
        println(cxfCodegen.wsdl2java.getByName("example").wsdl.get())
    }
}
