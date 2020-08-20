package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.acl.Owner;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.common.NamespaceMapper;
import org.gamc.spmi.iwxxmConverter.common.StringConstants;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.general.SolarCalc;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.spaceweatherconverter.SpaceWeatherEffectLocation;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
import org.gamc.spmi.iwxxmConverter.wmo.WMONilReasonRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.gamc.spmi.iwxxmConverter.wmo.WMOSpaceWeatherLocationRegister;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import schemabindings31._int.icao.iwxxm._3.AbstractTimeObjectPropertyType;
import schemabindings31._int.icao.iwxxm._3.AirspaceVolumePropertyType;
import schemabindings31._int.icao.iwxxm._3.PermissibleUsageType;
import schemabindings31._int.icao.iwxxm._3.SIGMETType;
import schemabindings31._int.icao.iwxxm._3.SpaceWeatherAdvisoryType;
import schemabindings31._int.icao.iwxxm._3.SpaceWeatherAnalysisPropertyType;
import schemabindings31._int.icao.iwxxm._3.SpaceWeatherAnalysisType;
import schemabindings31._int.icao.iwxxm._3.SpaceWeatherLocationType;
import schemabindings31._int.icao.iwxxm._3.SpaceWeatherPhenomenaType;
import schemabindings31._int.icao.iwxxm._3.SpaceWeatherRegionPropertyType;
import schemabindings31._int.icao.iwxxm._3.SpaceWeatherRegionType;
import schemabindings31._int.icao.iwxxm._3.StringWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.TimeIndicatorType;
import schemabindings31._int.icao.iwxxm._3.UnitPropertyType;
import schemabindings31.aero.aixm.schema._5_1.AirspaceVolumeType;
import schemabindings31.aero.aixm.schema._5_1.CodeAirspaceType;
import schemabindings31.aero.aixm.schema._5_1.SurfacePropertyType;
import schemabindings31.aero.aixm.schema._5_1.SurfaceType;
import schemabindings31.aero.aixm.schema._5_1.TextNameType;
import schemabindings31.aero.aixm.schema._5_1.UnitTimeSlicePropertyType;
import schemabindings31.aero.aixm.schema._5_1.UnitTimeSliceType;
import schemabindings31.aero.aixm.schema._5_1.UnitType;
import schemabindings31.net.opengis.gml.v_3_2_1.AbstractCurveSegmentType;
import schemabindings31.net.opengis.gml.v_3_2_1.AbstractRingPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.AbstractSurfacePatchType;
import schemabindings31.net.opengis.gml.v_3_2_1.AbstractTimeObjectType;
import schemabindings31.net.opengis.gml.v_3_2_1.CircleByCenterPointType;
import schemabindings31.net.opengis.gml.v_3_2_1.CodeType;
import schemabindings31.net.opengis.gml.v_3_2_1.CurvePropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.CurveSegmentArrayPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.CurveType;
import schemabindings31.net.opengis.gml.v_3_2_1.DirectPositionListType;
import schemabindings31.net.opengis.gml.v_3_2_1.LengthType;
import schemabindings31.net.opengis.gml.v_3_2_1.LinearRingPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.LinearRingType;
import schemabindings31.net.opengis.gml.v_3_2_1.LocationPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.PolygonPatchType;
import schemabindings31.net.opengis.gml.v_3_2_1.RingType;
import schemabindings31.net.opengis.gml.v_3_2_1.SurfacePatchArrayPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimeInstantPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimeInstantType;

public class SPACEWEATHERConverterV3 implements TacConverter<SPACEWEATHERTacMessage, SpaceWeatherAdvisoryType,IWXXM31Helpers> {

	IWXXM31Helpers iwxxmHelpers = new IWXXM31Helpers();
	final static String DAYLIGHT_SIDE = "DAYLIGHT_SIDE";
	final static int RADIUS = 10100;
	protected Logger logger = LoggerFactory.getLogger(SPACEWEATHERConverterV3.class);


