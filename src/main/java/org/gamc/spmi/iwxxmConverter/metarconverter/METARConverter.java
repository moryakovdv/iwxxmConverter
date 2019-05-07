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
package org.gamc.spmi.iwxxmConverter.metarconverter;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.common.IWXXM21Helpers;
import org.gamc.spmi.iwxxmConverter.common.NamespaceMapper;
import org.gamc.spmi.iwxxmConverter.common.StringConstants;
import org.gamc.spmi.iwxxmConverter.common.UriConstants;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.general.MetarForecastSection;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.ANGLE_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.TEMPERATURE_UNITS;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
import org.gamc.spmi.iwxxmConverter.wmo.WMOCloudRegister;
import org.joda.time.DateTime;


import schemabindings21._int.icao.iwxxm._2.AerodromeCloudForecastPropertyType;
import schemabindings21._int.icao.iwxxm._2.AerodromeCloudForecastType;
import schemabindings21._int.icao.iwxxm._2.AerodromeHorizontalVisibilityPropertyType;
import schemabindings21._int.icao.iwxxm._2.AerodromeHorizontalVisibilityType;
import schemabindings21._int.icao.iwxxm._2.AerodromeObservedCloudsPropertyType;
import schemabindings21._int.icao.iwxxm._2.AerodromeObservedCloudsType;
import schemabindings21._int.icao.iwxxm._2.AerodromeRunwayStatePropertyType;
import schemabindings21._int.icao.iwxxm._2.AerodromeRunwayStateType;
import schemabindings21._int.icao.iwxxm._2.AerodromeRunwayVisualRangePropertyType;
import schemabindings21._int.icao.iwxxm._2.AerodromeRunwayVisualRangeType;
import schemabindings21._int.icao.iwxxm._2.AerodromeSurfaceWindPropertyType;
import schemabindings21._int.icao.iwxxm._2.AerodromeSurfaceWindTrendForecastPropertyType;
import schemabindings21._int.icao.iwxxm._2.AerodromeSurfaceWindTrendForecastType;
import schemabindings21._int.icao.iwxxm._2.AerodromeSurfaceWindType;
import schemabindings21._int.icao.iwxxm._2.AerodromeWindShearPropertyType;
import schemabindings21._int.icao.iwxxm._2.AerodromeWindShearType;
import schemabindings21._int.icao.iwxxm._2.DistanceWithNilReasonType;
import schemabindings21._int.icao.iwxxm._2.ForecastChangeIndicatorType;
import schemabindings21._int.icao.iwxxm._2.LengthWithNilReasonType;
import schemabindings21._int.icao.iwxxm._2.METARType;
import schemabindings21._int.icao.iwxxm._2.MeteorologicalAerodromeObservationRecordPropertyType;
import schemabindings21._int.icao.iwxxm._2.MeteorologicalAerodromeObservationRecordType;
import schemabindings21._int.icao.iwxxm._2.MeteorologicalAerodromeObservationRecordType.Cloud;
import schemabindings21._int.icao.iwxxm._2.MeteorologicalAerodromeReportStatusType;
import schemabindings21._int.icao.iwxxm._2.MeteorologicalAerodromeTrendForecastRecordPropertyType;
import schemabindings21._int.icao.iwxxm._2.MeteorologicalAerodromeTrendForecastRecordType;
import schemabindings21._int.icao.iwxxm._2.PermissibleUsageReasonType;
import schemabindings21._int.icao.iwxxm._2.PermissibleUsageType;
import schemabindings21._int.icao.iwxxm._2.RelationalOperatorType;
import schemabindings21._int.icao.iwxxm._2.RunwayContaminationType;
import schemabindings21._int.icao.iwxxm._2.RunwayDepositsType;
import schemabindings21._int.icao.iwxxm._2.RunwayDirectionPropertyType;
import schemabindings21._int.icao.iwxxm._2.RunwayFrictionCoefficientType;
import schemabindings21._int.icao.iwxxm._2.VisualRangeTendencyType;
import schemabindings21._int.wmo.def.metce._2013.ProcessType;
import schemabindings21.aero.aixm.schema._5_1.RunwayDirectionType;
import schemabindings21.net.opengis.gml.v_3_2_1.AngleType;
import schemabindings21.net.opengis.gml.v_3_2_1.CodeType;
import schemabindings21.net.opengis.gml.v_3_2_1.FeaturePropertyType;
import schemabindings21.net.opengis.gml.v_3_2_1.LengthType;
import schemabindings21.net.opengis.gml.v_3_2_1.MeasureType;
import schemabindings21.net.opengis.gml.v_3_2_1.ReferenceType;
import schemabindings21.net.opengis.gml.v_3_2_1.SpeedType;
import schemabindings21.net.opengis.gml.v_3_2_1.StringOrRefType;
import schemabindings21.net.opengis.gml.v_3_2_1.TimeInstantPropertyType;
import schemabindings21.net.opengis.gml.v_3_2_1.TimeInstantType;
import schemabindings21.net.opengis.gml.v_3_2_1.TimePeriodType;
import schemabindings21.net.opengis.gml.v_3_2_1.TimePositionType;
import schemabindings21.net.opengis.om._2.OMObservationPropertyType;
import schemabindings21.net.opengis.om._2.OMObservationType;
import schemabindings21.net.opengis.om._2.OMProcessPropertyType;
import schemabindings21.net.opengis.om._2.TimeObjectPropertyType;

