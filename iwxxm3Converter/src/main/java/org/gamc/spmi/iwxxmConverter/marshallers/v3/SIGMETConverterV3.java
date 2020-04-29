package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.TreeMap;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.common.CoordPoint;
import org.gamc.spmi.iwxxmConverter.common.MessageStatusType;
import org.gamc.spmi.iwxxmConverter.common.NamespaceMapper;
import org.gamc.spmi.iwxxmConverter.common.StringConstants;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.DirectionFromLine;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
import org.joda.time.DateTime;

import schemabindings31._int.icao.iwxxm._3.AbstractTimeObjectPropertyType;
import schemabindings31._int.icao.iwxxm._3.AeronauticalSignificantWeatherPhenomenonType;
import schemabindings31._int.icao.iwxxm._3.AirspacePropertyType;
import schemabindings31._int.icao.iwxxm._3.AirspaceVolumePropertyType;
import schemabindings31._int.icao.iwxxm._3.AngleWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.ExpectedIntensityChangeType;
import schemabindings31._int.icao.iwxxm._3.PermissibleUsageReasonType;
import schemabindings31._int.icao.iwxxm._3.PermissibleUsageType;
import schemabindings31._int.icao.iwxxm._3.ReportPropertyType;
import schemabindings31._int.icao.iwxxm._3.ReportStatusType;
import schemabindings31._int.icao.iwxxm._3.SIGMETEvolvingConditionCollectionPropertyType;
import schemabindings31._int.icao.iwxxm._3.SIGMETEvolvingConditionCollectionType;
import schemabindings31._int.icao.iwxxm._3.SIGMETEvolvingConditionPropertyType;
import schemabindings31._int.icao.iwxxm._3.SIGMETEvolvingConditionType;
import schemabindings31._int.icao.iwxxm._3.SIGMETType;
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
import schemabindings31.aero.aixm.schema._5_1.CodeYesNoType;
import schemabindings31.aero.aixm.schema._5_1.CurvePropertyType;
import schemabindings31.aero.aixm.schema._5_1.SurfacePropertyType;
import schemabindings31.aero.aixm.schema._5_1.SurfaceType;
import schemabindings31.aero.aixm.schema._5_1.TextNameType;
import schemabindings31.aero.aixm.schema._5_1.UnitTimeSlicePropertyType;
import schemabindings31.aero.aixm.schema._5_1.UnitTimeSliceType;
import schemabindings31.aero.aixm.schema._5_1.UnitType;
import schemabindings31.aero.aixm.schema._5_1.ValDistanceVerticalType;
import schemabindings31.net.opengis.gml.v_3_2_1.AbstractRingPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.AbstractRingType;
import schemabindings31.net.opengis.gml.v_3_2_1.AbstractSurfacePatchType;
import schemabindings31.net.opengis.gml.v_3_2_1.AbstractTimeComplexType;
import schemabindings31.net.opengis.gml.v_3_2_1.AbstractTimeObjectType;
import schemabindings31.net.opengis.gml.v_3_2_1.AssociationRoleType;
import schemabindings31.net.opengis.gml.v_3_2_1.DirectPositionListType;
import schemabindings31.net.opengis.gml.v_3_2_1.LinearRingType;
import schemabindings31.net.opengis.gml.v_3_2_1.PolygonPatchType;
import schemabindings31.net.opengis.gml.v_3_2_1.SpeedType;
import schemabindings31.net.opengis.gml.v_3_2_1.StringOrRefType;
import schemabindings31.net.opengis.gml.v_3_2_1.SurfacePatchArrayPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimeInstantPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimeInstantType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimePeriodPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimePeriodType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimePositionType;

public class SIGMETConverterV3 implements TacConverter<SIGMETTacMessage, SIGMETType> {
	private TreeMap<String, String> createdRunways = new TreeMap<>();
	String airTrafficUnit = "FIC";
	String watchOfficeType = "MWO";
	String firType = "OTHER:FIR_UIR";
	String interpretation = "SNAPSHOT";

