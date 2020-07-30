package org.gamc.spmi.iwxxmConverter.test.v3.metar;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.METARConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SPECIConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SPECITacMessage;
import org.junit.Test;

import schemabindings31._int.icao.iwxxm._3.SPECIType;

/**Related to https://github.com/moryakovdv/iwxxmConverter/issues/11*/
public class MetarTestIssue11 {
	
	String npeMetarNewLine = "SPECI YUDO 151115Z 05025G37KT 3000 1200NE +TSRA BKN005CB 25/22 Q1008 TEMPO TL1200 0600\n" + 
			"BECMG AT1200 8000 NSW NSC\n" + 
			"\n";
	
	String npeMetarNoNewLine = "SPECI YUDO 151115Z 05025G37KT 3000 1200NE +TSRA BKN005CB 25/22 Q1008 TEMPO TL1200 0600\n" + 
			"BECMG AT1200 8000 NSW NSC";
	@Test
	public void testNullPointerWithNewLines() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {
		SPECIConverterV3 cv = new SPECIConverterV3();
		
		SPECITacMessage speciMessage = new SPECITacMessage(npeMetarNewLine);
		speciMessage.parseMessage();
		
		
		SPECIType result = cv.convertMessage(speciMessage);
		
		System.out.println(cv.marshallMessageToXML(result));
	}
	
	@Test
	public void testNullPointerWithNoNewLines() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {
		SPECIConverterV3 cv = new SPECIConverterV3();
		
		SPECITacMessage speciMessage = new SPECITacMessage(npeMetarNoNewLine);
		speciMessage.parseMessage();
		
		
		SPECIType result = cv.convertMessage(speciMessage);
		
		System.out.println(cv.marshallMessageToXML(result));
	}
	
	

}
