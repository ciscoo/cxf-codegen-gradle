import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    java
    id("io.mateo.cxf-codegen")
}

// tag::code[]
repositories {
    mavenCentral()
    maven { // <1>
        url = uri("https://repository.apache.org/content/groups/snapshots-group/")
        content {
            includeGroup("org.apache.cxf")
        }
    }
}

dependencies {
    cxfCodegen(platform("org.apache.cxf:cxf-bom:4.0.0-SNAPSHOT")) // <2>
    cxfCodegen("jakarta.xml.ws:jakarta.xml.ws-api:4.0.0") // <3>
    cxfCodegen("jakarta.annotation:jakarta.annotation-api:2.1.1")
}
// end::code[]

tasks.register("calculator", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(file("calculator.wsdl"))
    }
}
