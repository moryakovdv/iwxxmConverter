package org.gamc.spmi.iwxxmConverter.test.v3.sigmet;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETConverterV3;
import org.gamc.spmi.iwxxmConverter.validation.FailedValidationAssert;
import org.gamc.spmi.iwxxmConverter.validation.IwxxmValidator;
import org.junit.Test;

public class SigmetRealFirCoordinatesTest {

	String referenceSigmet1 = "WSCH31SCFA 011035\n" + 
			"SCFZ SIGMET A1 VALID 011035/011435 SCFA-\n" + 
			"SCFZ ANTOFAGASTA FIR SEV ICE FCST E OF LINE S2127 W06840 - S2320 \n" + 
			"W06803 - S2442 W06846 FL180/280 STNR NC=";
	
	@Test
	public void testRefSigmet1() throws Exception {
		
		SIGMETConverterV3 sc = new SIGMETConverterV3();
		String s1Result = sc.convertTacToXML(referenceSigmet1);
		System.out.println(s1Result);
		IwxxmValidator v = new IwxxmValidator();
		v.init();
		List<FailedValidationAssert> asserts = v.validateString(s1Result);
		
		System.out.println(asserts);
		
		
	}

}
