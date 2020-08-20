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
package org.gamc.spmi.iwxxmConverter.test.v3.metar;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.METARConverterV3;
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

	
	
	
	String metarA3 = "METAR YUDO 221630Z 24004MPS 0600 R12/1000U DZ FG SCT010 OVC020 17/16 Q1018\n" +
    " BECMG TL1700 0800 FG BECMG AT1800 9999 NSW";
    
	String metar = "METAR UUEE 270830Z 02006MPS 2100 -SN OVC009 M04/M06 Q1008 R06L/590230\n" + 
			"     R06R/590230 TEMPO 1000 SHSN BKN012CB R06L/590330=";
	
	String metarCavok = "METAR ULLI 261230Z 32003MPS 280V360 CAVOK M02/M16 Q1008 R88/190055\n" + 
			"NOSIG";
	
	String metarTACWindShearRW = "METAR LZIB 211630Z 08005KT 060V120 1200 0900NW FG VCTS +SHRA FEW020 26/15 Q1022 WS RWY04L NOSIG=";
	
	@Test
	public void test() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException, WMORegisterException {
		METARConverterV3 mc = new METARConverterV3();
		String result = mc.convertTacToXML(metar);
		
		System.out.println(result);
		
		
	}
	@Test
	public void testMetarCavok() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException, WMORegisterException {
		METARConverterV3 mc = new METARConverterV3();
		String result = mc.convertTacToXML(metarCavok);
		
		System.out.println(result);
	}
	
	@Test
	public void testMetarWindShear() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException {
		METARConverterV3 mc = new METARConverterV3();
		String result = mc.convertTacToXML(metarTACWindShearRW);
		
		System.out.println(result);
	}

}
