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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.general.MetarForecastTimeSection;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.PRESSURE_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARBecomingSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARParsingException;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARTacMessage;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARTimedATSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARTimedTLSection;
import org.junit.Test;


/**Testing sample METARs for correct parsing*/
public class MetarTranslationTest {

	
	/**Sample description of incoming METAR in TAC-form:
	 * Icao=EDDF(FRANKFURT),
	 * TimeStamp=28th of current month 06:50 UTC,
	 * Wind direction = 320 degrees
	 * Wind speed =12 knots, 
	 * VRB = between 280 and 350 deg,
	 * Visibility = 10 kilometers, 
	 * Cloud Level 1 = FEW at 2500 feet, 
	 * Cloud Level 2 = SCT at 10000 feet, 
	 * Temperature =13, 
	 * Dewpoint =7, 
	 * QNH =1022 hPa,
	 * Changes = nosig.
	 * */
	
	
	
	/**Some simple METAR examples*/
	
	String metarTACSimple = "METAR EDDF 280650Z 32012KT 280V350 9999 FEW025 SCT100 13/07 Q1022 NOSIG=";
	
	String metarTACCAVOK = "METAR LZIB 211630Z 08005KT 060V120 CAVOK 26/15 Q1022 NOSIG=";
	
	String metarTACVRB = "METAR EDDF 280650Z VRB05KT 1200 0900NW FG FEW025 SCT100 OVC060 M02/M07 Q0972 NOSIG=";
	
	String metarTACBecoming = "METAR VHHH 251600Z 24015G25KT 200V280 0600 R07L/1000U R17R/2000D FG DZ SCT010 OVC020 17/16 Q1018 BECMG TL1700 0800 FG BECMG AT1800 9999 NSW=";
	
	String metarTACWeather = "METAR LZIB 211630Z 08005KT 060V120 1200 0900NW FG VCTS +SHRA FEW020 26/15 Q1022 NOSIG=";
	
	String metarTACWindShearRW = "METAR LZIB 211630Z 08005KT 060V120 1200 0900NW FG VCTS +SHRA FEW020 26/15 Q1022 WS RWY04L NOSIG=";
	
	String metarTACWindShearALL = "METAR LZIB 211630Z 08005KT 060V120 1200 0900NW FG VCTS +SHRA FEW020 26/15 Q1022 WS ALL RWY RETS NOSIG=";
	
	String metarTACVerticalVisibilityNotObserved = "METAR LZIB 211630Z 08005KT 060V120 1200 0900NW FG VCTS +SHRA VV/// 26/15 Q1022 WS ALL RWY RETS NOSIG=";
	
	String metarTACVerticalVisibilityObserved = "METAR LZIB 211630Z 08005KT 060V120 1200 0900NW FG VCTS +SHRA VV020 26/15 Q1022 WS ALL RWY RETS NOSIG=";
	
	String metarTACTempo = "METAR UUWW 171130Z 01007G12MPS 9999 -RA OVC032 08/04 Q1021 R06/190060 TEMPO 36015MPS SN";
	
	String metarTACContamination="METAR UUWW 171130Z 01007G12MPS 9999 R06L/M6000FT -RA OVC032 08/04 Q1021 R06L/123456 TEMPO 36015MPS SN";
	String metarTACContaminationAllRunways="METAR UUWW 171130Z 01007G12MPS 9999 R06L/M6000FT -RA OVC032 08/04 Q1021 R88/12//56 TEMPO 36015MPS SN";
	
	String metarUS = "METAR KORD 041656Z 19020G26KT 6SM -SHRA BKN070 12/08 A3016 RMK AO2";
	
	String metarUS1 = "METAR KORD 170651Z 20014G23KT 10SM SCT250 24/14 A2968 RMK AO2 SLP045 T02390144=";
	
	
	@Test
	public void translateSimpleMetar() throws METARParsingException {
		
		METARTacMessage metarMessage = new METARTacMessage(metarTACSimple);
		metarMessage.parseMessage();
		
		assertEquals("EDDF", metarMessage.getIcaoCode());
		assertEquals(50, metarMessage.getMessageIssueDateTime().getMinuteOfHour());
		assertEquals(Integer.valueOf("320"), metarMessage.getCommonWeatherSection().getWindDir());
		assertEquals(SPEED_UNITS.KT, metarMessage.getCommonWeatherSection().getSpeedUnits());
		assertEquals(Integer.valueOf("350"), metarMessage.getCommonWeatherSection().getWindVariableTo());
		
		assertEquals(Integer.valueOf("9999"), metarMessage.getCommonWeatherSection().getPrevailVisibility());
		assertNull(metarMessage.getCommonWeatherSection().getMinimumVisibility());
		
		assertTrue(metarMessage.getCommonWeatherSection().getCloudSections().size()>0);
		
		assertEquals("FEW",metarMessage.getCommonWeatherSection().getCloudSections().get(0).getAmount());
		
		assertEquals(new BigDecimal("13"), metarMessage.getCommonWeatherSection().getAirTemperature());
		assertEquals(new BigDecimal("7"), metarMessage.getCommonWeatherSection().getDewPoint());
		
		
		
	}
	
