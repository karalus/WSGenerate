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


public abstract class Type extends SchemaObject {

	private QName baseRef;
	private boolean restriction;

	public final QName getBaseRef() {
		return baseRef;
	}
	public boolean isRestriction() {
		return restriction;
	}
	public final void setBaseRef(QName baseType, boolean restriction) {
		this.baseRef = baseType;
	}
	public final boolean hasBaseType() {
		return baseRef != null && !XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(baseRef.getNamespaceURI());
	}
	public boolean isBaseTypeInNamespace(String namespace) {
		return baseRef != null && namespace.equals(baseRef.getNamespaceURI());
	}

}
