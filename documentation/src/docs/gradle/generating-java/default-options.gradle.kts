cxfCodegen {
    wsdl2java {
        register("first") {
            wsdl.set(file("path/to/first.wsdl"))
        }
        register("second") {
            wsdl.set(file("path/to/second.wsdl"))
        }
        register("third") {
            wsdl.set(file("path/to/third.wsdl"))
        }
    }
}
