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
      File genFolder = folder.newFolder("CurrencyConvertor");
      Issues issues = invokeWSGenerate("src/test/resources/CurrencyConvertor.wsdl -c wsdl2 -B dbSchema='ICIS_SERVICES' -d", genFolder);
      assertFalse("Errors while processing", issues.hasErrors());
      File file = new File(genFolder, "plsql/NETWebserviceXWww.PKS");
      assertTrue("NETWebserviceXWww.PKS not generated", file.exists());
      file = new File(genFolder, "plsql/ECurrencyConvertorSoap.PKS");
      assertTrue("ECurrencyConvertorSoap.PKS not generated", file.exists());
      dumpFile(file);
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
