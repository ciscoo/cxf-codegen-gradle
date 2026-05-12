plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

// #region code
cxfCodegen { // <1>

}
// #endregion code

tasks.register("verify") {
    doLast {
        println("Configured CXF version = ${cxfCodegen.cxfVersion.get()}")
    }
}
