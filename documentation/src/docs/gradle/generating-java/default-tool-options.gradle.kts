import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    java
    id("io.mateo.cxf-codegen")
}

//tag::primary[]
tasks {
    register("first", Wsdl2Java::class) {
        toolOptions {
            wsdl = file("path/to/first.wsdl").toPath().toAbsolutePath().toString()
        }
    }
    register("second", Wsdl2Java::class) {
        toolOptions {
            wsdl = file("path/to/second.wsdl").toPath().toAbsolutePath().toString()
        }
    }
    register("third", Wsdl2Java::class) {
        toolOptions {
            wsdl = file("path/to/third.wsdl").toPath().toAbsolutePath().toString()
        }
    }
}
//end::primary[]

//tag::secondary[]
tasks.withType(Wsdl2Java::class).configureEach {
    toolOptions {
        markGenerated = true
    }
}
//end::secondary[]

tasks.register("verify") {
    doLast {
        tasks.withType(Wsdl2Java::class).all {
            println("$name markGenerated ${wsdl2JavaOptions.markGenerated.get()}")
        }
    }
}
