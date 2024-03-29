package com.artofarc;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.cli.CommandLine;
import org.eclipse.emf.mwe.core.issues.Issues;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WSGenerateTest {

	public static void introspect(List<?> list) {
		System.out.println(list);
	}
	
   @Rule
   public TemporaryFolder folder = new TemporaryFolder();
   
   @Test
   public void testCurrencyConvertor() throws Exception {
      // @see http://www.webservicex.com/CurrencyConvertor.asmx?wsdl
      File genFolder = new File("C:\\dev\\git\\WSGenerate\\output");// folder.newFolder("CurrencyConvertor");
      Issues issues = invokeWSGenerate("src/test/resources/CurrencyConvertor.wsdl -c wsdl2 -B dbSchema='ICIS_SERVICES' -d", genFolder);
      assertFalse("Errors while processing", issues.hasErrors());
      File file = new File(genFolder, "plsql/ICIS_SERVICES/NETWebserviceXWwwV0.PKS");
      assertTrue("NETWebserviceXWwwV0.PKS not generated", file.exists());
      file = new File(genFolder, "plsql/ICIS_SERVICES/CCurrencyConvertorSoap0.PKS");
      assertTrue("CCurrencyConvertorSoap0.PKS not generated", file.exists());
      dumpFile(file);
   }
   
   @Test
   public void testKonvention() throws URISyntaxException {
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
