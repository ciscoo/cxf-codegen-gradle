import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

val initialSize = sourceSets.getByName("main", SourceSet::class).java.srcDirs.size

tasks {
    register("calculator", Wsdl2Java::class) {
        addToMainSourceSet.set(false)
        toolOptions {
            wsdl.set(file("wsdls/calculator.wsdl"))
        }
    }
    register("verify") {
        doLast {
            println("Source directories size match: ${initialSize == sourceSets.getByName("main", SourceSet::class).java.srcDirs.size}")
        }
    }
}
