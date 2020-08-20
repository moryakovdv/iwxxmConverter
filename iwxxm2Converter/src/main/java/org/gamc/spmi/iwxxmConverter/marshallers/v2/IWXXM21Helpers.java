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

import java.util.GregorianCalendar;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.gamc.spmi.iwxxmConverter.common.StringConstants;
import org.gamc.spmi.iwxxmConverter.general.IWXXMHelpers;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.wmo.WMOCloudRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMOCloudTypeRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMONilReasonRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMOPrecipitationRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.gamc.spmi.iwxxmConverter.wmo.WMORunWayContaminationRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMORunWayDepositsRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMORunWayFrictionRegister;
import org.joda.time.DateTime;

import schemabindings21._int.icao.iwxxm._2.AerodromeForecastWeatherType;
import schemabindings21._int.icao.iwxxm._2.AerodromePresentWeatherType;
import schemabindings21._int.icao.iwxxm._2.AerodromeRecentWeatherType;
import schemabindings21._int.icao.iwxxm._2.CloudAmountReportedAtAerodromeType;
import schemabindings21._int.icao.iwxxm._2.CloudLayerType;
import schemabindings21._int.icao.iwxxm._2.DistanceWithNilReasonType;
import schemabindings21._int.icao.iwxxm._2.LengthWithNilReasonType;
import schemabindings21._int.icao.iwxxm._2.ReportType;
import schemabindings21._int.icao.iwxxm._2.RunwayDirectionPropertyType;
import schemabindings21._int.icao.iwxxm._2.SigConvectiveCloudTypeType;
import schemabindings21.aero.aixm.schema._5_1.AirportHeliportTimeSlicePropertyType;
import schemabindings21.aero.aixm.schema._5_1.AirportHeliportTimeSliceType;
import schemabindings21.aero.aixm.schema._5_1.AirportHeliportType;
import schemabindings21.aero.aixm.schema._5_1.CodeAirportHeliportDesignatorType;
import schemabindings21.aero.aixm.schema._5_1.CodeICAOType;
import schemabindings21.aero.aixm.schema._5_1.RunwayDirectionTimeSlicePropertyType;
import schemabindings21.aero.aixm.schema._5_1.RunwayDirectionTimeSliceType;
import schemabindings21.aero.aixm.schema._5_1.RunwayDirectionType;
import schemabindings21.aero.aixm.schema._5_1.TextDesignatorType;
import schemabindings21.net.opengis.gml.v_3_2_1.FeaturePropertyType;
import schemabindings21.net.opengis.gml.v_3_2_1.ReferenceType;
import schemabindings21.net.opengis.gml.v_3_2_1.TimeInstantPropertyType;
import schemabindings21.net.opengis.gml.v_3_2_1.TimeInstantType;
import schemabindings21.net.opengis.gml.v_3_2_1.TimePeriodPropertyType;
import schemabindings21.net.opengis.gml.v_3_2_1.TimePeriodType;
import schemabindings21.net.opengis.gml.v_3_2_1.TimePositionType;
import schemabindings21.net.opengis.gml.v_3_2_1.TimePrimitivePropertyType;
import schemabindings21.net.opengis.samplingspatial._2.SFSpatialSamplingFeatureType;
import schemabindings21.net.opengis.samplingspatial._2.ShapeType;

/**
 * Set of the helper functions. Provides creation of a common objects to use
 * during xml creation. 
 * Helps to reduce boiler-plate code. 
 * The functionality may
 * be extended to provide specific implementation for METAR, TAF, SIGMET etc..
 */
public class IWXXM21Helpers extends IWXXMHelpers {
	public static final schemabindings21._int.icao.iwxxm._2.ObjectFactory ofIWXXM = new schemabindings21._int.icao.iwxxm._2.ObjectFactory();
	public static final schemabindings21.net.opengis.gml.v_3_2_1.ObjectFactory ofGML = new schemabindings21.net.opengis.gml.v_3_2_1.ObjectFactory();
	public static final schemabindings21.net.opengis.om._2.ObjectFactory ofOM = new schemabindings21.net.opengis.om._2.ObjectFactory();
	public static final schemabindings21.org.w3._1999.xlink.ObjectFactory ofXLink = new schemabindings21.org.w3._1999.xlink.ObjectFactory();
	public static final schemabindings21.aero.aixm.schema._5_1.ObjectFactory ofAIXM = new schemabindings21.aero.aixm.schema._5_1.ObjectFactory();
	public static final schemabindings21._int.wmo.def.metce._2013.ObjectFactory ofMetce = new schemabindings21._int.wmo.def.metce._2013.ObjectFactory();
	public static final schemabindings21.net.opengis.samplingspatial._2.ObjectFactory ofSams = new schemabindings21.net.opengis.samplingspatial._2.ObjectFactory();

	
	/*WMO registers**/
	final WMOCloudRegister cloudReg = new WMOCloudRegister();
	final WMOCloudTypeRegister cloudTypeReg = new WMOCloudTypeRegister();
	final WMOPrecipitationRegister precipitationReg = new WMOPrecipitationRegister();

