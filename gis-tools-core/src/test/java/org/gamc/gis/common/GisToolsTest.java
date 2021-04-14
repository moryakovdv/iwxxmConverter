/**
 * Copyright (C) 2018 Dmitry Moryakov, Main aeronautical meteorological center, Moscow, Russia
 * moryakovdv[at]gmail[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gamc.gis.common;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.gamc.gis.model.GTCalculatedRegion;
import org.gamc.gis.model.GTCoordPoint;
import org.gamc.gis.model.GTCoordinate;
import org.gamc.gis.model.GTDirectionFromLine;
import org.gamc.gis.model.GTLine;
import org.gamc.gis.service.GeoService;
import org.gamc.gis.service.GeoServiceException;
import org.junit.BeforeClass;
import org.junit.Test;

public class GisToolsTest {

	static LinkedList<GTCoordPoint> pointList = new LinkedList<GTCoordPoint>();
	static GeoService gs = new GeoService();
	@BeforeClass
	public static void fillPointList() throws URISyntaxException {
		/*
		 * LIMM N4600 E00900 - N4349 E00727 - N4313 E00924 - N4330 E01333 - N4434 E01133
		 * - N4600 E01331 - N4631 E01340 - N4600 E00900
		 */
		GTCoordPoint p1 = new GTCoordPoint("N", 46, 0, "E", 9, 0);
		GTCoordPoint p2 = new GTCoordPoint("N", 43, 49, "E", 7, 27);
		GTCoordPoint p3 = new GTCoordPoint("N", 43, 13, "E", 9, 24);
		GTCoordPoint p4 = new GTCoordPoint("N", 43, 30, "E", 13, 33);
		GTCoordPoint p5 = new GTCoordPoint("N", 44, 34, "E", 11, 33);
		GTCoordPoint p6 = new GTCoordPoint("N", 46, 0, "E", 13, 31);
		GTCoordPoint p7 = new GTCoordPoint("N", 46, 31, "E", 13, 40);
		GTCoordPoint p8 = new GTCoordPoint("N", 46, 0, "E", 9, 0);
		pointList.add(p1);
		pointList.add(p2);
		pointList.add(p3);
		pointList.add(p4);
		pointList.add(p5);
		pointList.add(p6);
		pointList.add(p7);
		pointList.add(p8);
		
		gs.init(false, "", true);
	}

	@Test
	public void testJsonPolygon() throws URISyntaxException, GeoServiceException {

		
		String result = gs.jsonFromPolygon("LIMM", pointList);
		assertNotNull(result);
		System.out.println(result);

	}

	@Test
	public void testJsonCircle() throws URISyntaxException, GeoServiceException {
		String result = gs.jsonFromPoint(pointList.get(0), 50000);
		assertNotNull(result);
		System.out.println(result);

	}

	@Test
	public void testPolygon() throws URISyntaxException, GeoServiceException {

		
		List<GTCalculatedRegion> result = gs.recalcFromPolygon("ZYSH", pointList);
		assertNotNull(result);
		System.out.println(result);

	}

	@Test
	/* UEEE YAKUTSK FIR SEV TURB FCST N OF N70 AND E OF E135 */
	public void test135() throws URISyntaxException, GeoServiceException {
	
		GTDirectionFromLine lineN70 = new GTDirectionFromLine("N", new GTLine(new GTCoordinate("N", 70, 0)));
		GTDirectionFromLine lineE135 = new GTDirectionFromLine("E", new GTLine(new GTCoordinate("E", 135, 0)));

		LinkedList<GTDirectionFromLine> lines = new LinkedList<GTDirectionFromLine>();
		lines.add(lineN70);
		lines.add(lineE135);

		List<GTCalculatedRegion> regions = gs.recalcFromLines("UEEE", lines);
		assertNotNull(regions);
		System.out.println(regions);

		String result = gs.jsonFromLines("UEEE", lines);
		assertNotNull(result);
		System.out.println(result);
	}

	@Test
	public void testEntireFir() throws URISyntaxException, GeoServiceException {
		
		List<GTCalculatedRegion> regions = gs.recalcEntireFir("UEEE");
		assertNotNull(regions);
		System.out.println(regions);

		String result = gs.jsonEntireFir("UEEE");
		assertNotNull(result);
		System.out.println(result);
	}

	/**
	 * from reference sigmet
	 * 
	 * WSRS32 RUAA 010154 UUYY SIGMET 1 VALID 010200/010600 UUYY- UUYY SYKTYVKAR FIR
	 * SEV TURB FCST W OF E06000 FL240/370 MOV NE 30KMH NC=
	 * 
	 * 
	 */
	@Test
	public void testReferences() throws URISyntaxException, GeoServiceException {
	
		GTDirectionFromLine lineW60 = new GTDirectionFromLine("W", new GTLine(new GTCoordinate("E", 60, 0)));

		LinkedList<GTDirectionFromLine> lines = new LinkedList<GTDirectionFromLine>();
		lines.add(lineW60);

		List<GTCalculatedRegion> regions = gs.recalcFromLines("UUYY", lines);
		assertNotNull(regions);
		System.out.println(regions);

		
		for (GTCalculatedRegion reg : regions) {
			for(int index=0;index<reg.getCoordinates().size()-1;index=index+2) {
				System.out.println(String.format("[%f, %f],",reg.getCoordinates().get(index),reg.getCoordinates().get(index+1)));
			}
		}
		
		String result = gs.jsonFromLines("UUYY", lines);
		assertNotNull(result);
		System.out.println(result);
	}
	
	@Test
	public void test6530() throws URISyntaxException, GeoServiceException {
		
		GTDirectionFromLine lineW60 = new GTDirectionFromLine("S", new GTLine(new GTCoordinate("N", 65, 30)));

		LinkedList<GTDirectionFromLine> lines = new LinkedList<GTDirectionFromLine>();
		lines.add(lineW60);

		List<GTCalculatedRegion> regions = gs.recalcFromLines("UEEE", lines);
		assertNotNull(regions);
		System.out.println(regions);

		
		for (GTCalculatedRegion reg : regions) {
			for(int index=0;index<reg.getCoordinates().size()-1;index=index+2) {
				System.out.println(String.format("[%f, %f],",reg.getCoordinates().get(index),reg.getCoordinates().get(index+1)));
			}
		}
		
		String result = gs.jsonFromLines("UEEE", lines);
		assertNotNull(result);
		System.out.println(result);
	}

}