/**
 * Base class to perform conversion of TAC into intermediate object
 * {@link METARTacMessage} and further IWXXM conversion and validation
 */
public class METARConverter implements TacConverter<METARTacMessage, METARType> {
	/*
	 * First of all we should create any involved object with ObjectFactory helpers
	 */
	schemabindings21._int.icao.iwxxm._2.ObjectFactory ofIWXXM = new schemabindings21._int.icao.iwxxm._2.ObjectFactory();
	schemabindings21.net.opengis.gml.v_3_2_1.ObjectFactory ofGML = new schemabindings21.net.opengis.gml.v_3_2_1.ObjectFactory();
	schemabindings21.net.opengis.om._2.ObjectFactory ofOM = new schemabindings21.net.opengis.om._2.ObjectFactory();
	schemabindings21.org.w3._1999.xlink.ObjectFactory ofXLink = new schemabindings21.org.w3._1999.xlink.ObjectFactory();
	schemabindings21.aero.aixm.schema._5_1.ObjectFactory ofAIXM = new schemabindings21.aero.aixm.schema._5_1.ObjectFactory();
	schemabindings21._int.wmo.def.metce._2013.ObjectFactory ofMetce = new schemabindings21._int.wmo.def.metce._2013.ObjectFactory();
	schemabindings21.net.opengis.samplingspatial._2.ObjectFactory ofSams = new schemabindings21.net.opengis.samplingspatial._2.ObjectFactory();

	IWXXM21Helpers iwxxmHelpers = new IWXXM21Helpers();

	private METARTacMessage translatedMetar;

	// Storage for created runways description
	private TreeMap<String, String> createdRunways = new TreeMap<>();

	private String dateTime = "";
	private String dateTimePosition = "";

	@Override
	public String convertTacToXML(String tac)
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, ParsingException {

		createdRunways.clear();

		METARTacMessage metarMessage = new METARTacMessage(tac);
		metarMessage.parseMessage();

		METARType result = convertMessage(metarMessage);

		String xmlResult = marshallMessageToXML(result);

		return xmlResult;

	}

	@Override
	public METARType convertMessage(METARTacMessage translatedMessage)
			throws DatatypeConfigurationException, UnsupportedEncodingException, JAXBException, ParsingException {

		this.translatedMetar = translatedMessage;

		// <iwxxm:METAR> root tag
		METARType metarRootTag = ofIWXXM.createMETARType();

		dateTime = translatedMessage.getMessageIssueDateTime().toString(iwxxmHelpers.getDateTimeFormat()) + "Z";
		dateTimePosition = translatedMessage.getMessageIssueDateTime().toString(iwxxmHelpers.getDateTimeISOFormat());

		// Id with ICAO code and current timestamp
		metarRootTag.setId(
				iwxxmHelpers.generateUUIDv4(String.format("metar-%s-%s", translatedMetar.getIcaoCode(), dateTime)));

		// metarRootTag.setAutomatedStation(true);

		// Set NON_OPERATIONAL and TEST properties.
		metarRootTag.setPermissibleUsage(PermissibleUsageType.NON_OPERATIONAL);
		metarRootTag.setPermissibleUsageReason(PermissibleUsageReasonType.TEST);

		// Some description
		metarRootTag.setPermissibleUsageSupplementary("METAR composing test using JAXB");

		// COR, NIL, NORMAL
		switch (translatedMetar.getMessageStatusType()) {
		case MISSING:
			metarRootTag.setStatus(MeteorologicalAerodromeReportStatusType.MISSING);
			break;
		case CORRECTION:
			metarRootTag.setStatus(MeteorologicalAerodromeReportStatusType.CORRECTION);
			break;
		default:
			metarRootTag.setStatus(MeteorologicalAerodromeReportStatusType.NORMAL);
		}

		//
		metarRootTag = addTranslationCentreHeader(metarRootTag);

		// Compose METAR body message and place it in the root
		metarRootTag.setObservation(createObservationResult());

		AtomicInteger sectionIndex = new AtomicInteger(0);
		// TODO : create TrendForecast and possible Extensions (RMK)

		if (translatedMetar.isNoSignificantChanges()) {

		}

		for (METARBecomingSection bcmgSection : translatedMetar.getBecomingSections()) {
			bcmgSection.parseSection();
			OMObservationPropertyType omptBcmg = createTrendForecast(bcmgSection, sectionIndex.getAndIncrement());
			metarRootTag.getTrendForecast().add(omptBcmg);
		}

		for (METARTempoSection tempoSection : translatedMetar.getTempoSections()) {
			tempoSection.parseSection();
			OMObservationPropertyType omptTempo = createTrendForecast(tempoSection, sectionIndex.getAndIncrement());
			metarRootTag.getTrendForecast().add(omptTempo);
		}

		// create XML representation
		return metarRootTag;

	}

