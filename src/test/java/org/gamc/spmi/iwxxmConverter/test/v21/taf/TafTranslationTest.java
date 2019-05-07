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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.general.ForecastSection;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFBecomingSection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFParsingException;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFProbabilitySection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFTacMessage;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFTempoSection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFTimedFMSection;
import org.junit.Test;

public class TafTranslationTest {

	String tafUUWW = "TAF UUWW 090750Z 0909/1008 30007MPS 9999 SCT015 TXM03/1009Z\r\n"
			+ "TNM06/0918Z TEMPO 0909/0913 30012MPS\r\nBECMG 0913/0915 OVC009 TEMPO\n" + "0915/1009 3100 -SN OVC005=";

	String tafUUEETempo = "TAF UUEE 090800Z 0909/1009 29006G13MPS 9999 BKN015 TXM04/0912Z\n"
			+ "TNM07/1006Z TEMPO 0912/1009 3100 -SHSN BKN011 SCT012CB=";

	String tafUUDDBecoming = "TAF UUDD 090757Z 0909/1015 30008MPS 9999 SCT015 TXM03/1007Z\n"
			+ "TNM07/0915Z TEMPO 0909/0915 30013MPS BECMG 0915/0916 OVC011 TEMPO\n"
			+ "0916/1007 3100 -SHSN SCT007 BKN011CB BECMG 1000/1002 30003MPS=";

	String tafULLICloud = "TAF ULLI 090755Z 0909/1009 26003MPS 6000 -SHSN SCT010 BKN015 TEMPO\n"
			+ "0909/0918 1000 -SHSN BR BKN005 BKN017CB TEMPO 0918/1009 3100 -SHSN\n" + "BR BKN006 BKN020CB=";

	String tafVHHH = "TAF VHHH 310500Z 3106/0112 13018KT 9000 BKN020 TX30/3106Z TN26/3122Z BECMG 3106/3108\n"
			+ "	SCT015CB BKN020 TEMPO 3108/3112 17025G40KT 1000 TSRA SCT010CB\n"
			+ "	BKN020 FM011200 15015KT 9999 NSW BKN020 BKN100=";

	String tafYUDOwmo = "TAF YUDO 151800Z 1600/1618 13005MPS 9000 BKN020\r\nBECMG 1606/1608 SCT015CB BKN020 TEMPO 1608/1612 17006G12MPS 1000 TSRA SCT010CB BKN020 FM161230 15004MPS 9999 BKN020";

	String taf24Hour = "TAF UUWW 270203Z 2703/2803 36006MPS 3100 -SN BR OVC007 TX00/2703Z\n"
			+ "TNM06/2724Z TEMPO 2703/2710 0700 +SHSN OVC003 BKN015CB PROB40 TEMPO\n"
			+ "2703/2710 -FZDZ BECMG 2712/2714 9000 NSW BKN012";

	String tafProbabilitySection = "TAF UUWW 270203Z 2703/2803 36006MPS 3100 -SN BR OVC007 TX00/2703Z\\n\" + \n"
			+ "			\"TNM06/2724Z TEMPO 2703/2710 0700 +SHSN OVC003 BKN015CB PROB40 TEMPO\\n\" + \n"
			+ "			\"2703/2710 -FZDZ BECMG 2712/2714 9000 NSW BKN012";

