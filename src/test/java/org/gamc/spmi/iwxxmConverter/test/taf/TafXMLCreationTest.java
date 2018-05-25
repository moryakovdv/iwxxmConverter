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
package org.gamc.spmi.iwxxmConverter.test.taf;

import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.common.IWXXM21Helpers;
import org.gamc.spmi.iwxxmConverter.common.NamespaceMapper;
import org.gamc.spmi.iwxxmConverter.common.StringConstants;
import org.gamc.spmi.iwxxmConverter.common.UriConstants;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.ANGLE_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.TEMPERATURE_UNITS;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.BeforeClass;
import org.junit.Test;

import _int.icao.iwxxm._2.AerodromeAirTemperatureForecastPropertyType;
import _int.icao.iwxxm._2.AerodromeAirTemperatureForecastType;
import _int.icao.iwxxm._2.AerodromeCloudForecastPropertyType;
import _int.icao.iwxxm._2.AerodromeCloudForecastType;
import _int.icao.iwxxm._2.AerodromeCloudForecastType.Layer;
import _int.icao.iwxxm._2.AerodromeSurfaceWindForecastPropertyType;
import _int.icao.iwxxm._2.AerodromeSurfaceWindForecastType;
import _int.icao.iwxxm._2.MeteorologicalAerodromeForecastRecordPropertyType;
import _int.icao.iwxxm._2.MeteorologicalAerodromeForecastRecordType;
import _int.icao.iwxxm._2.PermissibleUsageReasonType;
import _int.icao.iwxxm._2.PermissibleUsageType;
import _int.icao.iwxxm._2.TAFReportStatusType;
import _int.icao.iwxxm._2.TAFType;
import _int.wmo.def.metce._2013.ProcessType;
import net.opengis.gml.v_3_2_1.AngleType;
import net.opengis.gml.v_3_2_1.FeaturePropertyType;
import net.opengis.gml.v_3_2_1.LengthType;
import net.opengis.gml.v_3_2_1.MeasureType;
import net.opengis.gml.v_3_2_1.ReferenceType;
import net.opengis.gml.v_3_2_1.SpeedType;
import net.opengis.gml.v_3_2_1.StringOrRefType;
import net.opengis.gml.v_3_2_1.TimeInstantPropertyType;
import net.opengis.gml.v_3_2_1.TimeInstantType;
import net.opengis.gml.v_3_2_1.TimePeriodPropertyType;
import net.opengis.gml.v_3_2_1.TimePeriodType;
import net.opengis.gml.v_3_2_1.TimePositionType;
import net.opengis.om._2.OMObservationPropertyType;
import net.opengis.om._2.OMObservationType;
import net.opengis.om._2.OMProcessPropertyType;
import net.opengis.om._2.TimeObjectPropertyType;

public class TafXMLCreationTest {

	/*
	 * First of all we should create any involved object with ObjectFactory helpers
	 */
	_int.icao.iwxxm._2.ObjectFactory ofIWXXM = new _int.icao.iwxxm._2.ObjectFactory();
	net.opengis.gml.v_3_2_1.ObjectFactory ofGML = new net.opengis.gml.v_3_2_1.ObjectFactory();
	net.opengis.om._2.ObjectFactory ofOM = new net.opengis.om._2.ObjectFactory();
	org.w3._1999.xlink.ObjectFactory ofXLink = new org.w3._1999.xlink.ObjectFactory();
	aero.aixm.schema._5_1.ObjectFactory ofAIXM = new aero.aixm.schema._5_1.ObjectFactory();
	_int.wmo.def.metce._2013.ObjectFactory ofMetce = new _int.wmo.def.metce._2013.ObjectFactory();
	net.opengis.samplingspatial._2.ObjectFactory ofSams = new net.opengis.samplingspatial._2.ObjectFactory();

	static final DateTimeFormatter dtFormat = DateTimeFormat.forPattern("yyyyMMddHHmm");
	static final DateTimeFormatter dtFormatISO = ISODateTimeFormat.dateTimeNoMillis();

