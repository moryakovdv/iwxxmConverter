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
package org.gamc.spmi.iwxxmConverter.marshallers.v3;

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

import org.gamc.spmi.iwxxmConverter.common.NamespaceMapper;
import org.gamc.spmi.iwxxmConverter.common.StringConstants;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.general.MetarForecastSection;
import org.gamc.spmi.iwxxmConverter.general.MetarForecastTimeSection;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.ANGLE_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.TEMPERATURE_UNITS;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARBecomingSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARCloudSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARRVRSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARRunwayStateSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARTempoSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARTimedATSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARTimedFMSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARTimedTLSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.MetarCommonWeatherSection;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
import org.gamc.spmi.iwxxmConverter.wmo.WMOCloudRegister;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import schemabindings31._int.icao.iwxxm._3.AbstractTimeObjectPropertyType;
import schemabindings31._int.icao.iwxxm._3.AerodromeCloudForecastPropertyType;
import schemabindings31._int.icao.iwxxm._3.AerodromeCloudForecastType;
import schemabindings31._int.icao.iwxxm._3.AerodromeCloudType;
import schemabindings31._int.icao.iwxxm._3.AerodromeCloudType.Layer;
import schemabindings31._int.icao.iwxxm._3.MeteorologicalAerodromeObservationType.SurfaceWind;
import schemabindings31._int.icao.iwxxm._3.AerodromeHorizontalVisibilityType;
import schemabindings31._int.icao.iwxxm._3.AerodromeRunwayStateType;
import schemabindings31._int.icao.iwxxm._3.AerodromeRunwayVisualRangeType;
import schemabindings31._int.icao.iwxxm._3.AerodromeSurfaceWindPropertyType;
import schemabindings31._int.icao.iwxxm._3.AerodromeSurfaceWindTrendForecastPropertyType;
import schemabindings31._int.icao.iwxxm._3.AerodromeSurfaceWindTrendForecastType;
import schemabindings31._int.icao.iwxxm._3.AerodromeSurfaceWindType;
import schemabindings31._int.icao.iwxxm._3.AerodromeWindShearType;
import schemabindings31._int.icao.iwxxm._3.AirportHeliportPropertyType;
import schemabindings31._int.icao.iwxxm._3.AngleWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.CloudLayerPropertyType;
import schemabindings31._int.icao.iwxxm._3.DistanceWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.ForecastChangeIndicatorType;
import schemabindings31._int.icao.iwxxm._3.LengthWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.METARType;
import schemabindings31._int.icao.iwxxm._3.MeasureWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.MeteorologicalAerodromeObservationPropertyType;
import schemabindings31._int.icao.iwxxm._3.MeteorologicalAerodromeObservationType;
import schemabindings31._int.icao.iwxxm._3.MeteorologicalAerodromeTrendForecastPropertyType;
import schemabindings31._int.icao.iwxxm._3.MeteorologicalAerodromeTrendForecastType;
import schemabindings31._int.icao.iwxxm._3.PermissibleUsageReasonType;
import schemabindings31._int.icao.iwxxm._3.PermissibleUsageType;
import schemabindings31._int.icao.iwxxm._3.RelationalOperatorType;
import schemabindings31._int.icao.iwxxm._3.ReportStatusType;
import schemabindings31._int.icao.iwxxm._3.RunwayContaminationType;
import schemabindings31._int.icao.iwxxm._3.RunwayDepositsType;
import schemabindings31._int.icao.iwxxm._3.RunwayDirectionPropertyType;
import schemabindings31._int.icao.iwxxm._3.RunwayFrictionCoefficientType;
import schemabindings31._int.icao.iwxxm._3.TrendForecastTimeIndicatorType;
import schemabindings31._int.icao.iwxxm._3.VelocityWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.VisualRangeTendencyType;
import schemabindings31.aero.aixm.schema._5_1.RunwayDirectionType;
import schemabindings31.net.opengis.gml.v_3_2_1.AngleType;
import schemabindings31.net.opengis.gml.v_3_2_1.BoundingShapeType;
import schemabindings31.net.opengis.gml.v_3_2_1.CodeType;
import schemabindings31.net.opengis.gml.v_3_2_1.EnvelopeType;
import schemabindings31.net.opengis.gml.v_3_2_1.LengthType;
import schemabindings31.net.opengis.gml.v_3_2_1.SpeedType;
import schemabindings31.net.opengis.gml.v_3_2_1.StringOrRefType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimeInstantPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimePeriodPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimePeriodType;

/**
 * Base class to perform conversion of TAC into intermediate object
 * {@link METARTacMessage} and further IWXXM conversion and validation
 */
public class METARConverterV3 implements TacConverter<METARTacMessage, METARType, IWXXM31Helpers> {
	/*
	 * First of all we should create any involved object with ObjectFactory helpers
	 */
	
	
	
	private IWXXM31Helpers iwxxmHelpers = new IWXXM31Helpers();
	

	private METARTacMessage translatedMetar;

	protected Logger logger = LoggerFactory.getLogger(METARConverterV3.class);

	
	// Storage for created runways description
	private TreeMap<String, String> createdRunways = new TreeMap<>();

	private String dateTime = "";
	private String dateTimePosition = "";

