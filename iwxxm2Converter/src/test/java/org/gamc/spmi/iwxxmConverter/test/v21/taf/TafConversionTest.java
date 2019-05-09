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
package org.gamc.spmi.iwxxmConverter.test.v21.taf;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v2.TAFConverter;
import org.gamc.spmi.iwxxmConverter.marshallers.v2.TAFTacMessage;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFParsingException;
import org.gamc.spmi.iwxxmConverter.validation.FailedValidationAssert;
import org.gamc.spmi.iwxxmConverter.validation.IwxxmValidator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TafConversionTest {

	static IwxxmValidator validator; 
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		validator = new IwxxmValidator();
		validator.init();
		
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

	
	
	@Test
	public void testValidTaf() throws Exception {
		
		String testTaf = "TAF LZIB 101058Z 1012/1112 24015KT 9999 -SHRA FEW018 BKN028 TX10/1112Z TN05/1100Z BECMG 1014/1017 18012KT BECMG 1104/1107 14022KT 7000 RADZ SCT010 BKN015CB OVC025";
		
		TAFConverter tafconverter = new TAFConverter();
		String iwxxm = tafconverter.convertTacToXML(testTaf);
		
		List<FailedValidationAssert> failedResults = validator.validateString(iwxxm);
		
		assertTrue(failedResults.size()==0);
		
		
	}
	
	@Test
	public void testVRBTaf() throws TAFParsingException {
		
		String tafVrb = "TAF UUWW 241057Z 2412/2512 VRB01MPS 9999 SCT020 TX00/2412Z\n" + 
				"TNM07/2503Z BECMG 2504/2506 24006MPS";
		TAFTacMessage ttm = new TAFTacMessage(tafVrb);
		ttm.parseMessage();
		
		assertTrue(ttm.getCommonWeatherSection().isVrb());
		assertTrue(ttm.getCommonWeatherSection().getWindVrbSpeed()!=null);
		
	}
	
	@Test(expected =ParsingException.class)
	/**CAVOK with visibility and clouds */
	public void testMisplacedCAVOKTaf() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {
		
		String testTaf = "TAF UUBW 220457Z 2206/2306 28005MPS CAVOK 8000 BKN012\n" + 
				"TXM10/2212Z TNM16/2305Z\n" + 
				"TEMPO 2206/2215 3100 -SHSN BKN015CB=";
		
		TAFConverter tafconverter = new TAFConverter();
		String iwxxm = tafconverter.convertTacToXML(testTaf);
		System.out.println(iwxxm);
		
		/*
		List<FailedAssert> failedResults;
		try {
			failedResults = validator.validateString(iwxxm);
			assertTrue(failedResults.size()==0);
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		
		*/
	}
	
	@Test()
	public void testVerticalVisibilityTaf() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {
		
		String testTaf = "TAF ULLI 220455Z 2206/2306 27003MPS 0300 FZFG VV010 TEMPO 2206/2209\n" + 
				"1500 BR OVC002 BECMG 2209/2210 6000 NSW SCT011 BECMG 2215/2217 3100\n" + 
				"BR BKN003 TEMPO 2217/2306 0400 FZFG=";
		
		TAFConverter tafconverter = new TAFConverter();
		String iwxxm = tafconverter.convertTacToXML(testTaf);
		System.out.println(iwxxm);
		
		
		List<FailedValidationAssert> failedResults;
		try {
			failedResults = validator.validateString(iwxxm);
			assertTrue(failedResults.size()==0);
			for(FailedValidationAssert fAssert : failedResults) {
				System.out.println(fAssert);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Test()
	/**Invalid taf - VV misplaced error*/
	public void testInvalidTaf() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {
		
		String testTaf = "TAF ULLI 220455Z 2206/2306 27003MPS 0300 FZFG VV010 SCT010 TEMPO 2206/2209\n" + 
				"1500 BR OVC002 BECMG 2209/2210 6000 NSW SCT011 BECMG 2215/2217 3100\n" + 
				"BR BKN003 TEMPO 2217/2306 0400 FZFG=";
		
		TAFConverter tafconverter = new TAFConverter();
		String iwxxm = tafconverter.convertTacToXML(testTaf);
		System.out.println(iwxxm);
		
		
		List<FailedValidationAssert> failedResults;
		try {
			failedResults = validator.validateString(iwxxm);
			assertTrue(failedResults.size()>0);
			for(FailedValidationAssert fAssert : failedResults) {
				System.out.println(fAssert);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	
	String testProbabilityTaf = "TAF UUWW 270203Z 2703/2803 36006MPS 3100 -SN BR OVC007 TX00/2703Z\\n\" + \n"
			+ "			\"TNM06/2724Z TEMPO 2703/2710 0700 +SHSN OVC003 BKN015CB PROB40 TEMPO\\n\" + \n"
			+ "			\"2703/2710 -FZDZ BECMG 2712/2714 9000 NSW BKN012";
	@Test
	public void testProbabilityTaf() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {
		TAFConverter tafconverter = new TAFConverter();
		String iwxxm = tafconverter.convertTacToXML(testProbabilityTaf);
		System.out.println(iwxxm);
		
		
		List<FailedValidationAssert> failedResults;
		try {
			failedResults = validator.validateString(iwxxm);
			assertTrue(failedResults.size()==0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
