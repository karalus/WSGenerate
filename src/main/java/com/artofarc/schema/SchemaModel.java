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
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import com.artofarc.wsimport.BindingDefinition;

public final class SchemaModel {

   private BindingDefinition bindingDefinition;
	private final Map<String, SchemaDef> definitions = new HashMap<>(); 
	
	public BindingDefinition getBindingDefinition() {
		return bindingDefinition;
	}

	public void setBindingDefinition(BindingDefinition bindingDefinition) {
		this.bindingDefinition = bindingDefinition;
	}

	public SchemaDef getDefinition(String namespace) {
		return definitions.get(namespace);
	}
	
	public void addDefinition(String namespace, SchemaDef definition) {
		definitions.put(namespace, definition);
	}
	
	public Collection<SchemaDef> getDefinitions() {
		return definitions.values();
	}
	
	public Composite getCompositeType(QName ref) {
		SchemaDef schema = getDefinition(ref.getNamespaceURI());
		return schema.getCompositeType(ref.getLocalPart());
	}

	public Primitive getPrimitiveType(QName ref) {
		SchemaDef schema = getDefinition(ref.getNamespaceURI());
		return schema.getPrimitiveType(ref.getLocalPart());
	}

}