	@Override
	public String convertTacToXML(String tac)
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException {
		logger.debug("Parsing "+ tac);

		createdRunways.clear();

		METARTacMessage metarMessage = new METARTacMessage(tac);
		METARType result;
		try {
			metarMessage.parseMessage();
			 result = convertMessage(metarMessage);
		}
		catch(ParsingException pe) {
			result = iwxxmHelpers.getOfIWXXM().createMETARType();
			result.setTranslationFailedTAC(tac);
			logger.error("Message was NOT parsed properly. XML message with translationFailedTAC attribute was created. See error below",pe);

		}
		

		String xmlResult = marshallMessageToXML(result);

		return xmlResult;

	}

	@Override
	public METARType convertMessage(METARTacMessage translatedMessage)
			throws DatatypeConfigurationException, UnsupportedEncodingException, JAXBException, ParsingException {

		this.translatedMetar = translatedMessage;

		// <iwxxm:METAR> root tag
		METARType metarRootTag = iwxxmHelpers.getOfIWXXM().createMETARType();
		StringOrRefType refTacString = iwxxmHelpers.getOfGML().createStringOrRefType();
		refTacString.setValue(translatedMessage.getInitialTacString());
		metarRootTag.setDescription(refTacString);
	
		/*
		 * BoundingShapeType shape = iwxxmHelpers.getOfGML().createBoundingShapeType();
		 * EnvelopeType env = iwxxmHelpers.getOfGML().createEnvelopeType();
		 * 
		 * shape.setEnvelope(iwxxmHelpers.getOfGML().createEnvelope(env));
		 * 
		 * metarRootTag.setBoundedBy(shape);
		 */

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
			metarRootTag.setReportStatus(null);
			break;
		case CORRECTION:
			metarRootTag.setReportStatus(ReportStatusType.CORRECTION);
			break;
		default:
			metarRootTag.setReportStatus(ReportStatusType.NORMAL);
		}

		//
		metarRootTag = addTranslationCentreHeader(metarRootTag);

		metarRootTag.setAerodrome(createAirportDescriptionSectionTag());

		TimeInstantPropertyType issueTimeType = iwxxmHelpers.createTimeInstantPropertyTypeForDateTime(
				translatedMetar.getMessageIssueDateTime(), translatedMetar.getIcaoCode(),"issue");
		metarRootTag.setIssueTime(issueTimeType);
		
		TimeInstantPropertyType obsTimeType = iwxxmHelpers.createTimeInstantPropertyTypeForDateTime(
				translatedMetar.getMessageIssueDateTime(), translatedMetar.getIcaoCode(),"observation");
		
		metarRootTag.setObservationTime(obsTimeType);

		// Compose METAR body message and place it in the root
		MeteorologicalAerodromeObservationPropertyType observation = createMETARRecordTag();
		JAXBElement<MeteorologicalAerodromeObservationPropertyType> observationTag = iwxxmHelpers.getOfIWXXM()
				.createMeteorologicalAerodromeObservationReportTypeObservation(observation);
		metarRootTag.setObservation(observationTag);

		AtomicInteger sectionIndex = new AtomicInteger(0);
		// TODO : create TrendForecast and possible Extensions (RMK)

		if (translatedMetar.isNoSignificantChanges()) {

		}

		for (METARBecomingSection bcmgSection : translatedMetar.getBecomingSections()) {
			bcmgSection.parseSection();
			if (bcmgSection.getSectionType() == null)
				continue;
			MeteorologicalAerodromeTrendForecastPropertyType omptBcmg = createTrendResultsSection(bcmgSection,
					sectionIndex.getAndIncrement());
			metarRootTag.getTrendForecast().add(omptBcmg);
		}

		for (METARTempoSection tempoSection : translatedMetar.getTempoSections()) {
			tempoSection.parseSection();
			MeteorologicalAerodromeTrendForecastPropertyType omptTempo = createTrendResultsSection(tempoSection,
					sectionIndex.getAndIncrement());
			metarRootTag.getTrendForecast().add(omptTempo);
		}

		for (MetarForecastTimeSection tSection : translatedMetar.getTimedSections()) {
			tSection.parseSection();
			MeteorologicalAerodromeTrendForecastPropertyType omptBcmg = createTrendResultsSection(tSection,
					sectionIndex.getAndIncrement());
			metarRootTag.getTrendForecast().add(omptBcmg);
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

		jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, UriConstants.GLOBAL_SCHEMAS_LOCATION_V3);

		jaxbMarshaller.setProperty(StringConstants.SUN_JAXB_NAMESPACE_MAPPING_PROPERTY_NAME, new NamespaceMapper());

