package org.gamc.gis.common;

import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.gamc.gis.model.GTCalculatedRegion;
import org.gamc.gis.model.GTCoordPoint;
import org.gamc.gis.model.GTCoordinate;
import org.gamc.gis.model.GTDirectionFromLine;
import org.gamc.gis.model.GTLine;
import org.gamc.gis.service.GeoServiceException;
import org.junit.BeforeClass;
import org.junit.Test;

public class LineTest {

	@BeforeClass
	public static void fillPointList() throws URISyntaxException {
		GisToolsTest.gs.init(false, "", true);
	}

	@Test
	public void lineTest() throws GeoServiceException {
		GTLine l = new GTLine();
		l.addPoint(new GTCoordPoint(new GTCoordinate("N", 56, 20), new GTCoordinate("E", 120, 30)));
		l.addPoint(new GTCoordPoint(new GTCoordinate("N", 52, 40), new GTCoordinate("E", 116, 15)));
		l.addPoint(new GTCoordPoint(new GTCoordinate("N", 49, 53), new GTCoordinate("E", 115, 28)));
		GTDirectionFromLine line = new GTDirectionFromLine("E", l);

		LinkedList<GTDirectionFromLine> lines = new LinkedList<GTDirectionFromLine>();
		lines.add(line);

		List<GTCalculatedRegion> regions = GisToolsTest.gs.recalcFromLines("UIII", lines);
		assertNotNull(regions);
		System.out.println(regions);

		for (GTCalculatedRegion reg : regions) {
			for (int index = 0; index < reg.getCoordinates().size() - 1; index = index + 2) {
				System.out.println(String.format("[%f, %f],", reg.getCoordinates().get(index),
						reg.getCoordinates().get(index + 1)));
			}
		}

		String result = GisToolsTest.gs.jsonFromLines("UIII", lines);
		assertNotNull(result);
		System.out.println(result);
	}

}
