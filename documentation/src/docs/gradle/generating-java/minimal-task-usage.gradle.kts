// tag::code[]
import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java
// end::code[]

plugins {
    java
    id("io.mateo.cxf-codegen")
}

// tag::code[]

// ...

tasks.register("example", Wsdl2Java::class) { // <1>
    toolOptions { // <2>
        wsdl = file("path/to/example.wsdl").toPath().toAbsolutePath().toString() // <3>
    }
    allJvmArgs = listOf("-Duser.language=fr", "-Duser.country=CA") // <4>
}
// end::code[]

tasks.register("verify") {
    doLast {
        println(tasks.getByName("example", Wsdl2Java::class).wsdl2JavaOptions.wsdl.get())
    }
}
