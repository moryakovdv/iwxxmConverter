package org.gamc.spmi.iwxxmConverter.test.v3.sigmet;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETTropicalConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETTropicalTacMessage;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETVolcanoTacMessage;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SIGMETParsingException;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription.Intensity;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.junit.Test;

public class SigmetVolcanoTest {

	String sigmetIcaoExample = "WVJP31 RJTD 210320 RJJJ SIGMET E05 VALID 210320/210920 RJTD- "
			+ "RJJJ FUKUOKA FIR VA ERUPTION MT SAKURAJIMA- WAKAMIKO(AIRA-CALDERA) "
			+ "PSN N3136 E13039 VA CLD OBS AT 0215Z WI N3020 E13225 - N3051 E13328 - N3056 E13414 -N3042 E13422 - N3009 E13229 - N3020 E13225 SFC/FL170 "
			+ "FCST AT 0815Z WI N2939 E13308 - N2936 E13456 - N3116 E14050 - N2923 E13534 - N2856 E13219 - N2939 E13308= AND WI N3134 E13042 – N3121 E13117 – N3110 E13108 – N3119 E13031 – N3134 E13042 SFC/FL170 NC "
			+ "FCST AT 0815Z WI N3113 E13012 – N3129 E13045 – N3020 E13114 – N3045 E13627 – N2946 E13155 – N3003 E13034 – N3113 E13012 =";
	
	String nov16TestVAMessage = "WVRS31 UUWV 160800 UUWV SIGMET 1 VALID 160800/161400 UUWV- UUWV MOSCOW FIR VA ERUPTION MT EYJAFJALLAJOKULL PSN N6338 W01938 VA CLD OBS AT 0600Z ENTIRE FIR SFC/FL200 FCST AT 1400Z ENTIRE FIR=";
	
		@Test
	public void testParsing() throws SIGMETParsingException {
		SIGMETVolcanoTacMessage tac = new SIGMETVolcanoTacMessage(sigmetIcaoExample);
		tac.parseMessage();
		
		assertEquals("E05", tac.getSigmetNumber());
		assertEquals(6, tac.getHorizontalLocation().getPolygonPoints().size());

		assertEquals("MT SAKURAJIMA- WAKAMIKO(AIRA-CALDERA)", tac.getPhenomenonDescription().getPhenomenonGivenName());
		assertEquals(31, tac.getHorizontalLocation().getPoint().getLatitude().getDeg());
		assertEquals(130, tac.getHorizontalLocation().getPoint().getLongitude().getDeg());
		
		assertTrue(tac.getHorizontalLocation().isInPolygon());
		
		assertTrue(tac.getVerticalLocation().isBottomMarginOnSurface());
		assertTrue(tac.getVerticalLocation().getTopFL().get()==170);

		assertTrue(tac.getPhenomenonDescription().getIntencity()==Intensity.NC);
		
		assertEquals(2, tac.getPhenomenonDescription().getForecastSection().size());
		assertEquals(8,tac.getPhenomenonDescription().getForecastSection().get(0).getForecastedTime().getHourOfDay());
		
		//assertEquals(23, tac.getPhenomenonDescription().getForecastSection().getHorizontalLocation().getPoint().getLatitude().getDeg());
		//assertEquals(63, tac.getPhenomenonDescription().getForecastSection().getHorizontalLocation().getPoint().getLongitude().getDeg());

		
	}
	
	@Test
	public void testParsing1() throws SIGMETParsingException {
		SIGMETVolcanoTacMessage tac = new SIGMETVolcanoTacMessage(nov16TestVAMessage);
		tac.parseMessage();
		
		assertEquals("1", tac.getSigmetNumber());
	

		assertEquals("MT EYJAFJALLAJOKULL", tac.getPhenomenonDescription().getPhenomenonGivenName());
		
	}
	
	

}
