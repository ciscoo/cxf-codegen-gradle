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
      <td>Specifies the directory the generated code files are written. If not set, the convention is <code>build/$name-wsdl2js-generated-sources</code>. The <code>$name</code> variable is the option name</td>
    </tr>
    <tr>
      <td><code>Property&lt;String&gt;</code></td>
      <td><code>wsdlVersion</code></td>
      <td>Specifies the WSDL version. If not set, <code>wsdl2js</code> defaults to <code>WSDL1.1</code>; currently supports only WSDL1.1 version.</td>
    </tr>
    <tr>
      <td><code>ListProperty&lt;UriPrefixPair&gt;</code></td>
      <td><code>packagePrefixes</code></td>
      <td>Specifies a mapping between the namespaces used in the WSDL document and the prefixes used in the generated JavaScript.</td>
    </tr>
    <tr>
      <td><code>Property&lt;String&gt;</code></td>
      <td><code>catalog</code></td>
      <td>Specify catalog file to map the imported WSDL/schema.</td>
    </tr>
    <tr>
      <td><code>Property&lt;String&gt;</code></td>
      <td><code>validate</code></td>
      <td>Enables validating the WSDL before generating the code.</td>
    </tr>
    <tr>
      <td><code>Property&lt;Boolean&gt;</code></td>
      <td><code>verbose</code></td>
      <td>Enables or disables verbosity.</td>
    </tr>
    <tr>
      <td><code>Property&lt;Boolean&gt;</code></td>
      <td><code>quiet</code></td>
      <td>Enables or disables quiet mode. When enabled, suppresses comments during the code generation process.</td>
    </tr>
  </tbody>
</table>
