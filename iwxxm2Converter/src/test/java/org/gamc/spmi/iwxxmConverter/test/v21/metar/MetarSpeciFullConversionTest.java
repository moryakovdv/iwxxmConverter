/**
 * Copyright (C) 2018 Dmitry Moryakov, Main aeronautical meteorological center, Moscow, Russia
 * moryakovdv[at]gmail[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gamc.spmi.iwxxmConverter.test.v21.metar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v2.ConverterFactory;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.FiredRule;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;

import com.helger.schematron.ISchematronResource;
import com.helger.schematron.svrl.SVRLMarshaller;
import com.helger.schematron.xslt.SchematronResourceSCH;

public class MetarSpeciFullConversionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	Pattern selectAllMetarPattern = Pattern.compile("(METAR[^=]*)(?==)");
	Pattern selectAllSpeciPattern = Pattern.compile("(SPECI[^=]*)(?==)");
	
	final ISchematronResource aResSCH = SchematronResourceSCH
			.fromClassPath("iwxxm/schematron/iwxxm.sch");
			
	AtomicInteger failedRulesCounter = new AtomicInteger(0);
	
	@Test
	public void testMetars() throws IOException, ParsingException, DatatypeConfigurationException, JAXBException {
		testConvertionAndValidation(selectAllMetarPattern,"METAR");
	}
	
	@Test
	public void testSpecies() throws IOException, ParsingException, DatatypeConfigurationException, JAXBException {
		testConvertionAndValidation(selectAllSpeciPattern,"SPECI");
	}
	
	/**Performs validation using PH_Schematron*/
	private void testValidation(String inputXmlFile, String outReportFile) throws Exception {
		
		if (!aResSCH.isValidSchematron())
			throw new IllegalArgumentException("Invalid Schematron!");
		
			//System.setProperty("jaxp.debug", "true");
			File fTest = new File(inputXmlFile);
			File fOut = new File(outReportFile);
			
			
				
			SchematronOutputType results = aResSCH.applySchematronValidationToSVRL(new StreamSource(fTest));
			
			List<Object> allAsserts = results.getActivePatternAndFiredRuleAndFailedAssert();
			for (Object object : allAsserts) {
				
				if (object instanceof FiredRule) {
					FiredRule rule = (FiredRule)object;
					//System.out.println("Check rule: "+rule);
				}
				
			    if (object instanceof FailedAssert) {
			        FailedAssert failedAssert = (FailedAssert) object;
			        System.out.println(failedAssert.getText());
			        System.out.println(failedAssert.getTest());
			        failedRulesCounter.incrementAndGet();
			    }
			    
			}	
			SVRLMarshaller m = new SVRLMarshaller();
			m.write(results, fOut);
			
			 
		}
	int counter = 1;
	/**gets messages from input file using Pattern*/
	private void testConvertionAndValidation(Pattern p, String outputPrefix) throws IOException, ParsingException, DatatypeConfigurationException, JAXBException {
		//System.out.println(convertTacToXML(testTaf));

		InputStream fs = this.getClass().getResourceAsStream("/examples/metar-speci.txt");
		byte[] b = new byte[fs.available()];

		fs.read(b);

		String messages = new String(b, "UTF-8");

		Matcher m = p.matcher(messages);
		
		while (m.find()) {
			// System.out.println(m.group());
			System.out.println("Message " + counter);
			String msg = m.group();
			System.out.println(msg);
			String conversionResults ="";
			try {
				conversionResults = convertTacToXML(m.group());
			}
			catch(ParsingException e) {
				System.out.println("Skipped message because of Parsing error " + e);
				continue;
			}
			String path = "output/"+outputPrefix+counter+".xml";
			String pathReport = "output/"+outputPrefix+counter+".svrl";
			
			counter++;
			
			File f = new File(path);
			
			try(FileOutputStream fsOut = new FileOutputStream(f);) {
				fsOut.write(conversionResults.getBytes("UTF-8"));
			}
			catch(Exception e) {
				System.out.println(e);
				continue;
			}
			try {
				//crux.validate(null, "iwxxm/schematron/iwxxm.sch", f.getPath());
				
				testValidation(f.getPath(), pathReport);
				
			}
			catch(Exception e) {
				System.out.println(e);
				continue;
			}
					
			
			

		}
	}
	
	/**Performs xml conversion using ConverterFactory*/
	private String convertTacToXML(String message) throws ParsingException, UnsupportedEncodingException, DatatypeConfigurationException, JAXBException {
		TacConverter<?, ?,?> tc = ConverterFactory.createForTac(message); 
		String result = tc.convertTacToXML(message);
		return result;
	}
	

}