	IWXXM31Helpers iwxxmHelpers = new IWXXM31Helpers();
	private String dateTime = "";
	private String dateTimePosition = "";
	private SIGMETTacMessage translatedSigmet;

	@Override
	public String convertTacToXML(String tac)
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException {
		createdRunways.clear();

		SIGMETTacMessage sigmetMessage = new SIGMETTacMessage(tac);

		SIGMETType result;

		try {
			sigmetMessage.parseMessage();
			result = convertMessage(sigmetMessage);
		} catch (ParsingException pa) {
			result = IWXXM31Helpers.ofIWXXM.createSIGMETType();
			result.setTranslationFailedTAC(tac);

		}

		String xmlResult = marshallMessageToXML(result);

		return xmlResult;
	}

	@Override
	public SIGMETType convertMessage(SIGMETTacMessage translatedMessage)
			throws DatatypeConfigurationException, UnsupportedEncodingException, JAXBException, ParsingException {
		this.translatedSigmet = translatedMessage;
		SIGMETType sigmetRootTag = IWXXM31Helpers.ofIWXXM.createSIGMETType();

		StringOrRefType refTacString = IWXXM31Helpers.ofGML.createStringOrRefType();
		refTacString.setValue(translatedMessage.getInitialTacString());
		sigmetRootTag.setDescription(refTacString);

		dateTime = translatedMessage.getMessageIssueDateTime().toString(iwxxmHelpers.getDateTimeFormat()) + "Z";
		dateTimePosition = translatedMessage.getMessageIssueDateTime().toString(iwxxmHelpers.getDateTimeISOFormat());

		// Id with ICAO code and current timestamp
		sigmetRootTag.setId(
				iwxxmHelpers.generateUUIDv4(String.format("sigmet-%s-%s", translatedSigmet.getIcaoCode(), dateTime)));

		// sigmetRootTag.setAutomatedStation(true);

		// Set NON_OPERATIONAL and TEST properties.
		sigmetRootTag.setPermissibleUsage(PermissibleUsageType.NON_OPERATIONAL);
		sigmetRootTag.setPermissibleUsageReason(PermissibleUsageReasonType.TEST);

		// Some description
		sigmetRootTag.setPermissibleUsageSupplementary("SIGMET composing test using JAXB");

		// COR, NIL, NORMAL
		switch (translatedSigmet.getMessageStatusType()) {
		case MISSING:
			sigmetRootTag.setReportStatus(null);
			break;
		case CORRECTION:
			sigmetRootTag.setReportStatus(ReportStatusType.CORRECTION);
			break;
		default:
			sigmetRootTag.setReportStatus(ReportStatusType.NORMAL);
		}
		sigmetRootTag = addTranslationCentreHeader(sigmetRootTag);
		TimeInstantPropertyType obsTimeType = iwxxmHelpers.createTimeInstantPropertyTypeForDateTime(
				translatedSigmet.getMessageIssueDateTime(), translatedSigmet.getIcaoCode(), "issue");
		sigmetRootTag.setIssueTime(obsTimeType);

		StringWithNilReasonType seq = iwxxmHelpers.ofIWXXM.createStringWithNilReasonType();
		seq.setValue(translatedSigmet.getSigmetNumber());

		sigmetRootTag.setSequenceNumber(seq);

		sigmetRootTag.setIssuingAirTrafficServicesUnit(createUnitPropertyTypeNode(translatedSigmet.getIcaoCode(),
				translatedSigmet.getIcaoCode(), airTrafficUnit, interpretation));
		sigmetRootTag.setIssuingAirTrafficServicesRegion(createAirspacePropertyTypeNode(translatedSigmet.getIcaoCode(),
				translatedSigmet.getFirName(), firType, interpretation));

		sigmetRootTag.setOriginatingMeteorologicalWatchOffice(createUnitPropertyTypeNode(translatedSigmet.getIcaoCode(),
				translatedSigmet.getWatchOffice(), watchOfficeType, interpretation));

		sigmetRootTag.setValidPeriod(iwxxmHelpers.createTimePeriod(translatedSigmet.getIcaoCode(),
				translatedSigmet.getValidFrom(), translatedSigmet.getValidTo()));
		sigmetRootTag.setPhenomenon(setAeronauticalSignificantWeatherPhenomenonType());
		if (translatedSigmet.messageStatusType != MessageStatusType.CANCEL) {
			sigmetRootTag.getAnalysis().add(setAssociationRoleType());
		} else {
			ReportPropertyType reprop = IWXXM31Helpers.ofIWXXM.createReportPropertyType();
			sigmetRootTag.setReportStatus(reprop.getReport().getValue().getReportStatus());
		}
		// create XML representation
		return sigmetRootTag;
	}