	@Override
	public String convertTacToXML(String tac)
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException {
		
		logger.debug("Parsing "+ tac);

		SPACEWEATHERTacMessage swxMessage = new SPACEWEATHERTacMessage(tac);

		SpaceWeatherAdvisoryType result;

		try {
			swxMessage.parseMessage();
			result = convertMessage(swxMessage);

		} catch (ParsingException pe) {
			result = iwxxmHelpers.getOfIWXXM().createSpaceWeatherAdvisoryType();
			result.setTranslationFailedTAC(tac);
			logger.error("Message was NOT parsed properly. XML message with translationFailedTAC attribute was created. See error below",pe);


		}

		String xmlResult = marshallMessageToXML(result);
		return xmlResult;
	}

	@Override
	public SpaceWeatherAdvisoryType convertMessage(SPACEWEATHERTacMessage translatedMessage)
			throws DatatypeConfigurationException, UnsupportedEncodingException, JAXBException, ParsingException, WMORegisterException {

		// instantiating the result object
		SpaceWeatherAdvisoryType resultSwxType = new SpaceWeatherAdvisoryType();

		// set translation center attribute
		resultSwxType = addTranslationCentreHeader(resultSwxType);

		// set operational status
		resultSwxType.setPermissibleUsage(PermissibleUsageType.NON_OPERATIONAL);

		// set current swx number
		StringWithNilReasonType advNumber = iwxxmHelpers
				.createStringWithNilReasonForString(translatedMessage.getAdvisoryNumber(), null);
		resultSwxType.setAdvisoryNumber(advNumber);

		// set replaced number
		resultSwxType.setReplacedAdvisoryNumber(translatedMessage.getReplaceNumber());

		// set issuing time
		TimeInstantPropertyType issuedTime = iwxxmHelpers.createTimeInstantPropertyTypeForDateTime(
				translatedMessage.getIssued(), translatedMessage.getIssuingCenter(), "idt");
		resultSwxType.setIssueTime(issuedTime);

		// set phenomenas
		for (String effect : translatedMessage.getEffects())
			resultSwxType.getPhenomenon().add(createPhenomena(effect));

		// set advisory center
		resultSwxType.setIssuingSpaceWeatherCentre(
				createIssuingCenterSection(translatedMessage.getIssuingCenter(), translatedMessage.getIssued()));

		// set remarks
		StringWithNilReasonType remarks = iwxxmHelpers.createStringWithNilReasonForString(translatedMessage.getRemark(),
				null);
		resultSwxType.setRemarks(remarks);

		// create regions
		// for observation
		resultSwxType.getAnalysis().add(createAnalysisSection(translatedMessage.getIssuingCenter(),
				translatedMessage.getObservedLocation(), TimeIndicatorType.OBSERVATION));

		// for forecast
		for (SpaceWeatherEffectLocation fel : translatedMessage.getForecastedLocations()) {
			SpaceWeatherAnalysisPropertyType forecastedSection = createAnalysisSection(
					translatedMessage.getIssuingCenter(), fel, TimeIndicatorType.FORECAST);
			resultSwxType.getAnalysis().add(forecastedSection);
		}

		if (translatedMessage.getNextAdvDateTime() != null) {
			TimeInstantPropertyType nextAdvTime = iwxxmHelpers.createTimeInstantPropertyTypeForDateTime(
					translatedMessage.getNextAdvDateTime(), translatedMessage.getIssuingCenter(), "nat");
			resultSwxType.setNextAdvisoryTime(nextAdvTime);
		}
		else {
			TimeInstantPropertyType nextAdvTimeNil = iwxxmHelpers.getOfGML().createTimeInstantPropertyType();
			nextAdvTimeNil.getNilReason().add(iwxxmHelpers.getNilRegister().getWMOUrlByCode(WMONilReasonRegister.NIL_REASON_UNKNOWN));
			
			resultSwxType.setNextAdvisoryTime(nextAdvTimeNil);
		}
		return resultSwxType;
	}