	final WMORunWayContaminationRegister rwContaminationReg = new WMORunWayContaminationRegister();
	final WMORunWayDepositsRegister rwDepositReg = new WMORunWayDepositsRegister();
	final WMORunWayFrictionRegister rwFrictionReg = new WMORunWayFrictionRegister();
	
	private final WMONilReasonRegister nilRegister = new WMONilReasonRegister();

	
	/**
	 * Creates TimeInstantPropertyType from given DateTime
	 * 
	 * @param dt
	 *            - dateTime to process
	 * @param icaoCode
	 *            - aerodrome ICAO code
	 * @return {@link TimeInstantPropertyType}
	 */
	public TimeInstantPropertyType createTimeInstantPropertyTypeForDateTime(DateTime dt, String icaoCode) {

		String sDateTime = dt.toString(getDateTimeFormat()) + "Z";
		String sDateTimePosition = dt.toString(getDateTimeISOFormat());

		TimeInstantPropertyType timeInstantProperty = ofGML.createTimeInstantPropertyType();
		TimeInstantType timeInstant = ofGML.createTimeInstantType();
		timeInstant.setId(generateUUIDv4(String.format("ti-%s-%s", icaoCode, sDateTime)));
		TimePositionType timePosition = ofGML.createTimePositionType();
		timePosition.getValue().add(sDateTimePosition);
		timeInstant.setTimePosition(timePosition);
		timeInstantProperty.setTimeInstant(timeInstant);

		return timeInstantProperty;
	}

	/**
	 * Сreates JAXB TimeInstantSection for a given DateTime
	 * 
	 * @param dt
	 *            - dateTime to process
	 * @param icaoCode
	 *            - aerodrome ICAO code
	 * @return {@link TimeInstantpropertyType} in JAXB envelope which is ready to
	 *         embed into getRest() part of the root tag
	 */
	public TimeInstantPropertyType createJAXBTimeSection(DateTime dt, String icaoCode) {
		TimeInstantPropertyType timeProperty = createTimeInstantPropertyTypeForDateTime(dt, icaoCode);
		return timeProperty;

	}

	/**
	 * Creates valid period section for trend sections
	 * 
	 * @param start
	 *            - Begin timestamp
	 * @param end
	 *            - End timestamp
	 * @param sectionIndex
	 *            - number of the section to create valid id
	 * 
	 * @return TimePeriodPropertyType
	 */
	public TimePeriodPropertyType createTrendPeriodSection(String icaoCode, DateTime start, DateTime end,
			int sectionIndex) {

		String sectionTimePeriodBeginPosition = start.toString(getDateTimeISOFormat());
		String sectionTimePeriodEndPosition = end.toString(getDateTimeISOFormat());

		String sectionTimePeriodBegin = start.toString(getDateTimeFormat()) + "Z";
		String sectionTimePeriodEnd = end.toString(getDateTimeFormat()) + "Z";

		TimePeriodPropertyType timePeriodProperty = ofGML.createTimePeriodPropertyType();
		TimePeriodType timePeriodType = ofGML.createTimePeriodType();

		timePeriodType.setId(generateUUIDv4(String.format("tp-%d-%s-%s", sectionIndex, sectionTimePeriodBegin, sectionTimePeriodEnd)));

		// begin
		TimeInstantType timeBeginInstant = ofGML.createTimeInstantType();
		timeBeginInstant.setId(generateUUIDv4(String.format("ti-%d-%s-%s", sectionIndex, icaoCode, sectionTimePeriodBegin)));

		TimePositionType timePositionBegin = ofGML.createTimePositionType();
		// timePositionBegin.getValue().add(timePeriodBeginPosition);
		timePositionBegin.getValue().add(sectionTimePeriodBeginPosition);
		timeBeginInstant.setTimePosition(timePositionBegin);

		TimeInstantPropertyType timeBeginProperty = ofGML.createTimeInstantPropertyType();
		timeBeginProperty.setTimeInstant(timeBeginInstant);

		// end
		TimeInstantType timeEndInstant = ofGML.createTimeInstantType();
		timeEndInstant.setId(generateUUIDv4(String.format("ti-%s-%s", icaoCode, sectionTimePeriodEnd)));
		TimePositionType timePositionEnd = ofGML.createTimePositionType();
		timePositionEnd.getValue().add(sectionTimePeriodEndPosition);
		timeEndInstant.setTimePosition(timePositionEnd);

		TimeInstantPropertyType timeEndProperty = ofGML.createTimeInstantPropertyType();
		timeEndProperty.setTimeInstant(timeEndInstant);

		timePeriodType.setBeginPosition(timePositionBegin);
		timePeriodType.setEndPosition(timePositionEnd);

		timePeriodProperty.setTimePeriod(timePeriodType);

		return timePeriodProperty;

	}