	public AssociationRoleType setAssociationRoleType() {
		// ---------------AirspaceVolumePropertyType----------------//
		AirspaceVolumePropertyType air = IWXXM31Helpers.ofIWXXM.createAirspaceVolumePropertyType();
		air.setAirspaceVolume(setAirspaceVolume());
		// ---------------SIGMETEvolvingConditionType----------------//
		SIGMETEvolvingConditionCollectionType sicol = IWXXM31Helpers.ofIWXXM
				.createSIGMETEvolvingConditionCollectionType();
		JAXBElement<SIGMETEvolvingConditionCollectionType> evolvingAr = IWXXM31Helpers.ofIWXXM
				.createSIGMETEvolvingConditionCollection(sicol);
		SIGMETEvolvingConditionType evolvingType = IWXXM31Helpers.ofIWXXM.createSIGMETEvolvingConditionType();
		evolvingType.setGeometry(air);
		// ---------------SIGMETEvolvingConditionType(Speed-Motion-Id-Intencity)----------------//
		SpeedType speedType = IWXXM31Helpers.ofGML.createSpeedType();
		if (translatedSigmet.getPhenomenonDescription().getMovingSection() != null) {
			AngleWithNilReasonType motion = IWXXM31Helpers.ofIWXXM.createAngleWithNilReasonType();
			JAXBElement<AngleWithNilReasonType> dirMo = IWXXM31Helpers.ofIWXXM
					.createSIGMETEvolvingConditionTypeDirectionOfMotion(motion);
			motion.setUom(translatedSigmet.getPhenomenonDescription().getMovingSection().getMovingDirection().name());
			motion.setValue(translatedSigmet.getPhenomenonDescription().getMovingSection().getMovingDirection()
					.getDoubleValue());
			evolvingType.setDirectionOfMotion(dirMo);

			if (translatedSigmet.getPhenomenonDescription().getMovingSection().getSpeedUnits() != null
					&& translatedSigmet.getPhenomenonDescription().getMovingSection().getMovingSpeed() > 0) {
				speedType.setUom(translatedSigmet.getPhenomenonDescription().getMovingSection().getSpeedUnits()
						.getStringValue());
				speedType.setValue(translatedSigmet.getPhenomenonDescription().getMovingSection().getMovingSpeed());
			}
		}
		evolvingType.setSpeedOfMotion(speedType);
		evolvingType.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-ts", translatedSigmet.getIcaoCode())));
		if (translatedSigmet.getPhenomenonDescription().getIntencity().name().equals("INTSF")) {
			evolvingType.setIntensityChange(ExpectedIntensityChangeType.INTENSIFY);
		} else if (translatedSigmet.getPhenomenonDescription().getIntencity().name().equals("WKN")) {
			evolvingType.setIntensityChange(ExpectedIntensityChangeType.WEAKEN);
		} else if (translatedSigmet.getPhenomenonDescription().getIntencity().name().equals("NC")) {
			evolvingType.setIntensityChange(ExpectedIntensityChangeType.NO_CHANGE);
		}	
		sicol.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-ts", translatedSigmet.getIcaoCode())));
		// ---------------SIGMETEvolvingConditionType(Time)----------------//
		AbstractTimeObjectPropertyType absTime = IWXXM31Helpers.ofIWXXM.createAbstractTimeObjectPropertyType();
		if (translatedSigmet.getPhenomenonDescription().getPhenomenon().equals("VA")
				|| translatedSigmet.getPhenomenonDescription().getPhenomenon().equals("TC")) {
			TimeInstantType tmIn = IWXXM31Helpers.ofGML.createTimeInstantType();
			tmIn.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-ts", translatedSigmet.getIcaoCode())));
			TimePositionType tm = IWXXM31Helpers.ofGML.createTimePositionType();
			tm.getValue().add(translatedSigmet.getPhenomenonDescription().getPhenomenonTimeStamp().toString());
			tmIn.setTimePosition(tm);
			JAXBElement<TimeInstantType> elTmIn = IWXXM31Helpers.ofGML.createTimeInstant(tmIn);
			absTime.setAbstractTimeObject(elTmIn);
		} else {
			absTime.getNilReason().add("http://codes.wmo.int/common/nil/missing");
		}
		sicol.setPhenomenonTime(absTime);
		// ---------------SIGMETEvolvingConditionType----------------//
		SIGMETEvolvingConditionPropertyType evolvingTypeProp = IWXXM31Helpers.ofIWXXM
				.createSIGMETEvolvingConditionPropertyType();
		evolvingTypeProp.setSIGMETEvolvingCondition(evolvingType);
		sicol.getMember().add(evolvingTypeProp);
		// ---------------SIGMETEvolvingConditionType(PhenomenonObservation)----------------//
		if (translatedSigmet.getPhenomenonDescription().getPhenomenonObservation().name().equals("FCST")) {
			sicol.setTimeIndicator(TimeIndicatorType.FORECAST);
		} else if (translatedSigmet.getPhenomenonDescription().getPhenomenonObservation().name().equals("OBS")) {
			sicol.setTimeIndicator(TimeIndicatorType.OBSERVATION);
		}
		// ---------------Association Role----------------//
		AssociationRoleType asType = IWXXM31Helpers.ofGML.createAssociationRoleType();
		asType.setAny(evolvingAr);
		return asType;

	}

