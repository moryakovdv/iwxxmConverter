package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
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
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
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
import schemabindings31._int.icao.iwxxm._3.SIGMETPositionCollectionType;
import schemabindings31._int.icao.iwxxm._3.SIGMETPositionPropertyType;
import schemabindings31._int.icao.iwxxm._3.SIGMETPositionType;
import schemabindings31._int.icao.iwxxm._3.SIGMETType;
import schemabindings31._int.icao.iwxxm._3.StringWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.TimeIndicatorType;
import schemabindings31._int.icao.iwxxm._3.TropicalCycloneObservedConditionsPropertyType;
import schemabindings31._int.icao.iwxxm._3.TropicalCycloneObservedConditionsType;
import schemabindings31._int.icao.iwxxm._3.TropicalCycloneSIGMETEvolvingConditionCollectionType;
import schemabindings31._int.icao.iwxxm._3.TropicalCycloneSIGMETPositionCollectionPropertyType;
import schemabindings31._int.icao.iwxxm._3.TropicalCycloneSIGMETPositionCollectionType;
import schemabindings31._int.icao.iwxxm._3.TropicalCycloneSIGMETPropertyType;
import schemabindings31._int.icao.iwxxm._3.TropicalCycloneSIGMETType;
import schemabindings31._int.icao.iwxxm._3.UnitPropertyType;
import schemabindings31._int.wmo.def.metce._2013.TropicalCyclonePropertyType;
import schemabindings31._int.wmo.def.metce._2013.TropicalCycloneType;
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
import schemabindings31.net.opengis.gml.v_3_2_1.CoordinatesType;
import schemabindings31.net.opengis.gml.v_3_2_1.CurvePropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.CurveSegmentArrayPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.CurveType;
import schemabindings31.net.opengis.gml.v_3_2_1.DirectPositionType;
import schemabindings31.net.opengis.gml.v_3_2_1.LengthType;
import schemabindings31.net.opengis.gml.v_3_2_1.LocationPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.PointType;
import schemabindings31.net.opengis.gml.v_3_2_1.PolygonPatchType;
import schemabindings31.net.opengis.gml.v_3_2_1.RingType;
import schemabindings31.net.opengis.gml.v_3_2_1.SpeedType;
import schemabindings31.net.opengis.gml.v_3_2_1.StringOrRefType;
import schemabindings31.net.opengis.gml.v_3_2_1.SurfacePatchArrayPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimeInstantPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimePrimitivePropertyType;

