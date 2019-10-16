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


public class Member extends Referrer {
	
	private boolean optional;
	private boolean nillable;
	private boolean array;
	
	public boolean isOptional() {
		return optional;
	}
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	public boolean isNillable() {
		return nillable;
	}
	public void setNillable(boolean nillable) {
		this.nillable = nillable;
	}
	public boolean isArray() {
		return array;
	}
	public void setArray(boolean array) {
		this.array = array;
	}
	@Override
	public String toString() {
		return "Member [getFullName()=" + getFullName() + ", ref=" + getRef() + ", typeRef=" + getTypeRef() + ", optional=" + optional + ", nillable=" + nillable
				+ ", array=" + array + ", attribute=" + isAttribute() + "]";
	}
	
}