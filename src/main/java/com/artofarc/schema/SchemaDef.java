/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.artofarc.schema;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SchemaDef extends SchemaObject {

	private SchemaModel model;
	private String targetNamespace;
	private List<String> documentBaseURIs;
	private boolean elementFormDefaultQualified;
	private boolean attributeFormDefaultQualified;
	private final Map<String, Global> primitives = new LinkedHashMap<>();
	private final Map<String, Global> composites = new LinkedHashMap<>();
	private final Map<String, Global> decls = new LinkedHashMap<>(); 
	private final Map<String, Global> defs = new LinkedHashMap<>(); 
	
	public SchemaModel getModel() {
		return model;
	}

	public void setModel(SchemaModel model) {
		this.model = model;
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setTargetNamespace(String namespace) {
		this.targetNamespace = namespace;
	}

	public List<String> getDocumentBaseURIs() {
		return documentBaseURIs;
	}

	public void setDocumentBaseURIs(List<String> documentBaseURIs) {
		this.documentBaseURIs = documentBaseURIs;
	}

	public boolean isElementFormDefaultQualified() {
		return elementFormDefaultQualified;
	}

	public void setElementFormDefaultQualified(boolean elementFormDefaultQualified) {
		this.elementFormDefaultQualified = elementFormDefaultQualified;
	}

	public boolean isAttributeFormDefaultQualified() {
		return attributeFormDefaultQualified;
	}

	public void setAttributeFormDefaultQualified(boolean attributeFormDefaultQualified) {
		this.attributeFormDefaultQualified = attributeFormDefaultQualified;
	}

	public Collection<Global> getPrimitives() {
		return primitives.values();
	}

	public Collection<Global> getComposites() {
		return composites.values();
	}

	public Collection<Global> getDeclarations() {
		return decls.values();
	}

	public Collection<Global> getDefinitions() {
		return defs.values();
	}

	public void addPrimitive(String name, Global type) {
		primitives.put(name, type);
	}
	
	public void addComposite(String name, Global type) {
		composites.put(name, type);
	}
	
	public void addDeclaration(String name, Global global) {
		decls.put(name, global);
	}

	public void addDefinition(String name, Global global) {
		defs.put(name, global);
	}

	public Primitive getPrimitiveType(String name) {
		Global global = primitives.get(name);
		if (global != null) {
			return global.getTypeRef().getPrimitiveType(model);
		}
		return null;
	}
	
	public Composite getCompositeType(String name) {
		Global global = composites.get(name);
		if (global != null) {
			return global.getTypeRef().getCompositeType(model);
		}
		return null;
	}

	public Global getDeclaration(String name) {
		return decls.get(name);
	}

	public Global getDefinition(String name) {
		return defs.get(name);
	}

   public String getMappingForName(String name, SchemaObject annotated) {
      return model.getBindingDefinition().getMappingForName(targetNamespace, name, annotated);
   }

	@Override
	public String toString() {
		return "SchemaDef [namespace=" + targetNamespace + ", composites=" + composites + ", primitives=" + primitives + ", elements=" + defs + "]";
	}
	
}
