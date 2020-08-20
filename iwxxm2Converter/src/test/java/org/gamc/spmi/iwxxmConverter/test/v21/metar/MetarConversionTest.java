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

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v2.METARConverter;
import org.gamc.spmi.iwxxmConverter.validation.FailedValidationAssert;
import org.gamc.spmi.iwxxmConverter.validation.IwxxmValidator;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MetarConversionTest {

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

	
	String metar = "METAR UUEE 270830Z 02006MPS 2100 -SN OVC009 M04/M06 Q1008 R06L/590230\n" + 
			"     R06R/590230 TEMPO 1000 SHSN BKN012CB R06L/590330=";
	
	String metarCavok = "METAR ULLI 261230Z 32003MPS 280V360 CAVOK M02/M16 Q1008 R88/190055\n" + 
			"NOSIG";
	
	String metarVVTS = "METAR VVTS 271830Z 24004KT 8000 SCT015 BKN040 27/26 Q1010 NOSIG =";
	
	@Test
	public void test() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException, WMORegisterException {
		METARConverter mc = new METARConverter();
		String result = mc.convertTacToXML(metar);
		
		System.out.println(result);
		
		
	}
	@Test
	public void testMetarCavok() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException, WMORegisterException {
		METARConverter mc = new METARConverter();
		String result = mc.convertTacToXML(metarCavok);
		
		System.out.println(result);
	}

	@Test
	public void testMetarVVTS() throws Exception {
		IwxxmValidator validator =new IwxxmValidator();
		validator.init();
		METARConverter mc = new METARConverter();
		String result = mc.convertTacToXML(metarVVTS);
		System.out.println(result);
		List<FailedValidationAssert> asserts = validator.validateString(result);
		System.out.println(asserts);
		assertTrue(asserts.size()==0);
		
		
	}
}
