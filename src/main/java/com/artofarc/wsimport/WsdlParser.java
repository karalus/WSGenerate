/*
 * Copyright 2022 Andre Karalus
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

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.extensions.schema.SchemaReference;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.emf.mwe.core.WorkflowContext;
import org.eclipse.emf.mwe.core.issues.Issues;
import org.eclipse.emf.mwe.core.lib.AbstractWorkflowComponent2;
import org.eclipse.emf.mwe.core.monitor.ProgressMonitor;
import org.w3c.dom.Element;

import com.ibm.wsdl.DefinitionImpl;
import com.ibm.wsdl.OperationImpl;
import com.ibm.wsdl.factory.WSDLFactoryImpl;

public final class WsdlParser extends AbstractWorkflowComponent2 {

	public static class MyWSDLFactory extends WSDLFactoryImpl {

		@Override
		public Definition newDefinition() {
			Definition def = new MyDefinitionImpl();
			def.setExtensionRegistry(newPopulatedExtensionRegistry());
			return def;
		}
	}

	public static class MyDefinitionImpl extends DefinitionImpl {

		private static final long serialVersionUID = 5498876057733539141L;
		private static final Field fieldOperationImplFaults;

		static {
			try {
				fieldOperationImplFaults = OperationImpl.class.getDeclaredField("faults");
				fieldOperationImplFaults.setAccessible(true);
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public OperationImpl createOperation() {
			OperationImpl operationImpl = new OperationImpl();
			try {
				fieldOperationImplFaults.set(operationImpl, new LinkedHashMap<>());
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			return operationImpl;
		}
	}

	private static final WSDLFactory wsdlFactory;

	static {
		try {
			wsdlFactory = WSDLFactory.newInstance(MyWSDLFactory.class.getName());
		} catch (WSDLException e) {
			throw new RuntimeException("Cannot initialize wsdl4j", e);
		}
	}

   private final Unmarshaller xsdUnmarshaller;
   private final HashMap<String, ServiceNamespace> _done = new HashMap<>();
   private String outputSlot;
   private boolean verbose;
   private final List<String> fileNames = new ArrayList<>();
   private BindingDefinition bindingDefinition;

   public WsdlParser() throws JAXBException {
      xsdUnmarshaller = ServiceNamespace.createUnmarshaller();
   }

   public void setOutputSlot(String slot) {
      outputSlot = slot;
   }

   public void setVerbose(boolean b) {
      verbose = b;
   }

   public void addWsdlFileName(String fileName) {
      fileNames.add(fileName);
   }

   public void setBindingDefinition(BindingDefinition definition) {
      bindingDefinition = definition;
   }

   private void parse(Model model) throws WSDLException, JAXBException {
      WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
      wsdlReader.setFactoryImplName(MyWSDLFactory.class.getName());
      wsdlReader.setFeature("javax.wsdl.verbose", verbose);
      for (String fileName : fileNames) {
         Definition definition = wsdlReader.readWSDL(fileName);
         parseTypes(definition, model);
         model.getServiceNamespace(definition.getTargetNamespace()).getWsdls().add(definition);
      }
      // add well known namespaces
      model.getServiceNamespacesTopologicalSorted();
      ServiceNamespace serviceNamespace = model.getServiceNamespace(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      serviceNamespace.addSchema(XMLConstants.W3C_XML_SCHEMA_NS_URI, getClass().getClassLoader().getResource("schema.xsd"), xsdUnmarshaller);
      model.getServiceNamespacesTopologicalSorted().add(0, serviceNamespace);
   }

   private void parseTypes(Definition definition, Model model) throws JAXBException {
      for (Object o : definition.getTypes().getExtensibilityElements()) {
         if (Schema.class.isInstance(o)) {
            addSchema(Schema.class.cast(o), definition.getTargetNamespace(), model, false);
         }
      }
   }

   private void addSchema(Schema schema, String defaultNamespace, Model model, boolean checkDone) throws JAXBException {
      final String documentBaseURI = schema.getDocumentBaseURI();
      ServiceNamespace serviceNamespace = null;
      if (checkDone) {
          serviceNamespace = _done.get(documentBaseURI);
          if (verbose && serviceNamespace != null) System.out.println("Cache hit: " + documentBaseURI);
      }
      if (serviceNamespace == null) {
          Element schemaElement = schema.getElement();
          String targetNamespace = schemaElement.getAttribute("targetNamespace");
          if (targetNamespace.isEmpty() && defaultNamespace != null && !defaultNamespace.isEmpty()) {
             schemaElement.setAttribute("targetNamespace", defaultNamespace);
             schemaElement.setAttribute("xmlns", defaultNamespace);
             serviceNamespace = model.getServiceNamespace(defaultNamespace);
             serviceNamespace.addSchema(documentBaseURI, schemaElement, xsdUnmarshaller);
             schemaElement.setAttribute("targetNamespace", targetNamespace);
             schemaElement.setAttribute("xmlns", targetNamespace);
             targetNamespace = defaultNamespace;
          } else {
             serviceNamespace = model.getServiceNamespace(targetNamespace);
             serviceNamespace.addSchema(documentBaseURI, schemaElement, xsdUnmarshaller);
          }
          _done.put(documentBaseURI, serviceNamespace);
          @SuppressWarnings("unchecked")
          final Map<String, List<SchemaImport>> imports = schema.getImports();
          for (Map.Entry<String, List<SchemaImport>> entry : imports.entrySet()) {
             for (SchemaImport schemaImport : entry.getValue()) {
                addSchema(schemaImport.getReferencedSchema(), schemaImport.getNamespaceURI(), model, true);
                serviceNamespace.getReferenced().add(schemaImport.getNamespaceURI());
             }
          }
          @SuppressWarnings("unchecked")
          final List<SchemaReference> includes = schema.getIncludes();
          for (SchemaReference schemaReference : includes) {
             addSchema(schemaReference.getReferencedSchema(), targetNamespace, model, false);
          }
      }
      if (checkDone) ++serviceNamespace.predecessors;
   }

   @Override
   protected void checkConfigurationInternal(Issues issues) {
      if (outputSlot == null) issues.addError("outputSlot must be set");
      if (bindingDefinition == null) issues.addError("nameMapper must be set");
      if (fileNames.isEmpty()) issues.addWarning("fileNames must be added");
      for (String fileName : fileNames) {
         File file = new File(fileName);
         if (!file.exists()) {
            issues.addError("File not found: " + fileName);
         }
      }
   }

   @Override
   protected void invokeInternal(WorkflowContext ctx, ProgressMonitor monitor, Issues issues) {
      Model model = new Model(bindingDefinition);
      try {
         parse(model);
      } catch (WSDLException e) {
         issues.addError(e.getMessage(), e);
      } catch (JAXBException e) {
         issues.addError(e.getMessage(), e);
      }
      ctx.set(outputSlot, model);
   }

   public static Model createModel(List<String> fileNames, BindingDefinition binding, boolean verbose) throws WSDLException, JAXBException {
      WsdlParser wsdlParser = new WsdlParser();
      wsdlParser.setVerbose(verbose);
      for (String fileName : fileNames) {
         wsdlParser.addWsdlFileName(fileName);
      }
      Model model = new Model(binding);
      wsdlParser.parse(model);
      return model;
   }

}
