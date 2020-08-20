package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.common.NamespaceMapper;
import org.gamc.spmi.iwxxmConverter.common.StringConstants;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.general.IWXXMHelpers;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.ANGLE_UNITS;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
import org.gamc.spmi.iwxxmConverter.wmo.WMONilReasonRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import schemabindings31._int.icao.iwxxm._3.AIRMETType;
import schemabindings31._int.icao.iwxxm._3.AbstractTimeObjectPropertyType;
import schemabindings31._int.icao.iwxxm._3.AeronauticalAreaWeatherPhenomenonType;
import schemabindings31._int.icao.iwxxm._3.AeronauticalSignificantWeatherPhenomenonType;
import schemabindings31._int.icao.iwxxm._3.AirspacePropertyType;
import schemabindings31._int.icao.iwxxm._3.AirspaceVolumePropertyType;
import schemabindings31._int.icao.iwxxm._3.AngleWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.ExpectedIntensityChangeType;
import schemabindings31._int.icao.iwxxm._3.PermissibleUsageReasonType;
import schemabindings31._int.icao.iwxxm._3.PermissibleUsageType;
import schemabindings31._int.icao.iwxxm._3.ReportStatusType;
import schemabindings31._int.icao.iwxxm._3.AIRMETEvolvingConditionCollectionPropertyType;
import schemabindings31._int.icao.iwxxm._3.AIRMETEvolvingConditionCollectionType;
import schemabindings31._int.icao.iwxxm._3.AIRMETEvolvingConditionPropertyType;
import schemabindings31._int.icao.iwxxm._3.AIRMETEvolvingConditionType;
import schemabindings31._int.icao.iwxxm._3.AIRMETExpectedIntensityChangeType;
import schemabindings31._int.icao.iwxxm._3.AIRMETType;
import schemabindings31._int.icao.iwxxm._3.StringWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.TimeIndicatorType;
import schemabindings31._int.icao.iwxxm._3.UnitPropertyType;
import schemabindings31.aero.aixm.schema._5_1.AirspaceTimeSlicePropertyType;
import schemabindings31.aero.aixm.schema._5_1.AirspaceTimeSliceType;
import schemabindings31.aero.aixm.schema._5_1.AirspaceType;
import schemabindings31.aero.aixm.schema._5_1.AirspaceVolumeType;
import schemabindings31.aero.aixm.schema._5_1.CodeAirspaceDesignatorType;
import schemabindings31.aero.aixm.schema._5_1.CodeAirspaceType;
import schemabindings31.aero.aixm.schema._5_1.CodeVerticalReferenceType;
import schemabindings31.aero.aixm.schema._5_1.SurfacePropertyType;
import schemabindings31.aero.aixm.schema._5_1.SurfaceType;
import schemabindings31.aero.aixm.schema._5_1.TextNameType;
import schemabindings31.aero.aixm.schema._5_1.UnitTimeSlicePropertyType;
import schemabindings31.aero.aixm.schema._5_1.UnitTimeSliceType;
import schemabindings31.aero.aixm.schema._5_1.UnitType;
import schemabindings31.aero.aixm.schema._5_1.ValDistanceVerticalType;
import schemabindings31.net.opengis.gml.v_3_2_1.AbstractRingPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.AssociationRoleType;
import schemabindings31.net.opengis.gml.v_3_2_1.DirectPositionListType;
import schemabindings31.net.opengis.gml.v_3_2_1.LinearRingType;
import schemabindings31.net.opengis.gml.v_3_2_1.PolygonPatchType;
import schemabindings31.net.opengis.gml.v_3_2_1.SpeedType;
import schemabindings31.net.opengis.gml.v_3_2_1.StringOrRefType;
import schemabindings31.net.opengis.gml.v_3_2_1.SurfacePatchArrayPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimeInstantPropertyType;

public class AIRMETConverterV3 implements TacConverter<AIRMETTacMessage, AIRMETType, IWXXM31Helpers> {

	private TreeMap<String, String> createdRunways = new TreeMap<>();
	private String airTrafficUnit = "FIC";
	private String watchOfficeType = "MWO";
	private String firType = "OTHER:FIR_UIR";
	private String interpretation = "SNAPSHOT";

