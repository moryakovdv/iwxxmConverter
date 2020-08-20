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
 * distributed under  the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gamc.spmi.iwxxmConverter.test.v3.airmet;

import java.io.UnsupportedEncodingException;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.AIRMETConverterV3;
import org.gamc.spmi.iwxxmConverter.validation.IwxxmValidator;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AirmetConversionTest {

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

	String airmetTestYUDD = "WARS51 RUMA 151130\r\n" + 
			"  YUDD AIRMET 1 VALID 151520/151800 YUSO- \r\n" + 
			"    YUDD SHANLON FIR ISOL TS \r\n" + 
			"    OBS N OF S50 TOP ABV FL100 STNR WKN=";
	
	String airmetTestCnslYUDD = "WARS51 RUMA 151355\r\n" + 
			"UUWV AIRMET 4 VALID 151400/151600 UUWV–\r\n" + 
			"UUWV MOSCOW FIR CNL AIRMET 2 151200/151600=";

	/**@author alex
	 * @throws WMORegisterException */
	@Test
	public void testTest()
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException, WMORegisterException {
		AIRMETConverterV3 mc = new AIRMETConverterV3();
		String result = mc.convertTacToXML(airmetTestYUDD);

		System.out.println(result);
	}
	/**@author alex
	 * @throws WMORegisterException */
	@Test
	public void testCnslTest()
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException, WMORegisterException {
		AIRMETConverterV3 mc = new AIRMETConverterV3();
		String result = mc.convertTacToXML(airmetTestCnslYUDD);

		System.out.println(result);
	}

}
