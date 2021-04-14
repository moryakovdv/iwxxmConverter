package org.gamc.spmi.iwxxmConverter.test.v3.metar;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.METARConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SPECIConverterV3;
import org.gamc.spmi.iwxxmConverter.validation.FailedValidationAssert;
import org.gamc.spmi.iwxxmConverter.validation.IwxxmValidator;
import org.junit.Test;

public class MetarValidation {

	String metarTACSimple = "METAR EDDF 280650Z 32012KT 280V350 9999 FEW025 SCT100 13/07 Q1022 NOSIG=";

	String metarA3 = "METAR YUDO 221630Z 24004MPS 0600 R12/1000U DZ FG SCT010 OVC020 17/16 Q1018 "
			+ " BECMG TL1700 0800 FG " + " BECMG AT1800 9999 NSW";

	String metarTACCAVOK = "METAR LZIB 211630Z 08005KT 060V120 CAVOK 26/15 Q1022 NOSIG=";

	//Not valid because of missing RVR while visibility<1500
	String metarTACVRB = "METAR EDDF 280650Z VRB05KT 1200 0900NW FG FEW025 SCT100 OVC060 M02/M07 Q0972 NOSIG=";

	String metarTACBecoming = "METAR VHHH 251600Z 24015G25KT 200V280 0600 R07L/1000U R17R/2000D FG DZ SCT010 OVC020 17/16 Q1018 BECMG TL1700 0800 FG BECMG AT1800 9999 NSW=";

	String metarTACWeather = "METAR LZIB 211630Z 08005KT 060V120 1500 0900NW FG VCTS +SHRA FEW020 26/15 Q1022 NOSIG=";

	String metarTACWindShearRW = "METAR LZIB 211630Z 08005KT 060V120 200 0100NW FG VCTS +SHRA FEW020 26/15 Q1022 WS RWY04L NOSIG=";

	String metarTACWindShearALL = "METAR LZIB 211630Z 08005KT 060V120 1800 0900NW FG VCTS +SHRA FEW020 26/15 Q1022 WS ALL RWY RETS NOSIG=";

	String metarTACVerticalVisibilityNotObserved = "METAR LZIB 211630Z 08005KT 060V120 1700 0900NW FG VCTS +SHRA VV/// 26/15 Q1022 WS ALL RWY RETS NOSIG=";

	String metarTACVerticalVisibilityObserved = "METAR LZIB 211630Z 08005KT 060V120 800 0900NW FG VCTS +SHRA VV020 26/15 Q1022 R06R/1500U WS ALL RWY RETS NOSIG=";

	String metarTACTempo = "METAR UUWW 171130Z 01007G12MPS 9999 -RA OVC032 08/04 Q1021 R06/190060 TEMPO 36015MPS SN";

	String metarTACContamination = "METAR UUWW 171130Z 01007G12MPS 9999 R06L/M6000FT -RA OVC032 08/04 Q1021 R06L/123456 TEMPO 36015MPS SN";
	String metarTACContaminationAllRunways = "METAR UUWW 171130Z 01007G12MPS 9999 R06L/M6000FT -RA OVC032 08/04 Q1021 R88/12//56 TEMPO 36015MPS SN";

	String metarUS = "METAR KORD 041656Z 19020G26KT 6SM -SHRA BKN070 12/08 A3016 RMK AO2";

	String metarUS1 = "METAR KORD 170651Z 20014G23KT 10SM SCT250 24/14 A2968 RMK AO2 SLP045 T02390144=";
	
	String speci = "SPECI UUEE 270845Z 02006MPS 2100 -SN OVC009 M04/M06 Q1008 R06L/590230\n" + 
			"     R06R/590230 TEMPO 1000 SHSN BKN012CB=";
	
	String metarUUEE = "METAR UUEE 270830Z 02006MPS 2100 -SN OVC009 M04/M06 Q1008 R06L/590230\n" + 
			"     R06R/590230 TEMPO 1000 SHSN BKN012CB=";

	//Array of VALID metars to validate in loops
	String[] metarsValid = new String[] { metarTACSimple, metarTACCAVOK, metarTACBecoming,metarTACWeather,metarTACVerticalVisibilityObserved };
	

	//Array of INVALID metars to validate in loops
	String[] metarsINValid = new String[] {metarTACVRB};
	

	@Test
	public void validateThroughSchematronTest() throws Exception {
		METARConverterV3 cv = new METARConverterV3();
		IwxxmValidator v = new IwxxmValidator();

		for (String metar : metarsValid) {
			String xml = cv.convertTacToXML(metar);
			System.out.println(xml);
			List<FailedValidationAssert> results = v.validateString(xml);
			System.out.println(results);
			assertTrue(results.size() == 0);
		}
	}
	
	/**Test for these metars should fail because of violation of Schematron rules*/
	@Test(expected = AssertionError.class)
	public void validateInvalidThroughSchematronTest() throws Exception {
		METARConverterV3 cv = new METARConverterV3();
		IwxxmValidator v = new IwxxmValidator();

		for (String metar : metarsINValid) {
			String xml = cv.convertTacToXML(metar);
			System.out.println(xml);
			List<FailedValidationAssert> results = v.validateString(xml);
			System.out.println(results);
			assertTrue(results.size() == 0);
		}
	}
	
	@Test
	public void validateSpeciThroughSchematronTest() throws Exception {
		SPECIConverterV3 cv = new SPECIConverterV3();
		IwxxmValidator v = new IwxxmValidator();

		
			String xml = cv.convertTacToXML(speci);
			System.out.println(xml);
			List<FailedValidationAssert> results = v.validateString(xml);
			System.out.println(results);
			assertTrue(results.size() == 0);
		
	}
	
	@Test
	public void validateMetarUUEEThroughSchematronTest() throws Exception {
		METARConverterV3 cv = new METARConverterV3();
		IwxxmValidator v = new IwxxmValidator();

		
			String xml = cv.convertTacToXML(metarUUEE);
			System.out.println(xml);
			List<FailedValidationAssert> results = v.validateString(xml);
			System.out.println(results);
			assertTrue(results.size() == 0);
		
	}

}
