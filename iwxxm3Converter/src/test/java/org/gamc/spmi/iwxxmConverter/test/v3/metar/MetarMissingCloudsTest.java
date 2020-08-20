package org.gamc.spmi.iwxxmConverter.test.v3.metar;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.METARConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.METARTacMessage;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SPECIConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SPECITacMessage;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARParsingException;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.junit.Test;

import schemabindings31._int.icao.iwxxm._3.METARType;
import schemabindings31._int.icao.iwxxm._3.SPECIType;

public class MetarMissingCloudsTest {

	String missingVvMetar = "METAR EDDF 120550Z 03015KT 1400 R07R/P2000N R07C/P2000N R07L/1900U SN DRSN BR VV/// M04/M04 Q1000 R07R/11//90 R07C/15//90 R07L/15//90 BECMG 4000 NSW";
	
	String vvSpeci = "SPECI USTR 290030Z 35001MPS 1100 0900N R21/1900 +SHSN VV003 M02/M03 Q0996 R21/590742 TEMPO 0500 +SHSN VV002";
	
	
	@Test
	public void testMissingVV() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException, WMORegisterException {
		METARTacMessage message = new METARTacMessage(missingVvMetar);
		message.parseMessage();
		
		METARConverterV3 cv = new METARConverterV3();
		METARType mt =  cv.convertMessage(message);
	
		String resultXml = cv.marshallMessageToXML(mt);
		
		System.out.println(resultXml);
		
	}
	@Test
	public void testVvSpeci() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException, WMORegisterException {
		SPECITacMessage message = new SPECITacMessage(vvSpeci);
		message.parseMessage();
		
		SPECIConverterV3 cv = new SPECIConverterV3();
		SPECIType mt =  cv.convertMessage(message);
	
		String resultXml = cv.marshallMessageToXML(mt);
		
		System.out.println(resultXml);
	}

}
