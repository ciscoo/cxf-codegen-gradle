import io.mateo.cxf.codegen.workers.Wsdl2JavaOption

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

val initialSize = sourceSets.getByName("main", SourceSet::class).java.srcDirs.size

cxfCodegen {
    addToMainSourceSet = false
    options {
        register<Wsdl2JavaOption>("calculator") {
            wsdl = file("wsdls/calculator.wsdl").toPath().toAbsolutePath().toString()
        }
    }
}

afterEvaluate {
    println("Main source set size: ${sourceSets.getByName("main", SourceSet::class).java.srcDirs.size}")
}
