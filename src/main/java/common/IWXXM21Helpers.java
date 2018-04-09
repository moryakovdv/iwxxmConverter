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

package common;

import java.io.UnsupportedEncodingException;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import _int.icao.iwxxm._2.AerodromeForecastWeatherType;
import _int.icao.iwxxm._2.AerodromePresentWeatherType;
import _int.icao.iwxxm._2.AerodromeRecentWeatherType;
import _int.icao.iwxxm._2.CloudAmountReportedAtAerodromeType;
import _int.icao.iwxxm._2.CloudLayerType;
import _int.icao.iwxxm._2.DistanceWithNilReasonType;
import _int.icao.iwxxm._2.LengthWithNilReasonType;
import _int.icao.iwxxm._2.ReportType;
import _int.icao.iwxxm._2.RunwayDirectionPropertyType;
import _int.icao.iwxxm._2.SigConvectiveCloudTypeType;
import aero.aixm.schema._5_1.AirportHeliportTimeSlicePropertyType;
import aero.aixm.schema._5_1.AirportHeliportTimeSliceType;
import aero.aixm.schema._5_1.AirportHeliportType;
import aero.aixm.schema._5_1.CodeAirportHeliportDesignatorType;
import aero.aixm.schema._5_1.CodeICAOType;
import aero.aixm.schema._5_1.RunwayDirectionTimeSlicePropertyType;
import aero.aixm.schema._5_1.RunwayDirectionTimeSliceType;
import aero.aixm.schema._5_1.RunwayDirectionType;
import aero.aixm.schema._5_1.TextDesignatorType;
import iwxxmenums.LENGTH_UNITS;
import net.opengis.gml.v_3_2_1.FeaturePropertyType;
import net.opengis.gml.v_3_2_1.ReferenceType;
import net.opengis.gml.v_3_2_1.TimeInstantPropertyType;
import net.opengis.gml.v_3_2_1.TimeInstantType;
import net.opengis.gml.v_3_2_1.TimePeriodPropertyType;
import net.opengis.gml.v_3_2_1.TimePeriodType;
import net.opengis.gml.v_3_2_1.TimePositionType;
import net.opengis.gml.v_3_2_1.TimePrimitivePropertyType;
import net.opengis.samplingspatial._2.SFSpatialSamplingFeatureType;
import net.opengis.samplingspatial._2.ShapeType;
import wmo.WMOCloudRegister;
import wmo.WMOCloudTypeRegister;
import wmo.WMOPrecipitationRegister;
import wmo.WMORunWayContaminationRegister;
import wmo.WMORunWayDepositsRegister;
import wmo.WMORunWayFrictionRegister;

/**
 * Set of the helper functions. Provides creation of a common objects to use
 * during xml creation. 
 * Helps to reduce boiler-plate code. 
 * The functionality may
 * be extended to provide specific implementation for METAR, TAF, SIGMET etc..
 */
public class IWXXM21Helpers {

	public static final _int.icao.iwxxm._2.ObjectFactory ofIWXXM = new _int.icao.iwxxm._2.ObjectFactory();
	public static final net.opengis.gml.v_3_2_1.ObjectFactory ofGML = new net.opengis.gml.v_3_2_1.ObjectFactory();
	public static final net.opengis.om._2.ObjectFactory ofOM = new net.opengis.om._2.ObjectFactory();
	public static final org.w3._1999.xlink.ObjectFactory ofXLink = new org.w3._1999.xlink.ObjectFactory();
	public static final aero.aixm.schema._5_1.ObjectFactory ofAIXM = new aero.aixm.schema._5_1.ObjectFactory();
	public static final _int.wmo.def.metce._2013.ObjectFactory ofMetce = new _int.wmo.def.metce._2013.ObjectFactory();
	public static final net.opengis.samplingspatial._2.ObjectFactory ofSams = new net.opengis.samplingspatial._2.ObjectFactory();

	
	/*WMO registers**/
	final WMOCloudRegister cloudReg = new WMOCloudRegister();
	final WMOCloudTypeRegister cloudTypeReg = new WMOCloudTypeRegister();
	final WMOPrecipitationRegister precipitationReg = new WMOPrecipitationRegister();

	final WMORunWayContaminationRegister rwContaminationReg = new WMORunWayContaminationRegister();
	final WMORunWayDepositsRegister rwDepositReg = new WMORunWayDepositsRegister();
	final WMORunWayFrictionRegister rwFrictionReg = new WMORunWayFrictionRegister();

	/**
	 * By default date time format include minutes. Override this in children if
	 * necessary
	 */
	public DateTimeFormatter getDateTimeFormat() {
		return DateTimeFormat.forPattern("yyyyMMddHHmm");
	}

	/** ISO format without milliseconds */
	public DateTimeFormatter getDateTimeISOFormat() {
		return ISODateTimeFormat.dateTimeNoMillis();
	}

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
		timeInstant.setId(String.format("ti-%s-%s", icaoCode, sDateTime));
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

