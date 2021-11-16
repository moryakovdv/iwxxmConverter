package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.ObjectInputStream.GetField;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Consumer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;

import org.ejml.data.FSubmatrixD1;
import org.gamc.gis.model.GTCalculatedRegion;
import org.gamc.gis.model.GTCoordPoint;
import org.gamc.gis.model.GTDirectionFromLine;
import org.gamc.gis.service.GeoServiceException;
import org.gamc.spmi.iwxxmConverter.common.CoordPoint;
import org.gamc.spmi.iwxxmConverter.common.DirectionFromLine;
import org.gamc.spmi.iwxxmConverter.common.NamespaceMapper;
import org.gamc.spmi.iwxxmConverter.common.StringConstants;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.ANGLE_UNITS;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetForecastSection;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetHorizontalPhenomenonLocation;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetVerticalPhenomenonLocation;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
import org.gamc.spmi.iwxxmConverter.wmo.WMONilReasonRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import schemabindings31._int.icao.iwxxm._3.AbstractTimeObjectPropertyType;
import schemabindings31._int.icao.iwxxm._3.AeronauticalSignificantWeatherPhenomenonType;
import schemabindings31._int.icao.iwxxm._3.AirspacePropertyType;
import schemabindings31._int.icao.iwxxm._3.AirspaceVolumePropertyType;
import schemabindings31._int.icao.iwxxm._3.AngleWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.ExpectedIntensityChangeType;
import schemabindings31._int.icao.iwxxm._3.PermissibleUsageReasonType;
import schemabindings31._int.icao.iwxxm._3.PermissibleUsageType;
import schemabindings31._int.icao.iwxxm._3.ReportStatusType;
import schemabindings31._int.icao.iwxxm._3.SIGMETEvolvingConditionCollectionType;
import schemabindings31._int.icao.iwxxm._3.SIGMETEvolvingConditionPropertyType;
import schemabindings31._int.icao.iwxxm._3.SIGMETEvolvingConditionType;
import schemabindings31._int.icao.iwxxm._3.SIGMETPositionPropertyType;
import schemabindings31._int.icao.iwxxm._3.SIGMETPositionType;
import schemabindings31._int.icao.iwxxm._3.SIGMETType;
import schemabindings31._int.icao.iwxxm._3.StringWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.TimeIndicatorType;
import schemabindings31._int.icao.iwxxm._3.TropicalCycloneSIGMETEvolvingConditionCollectionType;
import schemabindings31._int.icao.iwxxm._3.TropicalCycloneSIGMETPositionCollectionType;
import schemabindings31._int.icao.iwxxm._3.UnitPropertyType;
import schemabindings31._int.icao.iwxxm._3.VolcanicAshSIGMETEvolvingConditionCollectionType;
import schemabindings31._int.icao.iwxxm._3.VolcanicAshSIGMETPositionCollectionType;
import schemabindings31._int.icao.iwxxm._3.VolcanicAshSIGMETType;
import schemabindings31._int.wmo.def.metce._2013.VolcanoPropertyType;
import schemabindings31._int.wmo.def.metce._2013.VolcanoType;
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
import schemabindings31.net.opengis.gml.v_3_2_1.CircleByCenterPointType;
import schemabindings31.net.opengis.gml.v_3_2_1.CodeType;
import schemabindings31.net.opengis.gml.v_3_2_1.CurvePropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.CurveSegmentArrayPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.CurveType;
import schemabindings31.net.opengis.gml.v_3_2_1.DirectPositionListType;
import schemabindings31.net.opengis.gml.v_3_2_1.DirectPositionType;
import schemabindings31.net.opengis.gml.v_3_2_1.LengthType;
import schemabindings31.net.opengis.gml.v_3_2_1.LinearRingType;
import schemabindings31.net.opengis.gml.v_3_2_1.LocationPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.PointType;
import schemabindings31.net.opengis.gml.v_3_2_1.PolygonPatchType;
import schemabindings31.net.opengis.gml.v_3_2_1.RingType;
import schemabindings31.net.opengis.gml.v_3_2_1.SpeedType;
import schemabindings31.net.opengis.gml.v_3_2_1.StringOrRefType;
import schemabindings31.net.opengis.gml.v_3_2_1.SurfacePatchArrayPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimeInstantPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimePrimitivePropertyType;

