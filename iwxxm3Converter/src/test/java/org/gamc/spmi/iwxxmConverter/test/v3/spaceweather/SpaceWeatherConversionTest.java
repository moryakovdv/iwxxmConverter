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
package org.gamc.spmi.iwxxmConverter.test.v3.spaceweather;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.METARConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SPACEWEATHERTacMessage;
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

public class SpaceWeatherConversionTest {

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

	String swxCommonTEst = "SWX ADVISORY\n" + 
			"DTG:                20161108/0100Z \n" + 
			"SWXC:               DONLON\n" + 
			"ADVISORY NR:        2016/2\n" + 
			"SWX EFFECT:         HF COM MOD AND GNSS MOD \n" + 
			"NR RPLC :           2016/1\n" + 
			"OBS SWX:            08/0100Z HNH HSH E18000 - W18000 \n" + 
			"FCST SWX +6 HR:     08/0700Z HNH HSH E18000 - W18000\n" + 
			"FCST SWX +12 HR:    08/1300Z HNH HSH E18000 - W18000\n" + 
			"FCST SWX +18 HR:    08/1900Z HNH HSH E18000 - W18000\n" + 
			"FCST SWX +24 HR:    09/0100Z NO SWX EXP\n" + 
			"RMK:                LOW LVL GEOMAGNETIC STORMING CAUSING INCREASED AURORAL ACT AND SUBSEQUENT MOD DEGRADATION OF GNSS AND HF COM AVBL IN THE AURORAL ZONE. THIS STORMING EXP TO SUBSIDE IN THE FCST PERIOD. SEE WWW.SPACEWEATHERPROVIDER.WEB \n" + 
			"NXT ADVISORY:       NO FURTHER ADVISORIES";
	
	@Test
	public void testSimpleParsing() throws ParsingException {
		SPACEWEATHERTacMessage swxTac = new SPACEWEATHERTacMessage(swxCommonTEst);
		swxTac.parseMessage();
		assertTrue(swxTac.getIssued().getYear()==2016);
		assertTrue(swxTac.getIssuingCenter().equalsIgnoreCase("DONLON"));
		assertTrue(swxTac.getAdvisoryNumber().equalsIgnoreCase("2016/2"));
		assertTrue(swxTac.getReplaceNumber().equalsIgnoreCase("2016/1"));
		
		assertTrue(swxTac.getEffects().size()>0);
		assertTrue(swxTac.getEffects().get(1).equalsIgnoreCase("GNSS_MOD"));
		
	}
	
	

}
