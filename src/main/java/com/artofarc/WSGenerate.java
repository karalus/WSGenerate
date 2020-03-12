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
package com.artofarc;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.emf.mwe.core.WorkflowContext;
import org.eclipse.emf.mwe.core.WorkflowContextDefaultImpl;
import org.eclipse.emf.mwe.core.issues.Issues;
import org.eclipse.emf.mwe.core.issues.IssuesImpl;
import org.eclipse.emf.mwe.core.issues.MWEDiagnostic;
import org.eclipse.emf.mwe.core.monitor.NullProgressMonitor;
import org.eclipse.xpand2.Generator;
import org.eclipse.xpand2.output.Outlet;
import org.eclipse.xtend.XtendComponent;
import org.eclipse.xtend.check.CheckComponent;
import org.eclipse.xtend.expression.AbstractExpressionsUsingWorkflowComponent;
import org.eclipse.xtend.expression.AbstractExpressionsUsingWorkflowComponent.GlobalVarDef;
import org.eclipse.xtend.type.impl.java.JavaBeansMetaModel;

import com.artofarc.wsimport.Model;
import com.artofarc.wsimport.BindingDefinition;
import com.artofarc.wsimport.BindingDefinitionByProperties;
import com.artofarc.wsimport.WsdlParser;

public class WSGenerate {

   public static final String ENCODING_ISO_8859_1 = "ISO-8859-1";
   public static final String VERSION;
   public static final String BUILD_TIME;
   