	/** Initial parsing tests */
	@Test
	public void testTafCommonWeatherSection() throws TAFParsingException {
		TAFTacMessage tafMessage = new TAFTacMessage(tafUUWW);
		tafMessage.parseMessage();

		// this taf valid for 23 hours
		assertEquals(23, tafMessage.getValidityInterval().toPeriod().toStandardHours().getHours());

		assertEquals(1, tafMessage.getBecomingSections().size());
		assertEquals(2, tafMessage.getTempoSections().size());
		assertEquals(BigDecimal.valueOf(-3), tafMessage.getCommonWeatherSection().getAirTemperatureMax());
		assertEquals(BigDecimal.valueOf(-6), tafMessage.getCommonWeatherSection().getAirTemperatureMin());

		tafMessage = new TAFTacMessage(tafUUEETempo);
		tafMessage.parseMessage();

		// this taf valid for 24 hours
		assertEquals(24, tafMessage.getValidityInterval().toPeriod().toStandardHours().getHours());
		assertEquals(0, tafMessage.getBecomingSections().size());
		assertEquals(1, tafMessage.getTempoSections().size());
		assertEquals(Integer.valueOf(13), tafMessage.getCommonWeatherSection().getGustSpeed());
		assertEquals(BigDecimal.valueOf(-4), tafMessage.getCommonWeatherSection().getAirTemperatureMax());
		assertEquals(BigDecimal.valueOf(-7), tafMessage.getCommonWeatherSection().getAirTemperatureMin());

		tafMessage = new TAFTacMessage(tafUUDDBecoming);
		tafMessage.parseMessage();
		// this taf valid for 30 hours
		assertEquals(30, tafMessage.getValidityInterval().toPeriod().toStandardHours().getHours());
		assertEquals(2, tafMessage.getBecomingSections().size());
		assertEquals(2, tafMessage.getTempoSections().size());
		assertEquals(Integer.valueOf(8), tafMessage.getCommonWeatherSection().getWindSpeed());
		assertEquals(BigDecimal.valueOf(-3), tafMessage.getCommonWeatherSection().getAirTemperatureMax());
		assertEquals(BigDecimal.valueOf(-7), tafMessage.getCommonWeatherSection().getAirTemperatureMin());

		tafMessage = new TAFTacMessage(tafULLICloud);
		tafMessage.parseMessage();
		// this taf valid for 24 hours
		assertEquals(24, tafMessage.getValidityInterval().toPeriod().toStandardHours().getHours());

		assertEquals(0, tafMessage.getBecomingSections().size());
		assertEquals(2, tafMessage.getTempoSections().size());
		assertEquals(Integer.valueOf(3), tafMessage.getCommonWeatherSection().getWindSpeed());
		assertNull(tafMessage.getCommonWeatherSection().getAirTemperatureMax());
		assertNull(tafMessage.getCommonWeatherSection().getAirTemperatureMin());
		assertEquals(2, tafMessage.getCommonWeatherSection().getCloudSections().size());
		assertEquals("-SHSN", tafMessage.getCommonWeatherSection().getCurrentWeather().get(0));

		tafMessage = new TAFTacMessage(tafVHHH);
		tafMessage.parseMessage();
		// this taf valid for 30 hours
		assertEquals(30, tafMessage.getValidityInterval().toPeriod().toStandardHours().getHours());

		assertEquals(1, tafMessage.getBecomingSections().size());
		assertEquals(1, tafMessage.getTempoSections().size());
		assertEquals(Integer.valueOf(18), tafMessage.getCommonWeatherSection().getWindSpeed());
		assertEquals(Integer.valueOf(130), tafMessage.getCommonWeatherSection().getWindDir());
		assertEquals(SPEED_UNITS.KT, tafMessage.getCommonWeatherSection().getSpeedUnits());

		assertEquals(BigDecimal.valueOf(30), tafMessage.getCommonWeatherSection().getAirTemperatureMax());
		assertEquals(BigDecimal.valueOf(26), tafMessage.getCommonWeatherSection().getAirTemperatureMin());

		assertEquals(1, tafMessage.getCommonWeatherSection().getCloudSections().size());
		assertEquals(0, tafMessage.getCommonWeatherSection().getCurrentWeather().size());

	}

	/** BCMG section parse testing */
	@Test
	public void parseBecomingSectionTest() throws TAFParsingException {
		TAFTacMessage tafMessage = new TAFTacMessage(tafUUDDBecoming);
		tafMessage.parseMessage();

		// this taf valid for 30 hours
		assertEquals(30, tafMessage.getValidityInterval().toPeriod().toStandardHours().getHours());

		assertEquals(2, tafMessage.getBecomingSections().size());
		assertEquals(2, tafMessage.getTempoSections().size());
		assertEquals(Integer.valueOf(8), tafMessage.getCommonWeatherSection().getWindSpeed());
		assertEquals(BigDecimal.valueOf(-3), tafMessage.getCommonWeatherSection().getAirTemperatureMax());
		assertEquals(BigDecimal.valueOf(-7), tafMessage.getCommonWeatherSection().getAirTemperatureMin());

		// first bcmg section -check clouds
		TAFBecomingSection bcmg1 = tafMessage.getBecomingSections().get(0);
		bcmg1.parseSection();
		assertEquals("OVC", bcmg1.getCommonWeatherSection().getCloudSections().get(0).getAmount());
		assertEquals(Integer.valueOf(1100), bcmg1.getCommonWeatherSection().getCloudSections().get(0).getHeight());

		// second bcmg section -check wind
		TAFBecomingSection bcmg2 = tafMessage.getBecomingSections().get(1);
		bcmg2.parseSection();
		assertEquals(0, bcmg2.getCommonWeatherSection().getCloudSections().size());
		assertEquals(Integer.valueOf(3), bcmg2.getCommonWeatherSection().getWindSpeed());
		assertEquals(Integer.valueOf(300), bcmg2.getCommonWeatherSection().getWindDir());

	}