	@Test
	public void translateVRBMetar() throws METARParsingException {
		METARTacMessage metarMessage = new METARTacMessage(metarTACVRB);
		metarMessage.parseMessage();
		
		assertEquals("EDDF", metarMessage.getIcaoCode());
		assertEquals(50, metarMessage.getMessageIssueDateTime().getMinuteOfHour());
		
		assertTrue(metarMessage.getCommonWeatherSection().isVrb());
		
		assertEquals(Integer.valueOf("05"), metarMessage.getCommonWeatherSection().getWindVrbSpeed());
		assertEquals(SPEED_UNITS.KT, metarMessage.getCommonWeatherSection().getVrbSpeedUnits());
		
		assertEquals(Integer.valueOf("1200"), metarMessage.getCommonWeatherSection().getPrevailVisibility());
		assertEquals(Integer.valueOf("900"), metarMessage.getCommonWeatherSection().getMinimumVisibility());
		assertEquals(RUMB_UNITS.NW, metarMessage.getCommonWeatherSection().getMinimumVisibilityDirection());
		
		assertEquals(new BigDecimal("-2"), metarMessage.getCommonWeatherSection().getAirTemperature());
		assertEquals(new BigDecimal("-7"), metarMessage.getCommonWeatherSection().getDewPoint());
	}
	
	@Test
	public void translateCAVOKMetar() throws METARParsingException {
		METARTacMessage metarMessage = new METARTacMessage(metarTACCAVOK);
		metarMessage.parseMessage();
		
		assertEquals("LZIB", metarMessage.getIcaoCode());
		assertEquals(30, metarMessage.getMessageIssueDateTime().getMinuteOfHour());
		assertTrue(metarMessage.getCommonWeatherSection().isCavok());
		
		assertEquals(new BigDecimal("26"), metarMessage.getCommonWeatherSection().getAirTemperature());
		assertEquals(new BigDecimal("15"), metarMessage.getCommonWeatherSection().getDewPoint());
		
	}
	
	@Test
	public void translateBecomingMetar() throws METARParsingException {
		METARTacMessage metarMessage = new METARTacMessage(metarTACBecoming);
		metarMessage.parseMessage();
		
		assertEquals("VHHH", metarMessage.getIcaoCode());
		assertEquals(16, metarMessage.getMessageIssueDateTime().getHourOfDay());
		assertEquals(Integer.valueOf("240"), metarMessage.getCommonWeatherSection().getWindDir());
		assertEquals(SPEED_UNITS.KT, metarMessage.getCommonWeatherSection().getSpeedUnits());
		assertEquals(Integer.valueOf("15"), metarMessage.getCommonWeatherSection().getWindSpeed());
		
		assertEquals(Integer.valueOf("25"), metarMessage.getCommonWeatherSection().getGustSpeed());
		
		assertEquals(Integer.valueOf("200"), metarMessage.getCommonWeatherSection().getWindVariableFrom());		
		assertEquals(Integer.valueOf("280"), metarMessage.getCommonWeatherSection().getWindVariableTo());
		
		assertEquals(Integer.valueOf("600"), metarMessage.getCommonWeatherSection().getPrevailVisibility());
		assertNull(metarMessage.getCommonWeatherSection().getMinimumVisibility());
		
		assertTrue(metarMessage.getRvrSections().size()==2);
		
		assertTrue(metarMessage.getCommonWeatherSection().getCurrentWeather().size()>0);
		assertTrue(metarMessage.getCommonWeatherSection().getCurrentWeather().contains("DZ"));
		assertEquals(new BigDecimal("17"), metarMessage.getCommonWeatherSection().getAirTemperature());
		assertEquals(new BigDecimal("16"), metarMessage.getCommonWeatherSection().getDewPoint());
		
		
	}
	
	@Test
	public void parseBecomingSectionInMetar() throws ParsingException {
		METARTacMessage metarMessage = new METARTacMessage(metarTACBecoming);
		metarMessage.parseMessage();
		
		assertTrue(metarMessage.getTimedSections().size()==2);
		
		for(MetarForecastTimeSection becmg : metarMessage.getTimedSections()) {
			becmg.parseSection();
			
		}
		
		assertEquals(METARTimedTLSection.class,metarMessage.getTimedSections().get(0).getClass());
		assertEquals(METARTimedATSection.class,metarMessage.getTimedSections().get(1).getClass());
		assertEquals(17, metarMessage.getTimedSections().get(0).getDateTimeTILL().getHourOfDay());
		assertEquals(18, metarMessage.getTimedSections().get(1).getDateTimeAT().getHourOfDay());
		
	}
	
