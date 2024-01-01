/*
 * Copyright 2020-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mateo.cxf.codegen.wsdl2js;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Options for the {@code wsdl2js} tool.
 *
 * @see <a href=
 * "https://cxf.apache.org/docs/wsdl-to-javascript.html#WSDLtoJavascript-Options">WSDL to
 * JavaScript options</a>
 */
public abstract class Wsdl2JsOptions {

	/**
	 * Specifies the WSDL document from which JavaScript is generated from
	 * @return wsdl file
	 */
	@Input
	public abstract Property<String> getWsdl();

	/**
	 * Specifies the WSDL version the tool expects.
	 * <p>
	 * If not set, {@code wsdl2js} defaults to {@code WSDL1.1}.
	 * @return wsdl version
	 */
	@Input
	@Optional
	public abstract Property<String> getWsdlVersion();

	/**
	 * Specifies a mapping between the namespaces used in the WSDL document and the
	 * prefixes used in the generated JavaScript.
	 * <p>
	 * The format is <code>URI=prefix</code>. In other words:
	 * <code>[wsdl namespace]=prefix</code>
	 * @return package prefixes
	 */
	@Input
	@Optional
	public abstract ListProperty<UriPrefixPair> getPackagePrefixes();

	/**
	 * Specifies the XML catalog to use for resolving imported schemas and WSDL documents.
	 * @return catalog
	 */
	@InputFile
	@PathSensitive(PathSensitivity.RELATIVE)
	@Optional
	public abstract RegularFileProperty getCatalog();

	/**
	 * Enables validating the WSDL before generating the code.
	 * @return validate WSDL
	 */
	@Input
	@Optional
	public abstract Property<String> getValidate();

	/**
	 * Enables or disables verbosity. When enabled, displays comments during the code
	 * generation process.
	 * @return verbosity indicator
	 */
	@Input
	@Optional
	public abstract Property<Boolean> getVerbose();

	/**
	 * Enables or disables quiet mode. When enabled, suppresses comments during the code
	 * generation process.
	 * @return quite mode enablement
	 */
	@Input
	@Optional
	public abstract Property<Boolean> getQuiet();

	/**
	 * Specifies the directory the generated code files are written.
	 * <p>
	 * If not set, the convention is
	 * {@code "$buildDir/$taskName-wsdl2js-generated-sources"}
	 * @return output directory
	 */
	@OutputDirectory
	@Optional
	public abstract DirectoryProperty getOutputDir();

	/**
	 * Container class for URI. An arbitrary URI can't be a XML element name.
	 * <p>
	 * Based on code from Apache CXF.
	 */
	public static class UriPrefixPair {

		/**
		 * Namespace URI.
		 */
		private String uri;

		/**
		 * Identifier prefix.
		 */
		private String prefix;

		public UriPrefixPair() {

		}

		public UriPrefixPair(String uri, String prefix) {
			this.uri = Objects.requireNonNull(uri, "'uri' must not be null for UriPrefixPair");
			this.prefix = Objects.requireNonNull(prefix, "'prefix' must not be null for UriPrefixPair");
		}

		public String getUri() {
			return this.uri;
		}

		public void setUri(String uri) {
			this.uri = Objects.requireNonNull(uri, "'uri' must not be null for UriPrefixPair");
		}

		public String getPrefix() {
			return this.prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = Objects.requireNonNull(prefix, "'prefix' must not be null for UriPrefixPair");
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", UriPrefixPair.class.getSimpleName() + "[", "]").add("uri='" + uri + "'")
				.add("prefix='" + prefix + "'")
				.toString();
		}

	}

}
