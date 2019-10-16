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

public final class AnyMember extends Member {

	private boolean processContentsStrict; 
	private List<String> namespaces;
	
	public boolean isProcessContentsStrict() {
		return processContentsStrict;
	}
	public void setProcessContentsStrict(boolean processContentsStrict) {
		this.processContentsStrict = processContentsStrict;
	}
	public List<String> getNamespaces() {
		return namespaces;
	}
	public void setNamespaces(List<String> namespaces) {
		this.namespaces = namespaces;
	}
	
	@Override
	public String toString() {
		return "AnyMember [processContentsStrict=" + processContentsStrict + ", namespaces=" + namespaces + ", isAttribute()=" + isAttribute() + "]";
	}
}
