package org.gamc.spmi.iwxxmConverter.test.v3.taf;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFTacMessage;
import org.gamc.spmi.iwxxmConverter.validation.FailedValidationAssert;
import org.gamc.spmi.iwxxmConverter.validation.IwxxmValidator;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.junit.Test;

import schemabindings31._int.icao.iwxxm._3.TAFType;

public class TafVerticalVisibilityTest {

	
	String tafVV = "TAF ULLI 220455Z 2206/2306 27003MPS 0300 FZFG VV010 "
			+ "TEMPO 2206/2209 1500 BR OVC002 "
			+ "BECMG 2209/2210 6000 NSW SCT011 "
			+ "BECMG 2215/2217 3100 BR BKN003 TEMPO 2217/2306 0400 FZFG=";
	@Test
	public void test() throws Exception {
		TAFTacMessage message = new TAFTacMessage(tafVV);
		message.parseMessage();
		
		TAFConverterV3 cv = new TAFConverterV3();
		TAFType tafType = cv.convertMessage(message);
		
		String resultXml = cv.marshallMessageToXML(tafType);
		
		System.out.println(resultXml);
		
		List<FailedValidationAssert> failedResults;
		
		IwxxmValidator validator = new IwxxmValidator();
		validator.init();
		
		failedResults = validator.validateString(resultXml);
		for(FailedValidationAssert fAssert : failedResults) {
			System.out.println(fAssert);
		}
		assertTrue(failedResults.size()==0);
		
		
	}

}
