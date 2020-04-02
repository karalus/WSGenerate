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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public final class Primitive extends Type {

	private boolean listType;
	private Integer minLength;
	private Integer maxLength;
	private Integer totalDigits;
	private Integer fractionDigits;
	private String minValue;
	private String maxValue;
	private List<String> patterns = new ArrayList<>();
	private List<Enum> enumerations = new ArrayList<>();

	public boolean isListType() {
		if (!listType && hasBaseType()) {
			Primitive basePrimitive = getSchemaModel().getPrimitiveType(getBaseRef());
			return basePrimitive.isListType();
		}
		return listType;
	}

	public void setListType(boolean listType) {
		this.listType = listType;
	}

	public Integer getMinLength() {
		if (minLength == null && hasBaseType()) {
			Primitive basePrimitive = getSchemaModel().getPrimitiveType(getBaseRef());
			return basePrimitive.getMinLength();
		}
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public Integer getMaxLength() {
		if (maxLength == null && hasBaseType()) {
			Primitive basePrimitive = getSchemaModel().getPrimitiveType(getBaseRef());
			return basePrimitive.getMaxLength();
		}
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public Integer getTotalDigits() {
		return totalDigits;
	}

	public void setTotalDigits(Integer totalTigits) {
		this.totalDigits = totalTigits;
	}

	public Integer getFractionDigits() {
		return fractionDigits;
	}

	public void setFractionDigits(Integer fractionDigits) {
		this.fractionDigits = fractionDigits;
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public List<String> getPatterns() {
		if (hasBaseType()) {
			List<String> result = new ArrayList<>(patterns);
			Primitive basePrimitive = getSchemaModel().getPrimitiveType(getBaseRef());
			result.addAll(basePrimitive.getPatterns());
			return result;
		}
		return patterns;
	}

	public List<Enum> getEnumerations() {
		if (enumerations.isEmpty() && hasBaseType()) {
			Primitive basePrimitive = getSchemaModel().getPrimitiveType(getBaseRef());
			return basePrimitive.getEnumerations();
		}
		return enumerations;
	}

	public void addPattern(String pattern) {
		patterns.add(pattern);
	}

	public void addEnumeration(Enum enumeration) {
		enumerations.add(enumeration);
	}

	public String getXSDBuiltInType() {
		if (hasBaseType()) {
			Primitive basePrimitive = getSchemaModel().getPrimitiveType(getBaseRef());
			return basePrimitive.getXSDBuiltInType();
		}
		return getBaseRef().getLocalPart();
	}

}