	private IWXXM31Helpers iwxxmHelpers = new IWXXM31Helpers();
	private String dateTime = "";
	private String dateTimePosition = "";
	private AIRMETTacMessage translatedAirmet;
	
	protected Logger logger = LoggerFactory.getLogger(AIRMETConverterV3.class);


	@Override
	public String convertTacToXML(String tac)
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException {
		
		logger.debug("Parsing "+ tac);
		
		createdRunways.clear();

		AIRMETTacMessage airmetMessage = new AIRMETTacMessage(tac);

		AIRMETType result;

		try {
			airmetMessage.parseMessage();
			result = convertMessage(airmetMessage);
		} catch (ParsingException pe) {
			result = iwxxmHelpers.getOfIWXXM().createAIRMETType();
			result.setTranslationFailedTAC(tac);
			logger.error("Message was NOT parsed properly. XML message with translationFailedTAC attribute was created. See error below",pe);


		}

		String xmlResult = marshallMessageToXML(result);

		return xmlResult;
	}

	@Override
	public AIRMETType convertMessage(AIRMETTacMessage translatedMessage)
			throws DatatypeConfigurationException, UnsupportedEncodingException, JAXBException, ParsingException, WMORegisterException {
		this.translatedAirmet = translatedMessage;
		AIRMETType airmetRootTag = iwxxmHelpers.getOfIWXXM().createAIRMETType();

		StringOrRefType refTacString = iwxxmHelpers.getOfGML().createStringOrRefType();
		refTacString.setValue(translatedMessage.getInitialTacString());
		airmetRootTag.setDescription(refTacString);

		dateTime = translatedMessage.getMessageIssueDateTime().toString(iwxxmHelpers.getDateTimeFormat()) + "Z";
		dateTimePosition = translatedMessage.getMessageIssueDateTime().toString(iwxxmHelpers.getDateTimeISOFormat());

		// Id with ICAO code and current timestamp
		airmetRootTag.setId(
				iwxxmHelpers.generateUUIDv4(String.format("airmet-%s-%s", translatedAirmet.getIcaoCode(), dateTime)));

		// airmetRootTag.setAutomatedStation(true);

		// Set NON_OPERATIONAL and TEST properties.
		airmetRootTag.setPermissibleUsage(PermissibleUsageType.NON_OPERATIONAL);
		airmetRootTag.setPermissibleUsageReason(PermissibleUsageReasonType.TEST);

		// Some description
		airmetRootTag.setPermissibleUsageSupplementary("AIRMET composing test using JAXB");

		// COR, NIL, NORMAL
		switch (translatedAirmet.getMessageStatusType()) {
		case MISSING:
			airmetRootTag.setReportStatus(null);
			break;
		case CORRECTION:
			airmetRootTag.setReportStatus(ReportStatusType.CORRECTION);
			break;

		default:
			airmetRootTag.setReportStatus(ReportStatusType.NORMAL);
		}
		airmetRootTag = addTranslationCentreHeader(airmetRootTag);
		TimeInstantPropertyType obsTimeType = iwxxmHelpers.createTimeInstantPropertyTypeForDateTime(
				translatedAirmet.getMessageIssueDateTime(), translatedAirmet.getIcaoCode(), "issue");
		airmetRootTag.setIssueTime(obsTimeType);

		StringWithNilReasonType seq = iwxxmHelpers.getOfIWXXM().createStringWithNilReasonType();
		seq.setValue(translatedAirmet.getAirmetNumber());

		airmetRootTag.setSequenceNumber(seq);

		airmetRootTag.setIssuingAirTrafficServicesUnit(createUnitPropertyTypeNode(translatedAirmet.getIcaoCode(),
				translatedAirmet.getIcaoCode(), airTrafficUnit, interpretation));
		airmetRootTag.setIssuingAirTrafficServicesRegion(createAirspacePropertyTypeNode(translatedAirmet.getIcaoCode(),
				translatedAirmet.getFirName(), firType, interpretation));

		airmetRootTag.setOriginatingMeteorologicalWatchOffice(createUnitPropertyTypeNode(translatedAirmet.getIcaoCode(),
				translatedAirmet.getWatchOffice(), watchOfficeType, interpretation));

		airmetRootTag.setValidPeriod(iwxxmHelpers.createTimePeriod(translatedAirmet.getIcaoCode(),
				translatedAirmet.getValidFrom(), translatedAirmet.getValidTo()));

		switch (translatedMessage.getMessageStatusType()) {

		case CANCEL:
			airmetRootTag.setCancelledReportSequenceNumber(translatedAirmet.getCancelAirmetNumber());
			airmetRootTag.setCancelledReportValidPeriod(iwxxmHelpers.createTimePeriod(translatedAirmet.getIcaoCode(),
					translatedAirmet.getCancelAirmetDateTimeFrom(), translatedAirmet.getCancelAirmetDateTimeTo()));

			break;
		default:
			airmetRootTag.setPhenomenon(setAeronauticalSignificantWeatherPhenomenonType());
			AIRMETEvolvingConditionCollectionPropertyType analists = iwxxmHelpers.getOfIWXXM()
					.createAIRMETEvolvingConditionCollectionPropertyType();
			analists.setAIRMETEvolvingConditionCollection(setAssociationRoleType());
			airmetRootTag.setAnalysis(analists);
			break;
		}

		// create XML representation
		return airmetRootTag;
	}

