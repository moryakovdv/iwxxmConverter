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

import java.util.GregorianCalendar;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.gamc.spmi.iwxxmConverter.general.IWXXMHelpers;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.wmo.WMOCloudRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMOCloudTypeRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMOPrecipitationRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMORunWayContaminationRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMORunWayDepositsRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMORunWayFrictionRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMOSigConvectiveCloudTypeRegister;
import org.joda.time.DateTime;

import schemabindings31._int.icao.iwxxm._3.AerodromeForecastWeatherType;
import schemabindings31._int.icao.iwxxm._3.AerodromePresentWeatherType;
import schemabindings31._int.icao.iwxxm._3.AerodromeRecentWeatherType;
import schemabindings31._int.icao.iwxxm._3.AirportHeliportPropertyType;
import schemabindings31._int.icao.iwxxm._3.CloudAmountReportedAtAerodromeType;
import schemabindings31._int.icao.iwxxm._3.CloudLayerType;
import schemabindings31._int.icao.iwxxm._3.DistanceWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.LengthWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.ReportType;
import schemabindings31._int.icao.iwxxm._3.RunwayDirectionPropertyType;
import schemabindings31._int.icao.iwxxm._3.SigConvectiveCloudTypeType;
import schemabindings31._int.icao.iwxxm._3.UnitPropertyType;
import schemabindings31.aero.aixm.schema._5_1.AirportHeliportTimeSlicePropertyType;
import schemabindings31.aero.aixm.schema._5_1.AirportHeliportTimeSliceType;
import schemabindings31.aero.aixm.schema._5_1.AirportHeliportType;
import schemabindings31.aero.aixm.schema._5_1.CodeAirportHeliportDesignatorType;
import schemabindings31.aero.aixm.schema._5_1.CodeAuthorityType;
import schemabindings31.aero.aixm.schema._5_1.CodeICAOType;
import schemabindings31.aero.aixm.schema._5_1.RunwayDirectionTimeSlicePropertyType;
import schemabindings31.aero.aixm.schema._5_1.RunwayDirectionTimeSliceType;
import schemabindings31.aero.aixm.schema._5_1.RunwayDirectionType;
import schemabindings31.aero.aixm.schema._5_1.TextDesignatorType;
import schemabindings31.aero.aixm.schema._5_1.UnitTimeSlicePropertyType;
import schemabindings31.aero.aixm.schema._5_1.UnitTimeSliceType;
import schemabindings31.aero.aixm.schema._5_1.UnitType;
import schemabindings31.net.opengis.gml.v_3_2_1.CodeWithAuthorityType;
import schemabindings31.net.opengis.gml.v_3_2_1.FeaturePropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimeInstantPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimeInstantType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimePeriodPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimePeriodType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimePositionType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimePrimitivePropertyType;



/**
 * Set of the helper functions. Provides creation of a common objects to use
 * during xml creation. 
 * Helps to reduce boiler-plate code. 
 * The functionality may
 * be extended to provide specific implementation for METAR, TAF, SIGMET etc..
 */
public class IWXXM31Helpers extends IWXXMHelpers {

	public static final schemabindings31._int.icao.iwxxm._3.ObjectFactory ofIWXXM = new schemabindings31._int.icao.iwxxm._3.ObjectFactory();
	public static final schemabindings31.net.opengis.gml.v_3_2_1.ObjectFactory ofGML = new schemabindings31.net.opengis.gml.v_3_2_1.ObjectFactory();
	public static final schemabindings31.net.opengis.om._2.ObjectFactory ofOM = new schemabindings31.net.opengis.om._2.ObjectFactory();
	public static final schemabindings31.org.w3._1999.xlink.ObjectFactory ofXLink = new schemabindings31.org.w3._1999.xlink.ObjectFactory();
	public static final schemabindings31.aero.aixm.schema._5_1.ObjectFactory ofAIXM = new schemabindings31.aero.aixm.schema._5_1.ObjectFactory();
	//public static final schemabindings31._int.wmo.def.metce._2013.ObjectFactory ofMetce = new schemabindings31._int.wmo.def.metce._2013.ObjectFactory();
	//public static final schemabindings31.net.opengis.samplingspatial._3.ObjectFactory ofSams = new schemabindings31.net.opengis.samplingspatial._3.ObjectFactory();

	
	/*WMO registers**/
	final WMOCloudRegister cloudReg = new WMOCloudRegister();
	final WMOCloudTypeRegister cloudTypeReg = new WMOCloudTypeRegister();
	
	final WMOSigConvectiveCloudTypeRegister sigCloudTypeReg = new WMOSigConvectiveCloudTypeRegister();