	/**
	 * Marshall root METAR to XML with output to string
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public String marshallMessageToXML(METARType metar) throws JAXBException, UnsupportedEncodingException {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		JAXBContext jaxbContext = JAXBContext.newInstance(METARType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, UriConstants.GLOBAL_SCHEMAS_LOCATION);

		jaxbMarshaller.setProperty(StringConstants.SUN_JAXB_NAMESPACE_MAPPING_PROPERTY_NAME, new NamespaceMapper());

		JAXBElement<METARType> metarRootElement = ofIWXXM.createMETAR(metar);

		jaxbMarshaller.marshal(metarRootElement, stream);

		return stream.toString("UTF-8");
	}

	@Override
	public METARType addTranslationCentreHeader(METARType report) throws DatatypeConfigurationException {
		report = iwxxmHelpers.addTranslationCentreHeaders(report, DateTime.now(), DateTime.now(),
				UUID.randomUUID().toString(), "UUWW", "Vnukovo, RU");
		report.setTranslationFailedTAC("");

		return report;
	}

	// -------------------------------//

	private OMObservationPropertyType createObservationResult() {

		// тег <>om:OM_Observation
		OMObservationPropertyType omOM_Observation = ofOM.createOMObservationPropertyType();

		OMObservationType ot = ofOM.createOMObservationType();
		ot.setId(iwxxmHelpers.generateUUIDv4(String.format("obs-%s-%s", translatedMetar.getIcaoCode(), dateTime)));

		// тип наблюдения - ссылка xlink:href
		ReferenceType observeType = ofGML.createReferenceType();
		observeType.setHref(UriConstants.OBSERVATION_TYPE_METAR);
		ot.setType(observeType);

		// Create instant time section
		TimeObjectPropertyType timeObjectProperty = ofOM.createTimeObjectPropertyType();
		TimeInstantType timeInstant = ofGML.createTimeInstantType();
		timeInstant
				.setId(iwxxmHelpers.generateUUIDv4(String.format("ti-%s-%s", translatedMetar.getIcaoCode(), dateTime)));
		TimePositionType timePosition = ofGML.createTimePositionType();
		timePosition.getValue().add(dateTimePosition);
		timeInstant.setTimePosition(timePosition);

		JAXBElement<TimeInstantType> timeElement = ofGML.createTimeInstant(timeInstant);
		timeObjectProperty.setAbstractTimeObject(timeElement);

		// and place it to <phenomenonTime>
		ot.setPhenomenonTime(timeObjectProperty);

		// create <resultTime>
		TimeInstantPropertyType timeInstantResult = ofGML.createTimeInstantPropertyType();
		timeInstantResult.setHref("#" + timeInstant.getId());// "#ti-UUWW-"+dateTime);
		ot.setResultTime(timeInstantResult);

		// create <om:procedure> frame
		ProcessType metceProcess = ofMetce.createProcessType();
		metceProcess.setId(iwxxmHelpers.generateUUIDv4("p-49-2-metar"));

		StringOrRefType processDescription = ofGML.createStringOrRefType();
		processDescription.setValue(StringConstants.WMO_49_2_METCE_METAR);
		metceProcess.setDescription(processDescription);

		OMProcessPropertyType omProcedure = ofOM.createOMProcessPropertyType();
		omProcedure.setAny(ofMetce.createProcess(metceProcess));
		ot.setProcedure(omProcedure);

		// tag om:ObserverdProperty
		ReferenceType observedProperty = ofGML.createReferenceType();
		observedProperty.setHref(UriConstants.OBSERVED_PROPERTY_METAR);
		observedProperty.setTitle(StringConstants.WMO_METAR_OBSERVED_PROPERTY_TITLE);

		ot.setObservedProperty(observedProperty);

		ot.setFeatureOfInterest(createAirportDescriptionSectionTag());

		// At last create payload
		MeteorologicalAerodromeObservationRecordPropertyType metarRecord = createMETARRecordTag();

		// and place it into <iwxxm:result> section
		ot.setResult(metarRecord);

		omOM_Observation.setOMObservation(ot);

		return omOM_Observation;

	}

	private MeteorologicalAerodromeTrendForecastRecordPropertyType createTrendResultsSection(
			MetarForecastSection section, int sectionIndex) {

		MeteorologicalAerodromeTrendForecastRecordPropertyType metarTrendType = ofIWXXM
				.createMeteorologicalAerodromeTrendForecastRecordPropertyType();
		MeteorologicalAerodromeTrendForecastRecordType metarTrend = ofIWXXM
				.createMeteorologicalAerodromeTrendForecastRecordType();
		metarTrendType.setMeteorologicalAerodromeTrendForecastRecord(metarTrend);

		ForecastChangeIndicatorType changeIndicator = ForecastChangeIndicatorType.BECOMING;
		switch (section.getSectionType()) {
		case BECMG:
			changeIndicator = ForecastChangeIndicatorType.BECOMING;
			break;
		case TEMPO:
			changeIndicator = ForecastChangeIndicatorType.TEMPORARY_FLUCTUATIONS;
			break;
		default:
			break;
		}
		metarTrend.setId(iwxxmHelpers
				.generateUUIDv4(String.format("change-record-%d-%s", sectionIndex, translatedMetar.getIcaoCode())));
		metarTrend.setChangeIndicator(changeIndicator);
		metarTrend.setCloudAndVisibilityOK(section.getCommonWeatherSection().isCavok());

		// visibility
		if (section.getCommonWeatherSection().getPrevailVisibility() != null) {
			LengthType vis = ofGML.createLengthType();
			vis.setUom(section.getCommonWeatherSection().getVisibilityUnits().getStringValue());
			vis.setValue(section.getCommonWeatherSection().getPrevailVisibility());
			metarTrend.setPrevailingVisibility(vis);
		}

		// surfaceWind
		AerodromeSurfaceWindTrendForecastPropertyType sWindpropertyType = ofIWXXM
				.createAerodromeSurfaceWindTrendForecastPropertyType();
		AerodromeSurfaceWindTrendForecastType sWindType = ofIWXXM.createAerodromeSurfaceWindTrendForecastType();
		boolean sectionHasWind = false;
		// Set gust speed
		if (section.getCommonWeatherSection().getGustSpeed() != null) {
			SpeedType speedGustType = ofGML.createSpeedType();
			speedGustType.setUom(section.getCommonWeatherSection().getSpeedUnits().getStringValue());
			speedGustType.setValue(section.getCommonWeatherSection().getGustSpeed());
			sWindType.setWindGustSpeed(speedGustType);
			sectionHasWind = true;
		}

		// Set mean wind
		if (section.getCommonWeatherSection().getWindSpeed() != null) {
			SpeedType speedMeanType = ofGML.createSpeedType();
			speedMeanType.setUom(section.getCommonWeatherSection().getSpeedUnits().getStringValue());
			speedMeanType.setValue(section.getCommonWeatherSection().getWindSpeed());
			sWindType.setMeanWindSpeed(speedMeanType);
			sectionHasWind = true;
		}

		// Set wind direction
		if (section.getCommonWeatherSection().getWindDir() != null) {
			AngleType windAngle = ofGML.createAngleType();
			windAngle.setUom(ANGLE_UNITS.DEGREES.getStringValue());
			windAngle.setValue(section.getCommonWeatherSection().getWindDir());
			sWindType.setMeanWindDirection(windAngle);
			sectionHasWind = true;
		}

		if (sectionHasWind) {
			JAXBElement<AerodromeSurfaceWindTrendForecastType> windElement = ofIWXXM
					.createAerodromeSurfaceWindTrendForecast(sWindType);
			sWindpropertyType.setAerodromeSurfaceWindTrendForecast(windElement);
			metarTrend.setSurfaceWind(sWindpropertyType);
		}

		// clouds

		AerodromeCloudForecastPropertyType cloudType = createTrendCloudSectionTag(section.getCommonWeatherSection(),
				translatedMetar.getIcaoCode(), sectionIndex);
		JAXBElement<AerodromeCloudForecastPropertyType> cloudTypeEl = ofIWXXM
				.createMeteorologicalAerodromeTrendForecastRecordTypeCloud(cloudType);
		metarTrend.setCloud(cloudTypeEl);

		// forecasted weather
		for (String weatherCode : section.getCommonWeatherSection().getCurrentWeather()) {
			metarTrend.getForecastWeather().add(iwxxmHelpers.createForecastWeatherSection(weatherCode));
		}

		// process runways state

		return metarTrendType;
	}

	/**
	 * Create aerodrome description section as GML FeatureOfInterest ICAO code
	 */
	private FeaturePropertyType createAirportDescriptionSectionTag() {

		return iwxxmHelpers.createAirportDescriptionSectionTag(translatedMetar.getIcaoCode());

	}

