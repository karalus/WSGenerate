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
import javax.xml.XMLConstants;

import org.w3._2001.xmlschema.Annotated;

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
		if (ns.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
			return getGlobalProperty("BasePackage", "SOAP_BASE");
		}
		String formatPattern = _propertiesExpansion.getProperty(ArtifactKind.Schema.name(), "%sV%s");
		String mapping;
		Object property = _propertiesExpansion.get(ns);
		if (property instanceof PropertiesExpansion) {
			mapping = ((PropertiesExpansion) property).getProperty(".", ns);
		} else {
			mapping = _propertiesExpansion.getProperty(ns, ns);
		}
		if (mapping.isEmpty()) {
			return null;
		}
		String[] split = extractVersionForNamespace(mapping);
		String uri = split[0].replaceAll("/", "");
		String uriDots = UriHelper.convertUri(ns, ".", false);
		Integer version = split[1].isEmpty() ? 0 : new Integer(split[1]);
		return String.format(formatPattern, uri, version, uriDots);
	}

	@Override
	public String getMappingForName(String ns, String name, Annotated annotated) {
		return _propertiesExpansion.getPropertyFromSection(ns, name, name);
	}

	@Override
	public String getGlobalProperty(String name, String def) {
		return _propertiesExpansion.getProperty(name, def);
	}

	@Override
	public String getPortTypeProperty(PortType portType, String name, String def) {
		return _propertiesExpansion.getPropertyFromSection(portType.getQName().getLocalPart(), name, def);
	}

	protected static String[] extractVersionForPortType(String ns, String portType) {
		int i = portType.length();
		String v = "";
		char c;
		while (Character.isDigit((c = portType.charAt(--i)))) {
			v = c + v;
		}
		if (v.isEmpty()) {
			String[] split = extractVersionForNamespace(ns);
			split[0] = portType;
			return split;
		} else {
			return new String[] { portType.substring(0, i + 1), v };
		}
	}

	protected static String[] extractVersionForNamespace(String ns) {
		ns = UriHelper.convertUri(ns, "/", true);
		int i = ns.length();
		String v = "";
		char c;
		while (i > 0 && Character.isDigit((c = ns.charAt(--i)))) {
			v = c + v;
		}
		if (v.length() > 2) {
			return new String[] { ns, "" };
		}
		int l = i == 0 || ns.charAt(i - 1) == '/' ? i : i + 1; // omit 'v'
		return new String[] { ns.substring(0, l), v };
	}

}
