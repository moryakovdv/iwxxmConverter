package org.gamc.spmi.iwxxmConverter.test.v3.taf;

import static org.junit.Assert.*;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.general.TafForecastTimeSection;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFTacMessage;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFParsingException;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFProbabilitySection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFTimedTLSection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTrickyTaf {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	String t1 = "TAF UUBW 231057Z 2312/2412 32003MPS 9999 SCT030\n" + 
			"TX23/2312Z TN11/2403Z\n" + 
			"FM232000 VRB01MPS 3100 BR FEW004\n" + 
			"PROB40 TEMPO 2321/2404 0300 FG FEW002\n" + 
			"FM240600 25005MPS 9999 SCT030=";
	@Test
	public void test() throws ParsingException {
		TAFTacMessage tafMessage = new TAFTacMessage(t1);
		tafMessage.parseMessage();
		for(TafForecastTimeSection ts : tafMessage.getTimedSections()) {
			ts.parseSection();
			
			System.out.println(ts);
			
		}
		
		for(TAFProbabilitySection p :tafMessage.getProbabilitySections() ) {
			p.parseSection();
			System.out.println(p);
		}
	}

}
