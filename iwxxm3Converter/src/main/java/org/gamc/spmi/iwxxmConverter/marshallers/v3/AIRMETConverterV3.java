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
import org.gamc.spmi.iwxxmConverter.iwxxmenums.ANGLE_UNITS;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
import org.joda.time.DateTime;

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

public class AIRMETConverterV3 implements TacConverter<AIRMETTacMessage, AIRMETType> {

	private TreeMap<String, String> createdRunways = new TreeMap<>();
	String airTrafficUnit = "FIC";
	String watchOfficeType = "MWO";
	String firType = "OTHER:FIR_UIR";
	String interpretation = "SNAPSHOT";

	IWXXM31Helpers iwxxmHelpers = new IWXXM31Helpers();
	private String dateTime = "";
	private String dateTimePosition = "";
	private AIRMETTacMessage translatedAirmet;

	@Override
	public String convertTacToXML(String tac)
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException {
		createdRunways.clear();

		AIRMETTacMessage airmetMessage = new AIRMETTacMessage(tac);

		AIRMETType result;

		try {
			airmetMessage.parseMessage();
			result = convertMessage(airmetMessage);
		} catch (ParsingException pa) {
			result = IWXXM31Helpers.ofIWXXM.createAIRMETType();
			result.setTranslationFailedTAC(tac);

		}

		String xmlResult = marshallMessageToXML(result);

		return xmlResult;
	}

	@Override
	public AIRMETType convertMessage(AIRMETTacMessage translatedMessage)
			throws DatatypeConfigurationException, UnsupportedEncodingException, JAXBException, ParsingException {
		this.translatedAirmet = translatedMessage;
		AIRMETType airmetRootTag = IWXXM31Helpers.ofIWXXM.createAIRMETType();

		StringOrRefType refTacString = IWXXM31Helpers.ofGML.createStringOrRefType();
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

		StringWithNilReasonType seq = iwxxmHelpers.ofIWXXM.createStringWithNilReasonType();
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
			AIRMETEvolvingConditionCollectionPropertyType analists = IWXXM31Helpers.ofIWXXM
					.createAIRMETEvolvingConditionCollectionPropertyType();
			analists.setAIRMETEvolvingConditionCollection(setAssociationRoleType());
			airmetRootTag.setAnalysis(analists);
			break;
		}

