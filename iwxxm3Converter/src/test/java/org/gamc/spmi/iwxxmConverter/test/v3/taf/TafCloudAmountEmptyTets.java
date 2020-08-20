package org.gamc.spmi.iwxxmConverter.test.v3.taf;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFTacMessage;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.junit.Test;

import schemabindings31._int.icao.iwxxm._3.TAFType;

/**refers to issue 11 
 * https://github.com/moryakovdv/iwxxmConverter/issues/11*/
public class TafCloudAmountEmptyTets {

	String emptyCloudTaf = "TAF OIZC 131130Z 1312/1418 11004MPS 4000 HZ NSC\n" + 
			"TEMPO 1312/1317 12008MPS FEW020TCU SCT025 BKN070\n" + 
			"BECMG 1403/1405 7000 NSC\n" + 
			"TEMPO 1408/1415 12009MPS 4000 SA NSC";
	
	@Test
	public void testTafCloudAmountEmpty() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException, WMORegisterException {
		TAFTacMessage message = new TAFTacMessage(emptyCloudTaf);
		message.parseMessage();
		
		TAFConverterV3 cv = new TAFConverterV3();
		TAFType tafType = cv.convertMessage(message);
		
		String resultXml = cv.marshallMessageToXML(tafType);
		
		System.out.println(resultXml);
		
		
	}

}