	/**
	 * Create valuable METAR section as Observation result. Tag
	 * <iwxxm:MeteorologicalAerodromeObservtionRecord>
	 */
	private MeteorologicalAerodromeObservationRecordPropertyType createMETARRecordTag() {

		// Envelop
		MeteorologicalAerodromeObservationRecordPropertyType metarRecordTag = ofIWXXM
				.createMeteorologicalAerodromeObservationRecordPropertyType();
		// body
		MeteorologicalAerodromeObservationRecordType metarRecord = ofIWXXM
				.createMeteorologicalAerodromeObservationRecordType();
		metarRecord.setId(iwxxmHelpers
				.generateUUIDv4(String.format("obs-record-%s-%s", translatedMetar.getIcaoCode(), dateTime)));

		// Set temperature
		MeasureType mtTemperature = ofGML.createMeasureType();
		mtTemperature.setUom(TEMPERATURE_UNITS.CELSIUS.getStringValue());
		mtTemperature.setValue(translatedMetar.getCommonWeatherSection().getAirTemperature().doubleValue());
		metarRecord.setAirTemperature(mtTemperature);

		// Set dew pont
		MeasureType mtDew = ofGML.createMeasureType();
		mtDew.setUom(TEMPERATURE_UNITS.CELSIUS.getStringValue());
		mtDew.setValue(translatedMetar.getCommonWeatherSection().getDewPoint().doubleValue());
		metarRecord.setDewpointTemperature(mtDew);

		// Set QNH
		MeasureType mtQNH = ofGML.createMeasureType();
		mtQNH.setUom(translatedMetar.getCommonWeatherSection().getQnhUnits().getStringValue());
		mtQNH.setValue(translatedMetar.getCommonWeatherSection().getQnh().doubleValue());
		metarRecord.setQnh(mtQNH);

		metarRecord.setCloudAndVisibilityOK(translatedMetar.getCommonWeatherSection().isCavok());

		// Create and set wind section
		metarRecord.setSurfaceWind(createWindSectionTag());

		// Create and set visibility section
		if (!translatedMetar.getCommonWeatherSection().isCavok())
			metarRecord.setVisibility(createVisibilitySectionTag());

		// create and set present weather conditions
		for (String weather : translatedMetar.getCommonWeatherSection().getCurrentWeather()) {
			metarRecord.getPresentWeather().add(iwxxmHelpers.createPresentWeatherSection(weather));
		}

		// add recent weather
		for (String recentWeather : translatedMetar.getCommonWeatherSection().getRecentWeather()) {
			metarRecord.getRecentWeather().add(iwxxmHelpers.createRecentWeatherSection(recentWeather));
		}

		// Create and set cloud section
		if (!translatedMetar.getCommonWeatherSection().isCavok())
			metarRecord.setCloud(createCloudSectionTag());

		// set wind shear
		metarRecord.setWindShear(createWindShearTag());

		// process runway visible range sections
		for (METARRVRSection rvrs : translatedMetar.getRvrSections()) {

			metarRecord.getRvr().add(createRVRTag(rvrs));

		}

		// process runways state
		for (METARRunwayStateSection rwState : translatedMetar.getRunwayStateSections()) {

			metarRecord.getRunwayState().add(createRunwayStateTag(rwState));
		}

		// Place the body into envelop
		metarRecordTag.setMeteorologicalAerodromeObservationRecord(metarRecord);

		return metarRecordTag;
	}

