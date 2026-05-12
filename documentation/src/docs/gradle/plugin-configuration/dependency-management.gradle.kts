import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

// #region code
dependencies {
    cxfCodegen("org.apache.cxf:cxf-rt-databinding-jibx:3.1.18")
}
// #endregion code

tasks.register("calculator", Wsdl2Java::class) {
    toolOptions {
        wsdl = layout.projectDirectory.file("calculator.wsdl").asFile.toPath().toAbsolutePath().toString()
        extraArgs.addAll(listOf("-databinding", "jibx"))
    }
}
