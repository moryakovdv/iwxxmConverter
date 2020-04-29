package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.acl.Owner;
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
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
import org.gamc.spmi.iwxxmConverter.wmo.WMOSpaceWeatherLocationRegister;
import org.gamc.spmi.ixwwmConverter.spaceweatherconverter.SpaceWeatherEffectLocation;
import org.joda.time.DateTime;

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
import schemabindings31.net.opengis.gml.v_3_2_1.AbstractRingPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.AbstractSurfacePatchType;
import schemabindings31.net.opengis.gml.v_3_2_1.AbstractTimeObjectType;
import schemabindings31.net.opengis.gml.v_3_2_1.CodeType;
import schemabindings31.net.opengis.gml.v_3_2_1.DirectPositionListType;
import schemabindings31.net.opengis.gml.v_3_2_1.LinearRingPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.LinearRingType;
import schemabindings31.net.opengis.gml.v_3_2_1.LocationPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.PolygonPatchType;
import schemabindings31.net.opengis.gml.v_3_2_1.SurfacePatchArrayPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimeInstantPropertyType;
import schemabindings31.net.opengis.gml.v_3_2_1.TimeInstantType;

public class SPACEWEATHERConverterV3 implements TacConverter<SPACEWEATHERTacMessage, SpaceWeatherAdvisoryType> {
	
	IWXXM31Helpers iwxxmHelpers = new IWXXM31Helpers();
	
	@Override
	public String convertTacToXML(String tac)
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException {
		SPACEWEATHERTacMessage swxMessage = new SPACEWEATHERTacMessage(tac);
		
		SpaceWeatherAdvisoryType result;

		try {
			swxMessage.parseMessage();
			result = convertMessage(swxMessage);
			
		} catch (ParsingException pa) {
			result = IWXXM31Helpers.ofIWXXM.createSpaceWeatherAdvisoryType();
			result.setTranslationFailedTAC(tac);

		}

		String xmlResult = marshallMessageToXML(result);
		return xmlResult; 
	}

	@Override
	public SpaceWeatherAdvisoryType convertMessage(SPACEWEATHERTacMessage translatedMessage)
			throws DatatypeConfigurationException, UnsupportedEncodingException, JAXBException, ParsingException {
		
		//instantiating the result object
		SpaceWeatherAdvisoryType resultSwxType = new SpaceWeatherAdvisoryType();
		
		//set translation center attribute
		resultSwxType = addTranslationCentreHeader(resultSwxType);
		
		//set operational status
		resultSwxType.setPermissibleUsage(PermissibleUsageType.NON_OPERATIONAL);
		
		//set current swx number
		StringWithNilReasonType advNumber = iwxxmHelpers.createStringWithNilReasonForString(translatedMessage.getAdvisoryNumber(), null);
		resultSwxType.setAdvisoryNumber(advNumber);
		
		//set replaced number
		resultSwxType.setReplacedAdvisoryNumber(translatedMessage.getReplaceNumber());
		
		//set issuing time
		TimeInstantPropertyType issuedTime = iwxxmHelpers.createTimeInstantPropertyTypeForDateTime(translatedMessage.getIssued(),translatedMessage.getIssuingCenter(),"idt");
		resultSwxType.setIssueTime(issuedTime);
		
		//set phenomenas
		for (String effect : translatedMessage.getEffects())
			resultSwxType.getPhenomenon().add(createPhenomena(effect));
		
		//set advisory center
		resultSwxType.setIssuingSpaceWeatherCentre(createIssuingCenterSection(translatedMessage.getIssuingCenter(), translatedMessage.getIssued()));
		
		//set remarks
		StringWithNilReasonType remarks = iwxxmHelpers.createStringWithNilReasonForString(translatedMessage.getRemark(), null);
		resultSwxType.setRemarks(remarks);
		
		//create regions 
		//for observation
		resultSwxType.getAnalysis().add(createObservationAnalysisSection(translatedMessage.getIssuingCenter(),translatedMessage.getObservedLocation()));
		
		//for forecast
		//TODO
		
		return resultSwxType;
	}

