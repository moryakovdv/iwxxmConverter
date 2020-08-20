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
package org.gamc.spmi.iwxxmConverter.test.v3.spaceweather;

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
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETTacMessage;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SPACEWEATHERConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFTacMessage;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SIGMETParsingException;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFBecomingSection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFParsingException;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFProbabilitySection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFTempoSection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFTimedFMSection;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.junit.Test;

public class SpaceWeatherTranslationTest {

	String swxCommonTest = "SWX ADVISORY\n" + 
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
	
	String swxNewFormatTest = "SWX ADVISORY\n" + 
			"DTG:                20161108/0100Z \n" + 
			"SWXC:               DONLON\n" + 
			"ADVISORY NR:        2016/2\n" + 
			"SWX EFFECT:         HF COM MOD \n" + 
			"NR RPLC :           2016/1\n" + 
			"OBS SWX:            08/0100Z HNH HSH E180 - W180 \n" + 
			"FCST SWX +6 HR:     08/0700Z HNH HSH E180 - W180\n" + 
			"FCST SWX +12 HR:    08/1300Z HNH HSH E180 - W180\n" + 
			"FCST SWX +18 HR:    08/1900Z HNH HSH E180 - W180\n" + 
			"FCST SWX +24 HR:    09/0100Z NO SWX EXP\n" + 
			"RMK:                LOW LVL GEOMAGNETIC STORMING CAUSING INCREASED AURORAL ACT AND SUBSEQUENT MOD DEGRADATION OF GNSS AND HF COM AVBL IN THE AURORAL ZONE. THIS STORMING EXP TO SUBSIDE IN THE FCST PERIOD. SEE WWW.SPACEWEATHERPROVIDER.WEB \n" + 
			"NXT ADVISORY:       NO FURTHER ADVISORIES";
	
	String swxDayLightSide = "SWX ADVISORY\n" + 
			"DTG:                20161108/0100Z \n" + 
			"SWXC:               DONLON\n" + 
			"ADVISORY NR:        2016/1\n" + 
			"SWX EFFECT:         HF COM SEV\n" + 
			"OBS SWX:            08/0100Z DAYLIGHT SIDE\n" + 
			"FCST SWX +6 HR:     08/0700Z DAYLIGHT SIDE\n" + 
			"FCST SWX +12 HR:    08/1300Z DAYLIGHT SIDE\n" + 
			"FCST SWX +18 HR:    08/1900Z DAYLIGHT SIDE\n" + 
			"FCST SWX +24 HR:    09/0100Z DAYLIGHT SIDE\n" + 
			"RMK:                PERIODIC HF COM ABSORPTION OBS AND LIKELY TO CONT IN THE NEAR TERM. CMPL AND PERIODIC LOSS OF HF ON THE SUNLIT SIDE OF THE EARTH EXP. CONT HF COM DEGRADATION LIKELY OVER THE NXT 7 DAYS. SEE WWW.SPACEWEATHERPROVIDER.WEB \n" + 
			"NXT ADVISORY:       20161108/0700Z";
	
	/**Converts the SWx example
	 * @throws WMORegisterException */
	//@Test
	public void convertSimple() throws ParsingException, UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException {
		SPACEWEATHERConverterV3 converter = new SPACEWEATHERConverterV3();
		String xmlResult = converter.convertTacToXML(swxCommonTest);
		
		System.out.println(xmlResult);
	}

	/**Converts the SWx example in new format (one for each effect, no minutes in lat/lon
	 * @throws WMORegisterException */
	//@Test
	public void convertNewVersion() throws ParsingException, UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException {
		SPACEWEATHERConverterV3 converter = new SPACEWEATHERConverterV3();
		String xmlResult = converter.convertTacToXML(swxNewFormatTest);
		
		System.out.println(xmlResult);
	}
	

	/**Converts the SWx example in new format (one for each effect, no minutes in lat/lon
	 * @throws WMORegisterException */
	@Test
	public void convertDayLightSide() throws ParsingException, UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException {
		SPACEWEATHERConverterV3 converter = new SPACEWEATHERConverterV3();
		String xmlResult = converter.convertTacToXML(swxDayLightSide);
		
		System.out.println(xmlResult);
	}

	
}