public class SIGMETVolcanoConverterV3 extends SIGMETConverterV3<SIGMETVolcanoTacMessage, VolcanicAshSIGMETType>
		implements TacConverter<SIGMETVolcanoTacMessage, VolcanicAshSIGMETType, IWXXM31Helpers> {
	private TreeMap<String, String> createdRunways = new TreeMap<>();
	String airTrafficUnit = "FIC";
	String watchOfficeType = "MWO";
	String firType = "OTHER:FIR_UIR";
	String interpretation = "SNAPSHOT";

	IWXXM31Helpers iwxxmHelpers = new IWXXM31Helpers();
	private String dateTime = "";
	private String dateTimePosition = "";
	//private SIGMETVolcanoTacMessage translatedSigmet;

	Logger logger = LoggerFactory.getLogger(SIGMETVolcanoConverterV3.class);

	@Override
	public String convertTacToXML(String tac)
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException {
		createdRunways.clear();

		SIGMETVolcanoTacMessage sigmetMessage = new SIGMETVolcanoTacMessage(tac);

		VolcanicAshSIGMETType result;

		try {
			sigmetMessage.parseMessage();
			result = convertMessage(sigmetMessage);
		} catch (ParsingException pa) {
			result = iwxxmHelpers.getOfIWXXM().createVolcanicAshSIGMETType();
			result.setTranslationFailedTAC(tac);

		}

		String xmlResult = marshallMessageToXML(result);

		return xmlResult;
	}

	@Override
	public VolcanicAshSIGMETType convertMessage(SIGMETVolcanoTacMessage translatedMessage)
			throws DatatypeConfigurationException, UnsupportedEncodingException, JAXBException, ParsingException, WMORegisterException {
		translatedSigmet = translatedMessage;
		StringOrRefType refTacString = iwxxmHelpers.getOfGML().createStringOrRefType();
		TimeInstantPropertyType obsTimeType = iwxxmHelpers.createTimeInstantPropertyTypeForDateTime(
				translatedSigmet.getMessageIssueDateTime(), translatedSigmet.getIcaoCode(), "issue");
		StringWithNilReasonType seq = iwxxmHelpers.getOfIWXXM().createStringWithNilReasonType();
		VolcanicAshSIGMETType sigmetRootTagVolc = iwxxmHelpers.getOfIWXXM().createVolcanicAshSIGMETType();
		refTacString.setValue(translatedMessage.getInitialTacString());
		sigmetRootTagVolc.setDescription(refTacString);
		dateTime = translatedMessage.getMessageIssueDateTime().toString(iwxxmHelpers.getDateTimeFormat()) + "Z";
		dateTimePosition = translatedMessage.getMessageIssueDateTime().toString(iwxxmHelpers.getDateTimeISOFormat());
		// Id with ICAO code and current timestamp
		sigmetRootTagVolc.setId(
				iwxxmHelpers.generateUUIDv4(String.format("sigmet-%s-%s", translatedSigmet.getIcaoCode(), dateTime)));
		// sigmetRootTag.setAutomatedStation(true);
		// Set NON_OPERATIONAL and TEST properties.
		sigmetRootTagVolc.setPermissibleUsage(PermissibleUsageType.NON_OPERATIONAL);
		sigmetRootTagVolc.setPermissibleUsageReason(PermissibleUsageReasonType.TEST);
		// Some description
		sigmetRootTagVolc.setPermissibleUsageSupplementary("SIGMET composing test using JAXB");
		// COR, NIL, NORMAL
		switch (translatedSigmet.getMessageStatusType()) {
		case MISSING:
			sigmetRootTagVolc.setReportStatus(null);
			break;
		case CORRECTION:
			sigmetRootTagVolc.setReportStatus(ReportStatusType.CORRECTION);
			break;

		default:
			sigmetRootTagVolc.setReportStatus(ReportStatusType.NORMAL);
		}

		sigmetRootTagVolc.setIssueTime(obsTimeType);
		seq.setValue(translatedSigmet.getSigmetNumber());
		sigmetRootTagVolc.setSequenceNumber(seq);
		sigmetRootTagVolc.setIssuingAirTrafficServicesUnit(createUnitPropertyTypeNode(translatedSigmet.getIcaoCode(),
				translatedSigmet.getIcaoCode(), airTrafficUnit, interpretation));
		sigmetRootTagVolc.setIssuingAirTrafficServicesRegion(createAirspacePropertyTypeNode(
				translatedSigmet.getIcaoCode(), translatedSigmet.getFirName(), firType, interpretation));
		sigmetRootTagVolc.setOriginatingMeteorologicalWatchOffice(createUnitPropertyTypeNode(
				translatedSigmet.getIcaoCode(), translatedSigmet.getWatchOffice(), watchOfficeType, interpretation));
		sigmetRootTagVolc.setValidPeriod(iwxxmHelpers.createTimePeriod(translatedSigmet.getIcaoCode(),
				translatedSigmet.getValidFrom(), translatedSigmet.getValidTo()));
		switch (translatedMessage.getMessageStatusType()) {
		case CANCEL:
			sigmetRootTagVolc.setCancelledReportSequenceNumber(translatedSigmet.getCancelSigmetNumber());
			sigmetRootTagVolc.setCancelledReportValidPeriod(iwxxmHelpers.createTimePeriod(
					translatedSigmet.getIcaoCode(), translatedSigmet.getCancelSigmetDateTimeFrom(),
					translatedSigmet.getCancelSigmetDateTimeTo()));
			break;

		default:
			sigmetRootTagVolc.getAnalysis().add(setAssociationRoleType());
			if (translatedSigmet.getPhenomenonDescription().getForecastSection() != null) {
				
				for(SigmetForecastSection fcsection : translatedSigmet.getPhenomenonDescription().getForecastSection()) {
					sigmetRootTagVolc.getForecastPositionAnalysis().add(setForecastAssociationRoleType(fcsection));
				}
				
			}
			
			sigmetRootTagVolc.getEruptingVolcano().add(setVolcProp());
			break;

		}

		addTranslationCentreHeader(sigmetRootTagVolc);
		return sigmetRootTagVolc;
	}

	public AssociationRoleType setForecastPositionAnalysis(SigmetForecastSection fcsection) throws WMORegisterException {
		AbstractTimeObjectPropertyType analysisTimeProperty = iwxxmHelpers.createAbstractTimeObject(
				translatedSigmet.getPhenomenonDescription().getPhenomenonTimeStamp(), translatedSigmet.getIcaoCode());
		AirspaceVolumePropertyType air = iwxxmHelpers.getOfIWXXM().createAirspaceVolumePropertyType();
		SIGMETPositionPropertyType evolvingTypeProp = iwxxmHelpers.getOfIWXXM().createSIGMETPositionPropertyType();
		SIGMETPositionType sigPos = iwxxmHelpers.getOfIWXXM().createSIGMETPositionType();
		//SIGMETEvolvingConditionType evolvingType = iwxxmHelpers.getOfIWXXM().createSIGMETEvolvingConditionType();
		AssociationRoleType asType = iwxxmHelpers.getOfGML().createAssociationRoleType();
		VolcanicAshSIGMETPositionCollectionType siVolcCol = iwxxmHelpers.getOfIWXXM()
				.createVolcanicAshSIGMETPositionCollectionType();
		JAXBElement<VolcanicAshSIGMETPositionCollectionType> evolvingVolcAr = iwxxmHelpers.getOfIWXXM()
				.createVolcanicAshSIGMETPositionCollection(siVolcCol);
		List<GTCalculatedRegion> listCoord = getGTCalculatedRegions(fcsection.getHorizontalLocation());
		
		AirspaceVolumeType asvt = createAirSpaceVolumeSection(listCoord, fcsection.getHorizontalLocation(), fcsection.getVerticalLocation());
		air.setAirspaceVolume(asvt);
	
		
		sigPos.setGeometry(air);
		
		siVolcCol.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-ts", translatedSigmet.getIcaoCode())));
		evolvingTypeProp.setSIGMETPosition(sigPos);
		siVolcCol.getMember().add(evolvingTypeProp);
		siVolcCol.setPhenomenonTime(analysisTimeProperty);
		asType.setAny(evolvingVolcAr);
		return asType;
	}

	

	private VolcanoPropertyType setVolcProp() {
		VolcanoPropertyType tropCyc = new VolcanoPropertyType();
		VolcanoType vlc = new VolcanoType();
		vlc.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-ts", translatedSigmet.getIcaoCode())));
		CodeType code = iwxxmHelpers.getOfGML().createCodeType();
		code.setValue(translatedSigmet.getPhenomenonDescription().getPhenomenonGivenName());
		vlc.getName().add(code);
		LocationPropertyType locProp = new LocationPropertyType();
		PointType pt = iwxxmHelpers.getOfGML().createPointType();
		pt.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-ts", translatedSigmet.getIcaoCode())));
		pt.getAxisLabels().add("Lat");
		pt.getAxisLabels().add("Long");
		pt.setSrsDimension(BigInteger.valueOf(2));
		pt.setSrsName("http://www.opengis.net/def/crs/EPSG/0/4326");
		DirectPositionType drPos = iwxxmHelpers.getOfGML().createDirectPositionType();
		DirectPositionType gmlPosType = iwxxmHelpers.getOfGML().createDirectPositionType();
		
		try {
			GTCalculatedRegion region = iwxxmHelpers.getGeoService().recalcFromSinglePoint(translatedSigmet.getHorizontalLocation().getPoint().toGTCoordPoint());
			gmlPosType.getValue().addAll(region.getCoordinates());
			
		} catch (URISyntaxException e) {
			logger.error("Unable to determine TC center coordinates");
		}
		pt.setPos(drPos);
		JAXBElement<PointType> trLocType = iwxxmHelpers.getOfGML().createPoint(pt);
		locProp.setAbstractGeometry(trLocType);
		JAXBElement<LocationPropertyType> arLocType = iwxxmHelpers.getOfGML().createLocation(locProp);
		vlc.setLocation(arLocType);
		JAXBElement<VolcanoType> trType = new JAXBElement<VolcanoType>(QName.valueOf("Volcano"), VolcanoType.class,
				vlc);
		tropCyc.setVolcano(trType);
		return tropCyc;
	}

	public AssociationRoleType setAssociationRoleType() throws WMORegisterException {
		// ---------------Association Role----------------//
		AssociationRoleType asType = iwxxmHelpers.getOfGML().createAssociationRoleType();

		// ---------------AirspaceVolumePropertyType----------------//
		AirspaceVolumePropertyType air = iwxxmHelpers.getOfIWXXM().createAirspaceVolumePropertyType();
		List<GTCalculatedRegion> listCoord = getGTCalculatedRegions(translatedSigmet.getHorizontalLocation());
		air.setAirspaceVolume(createAirSpaceVolumeSection(listCoord,translatedSigmet.getHorizontalLocation(),translatedSigmet.getVerticalLocation()));
		// ---------------SIGMETEvolvingConditionType(Time)----------------//
		AbstractTimeObjectPropertyType analysisTimeProperty = iwxxmHelpers.createAbstractTimeObject(
				translatedSigmet.getPhenomenonDescription().getPhenomenonTimeStamp(), translatedSigmet.getIcaoCode());
		// ---------------SIGMETEvolvingConditionType----------------//
		SIGMETEvolvingConditionPropertyType evolvingTypeProp = iwxxmHelpers.getOfIWXXM()
				.createSIGMETEvolvingConditionPropertyType();
		// ---------------SIGMETEvolvingConditionType----------------//
		SIGMETEvolvingConditionType evolvingType = iwxxmHelpers.getOfIWXXM().createSIGMETEvolvingConditionType();
		VolcanicAshSIGMETEvolvingConditionCollectionType siVolcCol = iwxxmHelpers.getOfIWXXM()
				.createVolcanicAshSIGMETEvolvingConditionCollectionType();
		JAXBElement<VolcanicAshSIGMETEvolvingConditionCollectionType> evolvingVolcAr = iwxxmHelpers.getOfIWXXM()
				.createVolcanicAshSIGMETEvolvingConditionCollection(siVolcCol);
		evolvingType.setGeometry(air);
		siVolcCol.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-ts", translatedSigmet.getIcaoCode())));
		// ---------------SIGMETEvolvingConditionType(PhenomenonObservation)----------------//
		if (translatedSigmet.getPhenomenonDescription().getPhenomenonObservation().name().equals("FCST")) {
			siVolcCol.setTimeIndicator(TimeIndicatorType.FORECAST);
		} else if (translatedSigmet.getPhenomenonDescription().getPhenomenonObservation().name().equals("OBS")) {
			siVolcCol.setTimeIndicator(TimeIndicatorType.OBSERVATION);
		}
		evolvingTypeProp.setSIGMETEvolvingCondition(evolvingType);
		siVolcCol.getMember().add(evolvingTypeProp);
		siVolcCol.setPhenomenonTime(analysisTimeProperty);
		asType.setAny(evolvingVolcAr);
		// ---------------SIGMETEvolvingConditionType(Speed-Motion-Id-Intencity)----------------//
		SpeedType speedType = iwxxmHelpers.getOfGML().createSpeedType();
		if (translatedSigmet.getPhenomenonDescription().getMovingSection() != null
				&& translatedSigmet.getPhenomenonDescription().getMovingSection().isMoving()) {
			AngleWithNilReasonType motion = iwxxmHelpers.getOfIWXXM().createAngleWithNilReasonType();
			JAXBElement<AngleWithNilReasonType> dirMo = iwxxmHelpers.getOfIWXXM()
					.createSIGMETEvolvingConditionTypeDirectionOfMotion(motion);

			motion.setUom(ANGLE_UNITS.DEGREES.getStringValue());
			motion.setValue(translatedSigmet.getPhenomenonDescription().getMovingSection().getMovingDirection()
					.getDoubleValue());

			evolvingType.setDirectionOfMotion(dirMo);

			if (translatedSigmet.getPhenomenonDescription().getMovingSection().getSpeedUnits() != null
					&& translatedSigmet.getPhenomenonDescription().getMovingSection().getMovingSpeed() > 0) {
				speedType.setUom(translatedSigmet.getPhenomenonDescription().getMovingSection().getSpeedUnits()
						.getStringValue());
				speedType.setValue(translatedSigmet.getPhenomenonDescription().getMovingSection().getMovingSpeed());
				evolvingType.setSpeedOfMotion(speedType);
			}
		}
		evolvingType.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-ts", translatedSigmet.getIcaoCode())));
		if (translatedSigmet.getPhenomenonDescription().getIntencity().name().equals("INTSF")) {
			evolvingType.setIntensityChange(ExpectedIntensityChangeType.INTENSIFY);
		} else if (translatedSigmet.getPhenomenonDescription().getIntencity().name().equals("WKN")) {
			evolvingType.setIntensityChange(ExpectedIntensityChangeType.WEAKEN);
		} else if (translatedSigmet.getPhenomenonDescription().getIntencity().name().equals("NC")) {
			evolvingType.setIntensityChange(ExpectedIntensityChangeType.NO_CHANGE);
		}
		return asType;
	}

	
	

	

	/** Get link for WMO register record for the phenomena 
	 * @throws WMORegisterException */
	public AeronauticalSignificantWeatherPhenomenonType setAeronauticalSignificantWeatherPhenomenonType() throws WMORegisterException {
		AeronauticalSignificantWeatherPhenomenonType typePhen = iwxxmHelpers.getOfIWXXM()
				.createAeronauticalSignificantWeatherPhenomenonType();
		String link = iwxxmHelpers.getSigWxPhenomenaRegister()
				.getWMOUrlByCode(translatedSigmet.getPhenomenonDescription().getPhenomenonForLink());
		typePhen.setHref(link);
		return typePhen;

	}

	public UnitPropertyType createUnitPropertyTypeNode(String icaoCode, String firname, String type,
			String interpretation) {
		UnitPropertyType pt = iwxxmHelpers.getOfIWXXM().createUnitPropertyType();

		UnitType ut = iwxxmHelpers.getOfAIXM().createUnitType();
		ut.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-%s", icaoCode, firname)));

		pt.setUnit(ut);

		UnitTimeSlicePropertyType tspt = iwxxmHelpers.getOfAIXM().createUnitTimeSlicePropertyType();
		UnitTimeSliceType tst = iwxxmHelpers.getOfAIXM().createUnitTimeSliceType();
		tst.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-%s-ts", icaoCode, firname)));
		tst.setInterpretation(interpretation);

		// TODO: Ask the team if it is nessessary
		TimePrimitivePropertyType emptyTime = iwxxmHelpers.getOfGML().createTimePrimitivePropertyType();
		tst.setValidTime(emptyTime);

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

	/** Airspace for issuing center */
	public AirspacePropertyType createAirspacePropertyTypeNode(String icaoCode, String firname, String type,
			String interpretation) {

		AirspacePropertyType pt = iwxxmHelpers.getOfIWXXM().createAirspacePropertyType();
		AirspaceType ast = iwxxmHelpers.getOfAIXM().createAirspaceType();
		ast.setId(iwxxmHelpers.generateUUIDv4(String.format("airspace-%s", icaoCode)));
		pt.setAirspace(ast);

		AirspaceTimeSlicePropertyType tsp = iwxxmHelpers.getOfAIXM().createAirspaceTimeSlicePropertyType();
		AirspaceTimeSliceType ts = iwxxmHelpers.getOfAIXM().createAirspaceTimeSliceType();
		ts.setId(iwxxmHelpers.generateUUIDv4(String.format("airspace-%s-ts", icaoCode)));

		// TODO: Ask the team if it is nessessary
		TimePrimitivePropertyType emptyTime = iwxxmHelpers.getOfGML().createTimePrimitivePropertyType();
		ts.setValidTime(emptyTime);

		ts.setInterpretation(interpretation);

		/**
		 * <aixm:interpretation>SNAPSHOT</aixm:interpretation>
		 * <aixm:type>OTHER:FIR_UIR</aixm:type> <aixm:designator>YUDD</aixm:designator>
		 * <aixm:name>SHANLON FIR/UIR</aixm:name>
		 *
		 *
		 */
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

		// add name
		TextNameType nType = iwxxmHelpers.getOfAIXM().createTextNameType();
		nType.setValue(firname);
		JAXBElement<TextNameType> ntType = iwxxmHelpers.getOfAIXM().createAirspaceTimeSliceTypeName(nType);
		ts.getRest().add(ntType);

		tsp.setAirspaceTimeSlice(ts);

		ast.getTimeSlice().add(tsp);

		return pt;
	}

	@Override
	public VolcanicAshSIGMETType addTranslationCentreHeader(VolcanicAshSIGMETType report)
			throws DatatypeConfigurationException {
		report = iwxxmHelpers.addTranslationCentreHeaders(report, DateTime.now(), DateTime.now(),
				UUID.randomUUID().toString(), "UUWV", "Moscow, RU");
		// report.setTranslationFailedTAC("");
		return report;
	}

	@Override
	public String marshallMessageToXML(VolcanicAshSIGMETType reportType)
			throws JAXBException, UnsupportedEncodingException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		JAXBContext jaxbContext = JAXBContext.newInstance(SIGMETType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, UriConstants.GLOBAL_SCHEMAS_LOCATION_V3);

		jaxbMarshaller.setProperty(StringConstants.SUN_JAXB_NAMESPACE_MAPPING_PROPERTY_NAME, new NamespaceMapper());

		JAXBElement<VolcanicAshSIGMETType> sigmetRootElement = iwxxmHelpers.getOfIWXXM()
				.createVolcanicAshSIGMET(reportType);

		jaxbMarshaller.marshal(sigmetRootElement, stream);

		return stream.toString("UTF-8");
	}

	@Override
	public IWXXM31Helpers getHelper() {
		return iwxxmHelpers;
	}

	@Override
	public SIGMETVolcanoConverterV3 withHelper(IWXXM31Helpers helper) {
		this.iwxxmHelpers = helper;
		return this;

	}
}
