// tag::code[]
import io.mateo.cxf.codegen.wsdl2js.Wsdl2Js
// end::code[]

plugins {
    id("io.mateo.cxf-codegen")
}

// tag::code[]

// ...

tasks.register("example", Wsdl2Js::class) { // <1>
    toolOptions { // <2>
        wsdl.set(file("path/to/example.wsdl")) // <3>
    }
}
// end::code[]

tasks.register("verify") {
    doLast {
        println(tasks.getByName("example", Wsdl2Js::class).wsdl2JsOptions.wsdl.get())
    }
}
