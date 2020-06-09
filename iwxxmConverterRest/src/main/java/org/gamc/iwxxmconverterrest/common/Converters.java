package org.gamc.iwxxmconverterrest.common;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.AIRMETConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.METARConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SPACEWEATHERConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SPECIConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFConverterV3;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;

public interface Converters {
	
	METARConverterV3 getMetarConverter();
	TAFConverterV3 getTafConverter();
	SPECIConverterV3 getSpeciConverter();
	SIGMETConverterV3 getSigmetConverter();
	AIRMETConverterV3 getAirmetConverter();
	SPACEWEATHERConverterV3 getSpaceWeatherConverter();
	
	TacConverter<?, ?, ?> createConverterForTac(String initialTac) throws ParsingException;
	

}