	/** creates UnitPropertyType section for issuing center */
	public UnitPropertyType createIssuingCenterSection(String issuingCenter, DateTime dateTime) {

		// create external envelope
		UnitPropertyType unitType = iwxxmHelpers.getOfIWXXM().createUnitPropertyType();
		UnitType unit = iwxxmHelpers.getOfAIXM().createUnitType();

		// add unit data "flesh"
		UnitTimeSliceType tst = iwxxmHelpers.getOfAIXM().createUnitTimeSliceType();
		tst.setId(iwxxmHelpers.generateUUIDv4("utst-" + issuingCenter + "-" + dateTime));

		// add interpretation
		tst.setInterpretation("SNAPSHOT");

		// add center name
		TextNameType nType = iwxxmHelpers.getOfAIXM().createTextNameType();
		nType.setValue(issuingCenter);
		JAXBElement<TextNameType> ntType = iwxxmHelpers.getOfAIXM().createAirspaceTimeSliceTypeName(nType);
		tst.getRest().add(ntType);

		// add airspace type
		CodeAirspaceType asType = iwxxmHelpers.getOfAIXM().createCodeAirspaceType();
		asType.setValue("OTHER:SWXC");
		JAXBElement<CodeAirspaceType> astType = iwxxmHelpers.getOfAIXM().createAirspaceTimeSliceTypeType(asType);
		tst.getRest().add(astType);

		UnitTimeSlicePropertyType unitTs = iwxxmHelpers.getOfAIXM().createUnitTimeSlicePropertyType();
		unitTs.setUnitTimeSlice(tst);
		// place unit in envelop
		unit.getTimeSlice().add(unitTs);
		unit.setId(iwxxmHelpers.generateUUIDv4("uts-" + issuingCenter + "-" + dateTime));

		unitType.setUnit(unit);

		// return aixm:unit element
		return unitType;

	}

	public List<Double> getListOfCoords() {
		LinkedList<Double> listOfCoords = new LinkedList<Double>();
		listOfCoords.add(60.0);
		listOfCoords.add(180.0);
		listOfCoords.add(60.0);
		listOfCoords.add(-180.0);
		listOfCoords.add(90.0);
		listOfCoords.add(-180.0);
		listOfCoords.add(90.0);
		listOfCoords.add(180.0);
		listOfCoords.add(60.0);
		listOfCoords.add(180.0);

		return listOfCoords;
	};

	public List<Double> getListOfDayLightSideCoords(DateTime dt) {
		SolarCalc sc = new SolarCalc();
		LinkedList<Double> listOfCoords = new LinkedList<Double>();
		ZonedDateTime zdt = ZonedDateTime.of(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth(), dt.getHourOfDay(), dt.getMinuteOfHour(), dt.getSecondOfMinute(), 0, ZoneId.of("UTC"));
		listOfCoords.add(roundDouble(sc.calcSubSolarPointLatitude(zdt),2));
		listOfCoords.add(roundDouble(sc.calcSubSolarPointLongitude(zdt),2));
	
		return listOfCoords;
	};

	private double roundDouble(double value, int precision) {
		
		 BigDecimal bd = new BigDecimal(Double.toString(value));
		   bd = bd.setScale(precision, RoundingMode.HALF_UP);
		   return bd.doubleValue();
		
	}
	
	/** create link for effect from WMO repository 
	 * @throws WMORegisterException */
	public SpaceWeatherPhenomenaType createPhenomena(String effect) throws WMORegisterException {

		SpaceWeatherPhenomenaType phenomena = iwxxmHelpers.getOfIWXXM().createSpaceWeatherPhenomenaType();
		String effectLink = iwxxmHelpers.getSpaceWeatherReg().getWMOUrlByCode(effect);
		phenomena.setHref(effectLink);

		return phenomena;

	}

