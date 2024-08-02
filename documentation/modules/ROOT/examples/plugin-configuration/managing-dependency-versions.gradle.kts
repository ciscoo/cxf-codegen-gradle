plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

// tag::code[]
configurations.cxfCodegen {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.apache.cxf") {
            useVersion("3.2.0")
            because("3.3.0 breaks the build")
        }
    }
}
// end::code[]