	static {
		Properties properties = new Properties();
		InputStream inputStream = WSGenerate.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
		if (inputStream != null) {
			try {
				properties.load(inputStream);
				inputStream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		VERSION = properties.getProperty("Implementation-Version", "0.0");
		BUILD_TIME = properties.getProperty("Build-Time", "1970-01-01 00:00UTC");
	}

   public static void main(String[] args) throws Exception {
	  System.out.println("WSGenerate Version " + VERSION + " Build Time " + BUILD_TIME); 
      CommandLine line = parse(args);
      if (line != null) {
         Issues issues = process(line);
         if (issues.hasErrors()) {
            System.exit(2);
         }
      } else {
         System.exit(1);
      }
   }

   /**
    * @param args
    */
   @SuppressWarnings("static-access")
   public static CommandLine parse(String[] args) {
      // create the parser
      Options options = new Options();
      options.addOption(new Option("help", "print this message"));
      options.addOption(new Option("verbose", "be extra verbose"));
      options.addOption(OptionBuilder.withArgName("check").hasArgs().withValueSeparator(',').withDescription("whether to check the model").create("c"));
      options.addOption(OptionBuilder.withArgName("directory").hasArg().withDescription("where to place generated output files").create("d"));
      options.addOption(OptionBuilder.withArgName("file").hasArg().withDescription("use file for mapping names").create("b"));
      options.addOption(OptionBuilder.withArgName("classname[=init param]").hasOptionalArgs().withValueSeparator()
                                     .withDescription("the class to perform name mapping").create("bc"));
      options.addOption(OptionBuilder.withArgName("property=value").hasArgs(2).withValueSeparator().withDescription("pass property to generator").create("B"));
      // parse the command line arguments
      CommandLine line = null;
      try {
         CommandLineParser parser = new BasicParser();
         line = parser.parse(options, args);
      } catch (ParseException e) {
         System.err.println("Error parsing command line: " + e.getMessage());
      }
      // create model
      if (line == null || line.hasOption("help") || line.getArgList().isEmpty()) {
         HelpFormatter formatter = new HelpFormatter();
         formatter.printHelp("WSGenerate <list of wsdl>", options);
         line = null;
      }
      return line;
   }
   
   public static Issues process(CommandLine line) throws Exception {
      BindingDefinition bindingDefinition;
      if (line.hasOption("bc")) {
         String[] optionValues = line.getOptionValues("bc");
         @SuppressWarnings("unchecked")
         Class<? extends BindingDefinition> cls = (Class<? extends BindingDefinition>) Class.forName(optionValues[0]);
         if (optionValues.length > 1) {
            Constructor<? extends BindingDefinition> ctor = cls.getConstructor(String.class);
            bindingDefinition = ctor.newInstance(optionValues[1]);
         } else {
            bindingDefinition = cls.newInstance();
         }
      } else {
         BindingDefinitionByProperties bindingDefinitionByProperties = new BindingDefinitionByProperties();
         if (line.hasOption("b")) {
            bindingDefinitionByProperties.addPropertyFile(line.getOptionValue("b"));
         }
         bindingDefinition = bindingDefinitionByProperties;
      }
      // parse WSDLs & XSDs
      @SuppressWarnings("unchecked")
      Model model = WsdlParser.createModel(line.getArgList(), bindingDefinition, line.hasOption("verbose"));
      WorkflowContext ctx = new WorkflowContextDefaultImpl();
      ctx.set("model", model);
      Properties globalProperties = line.getOptionProperties("B");
      globalProperties.setProperty("WSGenerateVersion", '"' + VERSION + '"');
      globalProperties.setProperty("WSGenerateBuildTime", '"' + BUILD_TIME + '"');
      if (line.hasOption("c")) {
         String[] optionValues = line.getOptionValues("c");
         Issues issues = check(ctx, globalProperties, optionValues);
         if (issues.hasErrors()) return issues;
      }
      // m2m
//      XtendComponent xtendComponent = new XtendComponent();
//      xtendComponent.setInvoke("oaw::extensions::simplify::transform(model)");
//      xtendComponent.setOutputSlot("model2");
//      invoke(ctx, xtendComponent, globalProperties);
//      Object object = ctx.get("model2");
      
      // Generate
      Generator generator = new Generator();
      generator.setFileEncoding(ENCODING_ISO_8859_1);
      generator.setExpand("oaw::templates::Root::_Root FOR model");
      Outlet defaultOutlet = line.hasOption("d") ? new Outlet(line.getOptionValue("d")) : new Outlet();
      defaultOutlet.setFileEncoding(ENCODING_ISO_8859_1);
      generator.addOutlet(defaultOutlet);
      return invoke(ctx, generator, globalProperties);
   }

   private static Issues check(WorkflowContext ctx, Properties globalProperties, String... checkFiles) {
      CheckComponent check = new CheckComponent();
      check.getResourceManager().setFileEncoding(ENCODING_ISO_8859_1);
      check.setAbortOnError(false);
      check.setWarnIfNothingChecked(true);
      check.setExpression("{model}.addAll(model.serviceNamespaces.wsdls).addAll(model.serviceNamespaces)");
      for (String checkFile : checkFiles) {
         check.addCheckFile("oaw::checks::" + checkFile);
      }
      return invoke(ctx, check, globalProperties);
   }

   private static Issues invoke(WorkflowContext ctx, AbstractExpressionsUsingWorkflowComponent workflowComponent, Properties globalProperties) {
      workflowComponent.addMetaModel(new JavaBeansMetaModel());
      workflowComponent.setSkipOnErrors(true);
      for (Entry<Object, Object> entry : globalProperties.entrySet()) {
         GlobalVarDef gvd = new GlobalVarDef();
         gvd.setName(entry.getKey().toString());
         gvd.setValue(entry.getValue().toString());
         workflowComponent.addGlobalVarDef(gvd);
      }
      Issues issues = new IssuesImpl();
      workflowComponent.invoke(ctx, new NullProgressMonitor(), issues);
      for (MWEDiagnostic issue : issues.getErrors()) {
         System.err.println(issue.getMessage());
      }
      for (MWEDiagnostic issue : issues.getWarnings()) {
         System.out.println(issue.getMessage());
      }
      return issues;
   }

}