	/** creates section for observed location of phenomenas 
	 * @throws WMORegisterException */
	public SpaceWeatherAnalysisPropertyType createAnalysisSection(String issuingCenter,
			SpaceWeatherEffectLocation location, TimeIndicatorType observationOrforecast) throws WMORegisterException {
		SpaceWeatherAnalysisPropertyType analysisSection = iwxxmHelpers.getOfIWXXM()
				.createSpaceWeatherAnalysisPropertyType();
		SpaceWeatherAnalysisType analysisType = iwxxmHelpers.getOfIWXXM().createSpaceWeatherAnalysisType();
		analysisType.setTimeIndicator(observationOrforecast);

		// set time of phenomena
		AbstractTimeObjectPropertyType analysisTimeProperty = iwxxmHelpers
				.createAbstractTimeObject(location.getEffectsDateTime(), issuingCenter);
		analysisType.setPhenomenonTime(analysisTimeProperty);

		// process hemisheres in observation
		for (String hemi : location.getHemiSpheres()) {
			SpaceWeatherRegionPropertyType regionSection = createSpaceWeatherRegion(hemi, getListOfCoords());
			analysisType.getRegion().add(regionSection);
		}

		if (location.isDayLightSide()) {
			SpaceWeatherRegionPropertyType dayLightSection = createDayLightSpaceWeatherRegion(
					getListOfDayLightSideCoords(location.getEffectsDateTime()));
			analysisType.getRegion().add(dayLightSection);
		}

		/***
		 * TODO when implemented in schemas ** if (observedLocation.isNightSide()) {
		 * SpaceWeatherRegionPropertyType nightSideSection =
		 * createNightSideSpaceWeatherRegion();
		 * analysisType.getRegion().add(nightSideSection); }
		 ***/

		analysisSection.setSpaceWeatherAnalysis(analysisType);
		return analysisSection;

	}

