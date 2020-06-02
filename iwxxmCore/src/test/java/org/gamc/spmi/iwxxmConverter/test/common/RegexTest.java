package org.gamc.spmi.iwxxmConverter.test.common;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetParsingRegexp;
import org.junit.Test;

public class RegexTest {

	@Test
	public void testZigZag() {
		String tac = "E OF LINE S2127 W06840 - S2320 \n" + 
				"W06803 - S2442 W06846 FL180/280 STNR NC=";
	
		 //(N|S(?:\\d{2})(?:\\d{2})\\s+(?:E|W)(?:\\d{3})(?:\\d{0,2}))(?:\\s*-\\s*)?(?2)?(?:\\s*-\\s*)?(?2)?
		Matcher matcherZigZag = Pattern.compile("(?<azimuth>N|NE|E|SE|S|SW|W|NW) OF LINE (N|S(?:\\d{2})(?:\\d{2})\\s+(?:E|W)(?:\\d{3})(?:\\d{0,2}))(?:\\s*-\\s*)?(\\2)?(?:\\s*-\\s*)?(\\2)?(?:\\s*-\\s*)?(\\2)?").matcher(tac);
		//SigmetParsingRegexp.sigmetMultiPointLine.matcher(tac);
		assertTrue(matcherZigZag.find());
		
	}

}
