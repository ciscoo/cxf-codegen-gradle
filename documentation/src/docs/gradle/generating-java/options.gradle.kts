plugins {
    java
    id("io.mateo.cxf-codegen")
}

// tag::code[]
cxfCodegen {
    wsdl2java {
        register("example") {
            wsdl.set(file("path/to/example.wsdl"))
            outputDir.set(file("$buildDir/generated-java")) // <1>
            markGenerated.set(true) // <2>
            packageNames.set(listOf("com.example", "com.foo.bar")) // <3>
            asyncMethods.set(listOf("foo", "bar")) // <4>
        }
    }
}
// end::code[]

tasks.register("verify") {
    doLast {
        val example = cxfCodegen.wsdl2java.getByName("example")
        println("wsdl=${example.wsdl.get()}")
        println("outputDir=${example.outputDir.get()}")
        println("markGenerated=${example.markGenerated.get()}")
        println("packageNames=${example.packageNames.get()}")
        println("asyncMethods=${example.asyncMethods.get()}")
    }
}
