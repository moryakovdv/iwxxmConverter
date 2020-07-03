package org.gamc.spmi.iwxxmConverter.test.v3.sigmet;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETTacMessage;
import org.junit.Test;

import schemabindings31._int.icao.iwxxm._3.SIGMETType;

public class SigmetForecastTest {

	String frctSigmet = "WSRS31RUMA 111143 XXX UUWV SIGMET 5 VALID 111200/111500 UUWV-\n"
			+ " UUWV MOSCOW FIR EMBD TSGR FCST N OF LINE N5100 E03520 - N5017 E04200\n"
			+ " AND S OF LINE N5400 E03150 - N5440 E04400 TOP FL400  FCST S OF N54 AND E OF W012 TOP FL390 MOV E 20KT WKN";
	
	@Test
	public void test() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException {
			SIGMETConverterV3<SIGMETTacMessage, SIGMETType>	 sConverterV3 = new SIGMETConverterV3<SIGMETTacMessage, SIGMETType>();
			String result = sConverterV3.convertTacToXML(frctSigmet);
			System.out.println(result);
	}

}