	public AIRMETEvolvingConditionCollectionType setAssociationRoleType() throws WMORegisterException {
		// ---------------AirspaceVolumePropertyType----------------//
		AirspaceVolumePropertyType air = iwxxmHelpers.getOfIWXXM().createAirspaceVolumePropertyType();
		air.setAirspaceVolume(createAirSpaceVolumeSection(getListOfCoords()));
		// ---------------AIRMETEvolvingConditionType----------------//
		AIRMETEvolvingConditionType evolvingType = iwxxmHelpers.getOfIWXXM().createAIRMETEvolvingConditionType();
		evolvingType.setGeometry(air);
		AIRMETEvolvingConditionPropertyType sicol = iwxxmHelpers.getOfIWXXM().createAIRMETEvolvingConditionPropertyType();
		sicol.setAIRMETEvolvingCondition(evolvingType);
		// ---------------Association Role----------------//
		AIRMETEvolvingConditionCollectionType asType = iwxxmHelpers.getOfIWXXM()
				.createAIRMETEvolvingConditionCollectionType();
		asType.getMember().add(sicol);
		// ---------------AIRMETEvolvingConditionType----------------//
		/*
		 * AIRMETEvolvingConditionCollectionType sicol = iwxxmHelpers.getOfIWXXM()
		 * .createAIRMETEvolvingConditionCollectionType();
		 * JAXBElement<AIRMETEvolvingConditionCollectionType> evolvingAr =
		 * iwxxmHelpers.getOfIWXXM() .createAIRMETEvolvingConditionCollection(sicol);
		 * AIRMETEvolvingConditionType evolvingType =
		 * iwxxmHelpers.getOfIWXXM().createAIRMETEvolvingConditionType();
		 * evolvingType.setGeometry(air);
		 */
		// ---------------AIRMETEvolvingConditionType(Speed-Motion-Id-Intencity)----------------//
		SpeedType speedType = iwxxmHelpers.getOfGML().createSpeedType();

		if (translatedAirmet.getPhenomenonDescription().getMovingSection() != null
				&& translatedAirmet.getPhenomenonDescription().getMovingSection().isMoving()) {
			AngleWithNilReasonType motion = iwxxmHelpers.getOfIWXXM().createAngleWithNilReasonType();
			JAXBElement<AngleWithNilReasonType> dirMo = iwxxmHelpers.getOfIWXXM()
					.createAIRMETEvolvingConditionTypeDirectionOfMotion(motion);

			motion.setUom(ANGLE_UNITS.DEGREES.getStringValue());
			motion.setValue(translatedAirmet.getPhenomenonDescription().getMovingSection().getMovingDirection()
					.getDoubleValue());

			evolvingType.setDirectionOfMotion(dirMo);

			if (translatedAirmet.getPhenomenonDescription().getMovingSection().getSpeedUnits() != null
					&& translatedAirmet.getPhenomenonDescription().getMovingSection().getMovingSpeed() > 0) {
				speedType.setUom(translatedAirmet.getPhenomenonDescription().getMovingSection().getSpeedUnits()
						.getStringValue());
				speedType.setValue(translatedAirmet.getPhenomenonDescription().getMovingSection().getMovingSpeed());

				evolvingType.setSpeedOfMotion(speedType);
			}

		}

		evolvingType.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-ts", translatedAirmet.getIcaoCode())));
		if (translatedAirmet.getPhenomenonDescription().getIntencity().name().equals("INTSF")) {
			evolvingType.setIntensityChange(AIRMETExpectedIntensityChangeType.INTENSIFY);
		} else if (translatedAirmet.getPhenomenonDescription().getIntencity().name().equals("WKN")) {
			evolvingType.setIntensityChange(AIRMETExpectedIntensityChangeType.WEAKEN);
		} else if (translatedAirmet.getPhenomenonDescription().getIntencity().name().equals("NC")) {
			evolvingType.setIntensityChange(AIRMETExpectedIntensityChangeType.NO_CHANGE);
		}
		asType.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-ts", translatedAirmet.getIcaoCode())));

		// ---------------AIRMETEvolvingConditionType(Time)----------------//
		AbstractTimeObjectPropertyType analysisTimeProperty = iwxxmHelpers.createAbstractTimeObject(
				translatedAirmet.getPhenomenonDescription().getPhenomenonTimeStamp(),
				translatedAirmet.getDisseminatingCentre());
		asType.setPhenomenonTime(analysisTimeProperty);

		// ---------------AIRMETEvolvingConditionType----------------//
		/*
		 * AIRMETEvolvingConditionPropertyType evolvingTypeProp = iwxxmHelpers.getOfIWXXM()
		 * .createAIRMETEvolvingConditionPropertyType();
		 * evolvingTypeProp.setAIRMETEvolvingCondition(evolvingType);
		 * sicol.getMember().add(evolvingTypeProp);
		 */

		// ---------------AIRMETEvolvingConditionType(PhenomenonObservation)----------------//
		if (translatedAirmet.getPhenomenonDescription().getPhenomenonObservation().name().equals("FCST")) {
			asType.setTimeIndicator(TimeIndicatorType.FORECAST);
		} else if (translatedAirmet.getPhenomenonDescription().getPhenomenonObservation().name().equals("OBS")) {
			asType.setTimeIndicator(TimeIndicatorType.OBSERVATION);
		}
		return asType;

	}