	/** Wind section */
	private AerodromeSurfaceWindPropertyType createWindSectionTag() {
		// Envelop
		AerodromeSurfaceWindPropertyType surfaceWindType = ofIWXXM.createAerodromeSurfaceWindPropertyType();

		// body
		AerodromeSurfaceWindType surfaceWind = ofIWXXM.createAerodromeSurfaceWindType();

		surfaceWind.setVariableWindDirection(translatedMetar.getCommonWeatherSection().isVrb());

		// Set gust speed
		if (translatedMetar.getCommonWeatherSection().getGustSpeed() != null) {
			SpeedType speedGustType = ofGML.createSpeedType();
			speedGustType.setUom(translatedMetar.getCommonWeatherSection().getSpeedUnits().getStringValue());
			speedGustType.setValue(translatedMetar.getCommonWeatherSection().getGustSpeed().doubleValue());
			surfaceWind.setWindGustSpeed(speedGustType);
		}

		// Set mean wind
		if (translatedMetar.getCommonWeatherSection().getWindSpeed() != null) {
			SpeedType speedMeanType = ofGML.createSpeedType();
			speedMeanType.setUom(translatedMetar.getCommonWeatherSection().getSpeedUnits().getStringValue());
			speedMeanType.setValue(translatedMetar.getCommonWeatherSection().getWindSpeed().doubleValue());
			surfaceWind.setMeanWindSpeed(speedMeanType);
		}

		// Set wind direction
		if (translatedMetar.getCommonWeatherSection().getWindDir() != null) {
			AngleType windAngle = ofGML.createAngleType();
			windAngle.setUom(ANGLE_UNITS.DEGREES.getStringValue());
			windAngle.setValue(translatedMetar.getCommonWeatherSection().getWindDir().doubleValue());
			surfaceWind.setMeanWindDirection(windAngle);
		}

		// Set wind angles
		if (translatedMetar.getCommonWeatherSection().getWindVariableFrom() != null) {
			AngleType windAngleCW = ofGML.createAngleType();
			windAngleCW.setUom(ANGLE_UNITS.DEGREES.getStringValue());
			windAngleCW.setValue(translatedMetar.getCommonWeatherSection().getWindVariableFrom());
			surfaceWind.setExtremeClockwiseWindDirection(windAngleCW);
		}

		if (translatedMetar.getCommonWeatherSection().getWindVariableTo() != null) {
			AngleType windAngleCCW = ofGML.createAngleType();
			windAngleCCW.setUom(ANGLE_UNITS.DEGREES.getStringValue());
			windAngleCCW.setValue(translatedMetar.getCommonWeatherSection().getWindVariableTo());
			surfaceWind.setExtremeCounterClockwiseWindDirection(windAngleCCW);
		}

		// Place body into envelop
		surfaceWindType.setAerodromeSurfaceWind(surfaceWind);

		return surfaceWindType;
	}

