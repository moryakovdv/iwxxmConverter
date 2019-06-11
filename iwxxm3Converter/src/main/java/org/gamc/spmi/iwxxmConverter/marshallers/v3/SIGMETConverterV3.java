package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;

import schemabindings31._int.icao.iwxxm._3.METARType;
import schemabindings31._int.icao.iwxxm._3.SIGMETType;

public class SIGMETConverterV3 implements TacConverter<SIGMETTacMessage, SIGMETType> {

	@Override
	public String convertTacToXML(String tac)
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SIGMETType convertMessage(SIGMETTacMessage translatedMessage)
			throws DatatypeConfigurationException, UnsupportedEncodingException, JAXBException, ParsingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SIGMETType addTranslationCentreHeader(SIGMETType report) throws DatatypeConfigurationException {
		// TODO Auto  -generated method stub
		return null;
	}

	@Override
	public String marshallMessageToXML(SIGMETType reportType) throws JAXBException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

}
