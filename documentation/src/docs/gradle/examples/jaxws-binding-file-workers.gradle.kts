// #region code
import io.mateo.cxf.codegen.workers.Wsdl2JavaOption
// #endregion code

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

// #region code

// ...

cxfCodegen {
    options {
        register<Wsdl2JavaOption>("calculator") {
            wsdl = file("wsdls/calculator.wsdl").toPath().toAbsolutePath().toString()
            bindingFiles.add(file("async-binding.xml").toPath().toAbsolutePath().toString())
        }
    }
}
// #endregion code