public class SIGMETTropicalConverterV3 extends SIGMETConverterV3<SIGMETTropicalTacMessage, TropicalCycloneSIGMETType>
		implements TacConverter<SIGMETTropicalTacMessage, TropicalCycloneSIGMETType, IWXXM31Helpers> {

	@Override
	public String convertTacToXML(String tac)
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException {

		logger.debug("Parsing "+ tac);
		SIGMETTropicalTacMessage sigmetMessage = new SIGMETTropicalTacMessage(tac);

		TropicalCycloneSIGMETType result;

		try {
			sigmetMessage.parseMessage();
			result = convertMessage(sigmetMessage);
		} catch (ParsingException pe) {
			result = iwxxmHelpers.getOfIWXXM().createTropicalCycloneSIGMETType();
			result.setTranslationFailedTAC(tac);
			logger.error("Message was NOT parsed properly. XML message with translationFailedTAC attribute was created. See error below",pe);

		}

		String xmlResult = marshallMessageToXML(result);

		return xmlResult;
	}

	@Override
	public TropicalCycloneSIGMETType convertMessage(SIGMETTropicalTacMessage translatedMessage)
			throws DatatypeConfigurationException, UnsupportedEncodingException, JAXBException, ParsingException, WMORegisterException {
		translatedSigmet = translatedMessage;
		StringOrRefType refTacString = iwxxmHelpers.getOfGML().createStringOrRefType();
		TimeInstantPropertyType obsTimeType = iwxxmHelpers.createTimeInstantPropertyTypeForDateTime(
				translatedSigmet.getMessageIssueDateTime(), translatedSigmet.getIcaoCode(), "issue");
		StringWithNilReasonType seq = iwxxmHelpers.getOfIWXXM().createStringWithNilReasonType();
		TropicalCycloneSIGMETPropertyType tropProp = iwxxmHelpers.getOfIWXXM()
				.createTropicalCycloneSIGMETPropertyType();
		TropicalCycloneSIGMETType sigmetRootTagCycl = iwxxmHelpers.getOfIWXXM().createTropicalCycloneSIGMETType();
		tropProp.setTropicalCycloneSIGMET(sigmetRootTagCycl);
		refTacString.setValue(translatedMessage.getInitialTacString());
		sigmetRootTagCycl.setDescription(refTacString);
		sigmetRootTagCycl = addTranslationCentreHeader(sigmetRootTagCycl);
		dateTime = translatedMessage.getMessageIssueDateTime().toString(iwxxmHelpers.getDateTimeFormat()) + "Z";
		dateTimePosition = translatedMessage.getMessageIssueDateTime().toString(iwxxmHelpers.getDateTimeISOFormat());
		// Id with ICAO code and current timestamp
		sigmetRootTagCycl.setId(
				iwxxmHelpers.generateUUIDv4(String.format("sigmet-%s-%s", translatedSigmet.getIcaoCode(), dateTime)));
		// sigmetRootTag.setAutomatedStation(true);
		// Set NON_OPERATIONAL and TEST properties.
		sigmetRootTagCycl.setPermissibleUsage(PermissibleUsageType.NON_OPERATIONAL);
		sigmetRootTagCycl.setPermissibleUsageReason(PermissibleUsageReasonType.TEST);
		// Some description
		sigmetRootTagCycl.setPermissibleUsageSupplementary("SIGMET composing test using JAXB");
		// COR, NIL, NORMAL
		switch (translatedSigmet.getMessageStatusType()) {
		case MISSING:
			sigmetRootTagCycl.setReportStatus(null);
			break;
		case CORRECTION:
			sigmetRootTagCycl.setReportStatus(ReportStatusType.CORRECTION);
			break;

		default:
			sigmetRootTagCycl.setReportStatus(ReportStatusType.NORMAL);
		}

		sigmetRootTagCycl.setIssueTime(obsTimeType);
		seq.setValue(translatedSigmet.getSigmetNumber());
		sigmetRootTagCycl.setSequenceNumber(seq);
		sigmetRootTagCycl.setIssuingAirTrafficServicesUnit(createUnitPropertyTypeNode(translatedSigmet.getIcaoCode(),
				translatedSigmet.getIcaoCode(), airTrafficUnit, interpretation));
		sigmetRootTagCycl.setIssuingAirTrafficServicesRegion(createAirspacePropertyTypeNode(
				translatedSigmet.getIcaoCode(), translatedSigmet.getFirName(), firType, interpretation));
		sigmetRootTagCycl.setOriginatingMeteorologicalWatchOffice(createUnitPropertyTypeNode(
				translatedSigmet.getIcaoCode(), translatedSigmet.getWatchOffice(), watchOfficeType, interpretation));
		sigmetRootTagCycl.setValidPeriod(iwxxmHelpers.createTimePeriod(translatedSigmet.getIcaoCode(),
				translatedSigmet.getValidFrom(), translatedSigmet.getValidTo()));
		switch (translatedMessage.getMessageStatusType()) {
		case CANCEL:
			sigmetRootTagCycl.setCancelledReportSequenceNumber(translatedSigmet.getCancelSigmetNumber());
			sigmetRootTagCycl.setCancelledReportValidPeriod(iwxxmHelpers.createTimePeriod(
					translatedSigmet.getIcaoCode(), translatedSigmet.getCancelSigmetDateTimeFrom(),
					translatedSigmet.getCancelSigmetDateTimeTo()));
			break;

		default:
			sigmetRootTagCycl.getAnalysis().add(setAssociationRoleType());
			if (translatedSigmet.getPhenomenonDescription().getForecastSection() != null) {
				
				for(SigmetForecastSection fcsection : translatedSigmet.getPhenomenonDescription().getForecastSection()) {
					sigmetRootTagCycl.getForecastPositionAnalysis().add(setForecastAssociationRoleType(fcsection));
				}
				
			}		
			
			sigmetRootTagCycl.getTropicalCyclone().add(setTropCyclone());

			break;

		}
		return sigmetRootTagCycl;
	}

	@Override
	/** created main analysis section for phenomena description */
	public AssociationRoleType setAssociationRoleType() throws WMORegisterException {
		// ---------------Association Role----------------//
		AssociationRoleType asType = iwxxmHelpers.getOfGML().createAssociationRoleType();

		// ---------------SIGMETEvolvingConditionType(Time)----------------//
		AbstractTimeObjectPropertyType analysisTimeProperty = iwxxmHelpers.createAbstractTimeObject(
				translatedSigmet.getPhenomenonDescription().getPhenomenonTimeStamp(), translatedSigmet.getIcaoCode());

		// ---------------SIGMETEvolvingConditionType----------------//
		SIGMETEvolvingConditionPropertyType evolvingTypeProp = iwxxmHelpers.getOfIWXXM()
				.createSIGMETEvolvingConditionPropertyType();

		// ---------------SIGMETEvolvingConditionType----------------//
		SIGMETEvolvingConditionType evolvingType = iwxxmHelpers.getOfIWXXM().createSIGMETEvolvingConditionType();

		TropicalCycloneSIGMETEvolvingConditionCollectionType sicol = iwxxmHelpers.getOfIWXXM()
				.createTropicalCycloneSIGMETEvolvingConditionCollectionType();

		JAXBElement<TropicalCycloneSIGMETEvolvingConditionCollectionType> evolvingAr = iwxxmHelpers.getOfIWXXM()
				.createTropicalCycloneSIGMETEvolvingConditionCollection(sicol);

		// ---------------AirspaceVolumePropertyType----------------//
		AirspaceVolumePropertyType air = iwxxmHelpers.getOfIWXXM().createAirspaceVolumePropertyType();

		if (this.translatedSigmet.getHorizontalLocation().isSectionFilled()
				|| this.translatedSigmet.getVerticalLocation().isSectionFilled()) {

			List<GTCalculatedRegion> listCoord = getGTCalculatedRegions(this.translatedSigmet.getHorizontalLocation());
			air.setAirspaceVolume(createAirSpaceVolumeSection(listCoord, this.translatedSigmet.getHorizontalLocation(),
					this.translatedSigmet.getVerticalLocation()));
		} else {
			air = createInapplicablePosition();
		}

		evolvingType.setGeometry(air);
		sicol.setId(iwxxmHelpers.generateUUIDv4(String.format("collection-%s-ts", translatedSigmet.getIcaoCode())));
		// ---------------SIGMETEvolvingConditionType(PhenomenonObservation)----------------//
		if (translatedSigmet.getPhenomenonDescription().getPhenomenonObservation().name().equals("FCST")) {
			sicol.setTimeIndicator(TimeIndicatorType.FORECAST);
		} else if (translatedSigmet.getPhenomenonDescription().getPhenomenonObservation().name().equals("OBS")) {
			sicol.setTimeIndicator(TimeIndicatorType.OBSERVATION);
		}
		evolvingTypeProp.setSIGMETEvolvingCondition(evolvingType);
		sicol.getMember().add(evolvingTypeProp);
		sicol.setPhenomenonTime(analysisTimeProperty);
		sicol.getRest().add(createTropicalCyclonePosition(this.translatedSigmet.getHorizontalLocation()));

		asType.setAny(evolvingAr);

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
		evolvingType.setId(iwxxmHelpers.generateUUIDv4(String.format("type-%s-ts", translatedSigmet.getIcaoCode())));
		if (translatedSigmet.getPhenomenonDescription().getIntencity().name().equals("INTSF")) {
			evolvingType.setIntensityChange(ExpectedIntensityChangeType.INTENSIFY);
		} else if (translatedSigmet.getPhenomenonDescription().getIntencity().name().equals("WKN")) {
			evolvingType.setIntensityChange(ExpectedIntensityChangeType.WEAKEN);
		} else if (translatedSigmet.getPhenomenonDescription().getIntencity().name().equals("NC")) {
			evolvingType.setIntensityChange(ExpectedIntensityChangeType.NO_CHANGE);
		}
		return asType;
	}

	public JAXBElement<?> createTropicalCyclonePosition(SigmetHorizontalPhenomenonLocation location) {

		schemabindings31.net.opengis.gml.v_3_2_1.PointPropertyType pointPropertyType = iwxxmHelpers.getOfGML()
				.createPointPropertyType();
		schemabindings31.net.opengis.gml.v_3_2_1.PointType gmlPoint = iwxxmHelpers.getOfGML().createPointType();
		DirectPositionType gmlPosType = iwxxmHelpers.getOfGML().createDirectPositionType();
		
		try {
			GTCalculatedRegion region = iwxxmHelpers.getGeoService().recalcFromSinglePoint(location.getPoint().toGTCoordPoint());
			gmlPosType.getValue().addAll(region.getCoordinates());
			
		} catch (URISyntaxException e) {
			logger.error("Unable to determine TC center coordinates");
		}
		
		
		gmlPoint.getAxisLabels().add("Lat");
		gmlPoint.getAxisLabels().add("Long");
		gmlPoint.setSrsDimension(BigInteger.valueOf(2));
		gmlPoint.setSrsName("http://www.opengis.net/def/crs/EPSG/0/4326");
		gmlPoint.setId(iwxxmHelpers.generateUUIDv4("surface-" + translatedSigmet.getIcaoCode()));

		gmlPoint.setPos(gmlPosType);

		JAXBElement<PointType> pointElement = iwxxmHelpers.getOfGML().createPoint(gmlPoint);

		pointPropertyType.setPoint(pointElement);

		return iwxxmHelpers.getOfIWXXM()
				.createTropicalCycloneSIGMETPositionCollectionTypeTropicalCyclonePosition(pointPropertyType);

	}

	@Override
	/** creates forecast section for phenomena */
	public AssociationRoleType setForecastAssociationRoleType(SigmetForecastSection fcsection) throws WMORegisterException {
		AssociationRoleType asType = iwxxmHelpers.getOfGML().createAssociationRoleType();

		// ---------------SIGMETEvolvingConditionType(Time)----------------//
		AbstractTimeObjectPropertyType analysisTimeProperty = iwxxmHelpers.createAbstractTimeObject(
				translatedSigmet.getPhenomenonDescription().getPhenomenonTimeStamp(), translatedSigmet.getIcaoCode());
		// ---------------SIGMETEvolvingConditionType----------------//
		SIGMETPositionPropertyType evolvingTypeProp = iwxxmHelpers.getOfIWXXM().createSIGMETPositionPropertyType();
		// ---------------SIGMETEvolvingConditionType----------------//
		SIGMETPositionType forecastedPositionType = iwxxmHelpers.getOfIWXXM().createSIGMETPositionType();

		TropicalCycloneSIGMETPositionCollectionType sicol = iwxxmHelpers.getOfIWXXM()
				.createTropicalCycloneSIGMETPositionCollectionType();
		JAXBElement<TropicalCycloneSIGMETPositionCollectionType> evolvingAr = iwxxmHelpers.getOfIWXXM()
				.createTropicalCycloneSIGMETPositionCollection(sicol); // ---------------AirspaceVolumePropertyType----------------//

		AirspaceVolumePropertyType air = iwxxmHelpers.getOfIWXXM().createAirspaceVolumePropertyType();

		if (fcsection.getHorizontalLocation().isSectionFilled()
				||

				fcsection.getVerticalLocation()
						.isSectionFilled()) {

			List<GTCalculatedRegion> listCoord = getGTCalculatedRegions(
					fcsection.getHorizontalLocation());

			air.setAirspaceVolume(createAirSpaceVolumeSection(listCoord,
					fcsection.getHorizontalLocation(),
					fcsection.getVerticalLocation()));
		} else {

			air = createInapplicablePosition();
		}

		forecastedPositionType.setGeometry(air);
		sicol.setId(iwxxmHelpers
				.generateUUIDv4(String.format("collection-%s-forecast-ts", translatedSigmet.getIcaoCode())));

		evolvingTypeProp.setSIGMETPosition(forecastedPositionType);
		sicol.getMember().add(evolvingTypeProp);
		sicol.setPhenomenonTime(analysisTimeProperty);
		sicol.getRest().add(createTropicalCyclonePosition(fcsection.getHorizontalLocation()));

		asType.setAny(evolvingAr);

		forecastedPositionType.setId(
				iwxxmHelpers.generateUUIDv4(String.format("type-%s-forecast-ts", translatedSigmet.getIcaoCode())));

		return asType;

	}

	private TropicalCyclonePropertyType setTropCyclone() {

		// -----------------createTropicalCycloneType-----------------------------------------------------------
		TropicalCycloneType trType = iwxxmHelpers.getOfMETCE().createTropicalCycloneType();
		trType.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-tc-description",
				translatedSigmet.getPhenomenonDescription().getPhenomenonGivenName())));

		JAXBElement<String> nameElement = iwxxmHelpers.getOfMETCE()
				.createVolcanoTypeName(translatedSigmet.getPhenomenonDescription().getPhenomenonGivenName());
		trType.getRest().add(nameElement);

		// -----------------createTropicalCyclonePropertyType-----------------------------------------------------------
		TropicalCyclonePropertyType tropCyc = iwxxmHelpers.getOfMETCE().createTropicalCyclonePropertyType();
		tropCyc.setTropicalCyclone(trType);
		return tropCyc;
	}

	@Override
	public String marshallMessageToXML(TropicalCycloneSIGMETType reportType)
			throws JAXBException, UnsupportedEncodingException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		JAXBContext jaxbContext = JAXBContext.newInstance(TropicalCycloneSIGMETType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, UriConstants.GLOBAL_SCHEMAS_LOCATION_V3);

		jaxbMarshaller.setProperty(StringConstants.SUN_JAXB_NAMESPACE_MAPPING_PROPERTY_NAME, new NamespaceMapper());

		JAXBElement<TropicalCycloneSIGMETType> sigmetRootElement = iwxxmHelpers.getOfIWXXM()
				.createTropicalCycloneSIGMET(reportType);

		jaxbMarshaller.marshal(sigmetRootElement, stream);

		return stream.toString("UTF-8");
	}

}