		// create XML representation
		return airmetRootTag;
	}

	public AIRMETEvolvingConditionCollectionType setAssociationRoleType() {
		// ---------------AirspaceVolumePropertyType----------------//
		AirspaceVolumePropertyType air = IWXXM31Helpers.ofIWXXM.createAirspaceVolumePropertyType();
		air.setAirspaceVolume(createAirSpaceVolumeSection(getListOfCoords()));
		// ---------------AIRMETEvolvingConditionType----------------//
		AIRMETEvolvingConditionType evolvingType = IWXXM31Helpers.ofIWXXM.createAIRMETEvolvingConditionType();
		evolvingType.setGeometry(air);
		AIRMETEvolvingConditionPropertyType sicol = IWXXM31Helpers.ofIWXXM.createAIRMETEvolvingConditionPropertyType();
		sicol.setAIRMETEvolvingCondition(evolvingType);
		// ---------------Association Role----------------//
		AIRMETEvolvingConditionCollectionType asType = IWXXM31Helpers.ofIWXXM
				.createAIRMETEvolvingConditionCollectionType();
		asType.getMember().add(sicol);
		// ---------------AIRMETEvolvingConditionType----------------//
		/*
		 * AIRMETEvolvingConditionCollectionType sicol = IWXXM31Helpers.ofIWXXM
		 * .createAIRMETEvolvingConditionCollectionType();
		 * JAXBElement<AIRMETEvolvingConditionCollectionType> evolvingAr =
		 * IWXXM31Helpers.ofIWXXM .createAIRMETEvolvingConditionCollection(sicol);
		 * AIRMETEvolvingConditionType evolvingType =
		 * IWXXM31Helpers.ofIWXXM.createAIRMETEvolvingConditionType();
		 * evolvingType.setGeometry(air);
		 */
		// ---------------AIRMETEvolvingConditionType(Speed-Motion-Id-Intencity)----------------//
		SpeedType speedType = IWXXM31Helpers.ofGML.createSpeedType();

		if (translatedAirmet.getPhenomenonDescription().getMovingSection() != null
				&& translatedAirmet.getPhenomenonDescription().getMovingSection().isMoving()) {
			AngleWithNilReasonType motion = IWXXM31Helpers.ofIWXXM.createAngleWithNilReasonType();
			JAXBElement<AngleWithNilReasonType> dirMo = IWXXM31Helpers.ofIWXXM
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
		 * AIRMETEvolvingConditionPropertyType evolvingTypeProp = IWXXM31Helpers.ofIWXXM
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

	/** returns section for airspaceVolumeDescription */
	public AirspaceVolumeType createAirSpaceVolumeSection(List<Double> coords) {

		AirspaceVolumeType airspaceVolumeType = IWXXM31Helpers.ofAIXM.createAirspaceVolumeType();
		airspaceVolumeType.setId(iwxxmHelpers.generateUUIDv4("airspace-" + translatedAirmet.getIcaoCode()));

		// lower limit if exists and check if on surface
		if (translatedAirmet.getVerticalLocation().getBottomFL().isPresent()) {
			ValDistanceVerticalType bottomFlType = IWXXM31Helpers.ofAIXM.createValDistanceVerticalType();

			bottomFlType.setUom("FL");
			bottomFlType.setValue(String.valueOf(translatedAirmet.getVerticalLocation().getBottomFL().get()));
			JAXBElement<ValDistanceVerticalType> bottomFlSection = IWXXM31Helpers.ofAIXM
					.createAirspaceLayerTypeLowerLimit(bottomFlType);
			airspaceVolumeType.setLowerLimit(bottomFlSection);

			CodeVerticalReferenceType valueCode = IWXXM31Helpers.ofAIXM.createCodeVerticalReferenceType();
			valueCode.setValue("STD");
			JAXBElement<CodeVerticalReferenceType> vertCodeType = IWXXM31Helpers.ofAIXM
					.createAirspaceLayerTypeLowerLimitReference(valueCode);
			airspaceVolumeType.setLowerLimitReference(vertCodeType);
		}

		// upper limit if exists
		if (translatedAirmet.getVerticalLocation().getTopFL().isPresent()) {

			ValDistanceVerticalType topFlType = IWXXM31Helpers.ofAIXM.createValDistanceVerticalType();
			topFlType.setUom("FL");

			if (translatedAirmet.getVerticalLocation().isTopMarginAboveFl()) {

				ValDistanceVerticalType unknownType = IWXXM31Helpers.ofAIXM.createValDistanceVerticalType();
				unknownType.setNilReason(iwxxmHelpers.getNilRegister().getWMOUrlByCode("unknown"));
				JAXBElement<ValDistanceVerticalType> unknownSection = IWXXM31Helpers.ofAIXM
						.createAirspaceLayerTypeUpperLimit(unknownType);
				airspaceVolumeType.setMaximumLimit(unknownSection);
				topFlType.setValue(String.valueOf(translatedAirmet.getVerticalLocation().getTopFL().get()));
			}
			// Below top fl - set max as top fl, set upper as unknown
			else if (translatedAirmet.getVerticalLocation().isTopMarginBelowFl()) {

				ValDistanceVerticalType topMaxType = IWXXM31Helpers.ofAIXM.createValDistanceVerticalType();
				topMaxType.setValue(String.valueOf(translatedAirmet.getVerticalLocation().getTopFL().get()));

				JAXBElement<ValDistanceVerticalType> topMaxSection = IWXXM31Helpers.ofAIXM
						.createAirspaceLayerTypeUpperLimit(topMaxType);
				airspaceVolumeType.setMaximumLimit(topMaxSection);

				topFlType.setNilReason(iwxxmHelpers.getNilRegister().getWMOUrlByCode("unknown"));
			} else {
				topFlType.setValue(String.valueOf(translatedAirmet.getVerticalLocation().getTopFL().get()));
			}

			JAXBElement<ValDistanceVerticalType> topFlSection = IWXXM31Helpers.ofAIXM
					.createAirspaceLayerTypeUpperLimit(topFlType);
			airspaceVolumeType.setUpperLimit(topFlSection);

			CodeVerticalReferenceType valueCode = IWXXM31Helpers.ofAIXM.createCodeVerticalReferenceType();
			valueCode.setValue("STD");
			JAXBElement<CodeVerticalReferenceType> vertCodeType = IWXXM31Helpers.ofAIXM
					.createAirspaceLayerTypeUpperLimitReference(valueCode);
			airspaceVolumeType.setUpperLimitReference(vertCodeType);

		}

		// if height is observed in feet or meters and low bound is on surface
		if (translatedAirmet.getVerticalLocation().isBottomMarginOnSurface()
				&& translatedAirmet.getVerticalLocation().getTopMarginMeters().isPresent()) {
			ValDistanceVerticalType bottomFlType = IWXXM31Helpers.ofAIXM.createValDistanceVerticalType();
			bottomFlType.setUom(translatedAirmet.getVerticalLocation().getUnits().getStringValue());
			bottomFlType.setValue("0");
			JAXBElement<ValDistanceVerticalType> bottomFlSection = IWXXM31Helpers.ofAIXM
					.createAirspaceLayerTypeUpperLimit(bottomFlType);
			airspaceVolumeType.setLowerLimit(bottomFlSection);

			ValDistanceVerticalType topHeightType = IWXXM31Helpers.ofAIXM.createValDistanceVerticalType();
			topHeightType.setUom(translatedAirmet.getVerticalLocation().getUnits().getStringValue());
			topHeightType.setValue(String.valueOf(translatedAirmet.getVerticalLocation().getTopMarginMeters().get()));
			JAXBElement<ValDistanceVerticalType> bottomHeightSection = IWXXM31Helpers.ofAIXM
					.createAirspaceLayerTypeUpperLimit(topHeightType);
			airspaceVolumeType.setLowerLimit(bottomHeightSection);

			// set flag from SURFACE - see aixm 5.1.1
			CodeVerticalReferenceType valueCodeSfc = IWXXM31Helpers.ofAIXM.createCodeVerticalReferenceType();
			valueCodeSfc.setValue("SFC");
			JAXBElement<CodeVerticalReferenceType> vertCodeTypeSfc = IWXXM31Helpers.ofAIXM
					.createAirspaceLayerTypeUpperLimitReference(valueCodeSfc);

			CodeVerticalReferenceType valueCodeStd = IWXXM31Helpers.ofAIXM.createCodeVerticalReferenceType();
			valueCodeSfc.setValue("STD");
			JAXBElement<CodeVerticalReferenceType> vertCodeTypeStd = IWXXM31Helpers.ofAIXM
					.createAirspaceLayerTypeUpperLimitReference(valueCodeStd);

			airspaceVolumeType.setUpperLimitReference(vertCodeTypeStd);
			airspaceVolumeType.setLowerLimitReference(vertCodeTypeSfc);
		}

		// create projection
		SurfacePropertyType surfaceSection = IWXXM31Helpers.ofAIXM.createSurfacePropertyType();
		SurfaceType sfType = IWXXM31Helpers.ofAIXM.createSurfaceType();
		sfType.getAxisLabels().add("Lat");
		sfType.getAxisLabels().add("Long");
		sfType.setSrsDimension(BigInteger.valueOf(2));
		sfType.setSrsName("http://www.opengis.net/def/crs/EPSG/0/4326");
		sfType.setId(iwxxmHelpers.generateUUIDv4("surface-" + translatedAirmet.getIcaoCode()));

		// add gml patches
		SurfacePatchArrayPropertyType patchArray = IWXXM31Helpers.ofGML.createSurfacePatchArrayPropertyType();

		// create polygon
		PolygonPatchType patchType = IWXXM31Helpers.ofGML.createPolygonPatchType();
		AbstractRingPropertyType ringType = IWXXM31Helpers.ofGML.createAbstractRingPropertyType();
		LinearRingType linearRingType = IWXXM31Helpers.ofGML.createLinearRingType();

		// fill polygon with coords
		DirectPositionListType dpListType = IWXXM31Helpers.ofGML.createDirectPositionListType();
		dpListType.getValue().addAll(coords);
		linearRingType.setPosList(dpListType);

		// put polygon in the envelope
		JAXBElement<LinearRingType> lrPt = IWXXM31Helpers.ofGML.createLinearRing(linearRingType);
		ringType.setAbstractRing(lrPt);
		patchType.setExterior(ringType);

		JAXBElement<PolygonPatchType> patch = IWXXM31Helpers.ofGML.createPolygonPatch(patchType);
		patchArray.getAbstractSurfacePatch().add(patch);

		JAXBElement<SurfacePatchArrayPropertyType> pta = IWXXM31Helpers.ofGML.createPatches(patchArray);

		sfType.setPatches(pta);

		JAXBElement<SurfaceType> syrfaceElement = IWXXM31Helpers.ofAIXM.createSurface(sfType);
		surfaceSection.setSurface(syrfaceElement);
		JAXBElement<SurfacePropertyType> spt = IWXXM31Helpers.ofAIXM
				.createAirspaceVolumeTypeHorizontalProjection(surfaceSection);
		// create aixm:horizontalProjection
		airspaceVolumeType.setHorizontalProjection(spt);

		return airspaceVolumeType;

	}

	/** Get link for WMO register record for the phenomena */
	public AeronauticalAreaWeatherPhenomenonType setAeronauticalSignificantWeatherPhenomenonType() {
		AeronauticalAreaWeatherPhenomenonType typePhen = IWXXM31Helpers.ofIWXXM
				.createAeronauticalAreaWeatherPhenomenonType();
		String link = iwxxmHelpers.getSigWxPhenomenaRegister()
				.getWMOUrlByCode(translatedAirmet.getPhenomenonDescription().getPhenomenonForLink());
		typePhen.setHref(link);
		return typePhen;

	}

	public UnitPropertyType createUnitPropertyTypeNode(String icaoCode, String firname, String type,
			String interpretation) {
		UnitPropertyType pt = IWXXM31Helpers.ofIWXXM.createUnitPropertyType();

		UnitType ut = IWXXM31Helpers.ofAIXM.createUnitType();
		ut.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s", icaoCode)));

		pt.setUnit(ut);

		UnitTimeSlicePropertyType tspt = IWXXM31Helpers.ofAIXM.createUnitTimeSlicePropertyType();
		UnitTimeSliceType tst = IWXXM31Helpers.ofAIXM.createUnitTimeSliceType();
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
		TextNameType nType = IWXXM31Helpers.ofAIXM.createTextNameType();
		nType.setValue(firname + " " + type);
		JAXBElement<TextNameType> ntType = IWXXM31Helpers.ofAIXM.createAirspaceTimeSliceTypeName(nType);
		tst.getRest().add(ntType);

		// add type
		CodeAirspaceType asType = IWXXM31Helpers.ofAIXM.createCodeAirspaceType();
		asType.setValue(type);
		JAXBElement<CodeAirspaceType> astType = IWXXM31Helpers.ofAIXM.createAirspaceTimeSliceTypeType(asType);
		tst.getRest().add(astType);

		// add designator
		CodeAirspaceDesignatorType cadType = IWXXM31Helpers.ofAIXM.createCodeAirspaceDesignatorType();
		cadType.setValue(firname);
		JAXBElement<CodeAirspaceDesignatorType> cast = IWXXM31Helpers.ofAIXM
				.createAirspaceTimeSliceTypeDesignator(cadType);
		tst.getRest().add(cast);

		ut.getTimeSlice().add(tspt);

		return pt;
	}

	public AirspacePropertyType createAirspacePropertyTypeNode(String icaoCode, String firname, String type,
			String interpretation) {

		AirspacePropertyType pt = IWXXM31Helpers.ofIWXXM.createAirspacePropertyType();
		AirspaceType ast = IWXXM31Helpers.ofAIXM.createAirspaceType();
		ast.setId(iwxxmHelpers.generateUUIDv4(String.format("airspace-%s", icaoCode)));
		pt.setAirspace(ast);

		AirspaceTimeSlicePropertyType tsp = IWXXM31Helpers.ofAIXM.createAirspaceTimeSlicePropertyType();
		AirspaceTimeSliceType ts = IWXXM31Helpers.ofAIXM.createAirspaceTimeSliceType();
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
		TextNameType nType = IWXXM31Helpers.ofAIXM.createTextNameType();
		nType.setValue(firname);
		JAXBElement<TextNameType> ntType = IWXXM31Helpers.ofAIXM.createAirspaceTimeSliceTypeName(nType);
		ts.getRest().add(ntType);

		// add type
		CodeAirspaceType asType = IWXXM31Helpers.ofAIXM.createCodeAirspaceType();
		asType.setValue(type);
		JAXBElement<CodeAirspaceType> astType = IWXXM31Helpers.ofAIXM.createAirspaceTimeSliceTypeType(asType);
		ts.getRest().add(astType);

		// add designator
		CodeAirspaceDesignatorType cadType = IWXXM31Helpers.ofAIXM.createCodeAirspaceDesignatorType();
		cadType.setValue(icaoCode);
		JAXBElement<CodeAirspaceDesignatorType> cast = IWXXM31Helpers.ofAIXM
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

		JAXBElement<AIRMETType> airmetRootElement = IWXXM31Helpers.ofIWXXM.createAIRMET(reportType);

		jaxbMarshaller.marshal(airmetRootElement, stream);

		return stream.toString("UTF-8");
	}

}