	/**
	 * Сreates FeaturePropertyType for given aerodrome icao code
	 * 
	 * @param icaoCode
	 *            - ICAO code for the aerodrome.
	 * @return {@link FeaturePropertyType} with aerodrome description
	 */
	public FeaturePropertyType createAirportDescriptionSectionTag(String icaoCode) {

		FeaturePropertyType airportTag = ofGML.createFeaturePropertyType();
		SFSpatialSamplingFeatureType sfFeature = ofSams.createSFSpatialSamplingFeatureType();
		sfFeature.setId(generateUUIDv4("sp-" + icaoCode));

		// empty shape tag
		ShapeType shape = ofSams.createShapeType();
		sfFeature.setShape(shape);

		ReferenceType sfType = ofGML.createReferenceType();
		sfType.setHref(UriConstants.GIS_SAMPLING_FEATURE);
		sfFeature.setType(sfType);

		AirportHeliportType airportType = ofAIXM.createAirportHeliportType();
		airportType.setId(generateUUIDv4("aerodrome-" + icaoCode));

		AirportHeliportTimeSlicePropertyType ahTimeSliceProperty = ofAIXM.createAirportHeliportTimeSlicePropertyType();
		AirportHeliportTimeSliceType ahTimeSliceType = ofAIXM.createAirportHeliportTimeSliceType();
		ahTimeSliceType.setId(generateUUIDv4(String.format("aerodrome-%s-ts", icaoCode)));

		TimePrimitivePropertyType validTime = ofGML.createTimePrimitivePropertyType();
		ahTimeSliceType.setValidTime(validTime);
		ahTimeSliceType.setInterpretation("BASELINE");

		CodeAirportHeliportDesignatorType designator = ofAIXM.createCodeAirportHeliportDesignatorType();
		designator.setValue(icaoCode);
		JAXBElement<CodeAirportHeliportDesignatorType> designatorTag = ofAIXM
				.createAirportHeliportTimeSliceTypeDesignator(designator);
		ahTimeSliceType.getRest().add(designatorTag);

		CodeICAOType icaoType = ofAIXM.createCodeICAOType();
		icaoType.setValue(icaoCode);
		JAXBElement<CodeICAOType> locationIdicator = ofAIXM
				.createAirportHeliportTimeSliceTypeLocationIndicatorICAO(icaoType);
		ahTimeSliceType.getRest().add(locationIdicator);

		ahTimeSliceProperty.setAirportHeliportTimeSlice(ahTimeSliceType);

		airportType.getTimeSlice().add(ahTimeSliceProperty);

		JAXBElement<AirportHeliportType> ahTag = ofAIXM.createAirportHeliport(airportType);

		FeaturePropertyType sfAirport = ofGML.createFeaturePropertyType();
		sfAirport.setAbstractFeature(ahTag);

		sfFeature.getSampledFeature().add(sfAirport);

		JAXBElement<SFSpatialSamplingFeatureType> sfFeatureTag = ofSams.createSFSpatialSamplingFeature(sfFeature);

		airportTag.setAbstractFeature(sfFeatureTag);
		return airportTag;

	}

