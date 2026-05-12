// #region code
import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java
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

tasks.register("calculator", Wsdl2Java::class) {
    toolOptions {
        wsdl = layout.projectDirectory.file("calculator.wsdl").asFile.toPath().toAbsolutePath().toString()
        serviceName = "Calculator"
    }
}
// #endregion code
