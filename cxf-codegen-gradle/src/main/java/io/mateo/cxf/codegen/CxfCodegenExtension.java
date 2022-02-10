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
package io.mateo.cxf.codegen;

import javax.inject.Inject;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.model.ObjectFactory;

/**
 * Extension to configure WSDL sources.
 *
 * @deprecated since 1.0.0 for removal in 1.1.0 in favor of
 * {@link io.mateo.cxf.codegen.wsdl2java.Wsdl2Java} tasks
 */
@Deprecated
public class CxfCodegenExtension {

	private final NamedDomainObjectContainer<io.mateo.cxf.codegen.wsdl2java.WsdlOption> wsdl2java;

	@Inject
	public CxfCodegenExtension(ObjectFactory objects) {
		this.wsdl2java = objects.domainObjectContainer(io.mateo.cxf.codegen.wsdl2java.WsdlOption.class);
	}

	/**
	 * Returns the Java sources used for code generation.
	 * @return sources for Java code generation
	 */
	public NamedDomainObjectContainer<io.mateo.cxf.codegen.wsdl2java.WsdlOption> getWsdl2java() {
		return this.wsdl2java;
	}

	/**
	 * Configures the WSDL sources to use for code generation.
	 * @param configure action or closure to configure the WSDL sources with
	 */
	public void wsdl2java(
			Action<? super NamedDomainObjectContainer<io.mateo.cxf.codegen.wsdl2java.WsdlOption>> configure) {
		configure.execute(this.wsdl2java);
	}

}