	private AirspaceVolumeType setAirspaceVolume() {
		// ---------------DIR Position----------------//
		DirectPositionListType postDir = IWXXM31Helpers.ofGML.createDirectPositionListType();
		for (DirectionFromLine dir : translatedSigmet.getHorizontalLocation().getDirectionsFromLines()) {
			postDir.getValue().add(dir.getDirection().getDoubleValue());
		}
		// ---------------Linear Ring----------------//
		LinearRingType ringAb = IWXXM31Helpers.ofGML.createLinearRingType();
		ringAb.setPosList(postDir);
		JAXBElement<LinearRingType> ringAbAr = IWXXM31Helpers.ofGML.createLinearRing(ringAb);
		AbstractRingPropertyType exType = IWXXM31Helpers.ofGML.createAbstractRingPropertyType();
		exType.setAbstractRing(ringAbAr);
		// ---------------Patches----------------//
		PolygonPatchType surfacePach = IWXXM31Helpers.ofGML.createPolygonPatchType();
		surfacePach.setExterior(exType);
		JAXBElement<PolygonPatchType> arrSurf = IWXXM31Helpers.ofGML.createPolygonPatch(surfacePach);
		SurfacePatchArrayPropertyType surPachAr = IWXXM31Helpers.ofGML.createSurfacePatchArrayPropertyType();
		surPachAr.getAbstractSurfacePatch().add(arrSurf);
		// ---------------Surface----------------//
		JAXBElement<SurfacePatchArrayPropertyType> pathPol = IWXXM31Helpers.ofGML.createPolygonPatches(surPachAr);
		BigInteger intDim = BigInteger.valueOf(2);
		SurfaceType typeSyr = IWXXM31Helpers.ofAIXM.createSurfaceType();
		typeSyr.setPatches(pathPol);
		typeSyr.setSrsDimension(intDim);
		typeSyr.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%d-%s", 1, translatedSigmet.getIcaoCode())));
		typeSyr.getAxisLabels().add("Lat Long");
		typeSyr.setSrsName("");
		JAXBElement<SurfaceType> surAr = IWXXM31Helpers.ofAIXM.createSurface(typeSyr);
		SurfacePropertyType srfType = IWXXM31Helpers.ofAIXM.createSurfacePropertyType();
		srfType.setSurface(surAr);
		JAXBElement<SurfacePropertyType> surArHor = IWXXM31Helpers.ofAIXM
				.createAirspaceVolumeTypeHorizontalProjection(srfType);
		// ---------------Distance Vertical----------------//
		ValDistanceVerticalType valDisUp = IWXXM31Helpers.ofAIXM.createValDistanceVerticalType();
		valDisUp.setUom("FL");
		valDisUp.setValue(String.valueOf(translatedSigmet.getVerticalLocation().getTopFL().get()));
		JAXBElement<ValDistanceVerticalType> valDisJaxUp = IWXXM31Helpers.ofAIXM
				.createAirspaceLayerTypeUpperLimit(valDisUp);
		// ---------------Vertical Refeence----------------//
		CodeVerticalReferenceType valueCode = IWXXM31Helpers.ofAIXM.createCodeVerticalReferenceType();
		valueCode.setValue("STD");
		JAXBElement<CodeVerticalReferenceType> vertCodeType = IWXXM31Helpers.ofAIXM
				.createAirspaceLayerTypeUpperLimitReference(valueCode);
		// ---------------Airspace Volume----------------//
		AirspaceVolumeType airS = IWXXM31Helpers.ofAIXM.createAirspaceVolumeType();
		airS.setUpperLimit(valDisJaxUp);
		airS.setHorizontalProjection(surArHor);
		airS.setUpperLimitReference(vertCodeType);
		// ---------------Curve Property----------------//
		CurvePropertyType curvePropType = IWXXM31Helpers.ofAIXM.createCurvePropertyType();
		JAXBElement<CurvePropertyType> curveProp = IWXXM31Helpers.ofAIXM
				.createAirspaceVolumeTypeCentreline(curvePropType);
		airS.setCentreline(curveProp);
		return airS;
	}