	/**
	 * creates new iwxxm:SpaceWeatherRegion for observation or forecasting section
	 * for hemisphere and coord list saves it to temporary storage seeks for storage
	 * and return previously created region if it already exists
	 */
	/**
	 * <iwxxm:SpaceWeatherRegion gml:id="uuid.186a603f-7a4a-4873-9ec7-29faccd2015e">
	 * <iwxxm:location>
	 * <aixm:AirspaceVolume gml:id="uuid.7138f8d1-9854-46cb-a1b1-9d96c5cee46f">
	 * <aixm:horizontalProjection>
	 * <aixm:Surface gml:id="uuid.3b00652b-4bed-4655-959c-1c9efd431cac" srsDimension
	 * ="2" axisLabels="Lat Long" srsName=
	 * "http://www.opengis.net/def/crs/EPSG/0/4326"> <gml:patches>
	 * <gml:PolygonPatch> <gml:exterior> <gml:LinearRing> <gml:posList>60 180 60
	 * -180 90 -180 90 180 60 180</gml:posList> </gml:LinearRing> </gml:exterior>
	 * </gml:PolygonPatch> </gml:patches> </aixm:Surface>
	 * </aixm:horizontalProjection> </aixm:AirspaceVolume> </iwxxm:location>
	 * <iwxxm:locationIndicator xlink:href=
	 * "http://codes.wmo.int/49-2/SpaceWxLocation/HNH"/> </iwxxm:SpaceWeatherRegion>
	 * @throws WMORegisterException 
	 **/
	public SpaceWeatherRegionPropertyType createSpaceWeatherRegion(String hemi, List<Double> coords) throws WMORegisterException {

		SpaceWeatherRegionPropertyType regionSection = iwxxmHelpers.getOfIWXXM().createSpaceWeatherRegionPropertyType();
		SpaceWeatherRegionType region = iwxxmHelpers.getOfIWXXM().createSpaceWeatherRegionType();

		region.setId(iwxxmHelpers.generateUUIDv4("region-" + hemi));

		// create location

		AirspaceVolumePropertyType airspaceVolumeSection = iwxxmHelpers.getOfIWXXM().createAirspaceVolumePropertyType();
		AirspaceVolumeType airspaceVolume = iwxxmHelpers.getOfAIXM().createAirspaceVolumeType();
		airspaceVolume.setId(iwxxmHelpers.generateUUIDv4("airspace-" + hemi));

		// create projection
		SurfacePropertyType surfaceSection = iwxxmHelpers.getOfAIXM().createSurfacePropertyType();
		SurfaceType sfType = iwxxmHelpers.getOfAIXM().createSurfaceType();
		sfType.getAxisLabels().add("Lat");
		sfType.getAxisLabels().add("Long");
		sfType.setSrsDimension(BigInteger.valueOf(2));
		sfType.setSrsName("http://www.opengis.net/def/crs/EPSG/0/4326");
		sfType.setId(iwxxmHelpers.generateUUIDv4("surface-" + hemi));

		// add gml patches
		SurfacePatchArrayPropertyType patchArray = iwxxmHelpers.getOfGML().createSurfacePatchArrayPropertyType();

		// create polygon
		PolygonPatchType patchType = iwxxmHelpers.getOfGML().createPolygonPatchType();
		AbstractRingPropertyType ringType = iwxxmHelpers.getOfGML().createAbstractRingPropertyType();
		LinearRingType linearRingType = iwxxmHelpers.getOfGML().createLinearRingType();

		// fill polygon with coords
		DirectPositionListType dpListType = iwxxmHelpers.getOfGML().createDirectPositionListType();
		dpListType.getValue().addAll(coords);
		linearRingType.setPosList(dpListType);

		// put polygon in the envelope
		JAXBElement<LinearRingType> lrPt = iwxxmHelpers.getOfGML().createLinearRing(linearRingType);
		ringType.setAbstractRing(lrPt);
		patchType.setExterior(ringType);

		JAXBElement<PolygonPatchType> patch = iwxxmHelpers.getOfGML().createPolygonPatch(patchType);
		patchArray.getAbstractSurfacePatch().add(patch);

		JAXBElement<SurfacePatchArrayPropertyType> pta = iwxxmHelpers.getOfGML().createPatches(patchArray);

		sfType.setPatches(pta);

		JAXBElement<SurfaceType> syrfaceElement = iwxxmHelpers.getOfAIXM().createSurface(sfType);
		surfaceSection.setSurface(syrfaceElement);
		JAXBElement<SurfacePropertyType> spt = iwxxmHelpers.getOfAIXM()
				.createAirspaceVolumeTypeHorizontalProjection(surfaceSection);
		// create aixm:horizontalProjection
		airspaceVolume.setHorizontalProjection(spt);

		// JAXBElement<AirspaceVolumePropertyType> locationSection =
		// iwxxmHelpers.ofIWXXM.createSpaceWeatherRegionTypeLocation(airspaceVolumeSection);

		// region.getRest().add(locationSection);
		airspaceVolumeSection.setAirspaceVolume(airspaceVolume);

		// put in iwxxm:region the AirSpaceVolume tree
		JAXBElement<AirspaceVolumePropertyType> locationSection = iwxxmHelpers.getOfIWXXM()
				.createSpaceWeatherRegionTypeLocation(airspaceVolumeSection);
		region.getRest().add(locationSection);

		// add locationIndicator node
		SpaceWeatherLocationType locationType = iwxxmHelpers.getOfIWXXM().createSpaceWeatherLocationType();
		locationType.setHref(iwxxmHelpers.getSpaceWeatherLocationReg().getWMOUrlByCode(hemi));
		JAXBElement<SpaceWeatherLocationType> locationIndicatorSection = iwxxmHelpers.getOfIWXXM()
				.createSpaceWeatherRegionTypeLocationIndicator(locationType);
		region.getRest().add(locationIndicatorSection);

		regionSection.setSpaceWeatherRegion(region);

		return regionSection;
	}

