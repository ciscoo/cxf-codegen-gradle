plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

// tag::code[]
dependencies {
    cxfCodegen("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3") // <1>
    cxfCodegen("jakarta.annotation:jakarta.annotation-api:1.3.5") // <2>
}
// end::code[]

tasks.register("verify") {
    doLast {
        println(configurations.cxfCodegen.get().dependencies.map { it.name }.toList())
    }
}