	static String testDateTime = "";
	static String testDateTimePosition = "";

	static String testTimePeriodBegin = "";
	static String testTimePeriodEnd = "";

	static String testTimePeriodBeginPosition = "";
	static String testTimePeriodEndPosition = "";
	
	/**Our own helpers to suppress boiler-plate code*/
	static IWXXM21Helpers iwxxmHelpers = new IWXXM21Helpers();
	
	static DateTime dtTest = DateTime.now().withZone(DateTimeZone.UTC).withTimeAtStartOfDay().plusHours(DateTime.now().withZone(DateTimeZone.UTC).getHourOfDay());
	
	DateTime dtTestPeriodBegin = dtTest.plusHours(3);
	DateTime dtTestPeriodEnd = dtTest.plusHours(6);
	
	/** Before the test provide DateTime initialization with JodaTime */
	@BeforeClass
	public static void beforeClass() {
		// today 00:00
		

		// set common TAF time - plus this hour and 00 minutes
		testDateTime = dtTest.toString(dtFormat) + "Z";
		testDateTimePosition = dtTest.toString(dtFormatISO);

		testTimePeriodBeginPosition = dtTest.plusHours(3).toString(dtFormatISO);
		testTimePeriodEndPosition = dtTest.plusHours(6).toString(dtFormatISO);

		testTimePeriodBegin = dtTest.plusHours(3).toString(dtFormat) + "Z";
		testTimePeriodEnd = dtTest.plusHours(6).toString(dtFormat) + "Z";
	}

	/** Generate TAF XML from scratch */
	@Test
	public void testTAFCreationFromScratch() throws DatatypeConfigurationException, JAXBException {
		// <iwxxm:TAF> root tag
		TAFType tafRootTag = ofIWXXM.createTAFType();

		// Id with ICAO code and current timestamp
		tafRootTag.setId("taf-UUWW-" + testDateTime);

		// Set NON_OPERATIONAL and TEST properties.
		tafRootTag.setPermissibleUsage(PermissibleUsageType.NON_OPERATIONAL);
		tafRootTag.setPermissibleUsageReason(PermissibleUsageReasonType.TEST);

		// Some description
		tafRootTag.setPermissibleUsageSupplementary("TAF composing test using JAXB");

		// This is not a COR or AMD
		tafRootTag.setStatus(TAFReportStatusType.NORMAL);

		tafRootTag = addTranslationCentreHeader(tafRootTag);

		// issuetime and valid period are top-level tags
		tafRootTag.setIssueTime(createIssueTimesection());
		tafRootTag.setValidTime(createValidityPeriodSection());

		// Compose TAF body message and place it in the root
		OMObservationPropertyType tafBaseForecast = createBaseForecast();
		tafRootTag.setBaseForecast(tafBaseForecast);

		// TODO : create TrendForecast and possible Extensions (RMK)

		// create XML representation
		marshallMessageToXML(tafRootTag);

	}

	/** create issueTime */
	private TimeInstantPropertyType createIssueTimesection() {

		/*
		TimeInstantType timeInstant = ofGML.createTimeInstantType();
		timeInstant.setId("ti-UUWW-" + testDateTime);
		TimePositionType timePosition = ofGML.createTimePositionType();
		timePosition.getValue().add(testDateTimePosition);
		timeInstant.setTimePosition(timePosition);

		TimeInstantPropertyType issuedTimeProperty = ofGML.createTimeInstantPropertyType();
		issuedTimeProperty.setTimeInstant(timeInstant);

		// issueTime
		JAXBElement<TimeInstantPropertyType> issueTimeTag = ofIWXXM.createTAFTypeIssueTime(issuedTimeProperty);
		return issueTimeTag;
		
		*/
		
		return iwxxmHelpers.createJAXBTimeSection(dtTest, "UUWW");

	}

