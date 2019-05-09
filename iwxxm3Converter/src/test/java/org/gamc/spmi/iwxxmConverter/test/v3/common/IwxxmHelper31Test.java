package org.gamc.spmi.iwxxmConverter.test.v3.common;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.gamc.spmi.iwxxmConverter.common.NamespaceMapper;
import org.gamc.spmi.iwxxmConverter.common.StringConstants;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.IWXXM31Helpers;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.UriConstants;
import org.junit.Test;

import schemabindings31._int.icao.iwxxm._3.METARType;

public class IwxxmHelper31Test {

	IWXXM31Helpers helper = new IWXXM31Helpers();
	public static final schemabindings31._int.icao.iwxxm._3.ObjectFactory ofIWXXM = new schemabindings31._int.icao.iwxxm._3.ObjectFactory();
	public static final schemabindings31.net.opengis.gml.v_3_2_1.ObjectFactory ofGML = new schemabindings31.net.opengis.gml.v_3_2_1.ObjectFactory();
	public static final schemabindings31.net.opengis.om._2.ObjectFactory ofOM = new schemabindings31.net.opengis.om._2.ObjectFactory();
	public static final schemabindings31.org.w3._1999.xlink.ObjectFactory ofXLink = new schemabindings31.org.w3._1999.xlink.ObjectFactory();
	public static final schemabindings31.aero.aixm.schema._5_1.ObjectFactory ofAIXM = new schemabindings31.aero.aixm.schema._5_1.ObjectFactory();
	public static final schemabindings31._int.wmo.def.metce._2013.ObjectFactory ofMetce = new schemabindings31._int.wmo.def.metce._2013.ObjectFactory();
	
	//@Test
	public void testAirport() throws UnsupportedEncodingException, JAXBException {
		//JAXBElement<AirportHeliportType> ft =helper.createAirportDescriptionSectionTag("UUWW");
		//marshallMessageToXML(ft.getClass(), ft);
	}
	
	@Test
	public void testEnvelope() throws UnsupportedEncodingException, JAXBException {
		METARType mt=  ofIWXXM.createMETARType();
		mt.setAerodrome(helper.createAirportDescriptionSectionTag("UUWW"));
		JAXBElement<METARType> jmt = ofIWXXM.createMETAR(mt);
		
		System.out.println(marshallMessageToXML(mt.getClass(),jmt));
	}
	
	
	public String marshallMessageToXML(Class<?> featureClass, JAXBElement<?> element) throws JAXBException, UnsupportedEncodingException {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		JAXBContext jaxbContext = JAXBContext.newInstance(featureClass);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, UriConstants.GLOBAL_SCHEMAS_LOCATION);

		jaxbMarshaller.setProperty(StringConstants.SUN_JAXB_NAMESPACE_MAPPING_PROPERTY_NAME, new NamespaceMapper());

		
		jaxbMarshaller.marshal(element, stream);

		return stream.toString("UTF-8");
	}

}
