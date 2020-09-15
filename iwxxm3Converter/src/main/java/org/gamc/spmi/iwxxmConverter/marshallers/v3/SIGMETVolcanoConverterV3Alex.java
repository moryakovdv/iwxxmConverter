package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;

import org.gamc.gis.model.GTCalculatedRegion;
import org.gamc.spmi.iwxxmConverter.common.NamespaceMapper;
import org.gamc.spmi.iwxxmConverter.common.StringConstants;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.ANGLE_UNITS;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetHorizontalPhenomenonLocation;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;
import schemabindings31._int.icao.iwxxm._3.AbstractTimeObjectPropertyType;
import schemabindings31._int.icao.iwxxm._3.AirspaceVolumePropertyType;
import schemabindings31._int.icao.iwxxm._3.AngleWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.ExpectedIntensityChangeType;
import schemabindings31._int.icao.iwxxm._3.PermissibleUsageReasonType;
import schemabindings31._int.icao.iwxxm._3.PermissibleUsageType;
import schemabindings31._int.icao.iwxxm._3.ReportStatusType;
import schemabindings31._int.icao.iwxxm._3.SIGMETEvolvingConditionPropertyType;
import schemabindings31._int.icao.iwxxm._3.SIGMETEvolvingConditionType;
import schemabindings31._int.icao.iwxxm._3.SIGMETPositionPropertyType;
import schemabindings31._int.icao.iwxxm._3.SIGMETPositionType;
import schemabindings31._int.icao.iwxxm._3.StringWithNilReasonType;
import schemabindings31._int.icao.iwxxm._3.TimeIndicatorType;
import schemabindings31._int.icao.iwxxm._3.TropicalCycloneSIGMETPositionCollectionType;
import schemabindings31._int.icao.iwxxm._3.VolcanicAshSIGMETType;
import schemabindings31._int.icao.iwxxm._3.VolcanicAshSIGMETEvolvingConditionCollectionType;
import schemabindings31._int.icao.iwxxm._3.VolcanicAshSIGMETPropertyType;
import schemabindings31._int.wmo.def.metce._2013.VolcanoPropertyType;
import schemabindings31._int.wmo.def.metce._2013.VolcanoType;
import schemabindings31.net.opengis.gml.v_3_2_1.AssociationRoleType;
import schemabindings31.net.opengis.gml.v_3_2_1.CodeType;
import schemabindings31.net.opengis.gml.v_3_2_1.DirectPositionType;
import schemabindings31.net.opengis.gml.v_3_2_1.LocationPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.PointType;
import schemabindings31.net.opengis.gml.v_3_2_1.SpeedType;
import schemabindings31.net.opengis.gml.v_3_2_1.StringOrRefType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimeInstantPropertyType;

