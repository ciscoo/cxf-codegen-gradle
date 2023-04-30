import io.mateo.cxf.codegen.wsdl2js.Wsdl2Js

plugins {
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

tasks.register("calculator", Wsdl2Js::class) {
    toolOptions {
        wsdl.set(layout.projectDirectory.file("wsdls/calculator.wsdl").asFile.absolutePath)
    }
}
