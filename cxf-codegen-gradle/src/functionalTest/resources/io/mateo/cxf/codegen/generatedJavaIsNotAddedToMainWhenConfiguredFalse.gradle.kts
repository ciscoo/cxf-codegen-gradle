import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

dependencies {
    cxfCodegen("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    cxfCodegen("jakarta.annotation:jakarta.annotation-api:1.3.5")
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