		JAXBElement<METARType> metarRootElement = iwxxmHelpers.getOfIWXXM().createMETAR(metar);
		
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
	/*
	 * private MeteorologicalAerodromeObservationPropertyType
	 * createObservationResult() {
	 * 
	 * // тег <>om:OM_Observation MeteorologicalAerodromeObservationPropertyType
	 * result = ofIWXXM.createMeteorologicalAerodromeObservationPropertyType();
	 * MeteorologicalAerodromeObservationType obsType =
	 * ofIWXXM.createMeteorologicalAerodromeObservationType();
	 * result.setMeteorologicalAerodromeObservation(obsType);
	 * 
	 * obsType.setId(iwxxmHelpers.generateUUIDv4(String.format("obs-%s-%s",
	 * translatedMetar.getIcaoCode(), dateTime)));
	 * 
	 * // тип наблюдения - ссылка xlink:href ReferenceType observeType =
	 * ofGML.createReferenceType();
	 * observeType.setHref(UriConstants.OBSERVATION_TYPE_METAR);
	 * obsType.setType(observeType);
	 * 
	 * // Create instant time section TimeObjectPropertyType timeObjectProperty =
	 * ofOM.createTimeObjectPropertyType(); TimeInstantType timeInstant =
	 * ofGML.createTimeInstantType(); timeInstant
	 * .setId(iwxxmHelpers.generateUUIDv4(String.format("ti-%s-%s",
	 * translatedMetar.getIcaoCode(), dateTime))); TimePositionType timePosition =
	 * ofGML.createTimePositionType();
	 * timePosition.getValue().add(dateTimePosition);
	 * timeInstant.setTimePosition(timePosition);
	 * 
	 * JAXBElement<TimeInstantType> timeElement =
	 * ofGML.createTimeInstant(timeInstant);
	 * timeObjectProperty.setAbstractTimeObject(timeElement);
	 * 
	 * // and place it to <phenomenonTime>
	 * obsType.se.setPhenomenonTime(timeObjectProperty);
	 * 
	 * // create <resultTime> TimeInstantPropertyType timeInstantResult =
	 * ofGML.createTimeInstantPropertyType(); timeInstantResult.setHref("#" +
	 * timeInstant.getId());// "#ti-UUWW-"+dateTime);
	 * obsType.setResultTime(timeInstantResult);
	 * 
	 * // create <om:procedure> frame ProcessType metceProcess =
	 * ofMetce.createProcessType();
	 * metceProcess.setId(iwxxmHelpers.generateUUIDv4("p-49-2-metar"));
	 * 
	 * StringOrRefType processDescription = ofGML.createStringOrRefType();
	 * processDescription.setValue(StringConstants.WMO_49_2_METCE_METAR);
	 * metceProcess.setDescription(processDescription);
	 * 
	 * OMProcessPropertyType omProcedure = ofOM.createOMProcessPropertyType();
	 * omProcedure.setAny(ofMetce.createProcess(metceProcess));
	 * ot.setProcedure(omProcedure);
	 * 
	 * // tag om:ObserverdProperty ReferenceType observedProperty =
	 * ofGML.createReferenceType();
	 * observedProperty.setHref(UriConstants.OBSERVED_PROPERTY_METAR);
	 * observedProperty.setTitle(StringConstants.WMO_METAR_OBSERVED_PROPERTY_TITLE);
	 * 
	 * ot.setObservedProperty(observedProperty);
	 * 
	 * ot.setFeatureOfInterest(createAirportDescriptionSectionTag());
	 * 
	 * // At last create payload MeteorologicalAerodromeObservationPropertyType
	 * metarRecord = createMETARRecordTag();
	 * 
	 * 
	 * 
	 * 
	 * 
	 * return result;
	 * 
	 * }
	 */
	private MeteorologicalAerodromeTrendForecastPropertyType createTrendResultsSection(MetarForecastSection section,
			int sectionIndex) {

		MeteorologicalAerodromeTrendForecastPropertyType metarTrendType = iwxxmHelpers.getOfIWXXM()
				.createMeteorologicalAerodromeTrendForecastPropertyType();
		MeteorologicalAerodromeTrendForecastType metarTrend = iwxxmHelpers.getOfIWXXM()
				.createMeteorologicalAerodromeTrendForecastType();
		metarTrendType.setMeteorologicalAerodromeTrendForecast(metarTrend);

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
			LengthType vis = iwxxmHelpers.getOfGML().createLengthType();
			vis.setUom(section.getCommonWeatherSection().getVisibilityUnits().getStringValue());
			vis.setValue(section.getCommonWeatherSection().getPrevailVisibility());
			metarTrend.setPrevailingVisibility(vis);
		}

		// surfaceWind
		AerodromeSurfaceWindTrendForecastPropertyType sWindpropertyType = iwxxmHelpers.getOfIWXXM()
				.createAerodromeSurfaceWindTrendForecastPropertyType();
		AerodromeSurfaceWindTrendForecastType sWindType = iwxxmHelpers.getOfIWXXM()
				.createAerodromeSurfaceWindTrendForecastType();
		boolean sectionHasWind = false;
		// Set gust speed
		if (section.getCommonWeatherSection().getGustSpeed() != null) {
			SpeedType speedGustType = iwxxmHelpers.getOfGML().createSpeedType();
			speedGustType.setUom(section.getCommonWeatherSection().getSpeedUnits().getStringValue());
			speedGustType.setValue(section.getCommonWeatherSection().getGustSpeed());
			sWindType.setWindGustSpeed(speedGustType);
			sectionHasWind = true;
		}

		// Set mean wind
		if (section.getCommonWeatherSection().getWindSpeed() != null) {
			SpeedType speedMeanType = iwxxmHelpers.getOfGML().createSpeedType();
			speedMeanType.setUom(section.getCommonWeatherSection().getSpeedUnits().getStringValue());
			speedMeanType.setValue(section.getCommonWeatherSection().getWindSpeed());
			sWindType.setMeanWindSpeed(speedMeanType);
			sectionHasWind = true;
		}

