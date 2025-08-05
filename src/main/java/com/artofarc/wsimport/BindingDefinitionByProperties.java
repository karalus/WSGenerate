/*
 * Copyright 2025 Andre Karalus
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.wsdl.PortType;
import javax.xml.XMLConstants;

import org.w3._2001.xmlschema.Annotated;

import com.artofarc.util.PropertiesExpansion;
import com.artofarc.util.UriHelper;

public class BindingDefinitionByProperties implements BindingDefinition {

	protected final PropertiesExpansion _propertiesExpansion = new PropertiesExpansion();
	private List<Map.Entry<String, String>> globalReplaces;
	private List<Map.Entry<String, String>> replacePackagePrefixes;

	public BindingDefinitionByProperties() {
	}

	public BindingDefinitionByProperties(String fileName) throws IOException {
		addPropertyFile(fileName);
	}

	private static List<Map.Entry<String, String>> extractPropertiesForPrefix(PropertiesExpansion propertiesExpansion, String prefix) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, String> replaces = (Map) propertiesExpansion.getPropertiesForPrefix(prefix);
		return replaces.isEmpty() ? Collections.emptyList()
			: replaces.entrySet().stream().sorted((e1, e2) -> e2.getKey().length() - e1.getKey().length()).collect(Collectors.toList());
	}

	public void addPropertyFile(String fileName) throws IOException {
		try (FileInputStream fis = new FileInputStream(fileName)) {
			_propertiesExpansion.load(fis);
		}
		globalReplaces = extractPropertiesForPrefix(_propertiesExpansion, "replaceRegExp");
		replacePackagePrefixes = extractPropertiesForPrefix(_propertiesExpansion, "replacePackagePrefix");
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
		if (mapping == ns && replacePackagePrefixes != null) {
			for (Map.Entry<String,String> entry : replacePackagePrefixes) {
				if (uri.startsWith(entry.getKey())) {
					uri = entry.getValue() + uri.substring(entry.getKey().length());
					break;
				}
			}
		}
		if (split[1].isEmpty()) {
			if (mapping == ns) {
				throw new IllegalStateException("No version determined(" + uri + "). A mapping must be defined for " + ns);
			}
			return mapping;
		} else {
			String uriDots = UriHelper.convertUri(ns, ".", false);
			return String.format(formatPattern, uri, Integer.valueOf(split[1]), uriDots);
		}
	}

	@Override
	public String getMappingForName(String ns, String name, Annotated annotated) {
		String mappedName = _propertiesExpansion.getPropertyFromSection(ns, name, name);
		if (mappedName == name) {
			Object property = _propertiesExpansion.get(ns);
			if (property instanceof PropertiesExpansion) {
				for (Map.Entry<String, String> entry : extractPropertiesForPrefix((PropertiesExpansion) property, "replaceRegExp")) {
					mappedName = mappedName.replaceAll(entry.getKey(), entry.getValue());
				}
			}
			if (globalReplaces != null) {
				for (Map.Entry<String, String> entry : globalReplaces) {
					mappedName = mappedName.replaceAll(entry.getKey(), entry.getValue());
				}
			}
		}
		return mappedName;
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