	/** Visibility section */
	private AerodromeHorizontalVisibilityPropertyType createVisibilitySectionTag() {
		// Envelop
		AerodromeHorizontalVisibilityPropertyType visiblityType = ofIWXXM
				.createAerodromeHorizontalVisibilityPropertyType();
		// body
		AerodromeHorizontalVisibilityType visibility = ofIWXXM.createAerodromeHorizontalVisibilityType();

		// Minimal visibility
		if (translatedMetar.getCommonWeatherSection().getMinimumVisibility() != null) {
			LengthType minVis = ofGML.createLengthType();
			minVis.setUom(translatedMetar.getCommonWeatherSection().getVisibilityUnits().getStringValue());
			minVis.setValue(translatedMetar.getCommonWeatherSection().getMinimumVisibility());
			visibility.setMinimumVisibility(minVis);
		}

		// Prevailing visibility
		if (translatedMetar.getCommonWeatherSection().getPrevailVisibility() != null) {
			LengthType prevailVis = ofGML.createLengthType();
			prevailVis.setUom(translatedMetar.getCommonWeatherSection().getVisibilityUnits().getStringValue());
			prevailVis.setValue(translatedMetar.getCommonWeatherSection().getPrevailVisibility());
			visibility.setPrevailingVisibility(prevailVis);
		}

		// Place body to the envelop
		visiblityType.setAerodromeHorizontalVisibility(visibility);

		return visiblityType;
	}

	/** Cloud section */
	private JAXBElement<MeteorologicalAerodromeObservationRecordType.Cloud> createCloudSectionTag() {

		MeteorologicalAerodromeObservationRecordType.Cloud cloudSection = new Cloud();

		// Envelop
		AerodromeObservedCloudsPropertyType cloudsType = ofIWXXM.createAerodromeObservedCloudsPropertyType();

		// Body
		AerodromeObservedCloudsType clouds = ofIWXXM.createAerodromeObservedCloudsType();

		for (METARCloudSection cloudS : translatedMetar.getCommonWeatherSection().getCloudSections()) {

			int cloudAmount = iwxxmHelpers.getCloudReg().getCloudAmountByStringCode(cloudS.getAmount());
			String nilReason = null;
			if (cloudAmount == WMOCloudRegister.missingCode) {
				JAXBElement<LengthWithNilReasonType> vVisibility = iwxxmHelpers
						.createVerticalVisibilitySection(cloudS.getHeight());
				clouds.setVerticalVisibility(vVisibility);

			} else {
				AerodromeObservedCloudsType.Layer cloudLayer = ofIWXXM.createAerodromeObservedCloudsTypeLayer();
				cloudLayer.setCloudLayer(iwxxmHelpers.createCloudLayerSection(cloudAmount, cloudS.getHeight(),
						cloudS.getType(), nilReason, LENGTH_UNITS.FT));
				clouds.getLayer().add(cloudLayer);
			}
		}
		// Place body into envelop
		// cloudsType.setAerodromeObservedClouds(clouds);
		cloudSection.setAerodromeObservedClouds(clouds);
		JAXBElement<MeteorologicalAerodromeObservationRecordType.Cloud> result = ofIWXXM
				.createMeteorologicalAerodromeObservationRecordTypeCloud(cloudSection);

		return result;

	}

	private AerodromeCloudForecastPropertyType createTrendCloudSectionTag(MetarCommonWeatherSection section,
			String icaoCode, int sectionIndex) {

		// Envelop
		AerodromeCloudForecastPropertyType cloudsType = ofIWXXM.createAerodromeCloudForecastPropertyType();

		// Body
		AerodromeCloudForecastType clouds = ofIWXXM.createAerodromeCloudForecastType();
		clouds.setId(iwxxmHelpers.generateUUIDv4(String.format("acf-%d-%s", sectionIndex, icaoCode)));
		for (METARCloudSection cloudS : section.getCloudSections()) {

			int cloudAmount = iwxxmHelpers.getCloudReg().getCloudAmountByStringCode(cloudS.getAmount());
			String nilReason = null;
			if (cloudAmount == WMOCloudRegister.missingCode) {
				nilReason = "Value is missing or VV provided";
			}
			AerodromeCloudForecastType.Layer cloudLayer = ofIWXXM.createAerodromeCloudForecastTypeLayer();
			cloudLayer.setCloudLayer(iwxxmHelpers.createCloudLayerSection(cloudAmount, cloudS.getHeight(),
					cloudS.getType(), nilReason, LENGTH_UNITS.FT));
			clouds.getLayer().add(cloudLayer);
		}
		// Place body into envelop
		cloudsType.setAerodromeCloudForecast(clouds);

		return cloudsType;

	}

	/** Wind shear section */
	private AerodromeWindShearPropertyType createWindShearTag() {
		// Envelop
		AerodromeWindShearPropertyType windShearType = ofIWXXM.createAerodromeWindShearPropertyType();
		// body
		AerodromeWindShearType windShear = ofIWXXM.createAerodromeWindShearType();

		for (String rwWs : translatedMetar.getWindShearSections()) {
			// Runway description
			RunwayDirectionPropertyType runwayType = ofIWXXM.createRunwayDirectionPropertyType();
			RunwayDirectionType runway = ofAIXM.createRunwayDirectionType();
			runway.setId(iwxxmHelpers.generateUUIDv4("runway-" + rwWs));
			CodeType rwCode = ofGML.createCodeType();
			rwCode.setValue(rwWs);
			runway.getName().add(rwCode);
			runwayType.setRunwayDirection(runway);

			windShear.getRunway().add(runwayType);

		}

		// Place body to envelop
		windShearType.setAerodromeWindShear(windShear);

		return windShearType;
	}

