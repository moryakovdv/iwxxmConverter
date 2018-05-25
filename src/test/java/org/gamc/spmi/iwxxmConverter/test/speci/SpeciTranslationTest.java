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
package org.gamc.spmi.iwxxmConverter.test.speci;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.PRESSURE_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARBecomingSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARParsingException;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARTacMessage;
import org.gamc.spmi.iwxxmConverter.speciconverter.SPECITacMessage;
import org.junit.Test;


/**Testing sample SPECIs for correct parsing*/
public class SpeciTranslationTest {

	
	/**Some simple SPECI examples*/
	
	String speciTACSimple = "SPECI ULLI 260451Z 04005MPS 6000 -SHSN FEW011 BKN016CB\n" + 
			"OVC025 M05/M06 Q1005 R10R/591036 R10L/490538 NOSIG=";
	
	
	String speciRVR = "SPECI UUWW 270505Z 02009MPS 1400 1200SW R01/1200U SN BKN008\n" + 
			"     BKN030 M03/M05 Q1006 R01/520240 TEMPO 0700 +SHSN BKN004\n" + 
			"     BKN015CB";
	
	@Test
	public void testSimpleSpeci() throws METARParsingException {
		
		SPECITacMessage speciMessage = new SPECITacMessage(speciTACSimple);
		speciMessage.parseMessage();
		
		assertEquals("ULLI", speciMessage.getIcaoCode());
		assertEquals(4, speciMessage.getMessageIssueDateTime().getHourOfDay());
		assertEquals(51, speciMessage.getMessageIssueDateTime().getMinuteOfHour());
		assertEquals(Integer.valueOf("40"), speciMessage.getCommonWeatherSection().getWindDir());
		assertEquals(SPEED_UNITS.MPS, speciMessage.getCommonWeatherSection().getSpeedUnits());
		
		assertEquals(Integer.valueOf("6000"), speciMessage.getCommonWeatherSection().getPrevailVisibility());
		assertNull(speciMessage.getCommonWeatherSection().getMinimumVisibility());
		
		assertTrue(speciMessage.getCommonWeatherSection().getCloudSections().size()==3);
		
		assertEquals("FEW",speciMessage.getCommonWeatherSection().getCloudSections().get(0).getAmount());
		
		assertEquals(new BigDecimal("-5"), speciMessage.getCommonWeatherSection().getAirTemperature());
		assertEquals(new BigDecimal("-6"), speciMessage.getCommonWeatherSection().getDewPoint());
	}
	
	@Test
	public void testRVRSpeci() throws METARParsingException {
		SPECITacMessage speciMessage = new SPECITacMessage(speciRVR);
		speciMessage.parseMessage();
		
		assertTrue(speciMessage.getRvrSections().size()>0);
		
	}
	
	
	
}
