import io.mateo.cxf.codegen.workers.Wsdl2JavaOption

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

// #region code
cxfCodegen {
    addToMainSourceSet = false
// #endregion code
    options {
        register<Wsdl2JavaOption>("example") {
            wsdl = file("src/main/resources/wsdl/example.wsdl").absolutePath
        }
    }
// #region code
}
// #endregion code

afterEvaluate {
    println("Main source set size: ${sourceSets.getByName("main", SourceSet::class).java.srcDirs.size}")
}