	/** stub for coordinates */
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

	/** returns section for airspaceVolumeDescription 
	 * @throws WMORegisterException */
	public AirspaceVolumeType createAirSpaceVolumeSection(List<Double> coords) throws WMORegisterException {

		AirspaceVolumeType airspaceVolumeType = iwxxmHelpers.getOfAIXM().createAirspaceVolumeType();
		airspaceVolumeType.setId(iwxxmHelpers.generateUUIDv4("airspace-" + translatedAirmet.getIcaoCode()));

		// lower limit if exists and check if on surface
		if (translatedAirmet.getVerticalLocation().getBottomFL().isPresent()) {
			ValDistanceVerticalType bottomFlType = iwxxmHelpers.getOfAIXM().createValDistanceVerticalType();

			bottomFlType.setUom("FL");
			bottomFlType.setValue(String.valueOf(translatedAirmet.getVerticalLocation().getBottomFL().get()));
			JAXBElement<ValDistanceVerticalType> bottomFlSection = iwxxmHelpers.getOfAIXM()
					.createAirspaceLayerTypeLowerLimit(bottomFlType);
			airspaceVolumeType.setLowerLimit(bottomFlSection);

			CodeVerticalReferenceType valueCode = iwxxmHelpers.getOfAIXM().createCodeVerticalReferenceType();
			valueCode.setValue("STD");
			JAXBElement<CodeVerticalReferenceType> vertCodeType = iwxxmHelpers.getOfAIXM()
					.createAirspaceLayerTypeLowerLimitReference(valueCode);
			airspaceVolumeType.setLowerLimitReference(vertCodeType);
		}

		// upper limit if exists
		if (translatedAirmet.getVerticalLocation().getTopFL().isPresent()) {

			ValDistanceVerticalType topFlType = iwxxmHelpers.getOfAIXM().createValDistanceVerticalType();
			topFlType.setUom("FL");

			if (translatedAirmet.getVerticalLocation().isTopMarginAboveFl()) {

				ValDistanceVerticalType unknownType = iwxxmHelpers.getOfAIXM().createValDistanceVerticalType();
				unknownType.setNilReason(iwxxmHelpers.getNilRegister().getWMOUrlByCode(WMONilReasonRegister.NIL_REASON_UNKNOWN));
				JAXBElement<ValDistanceVerticalType> unknownSection = iwxxmHelpers.getOfAIXM()
						.createAirspaceLayerTypeUpperLimit(unknownType);
				airspaceVolumeType.setMaximumLimit(unknownSection);
				topFlType.setValue(String.valueOf(translatedAirmet.getVerticalLocation().getTopFL().get()));
			}
			// Below top fl - set max as top fl, set upper as unknown
			else if (translatedAirmet.getVerticalLocation().isTopMarginBelowFl()) {

				ValDistanceVerticalType topMaxType = iwxxmHelpers.getOfAIXM().createValDistanceVerticalType();
				topMaxType.setValue(String.valueOf(translatedAirmet.getVerticalLocation().getTopFL().get()));

				JAXBElement<ValDistanceVerticalType> topMaxSection = iwxxmHelpers.getOfAIXM()
						.createAirspaceLayerTypeUpperLimit(topMaxType);
				airspaceVolumeType.setMaximumLimit(topMaxSection);

				topFlType.setNilReason(iwxxmHelpers.getNilRegister().getWMOUrlByCode(WMONilReasonRegister.NIL_REASON_UNKNOWN));
			} else {
				topFlType.setValue(String.valueOf(translatedAirmet.getVerticalLocation().getTopFL().get()));
			}

			JAXBElement<ValDistanceVerticalType> topFlSection = iwxxmHelpers.getOfAIXM()
					.createAirspaceLayerTypeUpperLimit(topFlType);
			airspaceVolumeType.setUpperLimit(topFlSection);

			CodeVerticalReferenceType valueCode = iwxxmHelpers.getOfAIXM().createCodeVerticalReferenceType();
			valueCode.setValue("STD");
			JAXBElement<CodeVerticalReferenceType> vertCodeType = iwxxmHelpers.getOfAIXM()
					.createAirspaceLayerTypeUpperLimitReference(valueCode);
			airspaceVolumeType.setUpperLimitReference(vertCodeType);

		}

		// if height is observed in feet or meters and low bound is on surface
		if (translatedAirmet.getVerticalLocation().isBottomMarginOnSurface()
				&& translatedAirmet.getVerticalLocation().getTopMarginMeters().isPresent()) {
			ValDistanceVerticalType bottomFlType = iwxxmHelpers.getOfAIXM().createValDistanceVerticalType();
			bottomFlType.setUom(translatedAirmet.getVerticalLocation().getUnits().getStringValue());
			bottomFlType.setValue("0");
			JAXBElement<ValDistanceVerticalType> bottomFlSection = iwxxmHelpers.getOfAIXM()
					.createAirspaceLayerTypeUpperLimit(bottomFlType);
			airspaceVolumeType.setLowerLimit(bottomFlSection);

			ValDistanceVerticalType topHeightType = iwxxmHelpers.getOfAIXM().createValDistanceVerticalType();
			topHeightType.setUom(translatedAirmet.getVerticalLocation().getUnits().getStringValue());
			topHeightType.setValue(String.valueOf(translatedAirmet.getVerticalLocation().getTopMarginMeters().get()));
			JAXBElement<ValDistanceVerticalType> bottomHeightSection = iwxxmHelpers.getOfAIXM()
					.createAirspaceLayerTypeUpperLimit(topHeightType);
			airspaceVolumeType.setLowerLimit(bottomHeightSection);

			// set flag from SURFACE - see aixm 5.1.1
			CodeVerticalReferenceType valueCodeSfc = iwxxmHelpers.getOfAIXM().createCodeVerticalReferenceType();
			valueCodeSfc.setValue("SFC");
			JAXBElement<CodeVerticalReferenceType> vertCodeTypeSfc = iwxxmHelpers.getOfAIXM()
					.createAirspaceLayerTypeUpperLimitReference(valueCodeSfc);

			CodeVerticalReferenceType valueCodeStd = iwxxmHelpers.getOfAIXM().createCodeVerticalReferenceType();
			valueCodeSfc.setValue("STD");
			JAXBElement<CodeVerticalReferenceType> vertCodeTypeStd = iwxxmHelpers.getOfAIXM()
					.createAirspaceLayerTypeUpperLimitReference(valueCodeStd);

			airspaceVolumeType.setUpperLimitReference(vertCodeTypeStd);
			airspaceVolumeType.setLowerLimitReference(vertCodeTypeSfc);
		}

		// create projection
		SurfacePropertyType surfaceSection = iwxxmHelpers.getOfAIXM().createSurfacePropertyType();
		SurfaceType sfType = iwxxmHelpers.getOfAIXM().createSurfaceType();
		sfType.getAxisLabels().add("Lat");
		sfType.getAxisLabels().add("Long");
		sfType.setSrsDimension(BigInteger.valueOf(2));
		sfType.setSrsName("http://www.opengis.net/def/crs/EPSG/0/4326");
		sfType.setId(iwxxmHelpers.generateUUIDv4("surface-" + translatedAirmet.getIcaoCode()));

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
		airspaceVolumeType.setHorizontalProjection(spt);

		return airspaceVolumeType;

	}

