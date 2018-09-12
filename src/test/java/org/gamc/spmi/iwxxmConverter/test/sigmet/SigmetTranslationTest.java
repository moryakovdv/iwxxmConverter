package org.gamc.spmi.iwxxmConverter.test.sigmet;

import static org.junit.Assert.*;

import org.gamc.spmi.iwxxmConverter.sigmetconverter.SIGMETParsingException;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SIGMETTacMessage;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription.ObservationType;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription.Severity;
import org.junit.Test;

public class SigmetTranslationTest {

	
	String sigmetGeneral = "WSSS20 VHHH 090900\r\n" + 
			"VHHK SIGMET 3 VALID 090900/091300 VHHHVHHK\r\n" + 
			"HONG KONG FIR EMBD TS OBS AT 0901Z N OF\r\n" + 
			"N2000 AND E OF E11330 TOP FL400 IN TSF FCST AT\r\n" + 
			"1300Z N OF N2000 AND E OF E11300=";
	@Test
	public void test() throws SIGMETParsingException {
		SIGMETTacMessage sigmetTac = new SIGMETTacMessage(sigmetGeneral);
		sigmetTac.parseMessage();
		
		assertEquals(sigmetTac.getBulletinNumber(),20);
		assertNotNull(sigmetTac.getPhenomenonDescription());
		assertEquals(sigmetTac.getPhenomenonDescription().getPhenomenonObservation(), ObservationType.OBS);
		assertEquals(sigmetTac.getPhenomenonDescription().getPhenomenon(), "TS");
		assertEquals(sigmetTac.getPhenomenonDescription().getPhenomenonSeverity(), Severity.EMBD);
		assertEquals(sigmetTac.getPhenomenonDescription().getPhenomenonTimeStamp().getHourOfDay(),9);
	}

}
