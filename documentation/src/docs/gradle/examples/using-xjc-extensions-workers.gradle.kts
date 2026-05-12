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

dependencies {
    implementation("org.apache.cxf.xjc-utils:cxf-xjc-runtime:4.0.0") // <1>
    cxfCodegen("org.apache.cxf.xjcplugins:cxf-xjc-ts:4.0.0") // <2>
}

cxfCodegen {
    options {
        register<Wsdl2JavaOption>("calculator") { // <5>
            wsdl = layout.projectDirectory.file("calculator.wsdl").asFile.toPath().toAbsolutePath().toString()
            xjcArgs.add("-Xts") // <3>
        }
    }
}
// #endregion code
