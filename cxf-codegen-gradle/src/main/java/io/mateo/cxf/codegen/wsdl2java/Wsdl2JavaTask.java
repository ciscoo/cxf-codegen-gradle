/*
 * Copyright 2021 the original author or authors.
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
package io.mateo.cxf.codegen.wsdl2java;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.inject.Inject;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaReference;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.*;

/**
 * Generates Java sources from WSDLs.
 */
@CacheableTask
public class Wsdl2JavaTask extends JavaExec {

	private static Set<Definition> getReferencedDefinitions(Definition definition) {
		Set<Definition> definitions = new HashSet<>();
		definitions.add(definition);
		for (Object wsdlImports : definition.getImports().values()) {
			if (wsdlImports instanceof Collection) {
				for (Object wsdlImport : (Collection) wsdlImports) {
					if (wsdlImport instanceof Import) {
						definitions.addAll(getReferencedDefinitions(((Import) wsdlImport).getDefinition()));
					}
				}
			}
		}
		return definitions;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Set<Schema> getReferencedSchemas(Schema schema) {

		Set<Schema> schemas = new HashSet<>();
		schemas.add(schema);

		Set<Object> references = new HashSet(schema.getIncludes());
		references.addAll(schema.getImports().values());

		for (Object refs : references) {
			if (refs instanceof Vector) {
				for (Object reference : (Vector) refs) {
					if (reference instanceof SchemaReference) {
						schemas.addAll(getReferencedSchemas(((SchemaReference) reference).getReferencedSchema()));
					}
				}
			}
		}
		return schemas;
	}

	public static ConfigurableFileCollection sourceFiles(RegularFileProperty wsdl, ObjectFactory objectFactory)
			throws WSDLException {
		ConfigurableFileCollection collection = objectFactory.fileCollection();
		WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
		wsdlReader.setFeature("javax.wsdl.verbose", false);
		Definition rootDefinition = wsdlReader.readWSDL(wsdl.get().getAsFile().getAbsolutePath());
		Set<Definition> definitions = getReferencedDefinitions(rootDefinition);
		Set<Schema> schemas = new HashSet<>();
		for (Definition definition : definitions) {
			for (Object ext : definition.getTypes().getExtensibilityElements()) {
				if (ext instanceof Schema) {
					schemas.addAll(getReferencedSchemas((Schema) ext));
				}
			}
		}
		for (Definition definition : definitions) {
			collection.from(definition.getDocumentBaseURI());
		}
		for (Schema schema : schemas) {
			collection.from(schema.getDocumentBaseURI());
		}
		return collection;
	}

	private final ObjectFactory objectFactory;

	private final RegularFileProperty source;

	@Inject
	public Wsdl2JavaTask(ObjectFactory objectFactory) {
		super();
		this.objectFactory = objectFactory;
		this.source = objectFactory.fileProperty();
	}

	@Internal
	public RegularFileProperty getSource() {
		return this.source;
	}

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	public ConfigurableFileCollection getInputFiles() {
		try {
			return sourceFiles(this.source, this.objectFactory);
		}
		catch (WSDLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