	/**Creates block for RunwayDirectionPropertyType with AIXXM description of the aerodrome runway*/
	public RunwayDirectionPropertyType createRunwayDesignatorSectionTag(String icaoCode, String designator) {
		RunwayDirectionPropertyType runwayDir = ofIWXXM.createRunwayDirectionPropertyType();
		RunwayDirectionType rdt = ofAIXM.createRunwayDirectionType();
		rdt.setId(generateUUIDv4(String.format("runway-%s-%s",icaoCode,designator)));
		
		
		RunwayDirectionTimeSlicePropertyType rdts = ofAIXM.createRunwayDirectionTimeSlicePropertyType();
		RunwayDirectionTimeSliceType rdtst = ofAIXM.createRunwayDirectionTimeSliceType();
		
		TextDesignatorType textDesignator = ofAIXM.createTextDesignatorType();
		textDesignator.setValue(designator);
		JAXBElement<TextDesignatorType> textDesTag = ofAIXM.createRunwayTimeSliceTypeDesignator(textDesignator);
		rdtst.setId(generateUUIDv4(String.format("runway-%s-%s-ts",icaoCode,designator)));
		rdtst.setDesignator(textDesTag);
		rdtst.setInterpretation("BASELINE");
		
		TimePrimitivePropertyType tppt = ofGML.createTimePrimitivePropertyType();
		rdtst.setValidTime(tppt);
		
		rdts.setRunwayDirectionTimeSlice(rdtst);
		rdt.getTimeSlice().add(rdts);
		
		runwayDir.setRunwayDirection(rdt);
		return runwayDir;
	}
	
	/**
	 * Adds header with translation center properties to the message
	 * 
	 * @param report
	 *            - message of the class, derived from {@link ReportType} - TAFType,
	 *            METARType, etc...
	 * @param translationTime
	 *            - translation time
	 * @param bulletinReceivedTime
	 *            - when bulletin was received (or null if not applicable))
	 * @param bulletinId
	 *            - bulletin id (or null if not applicable)
	 * @param designator
	 *            - ICAO code of the translation center
	 * @param centreName
	 *            - name of the translation center
	 * @return The same report object with filled properties
	 */
	public <T extends ReportType> T addTranslationCentreHeaders(T report, DateTime translationTime,
			DateTime bulletinReceivedTime, String bulletinId, String designator, String centreName)
			throws DatatypeConfigurationException {

		// Create and set special XML DateTime object
		GregorianCalendar calDateTime = translationTime.toGregorianCalendar();
		XMLGregorianCalendar xmlCalRepr = DatatypeFactory.newInstance().newXMLGregorianCalendar(calDateTime);
		report.setTranslationTime(xmlCalRepr);

		if (bulletinReceivedTime != null) {
			GregorianCalendar bulletinDateTime = bulletinReceivedTime.toGregorianCalendar();
			XMLGregorianCalendar bulletinCalRepr = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(bulletinDateTime);
			report.setTranslatedBulletinReceptionTime(bulletinCalRepr);
		}

		report.setTranslatedBulletinID(bulletinId);

		report.setTranslationCentreName(centreName);
		report.setTranslationCentreDesignator(designator);

		return report;
	}

	/**
	 * Creates cloud layer section. Takes the right linkk from WMO register helper
	 * 
	 * @param cloudAmount
	 *            - octants
	 * @param cloudHeight
	 *            - height of cloud in given units
	 * @param cloudTypeCode
	 *            - significant cloud type if exists
	 * 
	 * @param nilReason
	 *            - it should be set when somehow values are missed
	 * @param units
	 *            - {@link LENGTH_UNITS} unit of measure.
	 * @return cloudLayer
	 * @throws WMORegisterException 
	 */
	public CloudLayerType createCloudLayerSection(String cloudAmount, double cloudHeight, String cloudTypeCode,
			String nilReason, LENGTH_UNITS units) throws WMORegisterException {

		// Create layer
		// Layer cloudLayer = ofIWXXM.createAerodromeCloudForecastTypeLayer();
		CloudLayerType currentLayer = ofIWXXM.createCloudLayerType();

		// Cloud amount seems to conform WMO schemas with
		CloudAmountReportedAtAerodromeType amount = ofIWXXM.createCloudAmountReportedAtAerodromeType();

		// Get the right link to WMO code table for cloud amount octant

		amount.setHref(cloudReg.getWMOUrlByCode(cloudAmount));

		// Height of clouds
		DistanceWithNilReasonType layerDistanceBase = ofIWXXM.createDistanceWithNilReasonType();
		if (nilReason != null) {
			layerDistanceBase.getNilReason().add(nilReason);
		} else {
			layerDistanceBase.setUom(units.getStringValue());
			layerDistanceBase.setValue(cloudHeight);
		}

		currentLayer.setAmount(amount);
		currentLayer.setBase(layerDistanceBase);

		if (cloudTypeCode != null && !cloudTypeCode.isEmpty()) {

			SigConvectiveCloudTypeType cloudType = ofIWXXM.createSigConvectiveCloudTypeType();

			// Get the right link to WMO code table for cloud type

			cloudType.setHref(cloudTypeReg.getWMOUrlByCode(cloudTypeReg.getCloudTypeByStringCode(cloudTypeCode)));
			currentLayer.setCloudType(ofIWXXM.createCloudLayerTypeCloudType(cloudType));
		}

		// cloudLayer.setCloudLayer(currentLayer);
		return currentLayer;

	}
	
