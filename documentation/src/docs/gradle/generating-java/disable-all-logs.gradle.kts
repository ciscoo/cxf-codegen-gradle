import io.mateo.cxf.codegen.wsdl2java.Wsdl2JavaTask

tasks.withType(Wsdl2JavaTask::class).configureEach {
    jvmArgs = listOf("-Dlogback.configurationFile=logback.xml")
}
