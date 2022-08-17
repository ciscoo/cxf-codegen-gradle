// tag::code[]
import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java
// end::code[]

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

dependencies {
    cxfCodegen("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    cxfCodegen("jakarta.annotation:jakarta.annotation-api:1.3.5")
}

// tag::code[]

// ...

dependencies {
    implementation("org.apache.cxf.xjc-utils:cxf-xjc-runtime:3.3.2") // <1>
    cxfCodegen("org.apache.cxf.xjcplugins:cxf-xjc-ts:3.3.2") // <2>
}

tasks.register("calculator", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(layout.projectDirectory.file("calculator.wsdl"))
        xjcArgs.add("-Xts") // <3>
    }
}
// end::code[]