	@Test
	public void cutOffBecomingSections() throws METARParsingException {
		METARTacMessage metarMessage = new METARTacMessage(metarTACBecoming);
		metarMessage.fillAndRemoveTrendSections(new StringBuffer(metarTACBecoming));
	}
	
	@Test
	public void translateWindShear() throws METARParsingException {
		METARTacMessage metarMessage = new METARTacMessage(metarTACWindShearRW);
		metarMessage.parseMessage();
		assertTrue(metarMessage.getWindShearSections().contains("04L"));
		assertTrue(metarMessage.getCommonWeatherSection().getCurrentWeather().contains("+SHRA"));
		
	}
	
	@Test
	public void translateWindShearAndRecentWeather() throws METARParsingException {
		METARTacMessage metarMessage = new METARTacMessage(metarTACWindShearALL);
		metarMessage.parseMessage();
		assertTrue(metarMessage.isWindShearForAll());
		
		assertTrue(metarMessage.getCommonWeatherSection().getRecentWeather().get(0).equalsIgnoreCase("TS"));
		
		
		
	}
	
	@Test
	public void translateVerticalVisibilityNotObserved() throws METARParsingException {
		METARTacMessage metarMessage = new METARTacMessage(metarTACVerticalVisibilityNotObserved);
		metarMessage.parseMessage();
		assertTrue(metarMessage.getCommonWeatherSection().getCloudSections().size()==1);
		assertTrue(metarMessage.getCommonWeatherSection().getCloudSections().get(0).isVerticalVisibility());
		assertNull(metarMessage.getCommonWeatherSection().getCloudSections().get(0).getHeight());
		
		
	}
	
	@Test
	public void translateVerticalVisibility() throws METARParsingException {
		METARTacMessage metarMessage = new METARTacMessage(metarTACVerticalVisibilityObserved);
		metarMessage.parseMessage();
		assertTrue(metarMessage.getCommonWeatherSection().getCloudSections().size()==1);
		assertTrue(metarMessage.getCommonWeatherSection().getCloudSections().get(0).isVerticalVisibility());
		assertNotNull(metarMessage.getCommonWeatherSection().getCloudSections().get(0).getHeight());
		
		
	}
	
	@Test
	public void translateUSMetar() throws METARParsingException {
		METARTacMessage metarMessage = new METARTacMessage(metarUS);
		metarMessage.parseMessage();
		assertEquals(PRESSURE_UNITS.INCH_OF_MERCURY, metarMessage.getCommonWeatherSection().getQnhUnits());
		assertEquals(LENGTH_UNITS.SM, metarMessage.getCommonWeatherSection().getVisibilityUnits());
		
		assertTrue(metarMessage.getRemarkSections().size()>0);
			
	}
	
	@Test
	public void translateUS1Metar() throws METARParsingException {
		METARTacMessage metarMessage = new METARTacMessage(metarUS1);
		metarMessage.parseMessage();
		assertEquals(PRESSURE_UNITS.INCH_OF_MERCURY, metarMessage.getCommonWeatherSection().getQnhUnits());
		assertEquals(LENGTH_UNITS.SM, metarMessage.getCommonWeatherSection().getVisibilityUnits());
		assertEquals(new BigDecimal("2968"), metarMessage.getCommonWeatherSection().getQnh());
		assertTrue(metarMessage.getRemarkSections().size()>0);
			
	}
	
	@Test
	public void translateContaminationMetar() throws METARParsingException {
		METARTacMessage metarMessage = new METARTacMessage(metarTACContamination);
		metarMessage.parseMessage();
		assertTrue(metarMessage.getRunwayStateSections().size()==1);
		
		assertFalse(metarMessage.getRunwayStateSections().get(0).isApplicableForAllRunways());
		assertEquals(56, metarMessage.getRunwayStateSections().get(0).getFriction().get().intValue());
		
		
	}
	
	@Test
	public void translateContaminationMetarAllRunways() throws METARParsingException {
		METARTacMessage metarMessage = new METARTacMessage(metarTACContaminationAllRunways);
		metarMessage.parseMessage();
		assertTrue(metarMessage.getRunwayStateSections().size()==1);
		
		assertTrue(metarMessage.getRunwayStateSections().get(0).isApplicableForAllRunways());
		assertEquals(56, metarMessage.getRunwayStateSections().get(0).getFriction().get().intValue());
		assertFalse(metarMessage.getRunwayStateSections().get(0).getDepositDepth().isPresent());
		
		
	}
	
}