	/** Get link for WMO register record for the phenomena 
	 * @throws WMORegisterException */
	public AeronauticalAreaWeatherPhenomenonType setAeronauticalSignificantWeatherPhenomenonType() throws WMORegisterException {
		AeronauticalAreaWeatherPhenomenonType typePhen = iwxxmHelpers.getOfIWXXM()
				.createAeronauticalAreaWeatherPhenomenonType();
		String phenomenon = translatedAirmet.getPhenomenonDescription().getPhenomenonForLink();
		
		String link = iwxxmHelpers.getAirWxPhenomenaRegister()
				.getWMOUrlByCode(translatedAirmet.getPhenomenonDescription().getPhenomenonForLink());
		typePhen.setHref(link);
		return typePhen;

	}

	public UnitPropertyType createUnitPropertyTypeNode(String icaoCode, String firname, String type,
			String interpretation) {
		UnitPropertyType pt = iwxxmHelpers.getOfIWXXM().createUnitPropertyType();

		UnitType ut = iwxxmHelpers.getOfAIXM().createUnitType();
		ut.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s", icaoCode)));

		pt.setUnit(ut);

		UnitTimeSlicePropertyType tspt = iwxxmHelpers.getOfAIXM().createUnitTimeSlicePropertyType();
		UnitTimeSliceType tst = iwxxmHelpers.getOfAIXM().createUnitTimeSliceType();
		tst.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-ts", icaoCode)));
		tst.setInterpretation(interpretation);

		// tst.setValidTime(value);

		tspt.setUnitTimeSlice(tst);

		/**
		 * <aixm:interpretation>SNAPSHOT</aixm:interpretation>
		 * <aixm:type>OTHER:FIR_UIR</aixm:type> <aixm:designator>YUDD</aixm:designator>
		 * <aixm:name>SHANLON FIR/UIR</aixm:name>
		 *
		 *
		 */

		// add name
		TextNameType nType = iwxxmHelpers.getOfAIXM().createTextNameType();
		nType.setValue(firname + " " + type);
		JAXBElement<TextNameType> ntType = iwxxmHelpers.getOfAIXM().createAirspaceTimeSliceTypeName(nType);
		tst.getRest().add(ntType);

		// add type
		CodeAirspaceType asType = iwxxmHelpers.getOfAIXM().createCodeAirspaceType();
		asType.setValue(type);
		JAXBElement<CodeAirspaceType> astType = iwxxmHelpers.getOfAIXM().createAirspaceTimeSliceTypeType(asType);
		tst.getRest().add(astType);

		// add designator
		CodeAirspaceDesignatorType cadType = iwxxmHelpers.getOfAIXM().createCodeAirspaceDesignatorType();
		cadType.setValue(firname);
		JAXBElement<CodeAirspaceDesignatorType> cast = iwxxmHelpers.getOfAIXM()
				.createAirspaceTimeSliceTypeDesignator(cadType);
		tst.getRest().add(cast);

		ut.getTimeSlice().add(tspt);

		return pt;
	}

