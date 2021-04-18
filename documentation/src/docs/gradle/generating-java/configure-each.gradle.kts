plugins {
    java
    id("io.mateo.cxf-codegen")
}

cxfCodegen {
    wsdl2java {
        register("example") {
            wsdl.set(file("example.wsdl"))
        }
        register("anotherExample") {
            wsdl.set(file("another-example.wsdl"))
        }
    }
}

//tag::code[]
cxfCodegen {
    wsdl2java.configureEach {
        markGenerated.set(true)
    }
}
//end::code[]

tasks.register("verify") {
    doLast {
        cxfCodegen.wsdl2java.all {
            println("${name} markGenerated ${markGenerated.get()}")
        }
    }
}