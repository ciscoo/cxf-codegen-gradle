plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

// #region code
cxfCodegen {
    cxfVersion = "{{ previousCxfVersion }}"
}
// #endregion code

tasks.register("verify") {
    doLast {
        println("Configured CXF version = ${cxfCodegen.cxfVersion.get()}")
        println(configurations.getByName("cxfCodegen").incoming.dependencies.map { "${it.group}:${it.name}:${it.version}"})
    }
}
