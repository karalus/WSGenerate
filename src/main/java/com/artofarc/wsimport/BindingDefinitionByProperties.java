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
package com.artofarc.wsimport;

import java.io.FileInputStream;
import java.io.IOException;

import javax.wsdl.PortType;

import com.artofarc.schema.SchemaObject;
import com.artofarc.util.PropertiesExpansion;
import com.artofarc.util.UriHelper;

public class BindingDefinitionByProperties implements BindingDefinition {

	protected final PropertiesExpansion _propertiesExpansion = new PropertiesExpansion();

	public BindingDefinitionByProperties() {
	}

	public BindingDefinitionByProperties(String fileName) throws IOException {
		addPropertyFile(fileName);
	}

	public void addPropertyFile(String fileName) throws IOException {
		FileInputStream fis = new FileInputStream(fileName);
		_propertiesExpansion.load(fis);
		fis.close();
	}

	@Override
	public String getMappingForPortType(String ns, String portType, ArtifactKind kind) {
		String formatPattern = _propertiesExpansion.getProperty(kind.name(), kind.name().charAt(0) + "%s%s");
		String name = _propertiesExpansion.getPropertyFromSection(ns, portType, portType);
		String[] split = extractVersionForPortType(ns, name);
		Integer version = split[1].isEmpty() ? 0 : new Integer(split[1]);
		return String.format(formatPattern, split[0], version);
	}

	@Override
	public String getMappingForNamespace(String ns) {
		String formatPattern = _propertiesExpansion.getProperty(ArtifactKind.Schema.name(), "%sV%s");
		String mapping = _propertiesExpansion.getProperty(ns, ns);
		String[] split = extractVersionForNamespace(mapping);
		String uri = split[0].replaceAll("/", "");
		String uriDots = UriHelper.convertUri(ns, ".", false);
		Integer version = split[1].isEmpty() ? 0 : new Integer(split[1]);
		return String.format(formatPattern, uri, version, uriDots);
	}

	@Override
	public String getMappingForName(String ns, String name, SchemaObject annotated) {
		return _propertiesExpansion.getPropertyFromSection(ns, name, name);
	}

	@Override
	public String getMappingForBasePackage(String name) {
		String formatPattern = _propertiesExpansion.getProperty("BasePackage", "SOAP_%s");
		return String.format(formatPattern, name);
	}

	@Override
	public boolean generateClient(PortType portType) {
		return Boolean.parseBoolean(_propertiesExpansion.getPropertyFromSection(portType.getQName().getLocalPart(), "generateClient", "true"));
	}

	@Override
	public boolean generateServer(PortType portType) {
		return Boolean.parseBoolean(_propertiesExpansion.getPropertyFromSection(portType.getQName().getLocalPart(), "generateServer", "true"));
	}

	protected static String[] extractVersionForPortType(String ns, String portType) {
		int i = portType.length();
		String v = "";
		char c;
		while (Character.isDigit((c = portType.charAt(--i)))) {
			v = c + v;
		}
		return v.isEmpty() ? extractVersionForNamespace(ns) : new String[] { portType.substring(0, i + 1), v };
	}

	protected static String[] extractVersionForNamespace(String ns) {
		ns = UriHelper.convertUri(ns, "/", true);
		int i = ns.length();
		String v = "";
		char c;
		while (Character.isDigit((c = ns.charAt(--i)))) {
			v = c + v;
		}
		int l = ns.charAt(i - 1) == '/' ? i : i + 1; // omit 'v'
		return new String[] { ns.substring(0, l), v };
	}

}
