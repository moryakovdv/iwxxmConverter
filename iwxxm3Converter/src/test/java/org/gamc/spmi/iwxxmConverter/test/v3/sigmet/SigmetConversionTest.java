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
package org.gamc.spmi.iwxxmConverter.test.v3.sigmet;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.METARConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFTacMessage;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFParsingException;
import org.gamc.spmi.iwxxmConverter.validation.FailedValidationAssert;
import org.gamc.spmi.iwxxmConverter.validation.IwxxmValidator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SigmetConversionTest {

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

	String sigmetCommonTest = "WSRS31RUMA 111143 XXX\n" + " YUDD SIGMET 2 VALID 101200/101600 YUSO-\n"
			+ "      YUDD SHANLON FIR/UIR OBSC TS FCST S OF N54 AND E OF W012 TOP FL390 MOV E 20KT WKN";

	String sigmetLineCoords = "WSRS31RUMA 111143 XXX\n" + "UUWV SIGMET 5 VALID 111200/111500 UUWV-\n"
			+ " UUWV MOSCOW FIR EMBD TSGR FCST N OF LINE N5100 E03520 - N5017 E04200\n"
			+ " AND S OF LINE N5400 E03150 - N5440 E04400 TOP FL400 STNR NC=";

	String sigmentLines = "UEEE SIGMET 2 VALID 090500/090900 UEEE-\n"
			+ "UEEE YAKUTSK FIR SEV TURB FCST N OF N70 AND E OF E135\n" + "FL310/440 STNR NC=";

	String sigmetSevIce = "WSRS31RUMA 111143 XXX\n"
			+ "LIMM SIGMET 1 VALID 281135/281435 LIIP- LIMM MILANO FIR SEV ICE OBS \n"
			+ "    AT 1120Z WI N4526 E01147 - N4442 E01107 - N4352 E01219 - N4404 \n"
			+ "    E01330 - N4526 E01147 FL180/240 MOV E NC";

	String sigmetHeavyGR = "WSRS31RUMA 111143 XXX\n" + " YUDD SIGMET 2 VALID 101200/101600 YUSO-\n"
			+ "      YUDD SHANLON FIR/UIR FRQ TSGR S OF N54 AND E OF W012 TOP FL390 MOV E 20KT WKN";

	String sigmetTestYUDD = "WSRS31RUMA 111143 XXX\n" + "YUDD SIGMET 2 VALID 101200/101600 YUSO-\r\n" + 
			"      YUDD SHANLON FIR/UIR OBSC TS FCST S OF N54 AND E OF W012 TOP FL390 MOV E 20KT WKN";
	String sigmetTestCnslYUDD = "WSRS31RUMA 111143 XXX\n" + "YUDD SIGMET 3 VALID 101345/101600 YUSO-\r\n" + 
			"      YUDD SHANLON FIR/UIR CNL SIGMET 2 101200/101600";

	@Test
	public void testModIceSigmet()
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {
		SIGMETConverterV3 mc = new SIGMETConverterV3();
		String result = mc.convertTacToXML(sigmetSevIce);

		System.out.println(result);
	}

	/**@author alex*/
	@Test
	public void testTest()
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {
		SIGMETConverterV3 mc = new SIGMETConverterV3();
		String result = mc.convertTacToXML(sigmetTestYUDD);

		System.out.println(result);
	}
	/**@author alex*/
	//@Test
	public void testCnslTest()
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {
		SIGMETConverterV3 mc = new SIGMETConverterV3();
		String result = mc.convertTacToXML(sigmetTestCnslYUDD);

		System.out.println(result);
	}
	// @Test
	public void testCommonSigmet()
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {

		SIGMETConverterV3 mc = new SIGMETConverterV3();
		String result = mc.convertTacToXML(sigmetHeavyGR);

		System.out.println(result);

	}

	// @Test
	public void testSigmetWithLineCoords()
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {
		SIGMETConverterV3 mc = new SIGMETConverterV3();
		String result = mc.convertTacToXML(sigmetLineCoords);

		System.out.println(result);

	}

	// @Test
	public void testLine()
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {
		SIGMETConverterV3 mc = new SIGMETConverterV3();
		String result = mc.convertTacToXML(sigmentLines);

		System.out.println(result);

	}

}
