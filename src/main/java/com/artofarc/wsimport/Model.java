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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;


public final class Model {

   private final BindingDefinition bindingDefinition;
   private final LinkedHashMap<String, ServiceNamespace> serviceNamespaces = new LinkedHashMap<>();

   public Model(BindingDefinition binding) {
      bindingDefinition = binding;
   }

   public BindingDefinition getBindingDefinition() {
      return bindingDefinition;
   }

   public Set<String> getUsedNamespaces() {
      return serviceNamespaces.keySet();
   }

   public Collection<ServiceNamespace> getServiceNamespaces() {
      return serviceNamespaces.values();
   }

   public ServiceNamespace findServiceNamespace(String targetNamespace) {
      return serviceNamespaces.get(targetNamespace);
   }
   
   ServiceNamespace getServiceNamespace(String targetNamespace) {
      ServiceNamespace serviceNamespace = serviceNamespaces.get(targetNamespace);
      if (serviceNamespace == null) {
         serviceNamespace = new ServiceNamespace(this, targetNamespace);
         serviceNamespaces.put(targetNamespace, serviceNamespace);
      }
      return serviceNamespace;
   }
   
}
