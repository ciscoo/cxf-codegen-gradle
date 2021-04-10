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
