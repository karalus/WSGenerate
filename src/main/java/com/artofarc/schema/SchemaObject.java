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

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

public abstract class SchemaObject {

	private SchemaObject owner;
	private List<Object> appInfos;
	private List<Object> documentations;
	private Map<QName, String> otherAttributes;
	
	public final SchemaObject getOwner() {
		return owner;
	}
	public final SchemaObject setOwner(SchemaObject owner) {
		this.owner = owner;
		return this;
	}
	public final List<Object> getAppInfos() {
		return appInfos;
	}
	public final void setAppInfos(List<Object> appInfos) {
		this.appInfos = appInfos;
	}
	public final List<Object> getDocumentations() {
		return documentations;
	}
	public final void setDocumentations(List<Object> documentations) {
		this.documentations = documentations;
	}
	public final Map<QName, String> getOtherAttributes() {
		return otherAttributes;
	}
	public final void setOtherAttributes(Map<QName, String> otherAttributes) {
		this.otherAttributes = otherAttributes;
	}
	
	public String getFullName() {
		return owner != null ? owner.getFullName() : null;
	}

	public final SchemaModel getSchemaModel() {
		SchemaObject result = this;
		while (!(result instanceof SchemaDef)) {
			result = result.getOwner();
		}
		return ((SchemaDef) result).getModel();
	}
	
}
