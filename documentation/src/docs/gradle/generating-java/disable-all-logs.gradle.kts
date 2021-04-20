import io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

dependencies {
    cxfCodegen("ch.qos.logback:logback-classic:1.2.3")
    cxfCodegen("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    cxfCodegen("jakarta.annotation:jakarta.annotation-api:1.3.5")
}

cxfCodegen {
    wsdl2java {
        register("example") {
            wsdl.set(file("example.wsdl"))
        }
    }
}

//tag::code[]
tasks.withType(Wsdl2JavaTask::class).configureEach {
    jvmArgs = listOf("-Dlogback.configurationFile=logback.xml")
}
//end::code[]