		timePeriodType.setId(String.format("tp-%d-%s-%s", sectionIndex, sectionTimePeriodBegin, sectionTimePeriodEnd));

		// begin
		TimeInstantType timeBeginInstant = ofGML.createTimeInstantType();
		timeBeginInstant.setId(String.format("ti-%d-%s-%s", sectionIndex, icaoCode, sectionTimePeriodBegin));

		TimePositionType timePositionBegin = ofGML.createTimePositionType();
		// timePositionBegin.getValue().add(timePeriodBeginPosition);
		timePositionBegin.getValue().add(sectionTimePeriodBeginPosition);
		timeBeginInstant.setTimePosition(timePositionBegin);

		TimeInstantPropertyType timeBeginProperty = ofGML.createTimeInstantPropertyType();
		timeBeginProperty.setTimeInstant(timeBeginInstant);

		// end
		TimeInstantType timeEndInstant = ofGML.createTimeInstantType();
		timeEndInstant.setId(String.format("ti-%s-%s", icaoCode, sectionTimePeriodEnd));
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
		sfFeature.setId("sp-" + icaoCode);

		// empty shape tag
		ShapeType shape = ofSams.createShapeType();
		sfFeature.setShape(shape);

		ReferenceType sfType = ofGML.createReferenceType();
		sfType.setHref(UriConstants.GIS_SAMPLING_FEATURE);
		sfFeature.setType(sfType);

		AirportHeliportType airportType = ofAIXM.createAirportHeliportType();
		airportType.setId("aerodrome-" + icaoCode);

		AirportHeliportTimeSlicePropertyType ahTimeSliceProperty = ofAIXM.createAirportHeliportTimeSlicePropertyType();
		AirportHeliportTimeSliceType ahTimeSliceType = ofAIXM.createAirportHeliportTimeSliceType();
		ahTimeSliceType.setId(String.format("aerodrome-%s-ts", icaoCode));

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
		rdt.setId(String.format("runway-%s-%s",icaoCode,designator));
		
		
		RunwayDirectionTimeSlicePropertyType rdts = ofAIXM.createRunwayDirectionTimeSlicePropertyType();
		RunwayDirectionTimeSliceType rdtst = ofAIXM.createRunwayDirectionTimeSliceType();
		
		TextDesignatorType textDesignator = ofAIXM.createTextDesignatorType();
		textDesignator.setValue(designator);
		JAXBElement<TextDesignatorType> textDesTag = ofAIXM.createRunwayTimeSliceTypeDesignator(textDesignator);
		rdtst.setId(String.format("runway-%s-%s-ts",icaoCode,designator));
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
	 */
	public CloudLayerType createCloudLayerSection(int cloudAmount, double cloudHeight, String cloudTypeCode,
			String nilReason, LENGTH_UNITS units) {

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
		vvType.setUom(LENGTH_UNITS.FEETS.getStringValue());
		vvType.setValue(visibilityValue);
		return ofIWXXM.createAerodromeCloudForecastTypeVerticalVisibility(vvType);
		
	
	}

	/** Helper function to parse dateTime */
	public static DateTime parseDateTimeToken(String dtToken) {

		DateTime dtNow = DateTime.now();
		int year = dtNow.getYear();
		int month = dtNow.getMonthOfYear();

		int day = Integer.valueOf(dtToken.substring(0, 2));
		int hour = Integer.valueOf(dtToken.substring(2, 4));
		int minute = Integer.valueOf(dtToken.substring(4, 6));

		if (hour>23) {
			day++;
			hour=0;
		}
		
		int daysInMonth = dtNow.dayOfMonth().getMaximumValue();

		if (day > daysInMonth)
			month--;

		
		
		DateTime dtIssued = new DateTime(year, month, day, hour, minute, DateTimeZone.UTC);

		return dtIssued;

	}

	/** URN generating Helper */
	public String generateUUID(String strId) {

		try {
			return UUID.nameUUIDFromBytes(strId.getBytes("UTF-8")).toString();
		} catch (UnsupportedEncodingException e) {
			return UUID.randomUUID().toString();
		}
	}

	/** returns link for WMO weather register for present weather in METAR */
	public AerodromePresentWeatherType createPresentWeatherSection(String weather) {

		AerodromePresentWeatherType presentWeather = ofIWXXM.createAerodromePresentWeatherType();

		presentWeather.setHref(getPrecipitationReg().getWMOUrlByCode(weather));

		return presentWeather;
	}

	/** returns link for WMO weather register for recent weather in METAR */
	public AerodromeRecentWeatherType createRecentWeatherSection(String weather) {

		AerodromeRecentWeatherType recentWeather = ofIWXXM.createAerodromeRecentWeatherType();

		recentWeather.setHref(getPrecipitationReg().getWMOUrlByCode(weather));

		return recentWeather;
	}

	/**
	 * returns link for WMO weather register for forecasted weather in METAR and TAF
	 */
	public AerodromeForecastWeatherType createForecastWeatherSection(String weather) {
		AerodromeForecastWeatherType fcstWeather = ofIWXXM.createAerodromeForecastWeatherType();

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
