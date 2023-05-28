import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    java
    id("io.mateo.cxf-codegen")
}

// tag::code[]
tasks.register("example", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(file("path/to/example.wsdl").toPath().toAbsolutePath().toUri().toString())
        outputDir.set(file("$buildDir/generated-java")) // <1>
        markGenerated.set(true) // <2>
        packageNames.set(listOf("com.example", "com.foo.bar")) // <3>
        asyncMethods.set(listOf("foo", "bar")) // <4>
    }
}
// end::code[]

tasks.register("verify") {
    doLast {
        val example = tasks.getByName("example", Wsdl2Java::class).wsdl2JavaOptions
        println("wsdl=${example.wsdl.get()}")
        println("outputDir=${example.outputDir.get()}")
        println("markGenerated=${example.markGenerated.get()}")
        println("packageNames=${example.packageNames.get()}")
        println("asyncMethods=${example.asyncMethods.get()}")
    }
}
