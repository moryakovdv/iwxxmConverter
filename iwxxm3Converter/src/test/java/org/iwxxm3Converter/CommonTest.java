package org.iwxxm3Converter;

import static org.junit.Assert.assertNotNull;

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
import org.joda.time.DateTime;
import org.junit.Test;

import schemabindings31._int.icao.iwxxm._3.METARType;
import schemabindings31._int.icao.iwxxm._3.SIGMETEvolvingConditionCollectionPropertyType;
import schemabindings31._int.icao.iwxxm._3.SIGMETEvolvingConditionCollectionType;
import schemabindings31._int.icao.iwxxm._3.SIGMETType;
import schemabindings31._int.icao.iwxxm._3.TimeIndicatorType;
import schemabindings31.net.opengis.gml.v_3_2_1.AssociationRoleType;

public class CommonTest {

	public void testDateTime() {

		assertNotNull(DateTime.now());

	}

	public void testAcessibility() {

	}
	IWXXM31Helpers helpers = new IWXXM31Helpers();
	
	@Test
	public void sigmetFake() throws UnsupportedEncodingException, JAXBException {
		SIGMETType sigmet = new SIGMETType();
		String emptySigmet = marshallMessageToXML(sigmet);
		
		AssociationRoleType art = helpers.getOfGML().createAssociationRoleType();

		
		SIGMETEvolvingConditionCollectionType c = helpers.getOfIWXXM().createSIGMETEvolvingConditionCollectionType();
		
		c.setTimeIndicator(TimeIndicatorType.OBSERVATION);
		
		JAXBElement<SIGMETEvolvingConditionCollectionType> c1 = helpers.getOfIWXXM()
				.createSIGMETEvolvingConditionCollection(c);

		

		art.setAny(c1);
		
		sigmet.getAnalysis().add(art);
		String sigmetS = marshallMessageToXML(sigmet);
		System.out.println(sigmetS);
		
	}

	public String marshallMessageToXML(SIGMETType sigmet) throws JAXBException, UnsupportedEncodingException {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		JAXBContext jaxbContext = JAXBContext.newInstance(SIGMETType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, UriConstants.GLOBAL_SCHEMAS_LOCATION);

		jaxbMarshaller.setProperty(StringConstants.SUN_JAXB_NAMESPACE_MAPPING_PROPERTY_NAME, new NamespaceMapper());

		JAXBElement<SIGMETType> metarRootElement = helpers.getOfIWXXM().createSIGMET(sigmet);

		jaxbMarshaller.marshal(metarRootElement, stream);

		return stream.toString("UTF-8");
	}
}
