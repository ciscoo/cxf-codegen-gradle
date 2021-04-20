plugins {
    java
    id("io.mateo.cxf-codegen")
}

//tag::primary[]
cxfCodegen {
    wsdl2java {
        register("first") {
            wsdl.set(file("path/to/first.wsdl"))
        }
        register("second") {
            wsdl.set(file("path/to/second.wsdl"))
        }
        register("third") {
            wsdl.set(file("path/to/third.wsdl"))
        }
    }
}
//end::primary[]

//tag::secondary[]
cxfCodegen {
    wsdl2java.configureEach {
        markGenerated.set(true)
    }
}
//end::secondary[]

tasks.register("verify") {
    doLast {
        cxfCodegen.wsdl2java.all {
            println("${name} markGenerated ${markGenerated.get()}")
        }
    }
}