	public AirspacePropertyType createAirspacePropertyTypeNode(String icaoCode, String firname, String type,
			String interpretation) {

		AirspacePropertyType pt = iwxxmHelpers.getOfIWXXM().createAirspacePropertyType();
		AirspaceType ast = iwxxmHelpers.getOfAIXM().createAirspaceType();
		ast.setId(iwxxmHelpers.generateUUIDv4(String.format("airspace-%s", icaoCode)));
		pt.setAirspace(ast);

		AirspaceTimeSlicePropertyType tsp = iwxxmHelpers.getOfAIXM().createAirspaceTimeSlicePropertyType();
		AirspaceTimeSliceType ts = iwxxmHelpers.getOfAIXM().createAirspaceTimeSliceType();
		ts.setId(iwxxmHelpers.generateUUIDv4(String.format("airspace-%s-ts", icaoCode)));

		ts.setInterpretation(interpretation);

		/**
		 * <aixm:interpretation>SNAPSHOT</aixm:interpretation>
		 * <aixm:type>OTHER:FIR_UIR</aixm:type> <aixm:designator>YUDD</aixm:designator>
		 * <aixm:name>SHANLON FIR/UIR</aixm:name>
		 *
		 *
		 */

		// add name
		TextNameType nType = iwxxmHelpers.getOfAIXM().createTextNameType();
		nType.setValue(firname);
		JAXBElement<TextNameType> ntType = iwxxmHelpers.getOfAIXM().createAirspaceTimeSliceTypeName(nType);
		ts.getRest().add(ntType);

		// add type
		CodeAirspaceType asType = iwxxmHelpers.getOfAIXM().createCodeAirspaceType();
		asType.setValue(type);
		JAXBElement<CodeAirspaceType> astType = iwxxmHelpers.getOfAIXM().createAirspaceTimeSliceTypeType(asType);
		ts.getRest().add(astType);

		// add designator
		CodeAirspaceDesignatorType cadType = iwxxmHelpers.getOfAIXM().createCodeAirspaceDesignatorType();
		cadType.setValue(icaoCode);
		JAXBElement<CodeAirspaceDesignatorType> cast = iwxxmHelpers.getOfAIXM()
				.createAirspaceTimeSliceTypeDesignator(cadType);
		ts.getRest().add(cast);

		tsp.setAirspaceTimeSlice(ts);

		ast.getTimeSlice().add(tsp);

		return pt;
	}

