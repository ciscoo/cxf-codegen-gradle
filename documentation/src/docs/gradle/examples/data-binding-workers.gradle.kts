import io.mateo.cxf.codegen.workers.Wsdl2JavaOption

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

// #region code
dependencies {
    cxfCodegen("org.apache.cxf:cxf-rt-databinding-jibx:3.1.18") // <1>
}

cxfCodegen {
    options {
        register<Wsdl2JavaOption>("calculator") {
            wsdl = layout.projectDirectory.file("calculator.wsdl").asFile.toPath().toAbsolutePath().toString()
            extraArgs.addAll(listOf("-databinding", "jibx")) // <2>
        }
    }
}
// #endregion code
