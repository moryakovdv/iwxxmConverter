package org.gamc.spmi.iwxxmConverter.test.v3.taf;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFConverterV3;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.junit.Test;

/**Refers to 
 * https://github.com/cb-steven-matison/iwxxm-sample-data/blob/master/taf-SARP-131100Z.tac
 * https://github.com/cb-steven-matison/iwxxm-sample-data/blob/master/taf-OIZC-131130Z.tac
 * https://github.com/cb-steven-matison/iwxxm-sample-data/blob/master/taf-SARP-131251Z.tac
 * */

public class TafConversionIssue11 {

	String tafSARP131100Z = "TAF SARP 131100Z 1312/1412 05005KT CAVOK TX28/1318Z TN15/1410Z\n" + 
			"PROB30 1407/1411 5000 BR NSC";
	
	
	String tafOIZC131130Z = "TAF OIZC 131130Z 1312/1418 11004MPS 4000 HZ NSC\n" + 
	"TEMPO 1312/1317 12008MPS FEW020TCU SCT025 BKN070\n" + 
	"BECMG 1403/1405 7000 NSC\n" + 
	"TEMPO 1408/1415 12009MPS 4000 SA NSC";
	
	String tafAmdSARP131251Z = "TAF AMD SARP 131251Z 1313/1412 05005KT CAVOK TX28/1318Z TN15/1410Z\n" + 
			"PROB30 1407/1411 5000 BR NSC";
	
	@Test
	public void testTafSARP131100Z() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException {
		TAFConverterV3 cv = new TAFConverterV3();
		String resultXml = cv.convertTacToXML(tafSARP131100Z);
		System.out.println(resultXml);
	}
	
	@Test
	public void testTafOIZC131130Z() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException {
		TAFConverterV3 cv = new TAFConverterV3();
		String resultXml = cv.convertTacToXML(tafOIZC131130Z);
		System.out.println(resultXml);
	}
	
	@Test
	public void testTafAmdSARP131251Z() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException {
		TAFConverterV3 cv = new TAFConverterV3();
		String resultXml = cv.convertTacToXML(tafAmdSARP131251Z);
		System.out.println(resultXml);
	}
	

}