	/**
	 * <gml:Ring> <gml:curveMember>
	 * <gml:Curve gml:id="uuid.e0025db0-7a34-49da-a66c-17757d9e1a45"> <gml:segments>
	 * <gml:CircleByCenterPoint numArc="1"> <gml:pos>-16.93 160.96</gml:pos>
	 * <gml:radius uom="km">10100</gml:radius> </gml:CircleByCenterPoint>
	 * </gml:segments> </gml:Curve> </gml:curveMember> </gml:Ring>
	 * @throws WMORegisterException 
	 **/
	public SpaceWeatherRegionPropertyType createDayLightSpaceWeatherRegion(List<Double> coords) throws WMORegisterException {
		SpaceWeatherRegionPropertyType regionSection = iwxxmHelpers.getOfIWXXM().createSpaceWeatherRegionPropertyType();
		SpaceWeatherRegionType region = iwxxmHelpers.getOfIWXXM().createSpaceWeatherRegionType();

		region.setId(iwxxmHelpers.generateUUIDv4("region-" + DAYLIGHT_SIDE));

		// create location

		AirspaceVolumePropertyType airspaceVolumeSection = iwxxmHelpers.getOfIWXXM().createAirspaceVolumePropertyType();
		AirspaceVolumeType airspaceVolume = iwxxmHelpers.getOfAIXM().createAirspaceVolumeType();
		airspaceVolume.setId(iwxxmHelpers.generateUUIDv4("airspace-" + DAYLIGHT_SIDE));

		// create projection
		SurfacePropertyType surfaceSection = iwxxmHelpers.getOfAIXM().createSurfacePropertyType();
		SurfaceType sfType = iwxxmHelpers.getOfAIXM().createSurfaceType();
		sfType.getAxisLabels().add("Lat");
		sfType.getAxisLabels().add("Long");
		sfType.setSrsDimension(BigInteger.valueOf(2));
		sfType.setSrsName("http://www.opengis.net/def/crs/EPSG/0/4326");
		sfType.setId(iwxxmHelpers.generateUUIDv4("surface-" + DAYLIGHT_SIDE));

		// add gml patches
		SurfacePatchArrayPropertyType patchArray = iwxxmHelpers.getOfGML().createSurfacePatchArrayPropertyType();

		// create polygon
		PolygonPatchType patchType = iwxxmHelpers.getOfGML().createPolygonPatchType();
		AbstractRingPropertyType ringType = iwxxmHelpers.getOfGML().createAbstractRingPropertyType();

		// create ring with radius and fill it with data
		RingType internalRingType = iwxxmHelpers.getOfGML().createRingType();

		CurvePropertyType curveType = iwxxmHelpers.getOfGML().createCurvePropertyType();

		CurveType curveMemberSection = iwxxmHelpers.getOfGML().createCurveType();
		curveMemberSection.setId(iwxxmHelpers.generateUUIDv4("curve-" + DAYLIGHT_SIDE));

		// add segments
		CurveSegmentArrayPropertyType segmentArraySection = iwxxmHelpers.getOfGML().createCurveSegmentArrayPropertyType();

		// describe circle
		CircleByCenterPointType segmentCircle = iwxxmHelpers.getOfGML().createCircleByCenterPointType();

		LengthType radius = iwxxmHelpers.getOfGML().createLengthType();
		radius.setUom(LENGTH_UNITS.KM.getStringValue());
		radius.setValue(RADIUS);

		segmentCircle.setRadius(radius);
		DirectPositionListType dpListType = iwxxmHelpers.getOfGML().createDirectPositionListType();
		dpListType.getValue().addAll(coords);
		segmentCircle.setPosList(dpListType);

		JAXBElement<CircleByCenterPointType> segmentSection = iwxxmHelpers.getOfGML()
				.createCircleByCenterPoint(segmentCircle);
		segmentArraySection.getAbstractCurveSegment().add(segmentSection);

		curveMemberSection.setSegments(segmentArraySection);

		JAXBElement<CurveType> curve = iwxxmHelpers.getOfGML().createCurve(curveMemberSection);
		curveType.setAbstractCurve(curve);

		internalRingType.getCurveMember().add(curveType);

		// put polygon in the envelope
		JAXBElement<RingType> lrPt = iwxxmHelpers.getOfGML().createRing(internalRingType);
		ringType.setAbstractRing(lrPt);
		patchType.setExterior(ringType);

		JAXBElement<PolygonPatchType> patch = iwxxmHelpers.getOfGML().createPolygonPatch(patchType);
		patchArray.getAbstractSurfacePatch().add(patch);

		JAXBElement<SurfacePatchArrayPropertyType> pta = iwxxmHelpers.getOfGML().createPatches(patchArray);

		sfType.setPatches(pta);

		JAXBElement<SurfaceType> syrfaceElement = iwxxmHelpers.getOfAIXM().createSurface(sfType);
		surfaceSection.setSurface(syrfaceElement);
		JAXBElement<SurfacePropertyType> spt = iwxxmHelpers.getOfAIXM()
				.createAirspaceVolumeTypeHorizontalProjection(surfaceSection);
		// create aixm:horizontalProjection
		airspaceVolume.setHorizontalProjection(spt);

		// JAXBElement<AirspaceVolumePropertyType> locationSection =
		// iwxxmHelpers.ofIWXXM.createSpaceWeatherRegionTypeLocation(airspaceVolumeSection);

		// region.getRest().add(locationSection);
		airspaceVolumeSection.setAirspaceVolume(airspaceVolume);

		// put in iwxxm:region the AirSpaceVolume tree
		JAXBElement<AirspaceVolumePropertyType> locationSection = iwxxmHelpers.getOfIWXXM()
				.createSpaceWeatherRegionTypeLocation(airspaceVolumeSection);
		region.getRest().add(locationSection);

		// add locationIndicator node
		SpaceWeatherLocationType locationType = iwxxmHelpers.getOfIWXXM().createSpaceWeatherLocationType();
		locationType.setHref(iwxxmHelpers.getSpaceWeatherLocationReg().getWMOUrlByCode(DAYLIGHT_SIDE));
		JAXBElement<SpaceWeatherLocationType> locationIndicatorSection = iwxxmHelpers.getOfIWXXM()
				.createSpaceWeatherRegionTypeLocationIndicator(locationType);
		region.getRest().add(locationIndicatorSection);

		regionSection.setSpaceWeatherRegion(region);

		return regionSection;
	}

