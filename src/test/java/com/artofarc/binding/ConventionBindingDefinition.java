package com.artofarc.binding;

import java.io.IOException;

import com.artofarc.wsimport.ArtifactKind;
import com.artofarc.wsimport.BindingDefinitionByProperties;


public class ConventionBindingDefinition extends BindingDefinitionByProperties {

   public ConventionBindingDefinition(String fileName) throws IOException {
      super(fileName);
      _propertiesExpansion.setProperty(ArtifactKind.ClientStub.name(), "%2$s%1$.1s%02d");
   }

   @Override
   public String getMappingForPortType(String ns, String portType, ArtifactKind kind) {
      return super.getMappingForPortType(ns, portType.replace("PortType", ""), kind);
   }

}
