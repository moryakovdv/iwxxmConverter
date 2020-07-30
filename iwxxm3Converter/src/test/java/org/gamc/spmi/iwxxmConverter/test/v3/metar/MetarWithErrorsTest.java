package org.gamc.spmi.iwxxmConverter.test.v3.metar;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.METARConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.METARTacMessage;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARParsingException;
import org.gamc.spmi.iwxxmConverter.validation.IwxxmValidator;
import org.junit.Test;

import schemabindings31._int.icao.iwxxm._3.METARType;

public class MetarWithErrorsTest {

	String metarErrorInTime = "METAR WIII 232230 Z 18004KT 2400 0600 OVC013 27/15 Q1014";
	
	String metarErrorInWeather = "METAR KORD 041656Z 19020G26KT 6SM -S_HRA BKN070 12/08 A3016 RMK AO2";

	
	public void translateErrorInTimeMetar() throws METARParsingException {
		METARTacMessage metarMessage = new METARTacMessage(metarErrorInTime);
		metarMessage.parseMessage();
	}
	
	@Test
	public void translateErrorInWeatherMetar() throws Exception {
		METARTacMessage metarMessage = new METARTacMessage(metarErrorInWeather);
		metarMessage.parseMessage();
		
		METARConverterV3 cv = new METARConverterV3();	
		METARType mt = cv.convertMessage(metarMessage);
		
		String xml = cv.marshallMessageToXML(mt);
		IwxxmValidator v = new IwxxmValidator();
		v.validateString(xml);
		
		System.out.println(xml);
		
	}

}