	/** create valid period section */
	private TimePeriodPropertyType createValidityPeriodSection() {
		TimePeriodPropertyType timePeriodProperty = ofGML.createTimePeriodPropertyType();
		TimePeriodType timePeriodType = ofGML.createTimePeriodType();

		timePeriodType.setId(String.format("tp-%s-%s", testTimePeriodBegin, testTimePeriodEnd));

		// begin
		TimeInstantType timeBeginInstant = ofGML.createTimeInstantType();
		timeBeginInstant.setId("ti-UUWW-" + testTimePeriodBegin);
		TimePositionType timePositionBegin = ofGML.createTimePositionType();
		timePositionBegin.getValue().add(testTimePeriodBeginPosition);
		timeBeginInstant.setTimePosition(timePositionBegin);

		TimeInstantPropertyType timeBeginProperty = ofGML.createTimeInstantPropertyType();
		timeBeginProperty.setTimeInstant(timeBeginInstant);

		timePeriodType.setBeginPosition(timePositionBegin);

		// end
		TimeInstantType timeEndInstant = ofGML.createTimeInstantType();
		timeEndInstant.setId("ti-UUWW-" + testTimePeriodEnd);
		TimePositionType timePositionEnd = ofGML.createTimePositionType();
		timePositionEnd.getValue().add(testTimePeriodEndPosition);
		timeEndInstant.setTimePosition(timePositionEnd);

		TimeInstantPropertyType timeEndProperty = ofGML.createTimeInstantPropertyType();
		timeEndProperty.setTimeInstant(timeEndInstant);

		timePeriodType.setEndPosition(timePositionEnd);

		timePeriodProperty.setTimePeriod(timePeriodType);

		return timePeriodProperty;
	}

	private OMObservationPropertyType createBaseForecast() {

		// тег <om:OM_Observation>
		OMObservationPropertyType omOM_Observation = ofOM.createOMObservationPropertyType();
		OMObservationType ot = ofOM.createOMObservationType();
		ot.setId("obs-UUWW-" + testDateTime);
		
		// тип наблюдения - ссылка xlink:href
		ReferenceType observeType = ofGML.createReferenceType();
		observeType.setHref(UriConstants.OBSERVATION_TYPE_TAF);
		ot.setType(observeType);

		ot.setFeatureOfInterest(createAirportDescriptionSectionTag());

		// phenomenon time for taf always equals to validityPeriod
		TimeObjectPropertyType phenomenonTimeProperty = ofOM.createTimeObjectPropertyType();
		phenomenonTimeProperty.setHref("#" + createValidityPeriodSection().getTimePeriod().getId());
		ot.setPhenomenonTime(phenomenonTimeProperty);

		// result time for taf always equals to issueTime
		TimeInstantPropertyType resultTime = ofGML.createTimeInstantPropertyType();
		resultTime.setHref("#" + createIssueTimesection().getTimeInstant().getId());
		ot.setResultTime(resultTime);

		// create <om:procedure> frame
		ProcessType metceProcess = ofMetce.createProcessType();
		metceProcess.setId("p-49-2-taf");

		StringOrRefType processDescription = ofGML.createStringOrRefType();
		processDescription.setValue(StringConstants.WMO_49_2_METCE_TAF);
		metceProcess.setDescription(processDescription);

		OMProcessPropertyType omProcedure = ofOM.createOMProcessPropertyType();
		omProcedure.setAny(ofMetce.createProcess(metceProcess));
		ot.setProcedure(omProcedure);

		// тег om:ObserverdProperty
		ReferenceType observedProperty = ofGML.createReferenceType();
		observedProperty.setHref(UriConstants.OBSERVED_PROPERTY_TAF);
		// observedProperty.setTitle(StringConstants.WMO_TAF_OBSERVED_PROPERTY_TITLE);
		ot.setObservedProperty(observedProperty);

		// set result section
		ot.setResult(createBaseResultSection());

		omOM_Observation.setOMObservation(ot);
		

		return omOM_Observation;
	}

