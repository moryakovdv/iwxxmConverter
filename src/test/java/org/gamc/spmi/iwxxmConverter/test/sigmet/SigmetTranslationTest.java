package org.gamc.spmi.iwxxmConverter.test.sigmet;

import static org.junit.Assert.*;

import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SIGMETParsingException;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SIGMETTacMessage;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription.ObservationType;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription.Severity;
import org.junit.Test;

public class SigmetTranslationTest {

	
	String sigmetGeneral = "WSSS20 VHHH 090900\r\n" + 
			"VHHK SIGMET 3 VALID 090900/091300 VHHHVHHK\r\n" + 
			"HONG KONG FIR EMBD TS OBS AT 0901Z N OF\r\n" + 
			"N2000 AND E OF E11330 TOP FL400 INTSF FCST AT\r\n" + 
			"1300Z N OF N2000 AND E OF E11300=";
	
	String sigmetPolygon = "WSAU21 AMRF 061700\r\n" + 
			"YMMM SIGMET M07 VALID 061700/062100 YMRFYMMM\r\n" + 
			"MELBOURNE FIR SEV MTW OBS WI S3704 E14244\r\n" + 
			"– S3611 E14753 – S3736 E14943 – S4006 E14800 – S3952\r\n" + 
			"E14353 – S3704 E14244 FL080/140 STNR NC=";
	
	String sigmetMoving = "WSSS20 VHHH 180830\r\n" + 
			"VHHK SIGMET 1 VALID 180830/181230 VHHHVHHK\r\n" + 
			"HONG KONG FIR RDOACT CLD FCST E OF E114\r\n" + 
			"SFC/FL100 MOV E 20KT WKN=";
	@Test
	public void testGeneral() throws SIGMETParsingException {
		SIGMETTacMessage sigmetTac = new SIGMETTacMessage(sigmetGeneral);
		sigmetTac.parseMessage();
		
		assertEquals(sigmetTac.getBulletinNumber(),20);
		assertNotNull(sigmetTac.getPhenomenonDescription());
		assertEquals(sigmetTac.getPhenomenonDescription().getPhenomenonObservation(), ObservationType.OBS);
		assertEquals(sigmetTac.getPhenomenonDescription().getPhenomenon(), "TS");
		assertEquals(sigmetTac.getPhenomenonDescription().getPhenomenonSeverity(), Severity.EMBD);
		assertEquals(sigmetTac.getPhenomenonDescription().getPhenomenonTimeStamp().getHourOfDay(),9);
		
		//FCST AT
		assertEquals(sigmetTac.getPhenomenonDescription().getForecastSection().getForecastedTime().getHourOfDay(),13);
		assertEquals(sigmetTac.getPhenomenonDescription().getForecastSection().getAreas().size(),2);
		
	}
	
	@Test
	public void testPolygon() throws SIGMETParsingException {
		SIGMETTacMessage sigmetTac = new SIGMETTacMessage(sigmetPolygon);
		sigmetTac.parseMessage();
		
		assertEquals(sigmetTac.getHorizontalLocation().getPolygonPoints().size(), 6);
		
		
	}
	@Test
	public void testMOving() throws SIGMETParsingException {
		SIGMETTacMessage sigmetTac = new SIGMETTacMessage(sigmetMoving);
		sigmetTac.parseMessage();
		
		assertTrue(sigmetTac.getPhenomenonDescription().getMovingSection().isMoving());
		assertEquals(sigmetTac.getPhenomenonDescription().getMovingSection().getMovingSpeed(),20);
		assertEquals(sigmetTac.getPhenomenonDescription().getMovingSection().getSpeedUnits(),SPEED_UNITS.KT);
		assertEquals(sigmetTac.getPhenomenonDescription().getMovingSection().getMovingDirection(),RUMB_UNITS.E);
	
	}
}
