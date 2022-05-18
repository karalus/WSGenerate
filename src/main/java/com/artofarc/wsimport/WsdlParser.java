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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

public final class WsdlParser extends AbstractWorkflowComponent2 {

   private final static WSDLFactory wsdlFactory;

   static {
      try {
         wsdlFactory = WSDLFactory.newInstance();
      } catch (WSDLException e) {
         throw new RuntimeException("Cannot initialize wsdl4j", e);
      }
   }

   private final Unmarshaller       xsdUnmarshaller;

   private final HashSet<String>    _done     = new HashSet<>();

   private String                   outputSlot;

   private boolean                  verbose;

   private final List<String>       fileNames = new ArrayList<>();

   private BindingDefinition        bindingDefinition;

   public WsdlParser() throws JAXBException {
      xsdUnmarshaller = ServiceNamespace.jaxbContext.createUnmarshaller();
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
      wsdlReader.setFeature("javax.wsdl.verbose", verbose);
      for (String fileName : fileNames) {
         Definition definition = wsdlReader.readWSDL(fileName);
         parseTypes(definition, model);
         model.getServiceNamespace(definition.getTargetNamespace()).getWsdls().add(definition);
      }
      // add well known namespaces
      model.getServiceNamespace(XMLConstants.W3C_XML_SCHEMA_NS_URI).addSchema(XMLConstants.W3C_XML_SCHEMA_NS_URI,
            getClass().getClassLoader().getResource("schema.xsd"), xsdUnmarshaller);
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
      if (checkDone && _done.contains(documentBaseURI)) {
          if (verbose) System.out.println("Cache hit: " + documentBaseURI);
      } else {
          Element schemaElement = schema.getElement();
          String targetNamespace = schemaElement.getAttribute("targetNamespace");
          if (targetNamespace.isEmpty() && !defaultNamespace.isEmpty()) {
             targetNamespace = defaultNamespace;
             schemaElement.setAttribute("targetNamespace", targetNamespace);
             schemaElement.setAttribute("xmlns", targetNamespace);
          }
          model.getServiceNamespace(targetNamespace).addSchema(documentBaseURI, schemaElement, xsdUnmarshaller);
          _done.add(documentBaseURI);
          @SuppressWarnings("unchecked")
          final Map<String, List<SchemaImport>> imports = schema.getImports();
          for (Entry<String, List<SchemaImport>> entry : imports.entrySet()) {
             for (SchemaImport schemaImport : entry.getValue()) {
                addSchema(schemaImport.getReferencedSchema(), schemaImport.getNamespaceURI(), model, checkDone);
             }
          }
          @SuppressWarnings("unchecked")
          final List<SchemaReference> includes = schema.getIncludes();
          for (SchemaReference schemaReference : includes) {
             addSchema(schemaReference.getReferencedSchema(), targetNamespace, model, checkDone);
          }
      }
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