		// Set wind direction
		if (section.getCommonWeatherSection().getWindDir() != null) {
			AngleType windAngle = iwxxmHelpers.getOfGML().createAngleType();
			windAngle.setUom(ANGLE_UNITS.DEGREES.getStringValue());
			windAngle.setValue(section.getCommonWeatherSection().getWindDir());
			sWindType.setMeanWindDirection(windAngle);
			sectionHasWind = true;
		}

		if (sectionHasWind) {
			JAXBElement<AerodromeSurfaceWindTrendForecastType> windElement = iwxxmHelpers.getOfIWXXM()
					.createAerodromeSurfaceWindTrendForecast(sWindType);
			sWindpropertyType.setAerodromeSurfaceWindTrendForecast(windElement);
			metarTrend.setSurfaceWind(sWindpropertyType);
		}

		// clouds
		if (section.getCommonWeatherSection().getCloudSections().size() > 0) {
			AerodromeCloudForecastPropertyType cloudType = createCloudSectionTag(section.getCommonWeatherSection(),
					translatedMetar.getIcaoCode(), sectionIndex);
			JAXBElement<AerodromeCloudForecastPropertyType> cloudTypeEl = iwxxmHelpers.getOfIWXXM()
					.createMeteorologicalAerodromeTrendForecastTypeCloud(cloudType);
			metarTrend.setCloud(cloudTypeEl);
		}
		// forecasted weather
		for (String weatherCode : section.getCommonWeatherSection().getCurrentWeather()) {
			metarTrend.getWeather().add(iwxxmHelpers.createForecastWeatherSection(weatherCode));
		}

		TrendForecastTimeIndicatorType timeIndicator = null;
		if (section instanceof METARTimedATSection) {
			timeIndicator = TrendForecastTimeIndicatorType.AT;

		} else if (section instanceof METARTimedFMSection) {
			timeIndicator = TrendForecastTimeIndicatorType.FROM;

		} else if (section instanceof METARTimedTLSection) {
			timeIndicator = TrendForecastTimeIndicatorType.UNTIL;
		}

		if (timeIndicator != null)
			metarTrend.setTimeIndicator(timeIndicator);

		Interval timeIntreval = section.getTrendValidityInterval();
		TimePeriodPropertyType period = iwxxmHelpers.createTrendPeriodSection(translatedMetar.getIcaoCode(),
				timeIntreval.getStart(), timeIntreval.getEnd(), sectionIndex);
		JAXBElement<TimePeriodType> periodTime = iwxxmHelpers.getOfGML().createTimePeriod(period.getTimePeriod());

		AbstractTimeObjectPropertyType aTime = iwxxmHelpers.getOfIWXXM().createAbstractTimeObjectPropertyType();
		aTime.setAbstractTimeObject(periodTime);

		metarTrend.setPhenomenonTime(aTime);

