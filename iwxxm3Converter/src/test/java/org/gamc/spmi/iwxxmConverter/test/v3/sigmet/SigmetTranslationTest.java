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
 * WITHOU T WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gamc.spmi.iwxxmConverter.test.v3.sigmet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETTacMessage;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.junit.Test;

import schemabindings31._int.icao.iwxxm._3.SIGMETType;

public class SigmetTranslationTest {

	String referenceSigmet1 = "WSCH31SCFA 011035\n" + 
			"SCFZ SIGMET A1 VALID 011035/011435 SCFA-\n" + 
			"SCFZ ANTOFAGASTA FIR SEV ICE FCST E OF LINE S2127 W06840 - S2320 \n" + 
			"W06803 - S2442 W06846 FL180/280 STNR NC=";
	
	String refrenceSigmet2 = "WSCH31SCFA 011035\n" + 
			"SCFZ SIGMET 2 VALID 011035/011435 SCFA-\n" + 
			"SCFZ ANTOFAGASTA FIR SEV TURB OBS AT 1200Z WI N4230 E02052 – N4245 E02145 – N4130 E02200 – N4107\n" + 
			"E02130 – N4123 E02045- N4230 E02052 FL250/370 WKN FCST AT 1600Z WI N4230 E02052 – N4145 E02245 –\n" + 
			"N4045 E02330 – N4040 E02248 – N4123 E02045 – N4230 E02052=";
	
	
	String refrenceSigmet3 = "WSCH31SCFA 011035\n" + 
			"SCFZ SIGMET 2 VALID 011035/011435 SCFA-\n" + 
			"SCFZ ANTOFAGASTA FIR SEV TURB FCST AT 1200Z WI N4230 E02052 – N4245 E02145 – N4130 E02200 – N4107\n" + 
			"E02130 – N4123 E02045- N4230 E02052 FL250/370 WKN FCST AT 1600Z WI N4530 E02052 – N4145 E02245 –\n" + 
			"N4045 E02330 – N4040 E02248 – N4123 E02045 – N4530 E02052=";
	
	@Test
	public void testReference1() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException, WMORegisterException {
		SIGMETConverterV3<SIGMETTacMessage, SIGMETType> c1 = new SIGMETConverterV3<SIGMETTacMessage, SIGMETType>();
		SIGMETTacMessage tac = new SIGMETTacMessage(referenceSigmet1);
		tac.parseMessage();
		
		assertEquals("A1", tac.getSigmetNumber());
		assertFalse(tac.getHorizontalLocation().getDirectionsFromLines().isEmpty());
		assertTrue(tac.getVerticalLocation().getBottomFL().get()==180);
		assertTrue(tac.getVerticalLocation().getTopFL().get()==280);
		
		c1.convertMessage(tac);
		
	}
	
	@Test
	public void test_OBS_and_FCST_Sigmet() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException, WMORegisterException {
		SIGMETConverterV3<SIGMETTacMessage, SIGMETType> c1 = new SIGMETConverterV3<SIGMETTacMessage, SIGMETType>();
		SIGMETTacMessage tac = new SIGMETTacMessage(refrenceSigmet2);
		tac.parseMessage();
		
		assertEquals("2", tac.getSigmetNumber());
		assertTrue(tac.getHorizontalLocation().isInPolygon());
		assertEquals(12, tac.getPhenomenonDescription().getPhenomenonTimeStamp().getHourOfDay());
		assertTrue(tac.getVerticalLocation().getBottomFL().get()==250);
		assertTrue(tac.getVerticalLocation().getTopFL().get()==370);
		
		assertNotNull(tac.getPhenomenonDescription().getForecastSection());
		
		assertEquals(1, tac.getPhenomenonDescription().getForecastSection().size());
		assertEquals(16, tac.getPhenomenonDescription().getForecastSection().get(0).getForecastedTime().getHourOfDay());
		assertTrue(tac.getPhenomenonDescription().getForecastSection().get(0).getHorizontalLocation().isInPolygon());
		assertEquals(6, tac.getPhenomenonDescription().getForecastSection().get(0).getHorizontalLocation().getPolygonPoints().size());
		
		
		c1.convertMessage(tac);
	}
	
	
	@Test
	public void test_FCST_and_FCST_Sigmet() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException, WMORegisterException {
		SIGMETConverterV3<SIGMETTacMessage, SIGMETType> c1 = new SIGMETConverterV3<SIGMETTacMessage, SIGMETType>();
		SIGMETTacMessage tac = new SIGMETTacMessage(refrenceSigmet3);
		tac.parseMessage();
		
		assertEquals("2", tac.getSigmetNumber());
		assertTrue(tac.getHorizontalLocation().isInPolygon());
		assertEquals(12, tac.getPhenomenonDescription().getPhenomenonTimeStamp().getHourOfDay());
		assertTrue(tac.getVerticalLocation().getBottomFL().get()==250);
		assertTrue(tac.getVerticalLocation().getTopFL().get()==370);
		
		assertNotNull(tac.getPhenomenonDescription().getForecastSection());
		assertEquals(1, tac.getPhenomenonDescription().getForecastSection().size());
		assertEquals(16, tac.getPhenomenonDescription().getForecastSection().get(0).getForecastedTime().getHourOfDay());
		assertTrue(tac.getPhenomenonDescription().getForecastSection().get(0).getHorizontalLocation().isInPolygon());
		assertEquals(6, tac.getPhenomenonDescription().getForecastSection().get(0).getHorizontalLocation().getPolygonPoints().size());
		
		
		String result = c1.marshallMessageToXML(c1.convertMessage(tac));
		System.out.println(result);
	}
	
}