	@Test
	public void parseTempoSectionTest() throws TAFParsingException {

		TAFTacMessage tafMessage = new TAFTacMessage(tafVHHH);
		tafMessage.parseMessage();
		// this taf valid for 30 hours
		assertEquals(30, tafMessage.getValidityInterval().toPeriod().toStandardHours().getHours());

		assertEquals(1, tafMessage.getBecomingSections().size());
		assertEquals(1, tafMessage.getTempoSections().size());

		TAFTempoSection tempo1 = tafMessage.getTempoSections().get(0);
		tempo1.parseSection();
		assertEquals(Integer.valueOf(40), tempo1.getCommonWeatherSection().getGustSpeed());
		assertEquals("TSRA", tempo1.getCommonWeatherSection().getCurrentWeather().get(0));

	}

	@Test
	public void parseBecomingTimeSectionTest() throws ParsingException {

		TAFTacMessage tafMessage = new TAFTacMessage(tafVHHH);
		tafMessage.parseMessage();

		assertEquals(1, tafMessage.getBecomingSections().size());
		assertEquals(1, tafMessage.getTempoSections().size());
		assertEquals(1, tafMessage.getTimedSections().size());

		ForecastSection sec = tafMessage.getTimedSections().get(0);
		assertTrue(sec instanceof TAFTimedFMSection);
		TAFTimedFMSection fmSec = (TAFTimedFMSection) sec;
		fmSec.parseSection();

		assertEquals(1, fmSec.getCommonWeatherSection().getCurrentWeather().size());
		assertEquals(2, fmSec.getCommonWeatherSection().getCloudSections().size());

		assertEquals("BKN", fmSec.getCommonWeatherSection().getCloudSections().get(1).getAmount());
		assertEquals(Integer.valueOf(10000), fmSec.getCommonWeatherSection().getCloudSections().get(1).getHeight());
	}

	@Test
	public void testTime24Hours() throws TAFParsingException {
		TAFTacMessage tafMessage = new TAFTacMessage(taf24Hour);
		tafMessage.parseMessage();

		assertEquals(0, tafMessage.getCommonWeatherSection().getAirTemperatureMinTime().getHourOfDay());

	}

	@Test
	public void testProbabilityTaf() throws TAFParsingException {
		TAFTacMessage tafTacMessage = new TAFTacMessage(tafProbabilitySection);
		tafTacMessage.parseMessage();
		assertTrue(tafTacMessage.getProbabilitySections().size() > 0);
		TAFProbabilitySection probSection = tafTacMessage.getProbabilitySections().get(0);
		assertTrue(probSection.isTempo());
		probSection.parseSection();

		assertEquals("-FZDZ", probSection.getCommonWeatherSection().getCurrentWeather().get(0));

	}

	String testTemporalTaf = "TAF UUWW 021700Z 0218/0318 16007MPS 6000 BKN007 TX04/0312Z TN00/0318Z TEMPO 0218/0306 1000 -SHRA BR BKN004 SCT020CB BECMG 0300/0302 22006MPS FM030800 28007MPS 9000 BKN011 TEMPO 0308/0315 3100 -SHRA SCT015CB";

	@Test
	public void testTemporalSection()
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {
		TAFTacMessage ttm = new TAFTacMessage(testTemporalTaf);
		ttm.parseMessage();

		assertTrue(ttm.getBecomingSections().size() == 1);
		assertTrue(ttm.getTimedSections().size() == 1);
		assertTrue(ttm.getTempoSections().size() == 2);

	}

}
