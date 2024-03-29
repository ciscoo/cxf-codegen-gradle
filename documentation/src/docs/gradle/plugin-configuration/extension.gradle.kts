plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

// tag::code[]
cxfCodegen { // <1>

}
// end::code[]

tasks.register("verify") {
    doLast {
        println("Configured CXF version = ${cxfCodegen.cxfVersion.get()}")
    }
}
