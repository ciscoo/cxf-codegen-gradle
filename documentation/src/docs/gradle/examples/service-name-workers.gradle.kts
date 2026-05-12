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
            wsdl = layout.projectDirectory.file("calculator.wsdl").asFile.toPath().toAbsolutePath().toString()
            serviceName = "Calculator"
        }
    }
}
// #endregion code
