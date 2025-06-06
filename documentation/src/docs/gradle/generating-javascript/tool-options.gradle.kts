import io.mateo.cxf.codegen.wsdl2js.Wsdl2Js
import io.mateo.cxf.codegen.wsdl2js.Wsdl2JsOptions.UriPrefixPair

plugins {
    id("io.mateo.cxf-codegen")
}

// tag::code[]
tasks.register("example", Wsdl2Js::class) {
    toolOptions {
        wsdl = file("path/to/example.wsdl").absolutePath
        outputDir = file("$buildDir/example-generated-js") // <1>
        catalog = file("path/to/example-catalog.xml") // <2>
        packagePrefixes = listOf(UriPrefixPair("https://example.com", "example")) // <3>
        verbose = true // <4>
    }
}
// end::code[]

tasks.register("verify") {
    doLast {
        val example = tasks.getByName("example", Wsdl2Js::class).wsdl2JsOptions
        println("wsdl=${example.wsdl.get()}")
        println("outputDir=${example.outputDir.get()}")
        println("catalog=${example.catalog.get()}")
        println("packagePrefixes=${example.packagePrefixes.get()}")
        println("verbose=${example.verbose.get()}")
    }
}
