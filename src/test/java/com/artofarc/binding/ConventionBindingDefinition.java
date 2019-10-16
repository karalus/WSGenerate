package com.artofarc.binding;

import java.io.IOException;

import javax.wsdl.PortType;

import com.artofarc.schema.SchemaObject;
import com.artofarc.util.UriHelper;
import com.artofarc.wsimport.BindingDefinitionByProperties;


public class ConventionBindingDefinition extends BindingDefinitionByProperties {

   public ConventionBindingDefinition(String fileName) throws IOException {
      super(fileName);
      // TODO Auto-generated constructor stub
   }

   @Override
   public String getMappingForPortType(String ns, String portType, String fileType) {
      String[] l = portType.split("PortType");
      String v = l[1];
      if (v.length() == 1) v = "0" + v;
      return super.getMappingForPortType(ns, l[0], "") + fileType + v;
   }

   @Override
   public String getMappingForNamespace(String ns, String fileType) {
      ns = UriHelper.convertUri(ns, "", true);
      int i = ns.length();
      String v = "";
      char c;
      while (Character.isDigit((c = ns.charAt(--i)))) {
         v = c + v;
      }
      switch (v.length()) {
         case 1:
            v = "0" + v;
         case 2:
            ns = ns.substring(0, c == 'V' ? i : i + 1);
            break;
         default:
            v = null;
            break;
      }
      String mappingForNamespace = _propertiesExpansion.getProperty(ns, ns);
      return v != null ? mappingForNamespace + fileType + v : mappingForNamespace;
   }

   @Override
   public String getMappingForName(String ns, String name, SchemaObject annotated) {
      // TODO Auto-generated method stub
      return super.getMappingForName(ns, name, annotated);
   }

   @Override
   public boolean generateClient(PortType portType) {
      // TODO Auto-generated method stub
      return super.generateClient(portType);
   }

   @Override
   public boolean generateServer(PortType portType) {
      // TODO Auto-generated method stub
      return super.generateServer(portType);
   }

}