		return metarTrendType;
	}

	/**
	 * Create aerodrome description section as GML FeatureOfInterest ICAO code
	 */
	private AirportHeliportPropertyType createAirportDescriptionSectionTag() {

		return iwxxmHelpers.createAirportDescriptionSectionTag(translatedMetar.getIcaoCode());

	}

	/**
	 * Create valuable METAR section as Observation result. Tag
	 * <iwxxm:MeteorologicalAerodromeObservtionRecord>
	 */
	private MeteorologicalAerodromeObservationPropertyType createMETARRecordTag() {

		// Envelop
		MeteorologicalAerodromeObservationPropertyType metarRecordTag = iwxxmHelpers.getOfIWXXM()
				.createMeteorologicalAerodromeObservationPropertyType();
		// body
		MeteorologicalAerodromeObservationType metarRecord = iwxxmHelpers.getOfIWXXM()
				.createMeteorologicalAerodromeObservationType();
		metarRecord.setId(iwxxmHelpers
				.generateUUIDv4(String.format("obs-record-%s-%s", translatedMetar.getIcaoCode(), dateTime)));

		// Set temperature
		MeasureWithNilReasonType mtTemperature = iwxxmHelpers.getOfIWXXM().createMeasureWithNilReasonType();
		mtTemperature.setUom(TEMPERATURE_UNITS.CELSIUS.getStringValue());
		mtTemperature.setValue(translatedMetar.getCommonWeatherSection().getAirTemperature().doubleValue());
		metarRecord.setAirTemperature(mtTemperature);

		// Set dew pont
		MeasureWithNilReasonType mtDew = iwxxmHelpers.getOfIWXXM().createMeasureWithNilReasonType();
		mtDew.setUom(TEMPERATURE_UNITS.CELSIUS.getStringValue());
		mtDew.setValue(translatedMetar.getCommonWeatherSection().getDewPoint().doubleValue());
		metarRecord.setDewpointTemperature(mtDew);

		// Set QNH
		MeasureWithNilReasonType mtQNH = iwxxmHelpers.getOfIWXXM().createMeasureWithNilReasonType();
		mtQNH.setUom(translatedMetar.getCommonWeatherSection().getQnhUnits().getStringValue());
		mtQNH.setValue(translatedMetar.getCommonWeatherSection().getQnh().doubleValue());
		metarRecord.setQnh(mtQNH);

		metarRecord.setCloudAndVisibilityOK(translatedMetar.getCommonWeatherSection().isCavok());

		// Create and set wind section
		metarRecord.setSurfaceWind(createWindSectionTag());

		// Create and set visibility section
		if (!translatedMetar.getCommonWeatherSection().isCavok()) {
			MeteorologicalAerodromeObservationType.Visibility hVis = createVisibilitySectionTag();
			JAXBElement<MeteorologicalAerodromeObservationType.Visibility> horVisibility = iwxxmHelpers.getOfIWXXM()
					.createMeteorologicalAerodromeObservationTypeVisibility(hVis);
			metarRecord.setVisibility(horVisibility);
		}

		// create and set present weather conditions
		for (String weather : translatedMetar.getCommonWeatherSection().getCurrentWeather()) {
			metarRecord.getPresentWeather().add(iwxxmHelpers.createPresentWeatherSection(weather));
		}

		// add recent weather
		for (String recentWeather : translatedMetar.getCommonWeatherSection().getRecentWeather()) {
			metarRecord.getRecentWeather().add(iwxxmHelpers.createRecentWeatherSection(recentWeather));
		}

		// Create and set cloud section

		if (!translatedMetar.getCommonWeatherSection().isCavok()) {
			JAXBElement<MeteorologicalAerodromeObservationType.Cloud> cloudTag = iwxxmHelpers.getOfIWXXM()
					.createMeteorologicalAerodromeObservationTypeCloud(createCloudSectionTag());
			metarRecord.setCloud(cloudTag);
		}
		// set wind shear
		JAXBElement<MeteorologicalAerodromeObservationType.WindShear> wsTag = iwxxmHelpers.getOfIWXXM()
				.createMeteorologicalAerodromeObservationTypeWindShear(createWindShearTag());
		metarRecord.setWindShear(wsTag);

		// process runway visible range sections
		for (METARRVRSection rvrs : translatedMetar.getRvrSections()) {

			metarRecord.getRvr().add(createRVRTag(rvrs));

		}

		// process runways state
		for (METARRunwayStateSection rwState : translatedMetar.getRunwayStateSections()) {

			metarRecord.getRunwayState().add(createRunwayStateTag(rwState));
		}

		// Place the body into envelop
		metarRecordTag.setMeteorologicalAerodromeObservation(metarRecord);

		return metarRecordTag;
	}

	/** Wind section */
	private SurfaceWind createWindSectionTag() {

		SurfaceWind result = iwxxmHelpers.getOfIWXXM().createMeteorologicalAerodromeObservationTypeSurfaceWind();
		// body
		AerodromeSurfaceWindType surfaceWind = iwxxmHelpers.getOfIWXXM().createAerodromeSurfaceWindType();

		surfaceWind.setVariableWindDirection(translatedMetar.getCommonWeatherSection().isVrb());

		// Set gust speed
		if (translatedMetar.getCommonWeatherSection().getGustSpeed() != null) {
			VelocityWithNilReasonType speedGustType = iwxxmHelpers.getOfIWXXM().createVelocityWithNilReasonType();

			speedGustType.setUom(translatedMetar.getCommonWeatherSection().getSpeedUnits().getStringValue());
			speedGustType.setValue(translatedMetar.getCommonWeatherSection().getGustSpeed().doubleValue());
			JAXBElement<VelocityWithNilReasonType> sg = iwxxmHelpers.getOfIWXXM()
					.createAerodromeSurfaceWindTypeWindGustSpeed(speedGustType);
			surfaceWind.setWindGustSpeed(sg);
		}

		// Set mean wind
		if (translatedMetar.getCommonWeatherSection().getWindSpeed() != null) {
			VelocityWithNilReasonType speedMeanType = iwxxmHelpers.getOfIWXXM().createVelocityWithNilReasonType();

			speedMeanType.setUom(translatedMetar.getCommonWeatherSection().getSpeedUnits().getStringValue());
			speedMeanType.setValue(translatedMetar.getCommonWeatherSection().getWindSpeed().doubleValue());
			surfaceWind.setMeanWindSpeed(speedMeanType);
		}

		// Set wind direction
		if (translatedMetar.getCommonWeatherSection().getWindDir() != null) {
			AngleWithNilReasonType windAngle = iwxxmHelpers.getOfIWXXM().createAngleWithNilReasonType();

			windAngle.setUom(ANGLE_UNITS.DEGREES.getStringValue());
			windAngle.setValue(translatedMetar.getCommonWeatherSection().getWindDir().doubleValue());
			JAXBElement<AngleWithNilReasonType> wa = iwxxmHelpers.getOfIWXXM()
					.createAerodromeSurfaceWindTypeMeanWindDirection(windAngle);
			wa.setValue(windAngle);
			surfaceWind.setMeanWindDirection(wa);
		}

		// Set wind angles
		if (translatedMetar.getCommonWeatherSection().getWindVariableFrom() != null) {
			AngleWithNilReasonType windAngleCW = iwxxmHelpers.getOfIWXXM().createAngleWithNilReasonType();

			windAngleCW.setUom(ANGLE_UNITS.DEGREES.getStringValue());
			windAngleCW.setValue(translatedMetar.getCommonWeatherSection().getWindVariableFrom());
			JAXBElement<AngleWithNilReasonType> waCW = iwxxmHelpers.getOfIWXXM()
					.createAerodromeSurfaceWindTypeExtremeClockwiseWindDirection(windAngleCW);
			surfaceWind.setExtremeClockwiseWindDirection(waCW);
		}

		if (translatedMetar.getCommonWeatherSection().getWindVariableTo() != null) {
			AngleWithNilReasonType windAngleCCW = iwxxmHelpers.getOfIWXXM().createAngleWithNilReasonType();
			windAngleCCW.setUom(ANGLE_UNITS.DEGREES.getStringValue());
			windAngleCCW.setValue(translatedMetar.getCommonWeatherSection().getWindVariableTo());
			JAXBElement<AngleWithNilReasonType> waCCW = iwxxmHelpers.getOfIWXXM()
					.createAerodromeSurfaceWindTypeExtremeCounterClockwiseWindDirection(windAngleCCW);
			surfaceWind.setExtremeCounterClockwiseWindDirection(waCCW);
		}

		result.setAerodromeSurfaceWind(surfaceWind);

		return result;
	}

	/** Visibility section */
	private MeteorologicalAerodromeObservationType.Visibility createVisibilitySectionTag() {

		// body
		AerodromeHorizontalVisibilityType visibility = iwxxmHelpers.getOfIWXXM().createAerodromeHorizontalVisibilityType();

		// Minimal visibility
		if (translatedMetar.getCommonWeatherSection().getMinimumVisibility() != null) {
			DistanceWithNilReasonType minVis = iwxxmHelpers.getOfIWXXM().createDistanceWithNilReasonType();

			minVis.setUom(translatedMetar.getCommonWeatherSection().getVisibilityUnits().getStringValue());
			minVis.setValue(translatedMetar.getCommonWeatherSection().getMinimumVisibility());
			JAXBElement<DistanceWithNilReasonType> minV = iwxxmHelpers.getOfIWXXM()
					.createAerodromeHorizontalVisibilityTypeMinimumVisibility(minVis);
			visibility.setMinimumVisibility(minV);

			if (translatedMetar.getCommonWeatherSection().getMinimumVisibilityDirection() != null) {
				Double dirAngleD = translatedMetar.getCommonWeatherSection().getMinimumVisibilityDirection()
						.getDoubleValue();
			    AngleWithNilReasonType minVisAngle = iwxxmHelpers.getOfIWXXM().createAngleWithNilReasonType();
			    minVisAngle.setValue(dirAngleD);
			    minVisAngle.setUom(ANGLE_UNITS.DEGREES.getStringValue());
			    JAXBElement<AngleWithNilReasonType> minVisDirection = iwxxmHelpers.getOfIWXXM()
			    		.createAerodromeHorizontalVisibilityTypeMinimumVisibilityDirection(minVisAngle);
			    visibility.setMinimumVisibilityDirection(minVisDirection);
			}
		}

		// Prevailing visibility
		if (translatedMetar.getCommonWeatherSection().getPrevailVisibility() != null) {
			DistanceWithNilReasonType prevailVis = iwxxmHelpers.getOfIWXXM().createDistanceWithNilReasonType();
			prevailVis.setUom(translatedMetar.getCommonWeatherSection().getVisibilityUnits().getStringValue());
			prevailVis.setValue(translatedMetar.getCommonWeatherSection().getPrevailVisibility());
			visibility.setPrevailingVisibility(prevailVis);
		}

		MeteorologicalAerodromeObservationType.Visibility resultVisibility = iwxxmHelpers.getOfIWXXM()
				.createMeteorologicalAerodromeObservationTypeVisibility();
		resultVisibility.setAerodromeHorizontalVisibility(visibility);
		return resultVisibility;
	}

	/** Cloud section */
	private MeteorologicalAerodromeObservationType.Cloud createCloudSectionTag() {
		// Body
		MeteorologicalAerodromeObservationType.Cloud clouds = iwxxmHelpers.getOfIWXXM()
				.createMeteorologicalAerodromeObservationTypeCloud();
		AerodromeCloudType cloud = iwxxmHelpers.getOfIWXXM().createAerodromeCloudType();
		for (METARCloudSection cloudS : translatedMetar.getCommonWeatherSection().getCloudSections()) {

			
			String nilReason = null;
			if (cloudS.getAmount().equalsIgnoreCase(WMOCloudRegister.missingCode)) {
				JAXBElement<LengthWithNilReasonType> vVisibility = iwxxmHelpers
						.createVerticalVisibilitySection(cloudS.getHeight());
				cloud.setVerticalVisibility(vVisibility);

			} else {
				Layer cloudLayer = iwxxmHelpers.getOfIWXXM().createAerodromeCloudTypeLayer();
				cloudLayer.setCloudLayer(iwxxmHelpers.createCloudLayerSection(cloudS.getAmount(), cloudS.getHeight(),
						cloudS.getType(), nilReason, LENGTH_UNITS.FT));
				cloud.getLayer().add(cloudLayer);
			}
		}

		clouds.setAerodromeCloud(cloud);
		return clouds;

	}

	private AerodromeCloudForecastPropertyType createCloudSectionTag(MetarCommonWeatherSection section, String icaoCode,
			int sectionIndex) {

		// Envelop
		AerodromeCloudForecastPropertyType cloudsType = iwxxmHelpers.getOfIWXXM()
				.createAerodromeCloudForecastPropertyType();

		// Body
		AerodromeCloudForecastType clouds = iwxxmHelpers.getOfIWXXM().createAerodromeCloudForecastType();
		clouds.setId(iwxxmHelpers.generateUUIDv4(String.format("acf-%d-%s", sectionIndex, icaoCode)));
		for (METARCloudSection cloudS : section.getCloudSections()) {

			//int cloudAmount = iwxxmHelpers.getCloudReg().getCloudAmountByStringCode(cloudS.getAmount());
			String nilReason = null;
			if (cloudS.getAmount().equalsIgnoreCase(WMOCloudRegister.missingCode)) {
				nilReason = "Value is missing or VV provided";
			}
			CloudLayerPropertyType cloudLayer = iwxxmHelpers.getOfIWXXM().createCloudLayerPropertyType();
			cloudLayer.setCloudLayer(iwxxmHelpers.createCloudLayerSection(cloudS.getAmount(), cloudS.getHeight(),
					cloudS.getType(), nilReason, LENGTH_UNITS.FT));
			clouds.getLayer().add(cloudLayer);
		}
		// Place body into envelop
		cloudsType.setAerodromeCloudForecast(clouds);

		return cloudsType;

	}

	/** Wind shear section */
	private MeteorologicalAerodromeObservationType.WindShear createWindShearTag() {
		// body
		AerodromeWindShearType windShear = iwxxmHelpers.getOfIWXXM().createAerodromeWindShearType();

		if (translatedMetar.isWindShearForAll())
			windShear.setAllRunways(true);
		else
			for (String rwWs : translatedMetar.getWindShearSections()) {
				// Runway description
				RunwayDirectionPropertyType runwayType = iwxxmHelpers.getOfIWXXM().createRunwayDirectionPropertyType();
				RunwayDirectionType runway = iwxxmHelpers.getOfAIXM().createRunwayDirectionType();
				runway.setId(iwxxmHelpers.generateUUIDv4("runway-" + rwWs));
				CodeType rwCode = iwxxmHelpers.getOfGML().createCodeType();
				rwCode.setValue(rwWs);
				runway.getName().add(rwCode);
				runwayType.setRunwayDirection(runway);

				windShear.getRunway().add(runwayType);

			}

		// Place body to envelop

		MeteorologicalAerodromeObservationType.WindShear resultWindShear = iwxxmHelpers.getOfIWXXM()
				.createMeteorologicalAerodromeObservationTypeWindShear();
		resultWindShear.setAerodromeWindShear(windShear);
		return resultWindShear;
	}

	/** Creates Runway visual range tag */
	private MeteorologicalAerodromeObservationType.Rvr createRVRTag(METARRVRSection rvrs) {

		MeteorologicalAerodromeObservationType.Rvr rvrResult = iwxxmHelpers.getOfIWXXM()
				.createMeteorologicalAerodromeObservationTypeRvr();

		AerodromeRunwayVisualRangeType rvr = iwxxmHelpers.getOfIWXXM().createAerodromeRunwayVisualRangeType();
		rvrResult.setAerodromeRunwayVisualRange(rvr);

		RunwayDirectionPropertyType runwayDir = null;

		if (!createdRunways.containsKey(rvrs.getRvrDesignator())) {
			runwayDir = iwxxmHelpers.createRunwayDesignatorSectionTag(translatedMetar.getIcaoCode(),
					rvrs.getRvrDesignator());
			createdRunways.put(rvrs.getRvrDesignator(), runwayDir.getRunwayDirection().getId());
		} else {
			runwayDir = iwxxmHelpers.getOfIWXXM().createRunwayDirectionPropertyType();
			runwayDir.setHref("#" + createdRunways.get(rvrs.getRvrDesignator()));
		}

		rvr.setRunway(runwayDir);

		// mean rvr
		if (rvrs.getRvrValue() != null) {
			DistanceWithNilReasonType meanLength = iwxxmHelpers.getOfIWXXM().createDistanceWithNilReasonType();

			meanLength.setUom(rvrs.getUnits().getStringValue());
			meanLength.setValue(rvrs.getRvrValue());
			rvr.setMeanRVR(meanLength);
		}

		// mean operator

		if (rvrs.getOperator() != null) {
			RelationalOperatorType rvrOper = RelationalOperatorType.ABOVE;
			switch (rvrs.getOperator()) {
			case M:
				rvrOper = RelationalOperatorType.ABOVE;
				break;
			case P:
				rvrOper = RelationalOperatorType.BELOW;
				break;
			}
			JAXBElement<RelationalOperatorType> rwr = iwxxmHelpers.getOfIWXXM()
					.createAerodromeRunwayVisualRangeTypeMeanRVROperator(rvrOper);
			rvr.setMeanRVROperator(rwr);
		}

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
		rvr.setPastTendency(vrTendency);
		return rvrResult;

	}

	/** Creates Runway state tag to include into collection */
	private MeteorologicalAerodromeObservationType.RunwayState createRunwayStateTag(METARRunwayStateSection rwrs) {

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

		MeteorologicalAerodromeObservationType.RunwayState rvrTag = iwxxmHelpers.getOfIWXXM()
				.createMeteorologicalAerodromeObservationTypeRunwayState();
		AerodromeRunwayStateType rvrState = iwxxmHelpers.getOfIWXXM().createAerodromeRunwayStateType();
		rvrTag.setAerodromeRunwayState(rvrState);
		rvrState.setAllRunways(rwrs.isApplicableForAllRunways());
		rvrState.setCleared(rwrs.isCleared());

		if (!rwrs.isApplicableForAllRunways()) {
			RunwayDirectionPropertyType runwayDir = null;

			if (!createdRunways.containsKey(rwrs.getRvrDesignator())) {
				runwayDir = iwxxmHelpers.createRunwayDesignatorSectionTag(translatedMetar.getIcaoCode(),
						rwrs.getRvrDesignator());
				createdRunways.put(rwrs.getRvrDesignator(), runwayDir.getRunwayDirection().getId());
			} else {
				runwayDir = iwxxmHelpers.getOfIWXXM().createRunwayDirectionPropertyType();
				runwayDir.setHref("#" + createdRunways.get(rwrs.getRvrDesignator()));
			}
			if (runwayDir != null) {
				JAXBElement<RunwayDirectionPropertyType> rw = iwxxmHelpers.getOfIWXXM()
						.createAerodromeRunwayStateTypeRunway(runwayDir);
				rvrState.setRunway(rw);
			}
		}

		if (rwrs.getType().isPresent()) {
			RunwayDepositsType dType = iwxxmHelpers.getOfIWXXM().createRunwayDepositsType();
			JAXBElement<RunwayDepositsType> deposit = iwxxmHelpers.getOfIWXXM()
					.createAerodromeRunwayStateTypeDepositType(dType);
			String depUrl = iwxxmHelpers.getRwDepositReg().getWMOUrlByCode(rwrs.getType().get());
			dType.setHref(depUrl);
			rvrState.setDepositType(deposit);
		}

		if (rwrs.getContamination().isPresent()) {
			RunwayContaminationType cType = iwxxmHelpers.getOfIWXXM().createRunwayContaminationType();
			JAXBElement<RunwayContaminationType> contamination = iwxxmHelpers.getOfIWXXM()
					.createAerodromeRunwayStateTypeContamination(cType);
			String contUrl = iwxxmHelpers.getRwContaminationReg().getWMOUrlByCode(rwrs.getContamination().get());
			cType.setHref(contUrl);
			rvrState.setContamination(contamination);
		}

		if (rwrs.getDepositDepth().isPresent()) {
			DistanceWithNilReasonType depth = iwxxmHelpers.getOfIWXXM().createDistanceWithNilReasonType();
			depth.setValue(rwrs.getDepositDepth().get());
			depth.setUom(LENGTH_UNITS.MM.getStringValue());
			// JAXBElement<DistanceWithNilReasonType> depthTag =
			// ofIWXXM.createDistanceWithNilReason(depth);
			JAXBElement<DistanceWithNilReasonType> depthTag = iwxxmHelpers.getOfIWXXM()
					.createAerodromeRunwayStateTypeDepthOfDeposit(depth);
			rvrState.setDepthOfDeposit(depthTag);
		}

		if (rwrs.getFriction().isPresent()) {
			RunwayFrictionCoefficientType frictionType = iwxxmHelpers.getOfIWXXM().createRunwayFrictionCoefficientType();
			JAXBElement<RunwayFrictionCoefficientType> friction = iwxxmHelpers.getOfIWXXM()
					.createAerodromeRunwayStateTypeEstimatedSurfaceFrictionOrBrakingAction(frictionType);
			String frictionUrl = iwxxmHelpers.getRwFrictionReg().getWMOUrlByCode(rwrs.getFriction().get());
			frictionType.setHref(frictionUrl);
			rvrState.setEstimatedSurfaceFrictionOrBrakingAction(friction);
		}
		rvrTag.setAerodromeRunwayState(rvrState);
		return rvrTag;

	}

	/** Creates issueTime */
	private TimeInstantPropertyType createIssueTimesection() {
		return iwxxmHelpers.createJAXBTimeSection(translatedMetar.getMessageIssueDateTime(),
				translatedMetar.getIcaoCode());
	}

	
	@Override
	public IWXXM31Helpers getHelper() {
		return iwxxmHelpers;
	}
	
	@Override
	public METARConverterV3 withHelper(IWXXM31Helpers helper) {
		this.iwxxmHelpers = helper;
		return this;
		
	}
	/*
	 * private MeteorologicalAerodromeTrendForecastPropertyType
	 * createTrendForecast(MetarForecastSection section, int sectionIndex) {
	 * 
	 * 
	 * MeteorologicalAerodromeTrendForecastPropertyType trendType
	 * =ofIWXXM.createMeteorologicalAerodromeTrendForecastPropertyType();
	 * MeteorologicalAerodromeTrendForecastType trend =
	 * ofIWXXM.createMeteorologicalAerodromeTrendForecastType();
	 * trendType.setMeteorologicalAerodromeTrendForecast(trend);
	 * 
	 * trend.setId(iwxxmHelpers.generateUUIDv4(String.format("cf-%d-%s",
	 * sectionIndex, translatedMetar.getIcaoCode())));
	 * 
	 * 
	 * // phenomenon time for metar
	 * 
	 * JAXBElement<TimePeriodType> timeElement = ofGML.createTimePeriod(iwxxmHelpers
	 * .createTrendPeriodSection(translatedMetar.getIcaoCode(),
	 * section.getTrendValidityInterval().getStart(),
	 * section.getTrendValidityInterval().getEnd(), sectionIndex) .getTimePeriod());
	 * 
	 * 
	 * 
	 * AbstractTimeObjectPropertyType timeType =
	 * ofIWXXM.createAbstractTimeObjectPropertyType();
	 * timeType.setAbstractTimeObject(timeElement);
	 * 
	 * trend.setPhenomenonTime(timeType);
	 * 
	 * return trendType;
	 * 
	 * }
	 */
}
