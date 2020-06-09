/**
 * Copyright (C) 2018 Dmitry Moryakov, Main aeronautical meteorological center, Moscow, Russia
 * moryakovdv[at]gmail[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gamc.spmi.iwxxmConverter.marshallers.v2;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.common.NamespaceMapper;
import org.gamc.spmi.iwxxmConverter.common.StringConstants;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
import org.joda.time.DateTime;

import schemabindings21._int.icao.iwxxm._2.METARType;
import schemabindings21._int.icao.iwxxm._2.MeteorologicalAerodromeReportStatusType;
import schemabindings21._int.icao.iwxxm._2.PermissibleUsageReasonType;
import schemabindings21._int.icao.iwxxm._2.PermissibleUsageType;
import schemabindings21._int.icao.iwxxm._2.SPECIType;
import schemabindings21.net.opengis.om._2.OMObservationPropertyType;

/**
 * Base class to perform conversion of TAC into intermediate object
 * {@link METARTacMessage} and further IWXXM conversion and validation
 */
public class SPECIConverter implements TacConverter<SPECITacMessage, SPECIType, IWXXM21Helpers> {

	schemabindings21._int.icao.iwxxm._2.ObjectFactory ofIWXXM = new schemabindings21._int.icao.iwxxm._2.ObjectFactory();
	IWXXM21Helpers iwxxmHelpers = new IWXXM21Helpers();
	private SPECITacMessage translatedSpeci;

	private String dateTime = "";
	private String dateTimePosition = "";

	@Override
	public String marshallMessageToXML(SPECIType reportType) throws JAXBException, UnsupportedEncodingException {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		JAXBContext jaxbContext = JAXBContext.newInstance(SPECIType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, UriConstants.GLOBAL_SCHEMAS_LOCATION);

		jaxbMarshaller.setProperty(StringConstants.SUN_JAXB_NAMESPACE_MAPPING_PROPERTY_NAME, new NamespaceMapper());

		JAXBElement<SPECIType> metarRootElement = ofIWXXM.createSPECI(reportType);

		jaxbMarshaller.marshal(metarRootElement, stream);

		return stream.toString("UTF-8");

	}

	@Override
	public String convertTacToXML(String tac) throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException {
		SPECITacMessage speciMessage = new SPECITacMessage(tac);
		SPECIType result;
		try {
			speciMessage.parseMessage();
			 result = convertMessage(speciMessage);
		}
		catch(ParsingException pe) {
			result = IWXXM21Helpers.ofIWXXM.createSPECIType();
			result.setTranslationFailedTAC(tac);
		}

		String xmlResult = marshallMessageToXML(result); 
		
		return xmlResult;
	}

	@Override
	public SPECIType convertMessage(SPECITacMessage translatedMessage)
			throws DatatypeConfigurationException, UnsupportedEncodingException, JAXBException, ParsingException {

		this.translatedSpeci = translatedMessage;

		// <iwxxm:METAR> root tag
		SPECIType speciRootTag = ofIWXXM.createSPECIType();

		dateTime = translatedMessage.getMessageIssueDateTime().toString(iwxxmHelpers.getDateTimeFormat()) + "Z";
		dateTimePosition = translatedMessage.getMessageIssueDateTime().toString(iwxxmHelpers.getDateTimeISOFormat());

		// Id with ICAO code and current timestamp
		speciRootTag.setId(iwxxmHelpers.generateUUIDv4(String.format("metar-%s-%s", translatedSpeci.getIcaoCode(), dateTime)));

		// metarRootTag.setAutomatedStation(true);

		// Set NON_OPERATIONAL and TEST properties.
		speciRootTag.setPermissibleUsage(PermissibleUsageType.NON_OPERATIONAL);
		speciRootTag.setPermissibleUsageReason(PermissibleUsageReasonType.TEST);

		// Some description
		speciRootTag.setPermissibleUsageSupplementary("SPECI composing test using JAXB");
		
		//SPECI is always normal
		speciRootTag.setStatus(MeteorologicalAerodromeReportStatusType.NORMAL);

		speciRootTag = addTranslationCentreHeader(speciRootTag);

		String substitutionTac = translatedSpeci.getInitialTacString();
		substitutionTac = substitutionTac.replaceAll("SPECI", "METAR");
		
		//Mimic MetarConverter to use it's methods   
		METARConverter c = new METARConverter();
		METARTacMessage metarMessage = new METARTacMessage(substitutionTac);
		metarMessage.parseMessage();
		METARType metarResult = c.convertMessage(metarMessage); 
		
		
		//set prepared observation
		speciRootTag.setObservation(metarResult.getObservation());

		//copy trends
		for(OMObservationPropertyType trend : metarResult.getTrendForecast()) {
			speciRootTag.getTrendForecast().add(trend);
		}
		
		// create XML representation
		return speciRootTag;
		
	}

	@Override
	public SPECIType addTranslationCentreHeader(SPECIType report) throws DatatypeConfigurationException {
		report = iwxxmHelpers.addTranslationCentreHeaders(report, DateTime.now(), DateTime.now(),
				UUID.randomUUID().toString(), "UUWW", "Vnukovo, RU");
		report.setTranslationFailedTAC("");

		return report;
	}

	@Override
	public IWXXM21Helpers getHelper() {
		return iwxxmHelpers;
	}
	
	@Override
	public SPECIConverter withHelper(IWXXM21Helpers helper) {
		iwxxmHelpers = helper;
		return this;
		
	}

}
