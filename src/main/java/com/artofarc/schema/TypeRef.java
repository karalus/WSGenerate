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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

public final class TypeRef {
	private QName ref;
	private Type innerType;

	public QName getRef() {
		return ref;
	}

	public void setRef(QName ref) {
		this.ref = ref;
	}

	public Type getInnerType() {
		return innerType;
	}

	public void setInnerType(Type innerType) {
		this.innerType = innerType;
	}

	public boolean isInNamespace(String namespace) {
		return ref != null && namespace.equals(ref.getNamespaceURI());
	}

	public boolean isBuiltInType() {
		return isInNamespace(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	}

	public Composite getCompositeType(SchemaModel model) {
		Composite type = (Composite) innerType;
		if (type == null) {
			type = model.getCompositeType(ref);
		}
		return type;
	}

	public Primitive getPrimitiveType(SchemaModel model) {
		Primitive type = (Primitive) innerType;
		if (type == null) {
			type = model.getPrimitiveType(ref);
		}
		return type;
	}
	
	public Type getLocalType(SchemaDef schema) {
		Type type = innerType;
		if (type == null) {
			if (isInNamespace(schema.getTargetNamespace())) {
				type = schema.getCompositeType(ref.getLocalPart());
				if (type == null) {
					type = schema.getPrimitiveType(ref.getLocalPart());
				}
			}
		}
		return type;
	}

	@Override
	public String toString() {
		return ref != null ? ref.toString() : innerType.toString();
	}

}