	final WMOPrecipitationRegister precipitationReg = new WMOPrecipitationRegister();

	final WMORunWayContaminationRegister rwContaminationReg = new WMORunWayContaminationRegister();
	final WMORunWayDepositsRegister rwDepositReg = new WMORunWayDepositsRegister();
	final WMORunWayFrictionRegister rwFrictionReg = new WMORunWayFrictionRegister();

	
	/**
	 * Creates TimeInstantPropertyType from given DateTime
	 * 
	 * @param dt
	 *            - dateTime to process
	 * @param icaoCode
	 *            - aerodrome ICAO code
	 * @return {@link TimeInstantPropertyType}
	 */
	public TimeInstantPropertyType createTimeInstantPropertyTypeForDateTime(DateTime dt, String icaoCode, String suffix) {

		String sDateTime = dt.toString(getDateTimeFormat()) + "Z";
		String sDateTimePosition = dt.toString(getDateTimeISOFormat());

		TimeInstantPropertyType timeInstantProperty = ofGML.createTimeInstantPropertyType();
		TimeInstantType timeInstant = ofGML.createTimeInstantType();
		timeInstant.setId(generateUUIDv4(String.format("ti-%s-%s-%s", icaoCode, sDateTime,suffix)));
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
		TimeInstantPropertyType timeProperty = createTimeInstantPropertyTypeForDateTime(dt, icaoCode,"timeproperty");
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
	
	
	public TimePeriodPropertyType createTimePeriod(String icaoCode, DateTime from, DateTime to) {
		
		TimePeriodPropertyType timePeriodProperty = IWXXM31Helpers.ofGML.createTimePeriodPropertyType();
		TimePeriodType timePeriodType = IWXXM31Helpers.ofGML.createTimePeriodType();

		timePeriodType.setId(generateUUIDv4(String.format("tp-%s-%s-%s", icaoCode, from.toString(), to.toString())));

		// begin
		TimeInstantType timeBeginInstant = IWXXM31Helpers.ofGML.createTimeInstantType();
		timeBeginInstant.setId(generateUUIDv4(String.format("ti-%s-%s", icaoCode,from.toString())));
		TimePositionType timePositionBegin = IWXXM31Helpers.ofGML.createTimePositionType();
		timePositionBegin.getValue().add(from.toString());
		timeBeginInstant.setTimePosition(timePositionBegin);

		TimeInstantPropertyType timeBeginProperty = IWXXM31Helpers.ofGML.createTimeInstantPropertyType();
		timeBeginProperty.setTimeInstant(timeBeginInstant);

		timePeriodType.setBeginPosition(timePositionBegin);

		// end
		TimeInstantType timeEndInstant = IWXXM31Helpers.ofGML.createTimeInstantType();
		timeEndInstant.setId(generateUUIDv4(String.format("ti-%s-%s", icaoCode, to.toString())));
		TimePositionType timePositionEnd = IWXXM31Helpers.ofGML.createTimePositionType();
		timePositionEnd.getValue().add(to.toString());
		timeEndInstant.setTimePosition(timePositionEnd);

		TimeInstantPropertyType timeEndProperty = IWXXM31Helpers.ofGML.createTimeInstantPropertyType();
		timeEndProperty.setTimeInstant(timeEndInstant);

		timePeriodType.setEndPosition(timePositionEnd);

		timePeriodProperty.setTimePeriod(timePeriodType);

		return timePeriodProperty;
		
	}
	

	/**
	 * Сreates FeaturePropertyType for given aerodrome icao code  <iwxxm:aerodrome>
	 * 
	 * @param icaoCode
	 *            - ICAO code for the aerodrome.
	 * @return {@link FeaturePropertyType} with aerodrome description
	 */
	public AirportHeliportPropertyType createAirportDescriptionSectionTag(String icaoCode) {
		
		AirportHeliportPropertyType ahpt = ofIWXXM.createAirportHeliportPropertyType();
		AirportHeliportType aht = new AirportHeliportType();
		aht.setId(generateUUIDv4("aerodrome-" + icaoCode));
		
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

		aht.getTimeSlice().add(ahTimeSliceProperty);
		
		
		ahpt.setAirportHeliport(aht);
		
		return ahpt;

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

			cloudType.setHref(sigCloudTypeReg.getWMOUrlByCode(sigCloudTypeReg.getCloudTypeByStringCode(cloudTypeCode)));
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
	
	public WMOSigConvectiveCloudTypeRegister getSigCloudTypeReg() {
		return sigCloudTypeReg;
	}


}
