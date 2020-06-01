package org.gamc.spmi.iwxxmConverter.test.v3.sigmet;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.gis.model.GTCalculatedRegion;
import org.gamc.gis.model.GTCoordPoint;
import org.gamc.gis.model.GTDirectionFromLine;
import org.gamc.gis.service.GeoService;
import org.gamc.gis.service.GeoServiceException;
import org.gamc.spmi.iwxxmConverter.common.CoordPoint;
import org.gamc.spmi.iwxxmConverter.common.DirectionFromLine;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETConverterV3;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETTacMessage;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SIGMETParsingException;
import org.gamc.spmi.iwxxmConverter.validation.FailedValidationAssert;
import org.gamc.spmi.iwxxmConverter.validation.IwxxmValidator;
import org.junit.Test;

public class SigmetRealFirCoordinatesTest {

	String referenceSigmet1 = "WSCH31SCFA 011035\n" + 
			"SCFZ SIGMET A1 VALID 011035/011435 SCFA-\n" + 
			"SCFZ ANTOFAGASTA FIR SEV ICE FCST E OF LINE S2127 W06840 - S2320 \n" + 
			"W06803 - S2442 W06846 FL180/280 STNR NC=";
	
	double[][] coords1 = new double[][] {
		
		{-21.45, -68.67},
		{-23.33, -68.05},
		{-24.70, -68.77},
		{-24.69, -68.51},
		{-24.67, -68.51},
		{-24.50, -68.33},
        {-24.37, -68.19},
        {-24.25, -67.90},
        {-24.02, -67.35},
        {-23.69, -67.24},
        {-23.00, -67.00},
        {-22.82, -67.18},
        {-22.85, -67.33},
        {-22.90, -67.58},
        {-22.88, -67.79},
        {-22.71, -67.88},
        {-22.56, -67.85},
        {-22.42, -67.90},
        {-22.28, -67.93},
        {-22.05, -68.00},
        {-21.84, -68.09},
        {-21.61, -68.19},
        {-21.44, -68.19},
        {-21.45, -68.67},
		
		
	};
	
	String referenceSigmet2 = "WSRS32 RUAA 010154\n" + 
			"UUYY SIGMET 1 VALID 010200/010600 UUYY-\n" + 
			"UUYY SYKTYVKAR FIR SEV TURB FCST W OF E06000\n" + 
			"FL240/370 MOV NE 30KMH NC=";
	double[][] coords2 = new double[][] {
		{71.26,60.00},
		{71.17,57.65},
		{70.72,57.60},
		{69.47,58.57},
		{68.97,59.32},
		{68.46,59.70},
		{68.38,59.59},
        {68.28,59.47},
        {68.19,59.36},
        {68.10,59.25},
        {68.01,59.13},
        {67.92,59.02},
        {67.83,58.91},
        {67.74,58.81},
        {67.55,58.59},
        {67.37,58.38},
        {67.18,58.17},
        {67.00,57.97},
        {67.03,55.13},
        {66.95,53.97},
        {66.78,51.88},
        {65.82,50.00},
        {64.83,50.27},
        {64.72,50.03},
        {64.61,49.80},
        {64.50,49.57},
        {64.38,49.34},
        {64.27,49.12},
        {64.16,48.89},
        {64.04,48.67},
        {63.93,48.45},
        {63.81,48.23},
        {63.70,48.02},
        {63.58,47.80},
        {63.47,47.59},
        {63.35,47.38},
        {63.23,47.16},
        {63.12,46.96},
        {63.00,46.75},
        {62.00,48.17},
        {60.33,48.67},
        {60.00,50.00},
        {60.00,52.00},
        {60.72,55.70},
        {61.17,57.22},
        {62.00,59.50},
        {63.20,59.72},
        {64.22,60.00},
        {71.26,60.00}
		
	};
	@Test
	public void testRefSigmet1() throws Exception {
		
		SIGMETConverterV3 sc = new SIGMETConverterV3();
		String s1Result = sc.convertTacToXML(referenceSigmet1);
		System.out.println(s1Result);
		IwxxmValidator v = new IwxxmValidator();
		v.init();
		List<FailedValidationAssert> asserts = v.validateString(s1Result);
		
		System.out.println(asserts);
		
		
	}

	@Test
	public void testRefSigmet2() throws Exception {
		
		SIGMETConverterV3 sc = new SIGMETConverterV3();
		String s2Result = sc.convertTacToXML(referenceSigmet2);
		System.out.println(s2Result);
		IwxxmValidator v = new IwxxmValidator();
		v.init();
		List<FailedValidationAssert> asserts = v.validateString(s2Result);
		
		System.out.println(asserts);
		
	}
	
	@Test
	public void tesRefSigmet2Json() throws URISyntaxException, SIGMETParsingException, GeoServiceException {
		GeoService gs = new GeoService();
		gs.init(false, "", true);
		SIGMETTacMessage tac = new SIGMETTacMessage(referenceSigmet2);
		tac.parseMessage();
		
		assertTrue(tac.getHorizontalLocation().getDirectionsFromLines().size()>0);
		System.out.println(tac.getHorizontalLocation().getPolygonPoints());
		LinkedList<GTDirectionFromLine> list = new LinkedList<GTDirectionFromLine>();
		tac.getHorizontalLocation().getDirectionsFromLines()
				.stream().forEach(new Consumer<DirectionFromLine>() {
					
					@Override
					public void accept(DirectionFromLine arg0) {
						
						list.add(arg0.toGTDirectionFromLine());
					}
				});
		
		
		List<GTCalculatedRegion> regions = gs.recalcFromLines(tac.getFirCode(), list);
		assertNotNull(regions);
		System.out.println(regions);
		
		
		String geoJson = gs.jsonFromLines(tac.getFirCode(), list);
		System.out.println(geoJson);
	}
	
}
