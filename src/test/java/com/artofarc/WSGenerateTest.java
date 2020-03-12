package com.artofarc;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.cli.CommandLine;
import org.eclipse.emf.mwe.core.issues.Issues;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class WSGenerateTest {

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();
   
   @Test
   public void testCurrencyConvertor() throws Exception {
      // @see http://www.webservicex.com/CurrencyConvertor.asmx?wsdl
      File genFolder = new File("C:\\dev\\git\\WSGenerate\\output");// folder.newFolder("CurrencyConvertor");
      Issues issues = invokeWSGenerate("src/test/resources/CurrencyConvertor.wsdl -c wsdl2 -B dbSchema='ICIS_SERVICES' -d", genFolder);
      assertFalse("Errors while processing", issues.hasErrors());
      File file = new File(genFolder, "plsql/NETWebserviceXWww.PKS");
      assertTrue("NETWebserviceXWww.PKS not generated", file.exists());
      file = new File(genFolder, "plsql/ECurrencyConvertorSoap.PKS");
      assertTrue("ECurrencyConvertorSoap.PKS not generated", file.exists());
      dumpFile(file);
   }
   
   @Test
   public void testErledigungVoruebergehendeVerwahrung() throws Exception {
      File genFolder = new File("output");
      Issues issues = invokeWSGenerate("input/de.itzbund.cbt.zoll.VVUW.service.ErledigungVoruebergehendeVerwahrung1.wsdl -c wsdl2 -B dbSchema='SUMA' -b input/mapping.properties -d", genFolder);
      assertFalse("Errors while processing", issues.hasErrors());
   }
   
   @Test
   public void testEAS2() throws Exception {
      File genFolder = new File("output");
      Issues issues = invokeWSGenerate("input/ifc2/de.itzbund.cbt.zoll.ESUW.service.NESRiskAnalysis1.wsdl input/de.itzbund.cbt.zoll.VVUW.service.ErledigungVoruebergehendeVerwahrung1.wsdl -B dbSchema='sys_tlnr' -b input/mapping.properties -d", genFolder);
      assertFalse("Errors while processing", issues.hasErrors());
   }
   
   @Test
   public void testKonvention() {
	   System.out.println(String.format("%.1s%s_%02d", "Client", "CbtSharedSoa", null));
	   System.out.println(String.format("%sE01", "CbtSharedSoaV1"));
	   System.out.println(String.format("PKC_%s", "CbtSharedSoaV1"));
   }
   
   private static void dumpFile(File file) throws IOException {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;
      while ((line = br.readLine()) != null) {
         System.out.println(line);
      }
      br.close();
   }

   private static Issues invokeWSGenerate(Object...params) throws Exception {
      ArrayList<String> args = new ArrayList<>();
      for (Object param : params) {
         StringTokenizer tokenizer = new StringTokenizer(param.toString(), " ");
         while (tokenizer.hasMoreElements()) {
            args.add(tokenizer.nextToken());
         }
      }
      CommandLine line = WSGenerate.parse(args.toArray(new String[args.size()]));
      assertNotNull("Parameters could not be parsed", line);
      return WSGenerate.process(line);
   }

}
