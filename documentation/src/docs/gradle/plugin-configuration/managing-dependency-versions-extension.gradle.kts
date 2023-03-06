plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

// tag::code[]
cxfCodegen {
    cxfVersion.set("3.2.0") // 3.3.0 breaks the build
}
// end::code[]

tasks.register("verify") {
    doLast {
        println("Configured CXF version = ${cxfCodegen.cxfVersion.get()}")
        println(configurations.getByName("cxfCodegen").incoming.dependencies.map { "${it.group}:${it.name}:${it.version}"})
    }
}