    /**creates UnitPropertyType section for issuing center*/
	public UnitPropertyType createIssuingCenterSection(String issuingCenter,DateTime dateTime) {
		
		//create external envelope
		UnitPropertyType unitType = IWXXM31Helpers.ofIWXXM.createUnitPropertyType();
		UnitType unit = IWXXM31Helpers.ofAIXM.createUnitType();
		
	
		//add unit data "flesh"
		UnitTimeSliceType tst = IWXXM31Helpers.ofAIXM.createUnitTimeSliceType();
		tst.setId(iwxxmHelpers.generateUUIDv4("utst-"+issuingCenter+"-"+dateTime));
		
		//add interpretation
		tst.setInterpretation("SNAPSHOT");
		
		//add center name
		TextNameType nType = IWXXM31Helpers.ofAIXM.createTextNameType();
		nType.setValue(issuingCenter);
		JAXBElement<TextNameType> ntType = IWXXM31Helpers.ofAIXM.createAirspaceTimeSliceTypeName(nType);
		tst.getRest().add(ntType);
		
		// add airspace type
		CodeAirspaceType asType = IWXXM31Helpers.ofAIXM.createCodeAirspaceType();
		asType.setValue("OTHER:SWXC");
		JAXBElement<CodeAirspaceType> astType = IWXXM31Helpers.ofAIXM.createAirspaceTimeSliceTypeType(asType);
		tst.getRest().add(astType);
		
		
		UnitTimeSlicePropertyType unitTs = IWXXM31Helpers.ofAIXM.createUnitTimeSlicePropertyType();		
		unitTs.setUnitTimeSlice(tst);
		//place unit in envelop
		unit.getTimeSlice().add(unitTs);
		unit.setId(iwxxmHelpers.generateUUIDv4("uts-"+issuingCenter+"-"+dateTime));
		
		
		
		
		
		unitType.setUnit(unit);
		
		//return aixm:unit element
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
	
	/**create link for effect from WMO repository*/
	public SpaceWeatherPhenomenaType createPhenomena(String effect) {
		
		SpaceWeatherPhenomenaType phenomena = IWXXM31Helpers.ofIWXXM.createSpaceWeatherPhenomenaType();
		String effectLink = iwxxmHelpers.getSpaceWeatherReg().getWMOUrlByCode(effect);		
		phenomena.setHref(effectLink);
		
		return phenomena;
		
	}
	
	/**creates section for observed location of phenomenas*/
	public SpaceWeatherAnalysisPropertyType createObservationAnalysisSection(String issuingCenter, SpaceWeatherEffectLocation observedLocation) {
		SpaceWeatherAnalysisPropertyType analysisSection = IWXXM31Helpers.ofIWXXM.createSpaceWeatherAnalysisPropertyType();
		SpaceWeatherAnalysisType analysisType = IWXXM31Helpers.ofIWXXM.createSpaceWeatherAnalysisType();
		analysisType.setTimeIndicator(TimeIndicatorType.OBSERVATION);
		
		//set time of phenomena
		AbstractTimeObjectPropertyType analysisTimeProperty = iwxxmHelpers.createAbstractTimeObject(observedLocation.getEffectsDateTime(), issuingCenter);			
		analysisType.setPhenomenonTime(analysisTimeProperty);
		
		//process hemisheres in observation
		for(String hemi:observedLocation.getHemiSpheres()) {
			SpaceWeatherRegionPropertyType regionSection = createSpaceWeatherRegion(hemi,getListOfCoords());
			analysisType.getRegion().add(regionSection);
		}
			
		analysisSection.setSpaceWeatherAnalysis(analysisType);
		return analysisSection;
		
	}
	
	
	
	/**creates new iwxxm:SpaceWeatherRegion for observation or forecasting section for hemisphere and coord list
	 * saves it to temporary storage
	 * seeks for storage and return previously created region if it already exists
	 * */
	/** <iwxxm:SpaceWeatherRegion gml:id="uuid.186a603f-7a4a-4873-9ec7-29faccd2015e">
                    <iwxxm:location>
                        <aixm:AirspaceVolume gml:id="uuid.7138f8d1-9854-46cb-a1b1-9d96c5cee46f">
                            <aixm:horizontalProjection>
                                <aixm:Surface gml:id="uuid.3b00652b-4bed-4655-959c-1c9efd431cac" srsDimension="2" axisLabels="Lat Long" srsName="http://www.opengis.net/def/crs/EPSG/0/4326">
                                    <gml:patches>
                                        <gml:PolygonPatch>
                                            <gml:exterior>
                                                <gml:LinearRing>
                                                    <gml:posList>60 180 60 -180 90 -180 90 180 60 180</gml:posList>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:PolygonPatch>
                                    </gml:patches>
                                </aixm:Surface>
                            </aixm:horizontalProjection>
                        </aixm:AirspaceVolume>
                    </iwxxm:location>
                    <iwxxm:locationIndicator xlink:href="http://codes.wmo.int/49-2/SpaceWxLocation/HNH"/>
                </iwxxm:SpaceWeatherRegion>**/
	public SpaceWeatherRegionPropertyType createSpaceWeatherRegion(String hemi, List<Double> coords) {
		
		SpaceWeatherRegionPropertyType regionSection = IWXXM31Helpers.ofIWXXM.createSpaceWeatherRegionPropertyType();
		SpaceWeatherRegionType region = IWXXM31Helpers.ofIWXXM.createSpaceWeatherRegionType();
		
		region.setId(iwxxmHelpers.generateUUIDv4("region-"+hemi));
		
		//create location
		
		
		
		
		AirspaceVolumePropertyType airspaceVolumeSection = IWXXM31Helpers.ofIWXXM.createAirspaceVolumePropertyType();
		AirspaceVolumeType airspaceVolume = IWXXM31Helpers.ofAIXM.createAirspaceVolumeType();
		airspaceVolume.setId(iwxxmHelpers.generateUUIDv4("airspace-"+hemi));
		
	
	
		
		//create projection
		SurfacePropertyType surfaceSection = IWXXM31Helpers.ofAIXM.createSurfacePropertyType();
		SurfaceType sfType = IWXXM31Helpers.ofAIXM.createSurfaceType();
		sfType.getAxisLabels().add("Lat");
		sfType.getAxisLabels().add("Long");
		sfType.setSrsDimension(BigInteger.valueOf(2));
		sfType.setSrsName("http://www.opengis.net/def/crs/EPSG/0/4326");
		sfType.setId(iwxxmHelpers.generateUUIDv4("surface-"+hemi));
		
		//add gml patches
		SurfacePatchArrayPropertyType patchArray = IWXXM31Helpers.ofGML.createSurfacePatchArrayPropertyType();
		
		//create polygon
		PolygonPatchType patchType = IWXXM31Helpers.ofGML.createPolygonPatchType();
		AbstractRingPropertyType ringType = IWXXM31Helpers.ofGML.createAbstractRingPropertyType();
		LinearRingType linearRingType = IWXXM31Helpers.ofGML.createLinearRingType();
		
		//fill polygon with coords
		DirectPositionListType dpListType=  IWXXM31Helpers.ofGML.createDirectPositionListType();
		dpListType.getValue().addAll(coords);
		linearRingType.setPosList(dpListType);
		
		//put polygon in the envelope
		JAXBElement<LinearRingType> lrPt = IWXXM31Helpers.ofGML.createLinearRing(linearRingType);
		ringType.setAbstractRing(lrPt);
		patchType.setExterior(ringType);
		
		
		JAXBElement<PolygonPatchType> patch = IWXXM31Helpers.ofGML.createPolygonPatch(patchType);
		patchArray.getAbstractSurfacePatch().add(patch);
		
		JAXBElement<SurfacePatchArrayPropertyType> pta = IWXXM31Helpers.ofGML.createPatches(patchArray);
		
		sfType.setPatches(pta);
		
		JAXBElement<SurfaceType> syrfaceElement = IWXXM31Helpers.ofAIXM.createSurface(sfType);
		surfaceSection.setSurface(syrfaceElement);
		JAXBElement<SurfacePropertyType> spt = IWXXM31Helpers.ofAIXM.createAirspaceVolumeTypeHorizontalProjection(surfaceSection);
		//create aixm:horizontalProjection
		airspaceVolume.setHorizontalProjection(spt);
		
		//JAXBElement<AirspaceVolumePropertyType> locationSection =  iwxxmHelpers.ofIWXXM.createSpaceWeatherRegionTypeLocation(airspaceVolumeSection);
		
		
		//region.getRest().add(locationSection);
		airspaceVolumeSection.setAirspaceVolume(airspaceVolume);
		
		//put in iwxxm:region the AirSpaceVolume tree
		JAXBElement<AirspaceVolumePropertyType> locationSection = IWXXM31Helpers.ofIWXXM.createSpaceWeatherRegionTypeLocation(airspaceVolumeSection);	
		region.getRest().add(locationSection);
		
		
		//add locationIndicator node
		SpaceWeatherLocationType locationType = IWXXM31Helpers.ofIWXXM.createSpaceWeatherLocationType();
		locationType.setHref(iwxxmHelpers.getSpaceWeatherLocationReg().getWMOUrlByCode(hemi));
		JAXBElement<SpaceWeatherLocationType> locationIndicatorSection = IWXXM31Helpers.ofIWXXM.createSpaceWeatherRegionTypeLocationIndicator(locationType);
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

		JAXBElement<SpaceWeatherAdvisoryType> swxRootElement = IWXXM31Helpers.ofIWXXM.createSpaceWeatherAdvisory(reportType);

		jaxbMarshaller.marshal(swxRootElement, stream);

		return stream.toString("UTF-8");
	}
}
