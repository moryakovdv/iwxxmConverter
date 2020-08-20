package org.gamc.spmi.iwxxmConverter.test.v3.sigmet;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETTropicalConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETTropicalTacMessage;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SIGMETParsingException;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription.Intensity;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.junit.Test;

public class SigmetTCTest {

	String sigmetTcWithin = "WSCH31SCFA 011035\n" + 
	"SCFZ SIGMET 2 VALID 101200/101800 SCFA–\n" + 
			"SCFZ ANTOFAGASTA FIR TC GLORIA PSN N2200 W06145 CB OBS AT 1200Z WI N2200 W06200 – N2230 W06215\n" + 
			"– N2300 W06200 - N2245 W06245 – N2215 W06245 – N2145 W06230 –N2200 W06200 TOP BLW FL500 NC\n" + 
			"FCST AT 1800Z TC CENTRE PSN N2330 W06315=";
	
	String sigmetRadius = 
			"WSCH31SCFA 011035\n" + 
	"SCFZ SIGMET 3 VALID 251600/252200 SCFA-\n" + 
			"      SCFZ ANTOFAGASTA FIR TC GLORIA PSN N2706 W07306 CB OBS AT 1600Z WI 250NM OF TC CENTRE TOP FL500 NC\n" + 
			"      FCST AT 2200Z TC CENTRE PSN N2740 W07345";
	@Test
	public void testParsing() throws SIGMETParsingException {
		SIGMETTropicalTacMessage tac = new SIGMETTropicalTacMessage(sigmetTcWithin);
		tac.parseMessage();
		assertEquals("2", tac.getSigmetNumber());
		assertEquals("GLORIA", tac.getPhenomenonDescription().getPhenomenonGivenName());
		assertEquals(22, tac.getHorizontalLocation().getPoint().getLatitude().getDeg());
		assertEquals(61, tac.getHorizontalLocation().getPoint().getLongitude().getDeg());
		
		assertTrue(tac.getHorizontalLocation().isInPolygon());
		
		assertEquals(7, tac.getHorizontalLocation().getPolygonPoints().size());
		
		assertTrue(tac.getVerticalLocation().getTopFL().get()==500);

		assertTrue(tac.getPhenomenonDescription().getIntencity()==Intensity.NC);
		
		assertEquals(18,tac.getPhenomenonDescription().getForecastSection().getForecastedTime().getHourOfDay());
		
		assertEquals(23, tac.getPhenomenonDescription().getForecastSection().getHorizontalLocation().getPoint().getLatitude().getDeg());
		assertEquals(63, tac.getPhenomenonDescription().getForecastSection().getHorizontalLocation().getPoint().getLongitude().getDeg());

		}
	
	@Test
	public void testParsingAndConvertion() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException {
		SIGMETTropicalConverterV3 c1 = new SIGMETTropicalConverterV3();
		String result = c1.convertTacToXML(sigmetTcWithin);
		
		System.out.println(result);
	}
	
	@Test
	public void testRadiusTC() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException {
		SIGMETTropicalConverterV3 c1 = new SIGMETTropicalConverterV3();
		String result = c1.convertTacToXML(sigmetRadius);
		
		System.out.println(result);
	}

}
