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

// tag::code[]

// ...

dependencies {
    implementation("org.apache.cxf.xjc-utils:cxf-xjc-runtime:4.0.0") // <1>
    cxfCodegen("org.apache.cxf.xjcplugins:cxf-xjc-ts:4.0.0") // <2>
}

tasks.register("calculator", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(layout.projectDirectory.file("calculator.wsdl"))
        xjcArgs.add("-Xts") // <3>
    }
}
// end::code[]