	/**
	 * <iwxxm:validPeriod>
	 * <gml:TimePeriod gml:id="uuid.18e9591a-b3f0-4927-b723-b4f66d7c386f">
	 * <gml:beginPosition>2012-08-10T12:00:00Z</gml:beginPosition>
	 * <gml:endPosition>2012-08-10T16:00:00Z</gml:endPosition> </gml:TimePeriod>
	 * </iwxxm:validPeriod>
	 **/

	public AeronauticalSignificantWeatherPhenomenonType setAeronauticalSignificantWeatherPhenomenonType() {
		AeronauticalSignificantWeatherPhenomenonType typePhen = IWXXM31Helpers.ofIWXXM
				.createAeronauticalSignificantWeatherPhenomenonType();
		typePhen.setHref(translatedSigmet.getPhenomenonDescription().getPhenomenon());
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
	public SIGMETType addTranslationCentreHeader(SIGMETType report) throws DatatypeConfigurationException {
		report = iwxxmHelpers.addTranslationCentreHeaders(report, DateTime.now(), DateTime.now(),
				UUID.randomUUID().toString(), "UUWV", "Moscow, RU");
		// report.setTranslationFailedTAC("");
		return report;
	}

	@Override
	public String marshallMessageToXML(SIGMETType reportType) throws JAXBException, UnsupportedEncodingException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		JAXBContext jaxbContext = JAXBContext.newInstance(SIGMETType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, UriConstants.GLOBAL_SCHEMAS_LOCATION_V3);

		jaxbMarshaller.setProperty(StringConstants.SUN_JAXB_NAMESPACE_MAPPING_PROPERTY_NAME, new NamespaceMapper());

		JAXBElement<SIGMETType> sigmetRootElement = IWXXM31Helpers.ofIWXXM.createSIGMET(reportType);

		jaxbMarshaller.marshal(sigmetRootElement, stream);

		return stream.toString("UTF-8");
	}
}
