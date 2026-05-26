// #region code
import io.mateo.cxf.codegen.workers.Wsdl2JsOption
// #endregion code

plugins {
    id("io.mateo.cxf-codegen")
}

// #region code

// ...

cxfCodegen {
    options {
        register<Wsdl2JsOption>("example") {
            wsdl = file("src/main/resources/wsdl/example.wsdl").absolutePath
        }
    }
}
// #endregion code

tasks.register("verify") {
    doLast {
        println((cxfCodegen.options.getByName("example") as Wsdl2JsOption).wsdl.get())
    }
}