	/** Creates Runway visual range tag */
	private AerodromeRunwayVisualRangePropertyType createRVRTag(METARRVRSection rvrs) {
		AerodromeRunwayVisualRangePropertyType rvrpt = ofIWXXM.createAerodromeRunwayVisualRangePropertyType();
		AerodromeRunwayVisualRangeType rvrType = ofIWXXM.createAerodromeRunwayVisualRangeType();

		RunwayDirectionPropertyType runwayDir = null;

		if (!createdRunways.containsKey(rvrs.getRvrDesignator())) {
			runwayDir = iwxxmHelpers.createRunwayDesignatorSectionTag(translatedMetar.getIcaoCode(),
					rvrs.getRvrDesignator());
			createdRunways.put(rvrs.getRvrDesignator(), runwayDir.getRunwayDirection().getId());
		} else {
			runwayDir = ofIWXXM.createRunwayDirectionPropertyType();
			runwayDir.setHref("#" + createdRunways.get(rvrs.getRvrDesignator()));
		}

		rvrType.setRunway(runwayDir);

		// mean rvr
		if (rvrs.getRvrValue() != null) {
			LengthType meanLength = ofGML.createLengthType();
			meanLength.setUom(rvrs.getUnits().getStringValue());
			meanLength.setValue(rvrs.getRvrValue());
			rvrType.setMeanRVR(meanLength);
		}

		// mean operator
		RelationalOperatorType rvrOper = RelationalOperatorType.ABOVE;
		if (rvrs.getOperator() != null) {
			switch (rvrs.getOperator()) {
			case M:
				rvrOper = RelationalOperatorType.ABOVE;
				break;
			case P:
				rvrOper = RelationalOperatorType.BELOW;
				break;
			}
		}
		rvrType.setMeanRVROperator(rvrOper);

		// Visal range tendency
		VisualRangeTendencyType vrTendency = VisualRangeTendencyType.MISSING_VALUE;

		if (rvrs.getTendency() != null) {
			switch (rvrs.getTendency()) {
			case D:
				vrTendency = VisualRangeTendencyType.DOWNWARD;
				break;
			case N:
				vrTendency = VisualRangeTendencyType.NO_CHANGE;
				break;
			case U:
				vrTendency = VisualRangeTendencyType.UPWARD;
				break;
			}
		}
		rvrType.setPastTendency(vrTendency);

		// add to envelop
		rvrpt.setAerodromeRunwayVisualRange(rvrType);
		return rvrpt;

	}