public class SIGMETVolcanoConverterV3Alex extends SIGMETConverterV3<SIGMETVolcanoTacMessageAlex, VolcanicAshSIGMETType>
		implements TacConverter<SIGMETVolcanoTacMessageAlex, VolcanicAshSIGMETType, IWXXM31Helpers> {

	@Override
	public String convertTacToXML(String tac)
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException {

		logger.debug("Parsing " + tac);
		SIGMETVolcanoTacMessageAlex sigmetMessage = new SIGMETVolcanoTacMessageAlex(tac);

		VolcanicAshSIGMETType result;

		try {
			sigmetMessage.parseMessage();
			result = convertMessage(sigmetMessage);
		} catch (ParsingException pe) {
			result = iwxxmHelpers.getOfIWXXM().createVolcanicAshSIGMETType();
			result.setTranslationFailedTAC(tac);
			logger.error(
					"Message was NOT parsed properly. XML message with translationFailedTAC attribute was created. See error below",
					pe);

		}

		String xmlResult = marshallMessageToXML(result);

		return xmlResult;
	}
	@Override
	public VolcanicAshSIGMETType convertMessage(SIGMETVolcanoTacMessageAlex translatedMessage)
			throws DatatypeConfigurationException, UnsupportedEncodingException, JAXBException, ParsingException,
			WMORegisterException {
		translatedSigmet = translatedMessage;
		StringOrRefType refTacString = iwxxmHelpers.getOfGML().createStringOrRefType();
		TimeInstantPropertyType obsTimeType = iwxxmHelpers.createTimeInstantPropertyTypeForDateTime(
				translatedSigmet.getMessageIssueDateTime(), translatedSigmet.getIcaoCode(), "issue");
		StringWithNilReasonType seq = iwxxmHelpers.getOfIWXXM().createStringWithNilReasonType();
		VolcanicAshSIGMETPropertyType tropProp = iwxxmHelpers.getOfIWXXM().createVolcanicAshSIGMETPropertyType();
		VolcanicAshSIGMETType sigmetRootTagCycl = iwxxmHelpers.getOfIWXXM().createVolcanicAshSIGMETType();
		tropProp.setVolcanicAshSIGMET(sigmetRootTagCycl);
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
			sigmetRootTagCycl.getForecastPositionAnalysis().add(setForecastAssociationRoleType());
			sigmetRootTagCycl.getEruptingVolcano().add(setVolcProp());

			break;

		}
		return sigmetRootTagCycl;
	}

	@Override
	/** created main analysis section for phenomena description */
	public AssociationRoleType setAssociationRoleType() throws WMORegisterException {
		// ---------------Association Role----------------//
		AssociationRoleType asType = iwxxmHelpers.getOfGML().createAssociationRoleType();

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

	public JAXBElement<?> createTropicalCyclonePosition(SigmetHorizontalPhenomenonLocation location) {

		schemabindings31.net.opengis.gml.v_3_2_1.PointPropertyType pointPropertyType = iwxxmHelpers.getOfGML()
				.createPointPropertyType();
		schemabindings31.net.opengis.gml.v_3_2_1.PointType gmlPoint = iwxxmHelpers.getOfGML().createPointType();
		DirectPositionType gmlPosType = iwxxmHelpers.getOfGML().createDirectPositionType();

		try {
			GTCalculatedRegion region = iwxxmHelpers.getGeoService()
					.recalcFromSinglePoint(location.getPoint().toGTCoordPoint());
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
	public AssociationRoleType setForecastAssociationRoleType() throws WMORegisterException {
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

		if (translatedSigmet.getPhenomenonDescription().getForecastSection().getHorizontalLocation().isSectionFilled()
				||

				translatedSigmet.getPhenomenonDescription().getForecastSection().getVerticalLocation()
						.isSectionFilled()) {

			List<GTCalculatedRegion> listCoord = getGTCalculatedRegions(
					translatedSigmet.getPhenomenonDescription().getForecastSection().getHorizontalLocation());

			air.setAirspaceVolume(createAirSpaceVolumeSection(listCoord,
					translatedSigmet.getPhenomenonDescription().getForecastSection().getHorizontalLocation(),
					translatedSigmet.getPhenomenonDescription().getForecastSection().getVerticalLocation()));
		} else {

			air = createInapplicablePosition();
		}

		forecastedPositionType.setGeometry(air);
		sicol.setId(iwxxmHelpers
				.generateUUIDv4(String.format("collection-%s-forecast-ts", translatedSigmet.getIcaoCode())));

		evolvingTypeProp.setSIGMETPosition(forecastedPositionType);
		sicol.getMember().add(evolvingTypeProp);
		sicol.setPhenomenonTime(analysisTimeProperty);
		sicol.getRest().add(createTropicalCyclonePosition(
				this.translatedSigmet.getHorizontalLocation()));

		asType.setAny(evolvingAr);

		forecastedPositionType.setId(
				iwxxmHelpers.generateUUIDv4(String.format("type-%s-forecast-ts", translatedSigmet.getIcaoCode())));

		return asType;

	}

	private VolcanoPropertyType setVolcProp() {
		VolcanoPropertyType tropCyc = new VolcanoPropertyType();
		VolcanoType vlc = new VolcanoType();
		vlc.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-ts", translatedSigmet.getIcaoCode())));
		CodeType code = iwxxmHelpers.getOfGML().createCodeType();
		code.setValue("MT HEKLA");
		vlc.getName().add(code);
		LocationPropertyType locProp = new LocationPropertyType();
		PointType pt = iwxxmHelpers.getOfGML().createPointType();
		pt.setId(iwxxmHelpers.generateUUIDv4(String.format("unit-%s-ts", translatedSigmet.getIcaoCode())));
		pt.getAxisLabels().add("Lat");
		pt.getAxisLabels().add("Long");
		pt.setSrsDimension(BigInteger.valueOf(2));
		pt.setSrsName("http://www.opengis.net/def/crs/EPSG/0/4326");
		DirectPositionType drPos = iwxxmHelpers.getOfGML().createDirectPositionType();
		drPos.getValue().add(63.98);
		drPos.getValue().add(-19.67);
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

	@Override
	public String marshallMessageToXML(VolcanicAshSIGMETType reportType)
			throws JAXBException, UnsupportedEncodingException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		JAXBContext jaxbContext = JAXBContext.newInstance(VolcanicAshSIGMETType.class);
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

}
