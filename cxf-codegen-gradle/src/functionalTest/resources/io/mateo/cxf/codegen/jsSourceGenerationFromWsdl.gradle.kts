import io.mateo.cxf.codegen.wsdl2js.Wsdl2Js

plugins {
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

dependencies {
    cxfCodegen("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    cxfCodegen("jakarta.annotation:jakarta.annotation-api:1.3.5")
}

tasks.register("calculator", Wsdl2Js::class) {
    toolOptions {
        wsdl.set(file("wsdls/calculator.wsdl"))
    }
}