	/**Creates tag for vertical visibility*/
	public JAXBElement<LengthWithNilReasonType> createVerticalVisibilitySection(double visibilityValue) {
		LengthWithNilReasonType vvType = ofIWXXM.createLengthWithNilReasonType();
		vvType.setUom(LENGTH_UNITS.FT.getStringValue());
		vvType.setValue(visibilityValue);
		return ofIWXXM.createAerodromeCloudForecastTypeVerticalVisibility(vvType);
		
	
	}

		

	/** returns link for WMO weather register for present weather in METAR 
	 * @throws WMORegisterException */
	public AerodromePresentWeatherType createPresentWeatherSection(String weather) throws WMORegisterException {

		AerodromePresentWeatherType presentWeather = ofIWXXM.createAerodromePresentWeatherType();
		if (weather.equalsIgnoreCase(StringConstants.NO_SIGNIFICANT_WEATHER_CHANGES))		
			presentWeather.getNilReason().add(nilRegister.getWMOUrlByCode(WMONilReasonRegister.NIL_REASON_NOTHING_OF_OPERATIONAL_SIGNIFICANCE));
		else
		
		presentWeather.setHref(getPrecipitationReg().getWMOUrlByCode(weather));

		return presentWeather;
	}

	/** returns link for WMO weather register for recent weather in METAR 
	 * @throws WMORegisterException */
	public AerodromeRecentWeatherType createRecentWeatherSection(String weather) throws WMORegisterException {

		AerodromeRecentWeatherType recentWeather = ofIWXXM.createAerodromeRecentWeatherType();
		if (weather.equalsIgnoreCase(StringConstants.NO_SIGNIFICANT_WEATHER_CHANGES))		
			recentWeather.getNilReason().add(nilRegister.getWMOUrlByCode(WMONilReasonRegister.NIL_REASON_NOTHING_OF_OPERATIONAL_SIGNIFICANCE));
		else
		
		recentWeather.setHref(getPrecipitationReg().getWMOUrlByCode(weather));

		return recentWeather;
	}

	/**
	 * returns link for WMO weather register for forecasted weather in METAR and TAF
	 * @throws WMORegisterException 
	 */
	public AerodromeForecastWeatherType createForecastWeatherSection(String weather) throws WMORegisterException {
		AerodromeForecastWeatherType fcstWeather = ofIWXXM.createAerodromeForecastWeatherType();
		if (weather.equalsIgnoreCase(StringConstants.NO_SIGNIFICANT_WEATHER_CHANGES))		
			fcstWeather.getNilReason().add(nilRegister.getWMOUrlByCode(WMONilReasonRegister.NIL_REASON_NOTHING_OF_OPERATIONAL_SIGNIFICANCE));
		else
			fcstWeather.setHref(getPrecipitationReg().getWMOUrlByCode(weather));
		return fcstWeather;
	}

	public WMOCloudRegister getCloudReg() {
		return cloudReg;
	}

	public WMOCloudTypeRegister getCloudTypeReg() {
		return cloudTypeReg;
	}

	public WMOPrecipitationRegister getPrecipitationReg() {
		return precipitationReg;
	}

	public WMORunWayContaminationRegister getRwContaminationReg() {
		return rwContaminationReg;
	}

	public WMORunWayDepositsRegister getRwDepositReg() {
		return rwDepositReg;
	}

	public WMORunWayFrictionRegister getRwFrictionReg() {
		return rwFrictionReg;
	}

}
