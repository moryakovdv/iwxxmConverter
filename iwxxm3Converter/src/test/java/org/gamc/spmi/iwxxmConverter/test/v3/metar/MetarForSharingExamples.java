package org.gamc.spmi.iwxxmConverter.test.v3.metar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.general.MetarForecastTimeSection;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.PRESSURE_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.METARConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.METARTacMessage;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARParsingException;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARTimedATSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARTimedTLSection;
import org.gamc.spmi.iwxxmConverter.validation.FailedValidationAssert;
import org.gamc.spmi.iwxxmConverter.validation.IwxxmValidator;
import org.junit.Test;

/***use this class to test METARS for further sharing them in public WMO repos under CI**/


public class MetarForSharingExamples {
	
	
	private String metarSnow1 = "METAR UUWW 270430Z 01008G13MPS 1800 R06/1800D SN BKN005 BKN030\n" + 
			"     M03/M04 Q1005 R06/290050 TEMPO 0700 +SHSN BKN004 BKN015CB=";
	@Test
	public void translateMETAR() throws Exception {
		
		METARConverterV3 cv = new METARConverterV3();
		IwxxmValidator v =  new IwxxmValidator();
		
		String result = cv.convertTacToXML(metarSnow1);
		System.out.println(result);
		List<FailedValidationAssert> asserts = v.validateString(result);
		
		if (asserts.size()>0) {
			System.out.println(asserts);
			throw new Exception("asserts failed");
			
		}
		
	}

}
