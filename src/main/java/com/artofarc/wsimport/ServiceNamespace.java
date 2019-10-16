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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import javax.wsdl.Definition;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3._2001.xmlschema.Schema;
import org.w3c.dom.Element;

import com.artofarc.schema.SchemaObject;


public final class ServiceNamespace {
   
   final static JAXBContext jaxbContext;

   static {
      try {
         jaxbContext = JAXBContext.newInstance(Schema.class.getPackage().getName());
      } catch (JAXBException e) {
         throw new RuntimeException("Cannot initialize JAXBContext", e);
      }
   }

   private final Model model;
   private final String URI;
   private final List<Definition> wsdls = new ArrayList<>();
   private final LinkedHashMap<String, Schema> schemas = new LinkedHashMap<>();

   ServiceNamespace(Model model, String uri) {
      this.model = model;
      URI = uri;
   }

   public String getURI() {
      return URI;
   }
   
   public List<Definition> getWsdls() {
      return wsdls;
   }

   void addSchema(String documentBaseURI, Element element, Unmarshaller xsdUnmarshaller) throws JAXBException {
      final Schema schema = (Schema) xsdUnmarshaller.unmarshal(element);
      schemas.put(documentBaseURI, schema);
   }

   public Collection<Schema> getSchemas() {
      return schemas.values();
   }

   public Collection<String> getDocumentBaseURIs() {
      return schemas.keySet();
   }

   public ServiceNamespace findUsedServiceNamespace(String ns) {
      return model.findServiceNamespace(ns);
   }

   public String getMappingForNamespace(String fileType) {
      return model.getBindingDefinition().getMappingForNamespace(URI, fileType);
   }

   public String getMappingForName(String name, SchemaObject annotated) {
      return model.getBindingDefinition().getMappingForName(URI, name, annotated);
   }

   @Override
   public String toString() {
      return URI;
   }

}
