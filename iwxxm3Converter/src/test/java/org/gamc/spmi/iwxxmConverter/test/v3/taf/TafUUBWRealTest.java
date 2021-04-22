package org.gamc.spmi.iwxxmConverter.test.v3.taf;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFTacMessage;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFParsingException;
import org.gamc.spmi.iwxxmConverter.validation.FailedValidationAssert;
import org.gamc.spmi.iwxxmConverter.validation.IwxxmValidator;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.junit.Test;

import schemabindings31._int.icao.iwxxm._3.TAFType;

public class TafUUBWRealTest {

	String correctTaf = "TAF UUBW 220757Z 2209/2309 17005MPS 9999 BKN020\n" + 
			"TX16/2213Z TN07/2302Z\n" + 
			"TEMPO 2209/2217 19008G13MPS -SHRA SCT020CB\n" + 
			"PROB40 TEMPO 2211/2216 -TSRA BKN016CB=;";
	
	String errCavok = "TAF UUBW 220757Z 2209/2309 17005MPS CAVOK 9999 BKN020\n" + 
			"TX16/2213Z TN07/2302Z\n" + 
			"TEMPO 2209/2217 19008G13MPS -SHRA SCT020CB\n" + 
			"PROB40 TEMPO 2211/2216 -TSRA BKN016CB=;";
	
	String errTaf1 = "TAF UUBW 111057 2209/2309 17005MPS 9999 BKN020\n" + 
			"TX16/2213Z TN07/2302Z\n" + 
			"TEMPO 2209/2217 19008G13MPS -SHRA SCT020CB\n" + 
			"PROB40 TEMPO 2211/2216 -TSRA BKN016CB=";
	
	
	String errTaf2 = "TAF UUBW 031651Z 0318/0418 17005MPS 9999 BKN020\n" + 
			"TX16/2213Z TN10/0101Z\n" + 
			"TEMPO 2209/2217 19008G13MPS -SHRA SCT020CB\n" + 
			"PROB40 TEMPO 2211/2216 -TSRA BKN016CB=";
	
	
	IwxxmValidator v = new IwxxmValidator();
	

	
	
	@Test
	public void testCorrect() throws Exception {
		
		TAFTacMessage tafMessage = new TAFTacMessage(correctTaf);
		tafMessage.parseMessage();

		List<FailedValidationAssert> asserts  = validateParsedTaf(tafMessage);
		assertTrue(asserts.size()==0);
		

	}
	
	@Test(expected = TAFParsingException.class)
	public void testCavok() throws Exception {
		TAFTacMessage tafMessage = new TAFTacMessage(errCavok);
		tafMessage.parseMessage();

		List<FailedValidationAssert> asserts  = validateParsedTaf(tafMessage);
		assertTrue(asserts.size()==0);
		
		
		
	}
	
	@Test(expected = TAFParsingException.class)
	public void test1() throws Exception {
		TAFTacMessage tafMessage = new TAFTacMessage(errTaf1);
		tafMessage.parseMessage();

		List<FailedValidationAssert> asserts  = validateParsedTaf(tafMessage);
		assertTrue(asserts.size()==0);
	}
	
	@Test
	public void test2() throws Exception {
		TAFTacMessage tafMessage = new TAFTacMessage(errTaf2);
		tafMessage.parseMessage();
		
		List<FailedValidationAssert> asserts  = validateParsedTaf(tafMessage);
		assertTrue(asserts.size()==0);
	}

	
	
	
	private List<FailedValidationAssert> validateParsedTaf(TAFTacMessage taftac) throws Exception {
		TAFConverterV3 c = new TAFConverterV3();
		TAFType xmlTaf = c.convertMessage(taftac);
		String xml = c.marshallMessageToXML(xmlTaf);
		System.out.println(xml);
		
		List<FailedValidationAssert> failures = v.validateString(xml);
		return failures;
		
		
	}
	
}
