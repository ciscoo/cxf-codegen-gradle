// #region code
import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java
// #endregion code

plugins {
    java
    id("io.mateo.cxf-codegen")
}

// #region code

// ...

tasks.register<Wsdl2Java>("example") {
    toolOptions {
        wsdl = file("path/to/example.wsdl").absolutePath
    }
}
// #endregion code

tasks.register("verify") {
    doLast {
        println(tasks.getByName("example", Wsdl2Java::class).wsdl2JavaOptions.wsdl.get())
    }
}