	@Override
	public SpaceWeatherAdvisoryType addTranslationCentreHeader(SpaceWeatherAdvisoryType report)
			throws DatatypeConfigurationException {
		report = iwxxmHelpers.addTranslationCentreHeaders(report, DateTime.now(), DateTime.now(),
				UUID.randomUUID().toString(), "UUWV", "Moscow, RU");
		return report;

	}

	@Override
	public String marshallMessageToXML(SpaceWeatherAdvisoryType reportType)
			throws JAXBException, UnsupportedEncodingException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		JAXBContext jaxbContext = JAXBContext.newInstance(SIGMETType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, UriConstants.GLOBAL_SCHEMAS_LOCATION_V3);

		jaxbMarshaller.setProperty(StringConstants.SUN_JAXB_NAMESPACE_MAPPING_PROPERTY_NAME, new NamespaceMapper());

		JAXBElement<SpaceWeatherAdvisoryType> swxRootElement = iwxxmHelpers.getOfIWXXM()
				.createSpaceWeatherAdvisory(reportType);

		jaxbMarshaller.marshal(swxRootElement, stream);

		return stream.toString("UTF-8");
	}
	
	@Override
	public IWXXM31Helpers getHelper() {
		return iwxxmHelpers;
	}
	
	@Override
	public SPACEWEATHERConverterV3 withHelper(IWXXM31Helpers helper) {
		this.iwxxmHelpers = helper;
		return this;
		
	}
}
