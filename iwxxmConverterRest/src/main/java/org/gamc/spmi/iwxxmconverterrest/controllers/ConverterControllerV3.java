package org.gamc.spmi.iwxxmconverterrest.controllers;

import java.util.List;
import java.util.Optional;

import org.gamc.iwxxmconverterrest.common.Converters;
import org.gamc.iwxxmconverterrest.common.Validator;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFTacMessage;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
import org.gamc.spmi.iwxxmConverter.validation.FailedValidationAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("convert/v3")
public class ConverterControllerV3 {
	
	@Autowired
	private Validator validator;
	
	@Autowired
	private Converters converters;
	
	@RequestMapping(value = "/metar",produces= { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE  })
	public @ResponseBody ValidationResult convertMetar(@RequestBody String metar) throws Exception {
		
		String xml = converters.getMetarConverter().convertTacToXML(metar);
		List<FailedValidationAssert> asserts = validator.getIwxxmValidator().validateString(xml);
		ValidationResult result = new ValidationResult(asserts, xml);
		return result;
	}
	
	@RequestMapping(value = "/taf/raw",produces=  MediaType.APPLICATION_JSON_VALUE)
	public TAFTacMessage getParsedTaf(@RequestBody String taf) throws Exception {
		
		TAFTacMessage parsed = converters.getTafConverter().convertTacToTAFMessage(taf);
		
		ObjectMapper m = new ObjectMapper();
		m.findAndRegisterModules();

		String s = m.writeValueAsString(parsed);
		
		return parsed;
	}
	
	
	
	
	
	
	
	@RequestMapping(value = "/taf",produces= { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE  })
	public @ResponseBody ValidationResult convertTaf(@RequestBody String taf) throws Exception {
		
		String xml = converters.getTafConverter().convertTacToXML(taf);
		List<FailedValidationAssert> asserts = validator.getIwxxmValidator().validateString(xml);
		ValidationResult result = new ValidationResult(asserts, xml);
		return result;
	}
	
	@RequestMapping(value = "/speci",produces= { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE  })
	public @ResponseBody ValidationResult convertSpeci(@RequestBody String speci) throws Exception {
		
		String xml = converters.getSpeciConverter().convertTacToXML(speci);
		List<FailedValidationAssert> asserts = validator.getIwxxmValidator().validateString(xml);
		ValidationResult result = new ValidationResult(asserts, xml);
		return result;
	}
	 
	@RequestMapping(value = "/sigmet",produces= { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE  })
	public @ResponseBody ValidationResult convertSigmet(@RequestBody String sigmet) throws Exception {
		
		String xml = converters.getSigmetConverter().convertTacToXML(sigmet);
		List<FailedValidationAssert> asserts = validator.getIwxxmValidator().validateString(xml);
		ValidationResult result = new ValidationResult(asserts, xml);
		return result;
	}

	@RequestMapping(value = "/airmet",produces= { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE  })
	public @ResponseBody ValidationResult convertAirmet(@RequestBody String airmet) throws Exception {
		
		String xml = converters.getAirmetConverter().convertTacToXML(airmet);
		List<FailedValidationAssert> asserts = validator.getIwxxmValidator().validateString(xml);
		ValidationResult result = new ValidationResult(asserts, xml);
		return result;
	}
	
	@RequestMapping(value = "/swx",produces= { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE  })
	public @ResponseBody ValidationResult convertSpaceWeather(@RequestBody String swx) throws Exception {
		
		String xml = converters.getSpaceWeatherConverter().convertTacToXML(swx);
		List<FailedValidationAssert> asserts = validator.getIwxxmValidator().validateString(xml);
		ValidationResult result = new ValidationResult(asserts, xml);
		return result;
	}
	
	/**You don't know the message type? - use factory
	 * @throws Exception */
	@RequestMapping(value = "",produces= { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE  })
	public @ResponseBody ValidationResult convertUndefinedMessage(@RequestBody String message, @RequestParam Optional<Boolean> encodeXml) throws Exception {
		TacConverter<?, ?, ?> converter = converters.createConverterForTac(message);
		String xml = converter.convertTacToXML(message);
		List<FailedValidationAssert> asserts = validator.getIwxxmValidator().validateString(xml);
		
		if (encodeXml.isPresent() && encodeXml.get())
			xml = org.apache.commons.codec.binary.Base64.encodeBase64String(xml.getBytes("UTF-8"));	
		ValidationResult result = new ValidationResult(asserts, xml);
		return result;
	}
	
}
