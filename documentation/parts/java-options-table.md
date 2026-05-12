<table>
  <thead>
    <tr>
      <th>Type</th>
      <th>Name</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>Property&lt;String&gt;</code></td>
      <td><code>wsdl</code></td>
      <td>The WSDL to process. The value can either be a direct path to a file on a local system or URL to a remote file.</td>
    </tr>
    <tr>
      <td><code>DirectoryProperty</code></td>
      <td><code>outputDirectory</code></td>
      <td>Specifies the directory the generated code files are written. If not set, the convention is <code>build/$name-wsdl2java-generated-sources</code>. The <code>$name</code> variable is the option name</td>
    </tr>
    <tr>
      <td><code>ListProperty&lt;String&gt;</code></td>
      <td><code>packageNames</code></td>
      <td>Specifies zero, or more, package names to use for the generated code. Optionally specifies the WSDL namespace to package name mapping.</td>
    </tr>
    <tr>
      <td><code>ListProperty&lt;String&gt;</code></td>
      <td><code>extraArgs</code></td>
      <td>Specifies extra arguments to pass to the command-line code generator.</td>
    </tr>
    <tr>
      <td><code>ListProperty&lt;String&gt;</code></td>
      <td><code>xjcArgs</code></td>
      <td>Specifies arguments that are passed directly to the XJC processor when using the JAXB data binding.</td>
    </tr>
    <tr>
      <td><code>ListProperty&lt;String&gt;</code></td>
      <td><code>asyncMethods</code></td>
      <td>Specifies subsequently generated Java class methods to allow for client-side asynchronous calls, similar to <code>enableAsyncMapping</code> in a JAX-WS binding file.</td>
    </tr>
    <tr>
      <td><code>ListProperty&lt;String&gt;</code></td>
      <td><code>bareMethods</code></td>
      <td>Specifies subsequently generated Java class methods to have wrapper style, similar to <code>enableWrapperStyle</code> in JAX-WS binding file.</td>
    </tr>
    <tr>
      <td><code>ListProperty&lt;String&gt;</code></td>
      <td><code>mimeMethods</code></td>
      <td>Specifies subsequently generated Java class methods to enable mime:content mapping, similar to <code>enableMIMEContent</code> in JAX-WS binding file.</td>
    </tr>
    <tr>
      <td><code>ListProperty&lt;String&gt;</code></td>
      <td><code>namespaceExcludes</code></td>
      <td>Ignore the specified WSDL schema namespace when generating code. Optionally specifies the Java package name used by types described in the excluded namespace(s) using <code>schema-namespace[=java-packagename]</code>.</td>
    </tr>
    <tr>
      <td><code>Property&lt;Boolean&gt;</code></td>
      <td><code>defaultExcludesNamespace</code></td>
      <td>Enables or disables the loading of the default excludes namespace mapping. If not set, <code>wsdl2java</code> defaults to <code>true</code>.</td>
    </tr>
    <tr>
      <td><code>Property&lt;Boolean&gt;</code></td>
      <td><code>defaultNamespacePackageMapping</code></td>
      <td>Enables or disables the loading of the default namespace package name mapping. If not set, <code>wsdl2java</code> defaults to <code>true</code> and the <code>http://www.w3.org/2005/08/addressing</code> package mapping will be enabled.</td>
    </tr>
    <tr>
      <td><code>SetProperty&lt;String&gt;</code></td>
      <td><code>bindingFiles</code></td>
      <td>Specifies JAX-WS or JAXB binding files or XMLBeans context files. The values are evaluated as per <code>Directory#file(String)</code> from <code>ProjectLayout#getProjectDirectory()</code>.</td>
    </tr>
    <tr>
      <td><code>Property&lt;String&gt;</code></td>
      <td><code>wsdlLocation</code></td>
      <td>Specifies the value of the <code>@WebServiceClient</code> annotation's <code>wsdlLocation</code> property. By default, the <code>wsdl2java</code> tool will use the value of <code>wsdl</code>.</td>
    </tr>
    <tr>
      <td><code>Property&lt;Boolean&gt;</code></td>
      <td><code>wsdlList</code></td>
      <td>Specifies that the <code>wsdlurl</code> contains a plain text, new line delimited, list of <code>wsdlurl</code>s instead of the WSDL itself.</td>
    </tr>
    <tr>
      <td><code>Property&lt;String&gt;</code></td>
      <td><code>frontend</code></td>
      <td>Specifies the frontend. Currently only supports JAX-WS CXF frontends and a <code>jaxws21</code> frontend to generate JAX-WS 2.1 compliant code. If not set, <code>wsdl2java</code> defaults to <code>jaxws</code>.</td>
    </tr>
    <tr>
      <td><code>Property&lt;String&gt;</code></td>
      <td><code>databinding</code></td>
      <td>Specifies the databinding. Currently, supports JAXB, XMLBeans, SDO (sdo-static and sdo-dynamic), and JiBX. If not set, <code>wsdl2java</code> defaults to <code>jaxb</code>.</td>
    </tr>
    <tr>
      <td><code>Property&lt;String&gt;</code></td>
      <td><code>wsdlVersion</code></td>
      <td>Specifies the WSDL version. If not set, <code>wsdl2java</code> defaults to <code>WSDL1.1</code>; currently supports only WSDL1.1 version.</td>
    </tr>
    <tr>
      <td><code>Property&lt;String&gt;</code></td>
      <td><code>catalog</code></td>
      <td>Specify catalog file to map the imported WSDL/schema.</td>
    </tr>
    <tr>
      <td><code>Property&lt;Boolean&gt;</code></td>
      <td><code>extendedSoapHeaders</code></td>
      <td>Enables or disables processing of implicit SOAP headers (i.e. SOAP headers defined in the <code>wsdl:binding</code>, but not <code>wsdl:portType</code> section). If not set, <code>wsdl2java</code> defaults to <code>false</code>.</td>
    </tr>
    <tr>
      <td><code>Property&lt;String&gt;</code></td>
      <td><code>validateWsdl</code></td>
      <td>Enables validating the WSDL before generating the code.</td>
    </tr>
    <tr>
      <td><code>Property&lt;Boolean&gt;</code></td>
      <td><code>noTypes</code></td>
      <td>Enables or disables generation of the type classes. If not set, <code>wsdl2java</code> defaults to <code>false</code>.</td>
    </tr>
    <tr>
      <td><code>Property&lt;String&gt;</code></td>
      <td><code>faultSerialVersionUid</code></td>
      <td>Specifies how to generate serial version UID of fault exceptions. Use one of the following: <code>NONE</code>, <code>TIMESTAMP</code>, <code>FQCN</code>, or a specific number. If not set, <code>wsdl2java</code> defaults to <code>NONE</code>.</td>
    </tr>
    <tr>
      <td><code>Property&lt;String&gt;</code></td>
      <td><code>exceptionSuper</code></td>
      <td>Specifies the superclass for fault beans generated from <code>wsdl:fault</code> elements. If not set, <code>wsdl2java</code> defaults to <code>Exception</code>.</td>
    </tr>
    <tr>
      <td><code>ListProperty&lt;String&gt;</code></td>
      <td><code>seiSuper</code></td>
      <td>Specifies the superinterfaces to use for generated SEIs.</td>
    </tr>
    <tr>
      <td><code>Property&lt;Boolean&gt;</code></td>
      <td><code>markGenerated</code></td>
      <td>Enables or disables adding the <code>@Generated</code> annotation to classes generated.</td>
    </tr>
    <tr>
      <td><code>Property&lt;Boolean&gt;</code></td>
      <td><code>suppressGeneratedDate</code></td>
      <td>Enables or disables writing the current timestamp in the generated file (since CXF version 3.2.2).</td>
    </tr>
    <tr>
      <td><code>Property&lt;String&gt;</code></td>
      <td><code>serviceName</code></td>
      <td>Specifies the WSDL service name to use for the generated code.</td>
    </tr>
    <tr>
      <td><code>Property&lt;Boolean&gt;</code></td>
      <td><code>autoNameResolution</code></td>
      <td>Enables or disables automatically resolving naming conflicts without requiring the use of binding customizations.</td>
    </tr>
    <tr>
      <td><code>Property&lt;Boolean&gt;</code></td>
      <td><code>noAddressBinding</code></td>
      <td>For compatibility with CXF 2.0, this flag directs the code generator to generate the older CXF proprietary WS-Addressing types instead of the JAX-WS 2.1 compliant WS-Addressing types.</td>
    </tr>
    <tr>
      <td><code>Property&lt;Boolean&gt;</code></td>
      <td><code>allowElementRefs</code></td>
      <td>Enables or disables disregarding the rule given in section 2.3.1.2(v) of the JAX-WS 2.2 specification disallowing element references when using wrapper-style mapping.</td>
    </tr>
    <tr>
      <td><code>Property&lt;String&gt;</code></td>
      <td><code>encoding</code></td>
      <td>Encoding to use for generated sources (since CXF version 2.5.4).</td>
    </tr>
    <tr>
      <td><code>Property&lt;Boolean&gt;</code></td>
      <td><code>verbose</code></td>
      <td>Enables or disables verbosity.</td>
    </tr>
  </tbody>
</table>
