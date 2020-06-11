package org.gamc.iwxxmconverterrest.common;

import java.net.URISyntaxException;

import javax.annotation.PostConstruct;

import org.gamc.gis.service.GeoService;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.AIRMETConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.ConverterFactory;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.IWXXM31Helpers;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.METARConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SPACEWEATHERConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SPECIConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFConverterV3;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
public class ConvertersV3 implements Converters {

	@Value("${iwxxm.geoservice.storePath}")
	private String storePath;
	
	@Value("${iwxxm.geoservice.useExternalStorage}")
	private boolean useExternalStorage;
	
	@Value("${iwxxm.geoservice.useLatitudeFirst}")
	private boolean latitudeFirst;
	
	private METARConverterV3 metarConverter;
	private TAFConverterV3 tafConverter;
	private SPECIConverterV3 speciConverter;
	private SIGMETConverterV3 sigmetConverter;
	private AIRMETConverterV3 airmetConverter;
	private SPACEWEATHERConverterV3 spaceWeatherConverter;
	
	private Logger logger = LoggerFactory.getLogger("ConvertersV3");
	private IWXXM31Helpers helper;
	
	@PostConstruct
	public void onInit() throws URISyntaxException {
		
		//prepare helper with custom-configured geoservice 
		GeoService gs = new  GeoService();
		gs.init(useExternalStorage, storePath, latitudeFirst);
		
		helper = new IWXXM31Helpers();
		helper.setGeoService(gs);
		
		logger.info("Geoservice created");
		
		metarConverter = new METARConverterV3().withHelper(helper);
		tafConverter = new TAFConverterV3().withHelper(helper);
		speciConverter = new SPECIConverterV3().withHelper(helper);
		sigmetConverter = new SIGMETConverterV3().withHelper(helper);
		airmetConverter = new AIRMETConverterV3().withHelper(helper);
		spaceWeatherConverter = new SPACEWEATHERConverterV3().withHelper(helper);
		
		logger.info("Converters created");
	}
	
	@Override
	public METARConverterV3 getMetarConverter() {
		return metarConverter;
	}

	@Override
	public TAFConverterV3 getTafConverter() {
		return tafConverter;
	}

	@Override
	public SPECIConverterV3 getSpeciConverter() {
		return speciConverter;
	}

	@Override
	public SIGMETConverterV3 getSigmetConverter() {
		return sigmetConverter;
	}

	@Override
	public AIRMETConverterV3 getAirmetConverter() {
		return airmetConverter;
	}

	@Override
	public SPACEWEATHERConverterV3 getSpaceWeatherConverter() {
		return spaceWeatherConverter;
	}

	/**Delegates converter creation to the factory and builds it with pre-configured IwxxmHelper
	 * @throws ParsingException */
	@Override
	public TacConverter<?, ?, ?> createConverterForTac(String initialTac) throws ParsingException {
		TacConverter<?, ?, IWXXM31Helpers> converter = ConverterFactory.createForTac(initialTac).withHelper(helper);
		return converter;
	}
}
