package org.gamc.spmi.iwxxmConverter.test.common;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.gamc.gis.model.GTCalculatedRegion;
import org.gamc.gis.model.GTCoordPoint;
import org.gamc.gis.service.GeoService;
import org.gamc.gis.service.GeoServiceException;
import org.junit.BeforeClass;
import org.junit.Test;

public class GeoToolsTest {

	
static LinkedList<GTCoordPoint> pointList = new LinkedList<GTCoordPoint>();
	
	@BeforeClass
	public static void fillPointList() {
		/*
		 * LIMM
		 * N4600 E00900 - N4349 E00727 - N4313 E00924 - N4330 E01333 -
		 * N4434 E01133 - N4600 E01331 - N4631 E01340 - N4600 E00900 
   		 */
		GTCoordPoint p1 = new GTCoordPoint("N", 46, 0,  "E", 9, 0);
		GTCoordPoint p2 = new GTCoordPoint("N", 43, 49, "E", 7, 27);
		GTCoordPoint p3 = new GTCoordPoint("N", 43, 13, "E", 9, 24);
		GTCoordPoint p4 = new GTCoordPoint("N", 43, 30, "E", 13, 33);
		GTCoordPoint p5 = new GTCoordPoint("N", 44, 34, "E", 11, 33);
		GTCoordPoint p6 = new GTCoordPoint("N", 46, 0,  "E", 13, 31);
		GTCoordPoint p7 = new GTCoordPoint("N", 46, 31, "E", 13, 40);
		GTCoordPoint p8 = new GTCoordPoint("N", 46, 0,  "E", 9, 0);
		pointList.add(p1);
		pointList.add(p2);
		pointList.add(p3);
		pointList.add(p4);
		pointList.add(p5);
		pointList.add(p6);
		pointList.add(p7);
		pointList.add(p8);
	}

	@Test
	public void testJsonPolygon() throws URISyntaxException, GeoServiceException {
		
		GeoService gs = new GeoService();
		gs.init(false, "");
		String result = gs.jsonFromPolygon("LIMM", pointList);
		assertNotNull(result);
		System.out.println(result);
		
	}
	
	@Test
	public void testPolygon() throws URISyntaxException, GeoServiceException {
		
		GeoService gs = new GeoService();
		gs.init(false, "");
		List<GTCalculatedRegion> result = gs.recalcFromPolygon("ZYSH", pointList);
		assertNotNull(result);
		System.out.println(result);
		
	}

}
