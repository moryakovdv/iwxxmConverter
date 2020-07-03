package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetParsingRegexp;
/**Determines the type of the SIGMET message (weather/TC/VA) and chooses appropriate converter*/
public class SIGMETDefiningTypeConverterV3 {

	public SIGMETDefiningTypeConverterV3()
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException {
	}

	public void convertDefiningType(String tac)
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException {
		Matcher sigmetMachType = SigmetParsingRegexp.sigmetType.matcher(tac);
		if (sigmetMachType.find()) {
			while (sigmetMachType.find()) {
				switch (sigmetMachType.group("sigmetType").trim()) {
				case "TC":
					SIGMETTropicalConverterV3 mcTrop = new SIGMETTropicalConverterV3();
					String resultTrop = mcTrop.convertTacToXML(tac);
					System.out.println(resultTrop);
					break;
				case "VA":
					SIGMETVolcanoConverterV3 mcVolc = new SIGMETVolcanoConverterV3();
					String resultVolc = mcVolc.convertTacToXML(tac);
					System.out.println(resultVolc);
					break;
				default:
					SIGMETConverterV3 mcDef = new SIGMETConverterV3();
					String resultDef = mcDef.convertTacToXML(tac);
					System.out.println(resultDef);
					break;
				}
			}
		} else {
			SIGMETConverterV3 mc = new SIGMETConverterV3();
			String result = mc.convertTacToXML(tac);
			System.out.println(result);
		}
	}
}
