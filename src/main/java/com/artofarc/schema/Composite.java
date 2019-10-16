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

import java.util.ArrayList;
import java.util.List;

public final class Composite extends Type {
	
	private boolean _abstract;
	private boolean choice;
	private boolean simpleContent;
	private Boolean mixed;
	
	private final List<Member> members = new ArrayList<>();

	public boolean isAbstract() {
		return _abstract;
	}

	public void setAbstract(boolean _abstract) {
		this._abstract = _abstract;
	}

	public boolean isChoice() {
		return choice;
	}

	public void setChoice(boolean choice) {
		this.choice = choice;
	}

	public boolean hasSimpleContent() {
		if (!simpleContent && hasBaseType()) {
			Composite baseComposite = getSchemaModel().getCompositeType(getBaseRef());
			return baseComposite.hasSimpleContent();
		}
		return simpleContent;
	}

	public void setSimpleContent(boolean simpleContent) {
		this.simpleContent = simpleContent;
	}

	public boolean isMixed() {
		if (mixed == null) {
			Composite baseComposite = getSchemaModel().getCompositeType(getBaseRef());
			return baseComposite.isMixed();
		}
		return mixed;
	}

	public void setMixed(Boolean mixed) {
		this.mixed = mixed;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void addMember(Member member) {
		members.add(member);
	}

	public String getXSDBuiltInType() {
		Composite baseComposite, next = this;
		do {
			baseComposite = next;
			next = baseComposite.getSchemaModel().getCompositeType(getBaseRef());
		} while (next != null);
		if (baseComposite.hasBaseType()) {
			Primitive basePrimitive = getSchemaModel().getPrimitiveType(getBaseRef());
			return basePrimitive.getXSDBuiltInType();
		} else {
			return getBaseRef().getLocalPart();
		}
	}
	
	@Override
	public String toString() {
		return "Composite [_abstract=" + _abstract + ", baseRef=" + getBaseRef() + ", members=" + members + "]";
	}
	
}