	@Override
	public AIRMETType addTranslationCentreHeader(AIRMETType report) throws DatatypeConfigurationException {
		report = iwxxmHelpers.addTranslationCentreHeaders(report, DateTime.now(), DateTime.now(),
				UUID.randomUUID().toString(), "UUWV", "Moscow, RU");
		// report.setTranslationFailedTAC("");
		return report;
	}

	@Override
	public String marshallMessageToXML(AIRMETType reportType) throws JAXBException, UnsupportedEncodingException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		JAXBContext jaxbContext = JAXBContext.newInstance(AIRMETType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, UriConstants.GLOBAL_SCHEMAS_LOCATION_V3);

		jaxbMarshaller.setProperty(StringConstants.SUN_JAXB_NAMESPACE_MAPPING_PROPERTY_NAME, new NamespaceMapper());

		JAXBElement<AIRMETType> airmetRootElement = iwxxmHelpers.getOfIWXXM().createAIRMET(reportType);

		jaxbMarshaller.marshal(airmetRootElement, stream);

		return stream.toString("UTF-8");
	}

	@Override
	public IWXXM31Helpers getHelper() {
		return iwxxmHelpers;
	}
	
	@Override
	public AIRMETConverterV3 withHelper(IWXXM31Helpers helper) {
		this.iwxxmHelpers = helper;
		return this;
		
	}
	

}