	/**Result section of the BASE taf*/
	private MeteorologicalAerodromeForecastRecordPropertyType createBaseResultSection() {
		
		MeteorologicalAerodromeForecastRecordPropertyType recordPropertyType = ofIWXXM.createMeteorologicalAerodromeForecastRecordPropertyType();
		MeteorologicalAerodromeForecastRecordType recordType = ofIWXXM.createMeteorologicalAerodromeForecastRecordType();
		
		//set id
		recordType.setId("base-fcst-record-UUWW");
		
		// NOT CAVOK
		recordType.setCloudAndVisibilityOK(false);
		
		//visibility
		LengthType vis = ofGML.createLengthType();
		vis.setUom(LENGTH_UNITS.METERS.getStringValue());
		vis.setValue(6000d);
		
		recordType.setPrevailingVisibility(vis);
		
		//surfaceWind
		AerodromeSurfaceWindForecastPropertyType sWindpropertyType = ofIWXXM.createAerodromeSurfaceWindForecastPropertyType();
		AerodromeSurfaceWindForecastType sWindType = ofIWXXM.createAerodromeSurfaceWindForecastType();
		

		//Set gust speed 6m/s
		SpeedType speedGustType = ofGML.createSpeedType();
		speedGustType.setUom(SPEED_UNITS.MPS.getStringValue());
		speedGustType.setValue(6f);
		sWindType.setWindGustSpeed(speedGustType);

		//Set mean wind 2m/s
		SpeedType speedMeanType = ofGML.createSpeedType();
		speedMeanType.setUom(SPEED_UNITS.MPS.getStringValue());
		speedMeanType.setValue(2f);
		sWindType.setMeanWindSpeed(speedMeanType);

		//Set wind direction
		AngleType windAngle = ofGML.createAngleType();
		windAngle.setUom(ANGLE_UNITS.DEGREES.getStringValue());
		windAngle.setValue(80d);
		sWindType.setMeanWindDirection(windAngle);

		//NO VRB
		sWindType.setVariableWindDirection(false);
		
		sWindpropertyType.setAerodromeSurfaceWindForecast(sWindType);
		recordType.setSurfaceWind(sWindpropertyType);
		
		
		//clouds
		recordType.setCloud(createCloudSectionTag());
		
		//Min and Max temperatures from taf
		recordType.getTemperature().add(createTemperaturesSection());
		
		recordPropertyType.setMeteorologicalAerodromeForecastRecord(recordType);
		
		
		return recordPropertyType;
		
	}

