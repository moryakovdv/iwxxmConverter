package org.gamc.spmi.iwxxmConverter.test.v3.taf;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.general.TafForecastTimeSection;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFConverterV3;
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
	
	String t2 =  "TAF BIAR 210810Z 2109/2209 16020G30KT 9999 BKN020 OVC065\n" + 
			"      TX07/2115Z\n" + 
			"      TN00/2206Z\n" + 
			"      BECMG 2109/2111 13035G50KT 9999 SCT030 BKN045\n" + 
			"      BECMG 2113/2115 16048G62KT\n" + 
			"      FM211700 21015G25KT\n" + 
			"      BECMG 2120/2122 VRB02KT CAVOK=";
	
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
	@Test
	public void test2() throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException {
		
		TAFConverterV3 tafC = new TAFConverterV3();
		String xml = tafC.convertTacToXML(t2);
		System.out.println(xml);
		
	}

}