	/** Creates Runway state tag to include into collection */
	private AerodromeRunwayStatePropertyType createRunwayStateTag(METARRunwayStateSection rwrs) {

		/**
		 * output sample <iwxxm:runwayState> <iwxxm:AerodromeRunwayState> <iwxxm:runway>
		 * <aixm:RunwayDirection gml:id="runway-UUEE-06L"> <aixm:timeSlice>
		 * <aixm:RunwayDirectionTimeSlice gml:id="runway-UUEE-06L-ts"> <gml:validTime/>
		 * <aixm:interpretation>BASELINE</aixm:interpretation>
		 * <aixm:designator>06L</aixm:designator> </aixm:RunwayDirectionTimeSlice>
		 * </aixm:timeSlice> </aixm:RunwayDirection> </iwxxm:runway>
		 * <iwxxm:depositType xlink:href=
		 * "http://codes.wmo.int/bufr4/codeflag/0-20-086/5"/>
		 * <iwxxm:contamination xlink:href=
		 * "http://codes.wmo.int/bufr4/codeflag/0-20-087/9"/>
		 * <iwxxm:depthOfDeposit uom="mm">2</iwxxm:depthOfDeposit>
		 * <iwxxm:estimatedSurfaceFrictionOrBrakingAction xlink:href=
		 * "http://codes.wmo.int/bufr4/codeflag/0-20-089/30"/>
		 * </iwxxm:AerodromeRunwayState> </iwxxm:runwayState>
		 * 
		 */

		AerodromeRunwayStatePropertyType rvrTag = ofIWXXM.createAerodromeRunwayStatePropertyType();
		AerodromeRunwayStateType rvrState = ofIWXXM.createAerodromeRunwayStateType();

		rvrState.setAllRunways(rwrs.isApplicableForAllRunways());
		rvrState.setCleared(rwrs.isCleared());

		if (!rwrs.isApplicableForAllRunways()) {
			RunwayDirectionPropertyType runwayDir = null;

			if (!createdRunways.containsKey(rwrs.getRvrDesignator())) {
				runwayDir = iwxxmHelpers.createRunwayDesignatorSectionTag(translatedMetar.getIcaoCode(),
						rwrs.getRvrDesignator());
				createdRunways.put(rwrs.getRvrDesignator(), runwayDir.getRunwayDirection().getId());
			} else {
				runwayDir = ofIWXXM.createRunwayDirectionPropertyType();
				runwayDir.setHref("#" + createdRunways.get(rwrs.getRvrDesignator()));
			}

			rvrState.setRunway(runwayDir);
		}

		if (rwrs.getType().isPresent()) {
			RunwayDepositsType dType = ofIWXXM.createRunwayDepositsType();
			String depUrl = iwxxmHelpers.getRwDepositReg().getWMOUrlByCode(rwrs.getType().get());
			dType.setHref(depUrl);
			rvrState.setDepositType(dType);
		}

		if (rwrs.getContamination().isPresent()) {
			RunwayContaminationType cType = ofIWXXM.createRunwayContaminationType();
			String contUrl = iwxxmHelpers.getRwContaminationReg().getWMOUrlByCode(rwrs.getContamination().get());
			cType.setHref(contUrl);
			rvrState.setContamination(cType);
		}

		if (rwrs.getDepositDepth().isPresent()) {
			DistanceWithNilReasonType depth = ofIWXXM.createDistanceWithNilReasonType();
			depth.setValue(rwrs.getDepositDepth().get());
			depth.setUom(LENGTH_UNITS.MM.getStringValue());
			// JAXBElement<DistanceWithNilReasonType> depthTag =
			// ofIWXXM.createDistanceWithNilReason(depth);
			JAXBElement<DistanceWithNilReasonType> depthTag = ofIWXXM
					.createAerodromeRunwayStateTypeDepthOfDeposit(depth);
			rvrState.setDepthOfDeposit(depthTag);
		}

		if (rwrs.getFriction().isPresent()) {
			RunwayFrictionCoefficientType frictionType = ofIWXXM.createRunwayFrictionCoefficientType();
			String frictionUrl = iwxxmHelpers.getRwFrictionReg().getWMOUrlByCode(rwrs.getFriction().get());
			frictionType.setHref(frictionUrl);
			rvrState.setEstimatedSurfaceFrictionOrBrakingAction(frictionType);
		}
		rvrTag.setAerodromeRunwayState(rvrState);
		return rvrTag;

	}

	/** Creates issueTime */
	private TimeInstantPropertyType createIssueTimesection() {
		return iwxxmHelpers.createJAXBTimeSection(translatedMetar.getMessageIssueDateTime(),
				translatedMetar.getIcaoCode());
	}

	private OMObservationPropertyType createTrendForecast(MetarForecastSection section, int sectionIndex) {

		// тег <om:OM_Observation>
		OMObservationPropertyType omOM_Observation = ofOM.createOMObservationPropertyType();
		OMObservationType ot = ofOM.createOMObservationType();
		ot.setId(iwxxmHelpers.generateUUIDv4(String.format("cf-%d-%s", sectionIndex, translatedMetar.getIcaoCode())));

		// тип наблюдения - ссылка xlink:href
		ReferenceType observeType = ofGML.createReferenceType();
		observeType.setHref(UriConstants.OBSERVATION_TYPE_METAR);
		ot.setType(observeType);

		FeaturePropertyType featureOfInterestType = ofGML.createFeaturePropertyType();

		featureOfInterestType
				.setHref("#" + createAirportDescriptionSectionTag().getAbstractFeature().getValue().getId());
		ot.setFeatureOfInterest(featureOfInterestType);

		// phenomenon time for metar
		TimeObjectPropertyType phenomenonTimeProperty = ofOM.createTimeObjectPropertyType();
		JAXBElement<TimePeriodType> timeElement = ofGML.createTimePeriod(iwxxmHelpers
				.createTrendPeriodSection(translatedMetar.getIcaoCode(), section.getTrendValidityInterval().getStart(),
						section.getTrendValidityInterval().getEnd(), sectionIndex)
				.getTimePeriod());
		phenomenonTimeProperty.setAbstractTimeObject(timeElement);

		ot.setPhenomenonTime(phenomenonTimeProperty);

		// result time for metar = link to issueTime
		TimeInstantPropertyType resultTime = ofGML.createTimeInstantPropertyType();
		resultTime.setHref("#" + createIssueTimesection().getTimeInstant().getId());
		ot.setResultTime(resultTime);

		// create <om:procedure> frame
		OMProcessPropertyType omProcedure = ofOM.createOMProcessPropertyType();
		omProcedure.setHref("#"+iwxxmHelpers.generateUUIDv4(String.format("p-49-2-metar")));
		ot.setProcedure(omProcedure);

		// тег om:ObserverdProperty
		ReferenceType observedProperty = ofGML.createReferenceType();
		observedProperty.setHref(UriConstants.OBSERVED_PROPERTY_METAR);
		// observedProperty.setTitle(StringConstants.WMO_TAF_OBSERVED_PROPERTY_TITLE);
		ot.setObservedProperty(observedProperty);

		// set result section
		ot.setResult(createTrendResultsSection(section, sectionIndex));

		omOM_Observation.setOMObservation(ot);

		return omOM_Observation;

	}

}
