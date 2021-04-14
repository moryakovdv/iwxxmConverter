package org.gamc.gis.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.gamc.gis.model.GTCalculatedRegion;
import org.gamc.gis.model.GTCoordPoint;
import org.junit.Test;

public class UtilTest {

	@Test
	public void recalcPoint() {
		GTCoordPoint p = new GTCoordPoint("", 50, 30, "", 75, 45); 
		GTCalculatedRegion r = GisToolsTest.gs.recalcFromSinglePoint(p);
		assertNotNull(r);
		assertEquals(r.getCoordinates().size(), 2);
		assertEquals((double)(r.getCoordinates().get(0) - r.getCoordinates().get(0).intValue()), 0.5, 0);
		assertEquals((double)(r.getCoordinates().get(1) - r.getCoordinates().get(1).intValue()), 0.75, 0);
		r.getCoordinates().forEach(e -> System.out.println(e.doubleValue()));
	}

}
