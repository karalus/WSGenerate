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

import javax.xml.namespace.QName;


public abstract class Referrer extends SchemaObject {

	private String name;
	private TypeRef typeRef;
	/**
	 * Can refer to an Element, Attribute, NamedGroup or NamedAttributeGroup
	 */
	private QName ref;
	private boolean attribute;

	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final TypeRef getTypeRef() {
		return typeRef;
	}
	public final void setTypeRef(TypeRef typeRef) {
		this.typeRef = typeRef;
	}
	public final QName getRef() {
		return ref;
	}
	public final void setRef(QName ref) {
		this.ref = ref;
		setName(ref.getLocalPart());
	}
	public boolean isAttribute() {
		return attribute;
	}
	public void setAttribute(boolean attribute) {
		this.attribute = attribute;
	}

	@Override
	public String getFullName() {
		return getOwner() != null && getOwner().getFullName() != null ? getOwner().getFullName() + "." + name : name;
	}

	public Type getLocalType(SchemaDef schema) {
		if (ref != null) {
			if (schema.getTargetNamespace().equals(ref.getNamespaceURI())) {
				Global definition = schema.getDefinition(ref.getLocalPart());
				if (definition != null) {
					return definition.getLocalType(schema);
				}
				Global declaration = schema.getDeclaration(ref.getLocalPart());
				if (declaration != null) {
					return declaration.getLocalType(schema);
				}
			}
		} else if (typeRef != null) {
			if (typeRef.isInNamespace(schema.getTargetNamespace())) {
				return typeRef.getLocalType(schema);
			}
		}
		return null;
	}

	public Type getType() {
		SchemaModel model = getSchemaModel();
		if (ref != null) {
			Global global = model.getDefinition(ref.getNamespaceURI()).getDefinition(ref.getLocalPart());
			return global.getType();
		} else if (typeRef != null) {
			return typeRef.getType(model);
		}
		return null;
	}
	
	@Override
	public String toString() {
		return getFullName();
	}

}
