/*
 * Copyright 2024 Andre Karalus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.artofarc.wsimport;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.wsdl.PortType;

import org.w3._2001.xmlschema.Annotated;

public interface BindingDefinition {

	String getMappingForPortType(String ns, String portType, ArtifactKind kind);

	String getMappingForNamespace(String ns);

	String getMappingForName(String ns, String name, Annotated annotated);

	String getGlobalProperty(String name, String def);

	String getPortTypeProperty(PortType portType, String name, String def);

	default List<String> getGlobalPropertyAsList(String name) {
		List<String> result = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(getGlobalProperty(name, ""), ",");
		while (tokenizer.hasMoreTokens()) {
			result.add(tokenizer.nextToken());
		}
		return result;
	}

}