	private AerodromeAirTemperatureForecastPropertyType createTemperaturesSection() {
		
		
		
		AerodromeAirTemperatureForecastPropertyType tempPropertyType = ofIWXXM
				.createAerodromeAirTemperatureForecastPropertyType();
		AerodromeAirTemperatureForecastType temps = ofIWXXM.createAerodromeAirTemperatureForecastType();

		// Set min temperature
		MeasureType minTemperature = ofGML.createMeasureType();
		minTemperature.setUom(TEMPERATURE_UNITS.CELSIUS.getStringValue());
		minTemperature.setValue(11.2);

		// Time of the min temp forecasted
		/*
		TimeInstantPropertyType timeInstantMinTempProperty = ofGML.createTimeInstantPropertyType();
		TimeInstantType timeInstantMinTemp = ofGML.createTimeInstantType();
		timeInstantMinTemp.setId("ti-UUWW-" + testTimePeriodBegin);
		TimePositionType timePositionMintemp = ofGML.createTimePositionType();
		timePositionMintemp.getValue().add(testTimePeriodBeginPosition);
		timeInstantMinTemp.setTimePosition(timePositionMintemp);
		timeInstantMinTempProperty.setTimeInstant(timeInstantMinTemp);
		*/
		TimeInstantPropertyType timeInstantMinTempProperty = iwxxmHelpers.createTimeInstantPropertyTypeForDateTime(dtTestPeriodBegin, "UUWW");
		TimeInstantPropertyType timeInstantMaxTempProperty = iwxxmHelpers.createTimeInstantPropertyTypeForDateTime(dtTestPeriodEnd, "UUWW");
		
		// Set max temperature
		MeasureType maxTemperature = ofGML.createMeasureType();
		maxTemperature.setUom(TEMPERATURE_UNITS.CELSIUS.getStringValue());
		maxTemperature.setValue(24.0);

		// Time of the min temp forecasted
		/*
		TimeInstantPropertyType timeInstantMaxTempProperty = ofGML.createTimeInstantPropertyType();
		TimeInstantType timeInstantMaxTemp = ofGML.createTimeInstantType();
		timeInstantMaxTemp.setId("ti-UUWW-" + testTimePeriodEnd);
		TimePositionType timePositionMaxtemp = ofGML.createTimePositionType();
		timePositionMaxtemp.getValue().add(testTimePeriodEndPosition);
		timeInstantMaxTemp.setTimePosition(timePositionMaxtemp);
		timeInstantMaxTempProperty.setTimeInstant(timeInstantMaxTemp);
		*/
		
		temps.setMinimumAirTemperature(minTemperature);
		temps.setMinimumAirTemperatureTime(timeInstantMinTempProperty);
		
		temps.setMaximumAirTemperature(maxTemperature);
		temps.setMaximumAirTemperatureTime(timeInstantMaxTempProperty);
		
		tempPropertyType.setAerodromeAirTemperatureForecast(temps);
		return tempPropertyType;
		
	}

	/** Cloud section */
	private AerodromeCloudForecastPropertyType createCloudSectionTag() {
		// Envelop
		AerodromeCloudForecastPropertyType cloudsType = ofIWXXM.createAerodromeCloudForecastPropertyType();

		// Body
		AerodromeCloudForecastType clouds = ofIWXXM.createAerodromeCloudForecastType();

		
		Layer lay1 = ofIWXXM.createAerodromeCloudForecastTypeLayer();
		lay1.setCloudLayer(iwxxmHelpers.createCloudLayerSection(1, 100, "CB", null, LENGTH_UNITS.FEETS));
		
		Layer lay2 = ofIWXXM.createAerodromeCloudForecastTypeLayer();
		lay2.setCloudLayer(iwxxmHelpers.createCloudLayerSection(2, 300, null, null, LENGTH_UNITS.FEETS));
		
		clouds.getLayer().add(lay1);
		clouds.getLayer().add(lay2);
		

		// Place body into envelop
		cloudsType.setAerodromeCloudForecast(clouds);
		return cloudsType;
	}

	/**
	 * Create aerodrome description section as GML FeatureOfInterest ICAO code=UUWW
	 */
	private FeaturePropertyType createAirportDescriptionSectionTag() {

		return iwxxmHelpers.createAirportDescriptionSectionTag("UUWW");

	}

	/**Test adding headers to the root*/
	private TAFType addTranslationCentreHeader(TAFType taf) throws DatatypeConfigurationException {

		taf  = iwxxmHelpers.addTranslationCentreHeaders(taf, DateTime.now(), DateTime.now(), UUID.randomUUID().toString(), "UUWW", "Vnukovo, RU");
		taf.setTranslationFailedTAC("");

		return taf;
	}

	/** Marshall root TAF to XML with console output */
	public void marshallMessageToXML(TAFType taf) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(TAFType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, UriConstants.GLOBAL_SCHEMAS_LOCATION);

		jaxbMarshaller.setProperty(StringConstants.SUN_JAXB_NAMESPACE_MAPPING_PROPERTY_NAME, new NamespaceMapper());

		JAXBElement<TAFType> metarRootElement = ofIWXXM.createTAF(taf);

		jaxbMarshaller.marshal(metarRootElement, System.out);
	}

}
