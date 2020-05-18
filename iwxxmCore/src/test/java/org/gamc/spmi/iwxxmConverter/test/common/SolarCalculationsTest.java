package org.gamc.spmi.iwxxmConverter.test.common;

import static org.junit.Assert.assertEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.gamc.spmi.iwxxmConverter.general.SolarCalc;
import org.junit.Test;

public class SolarCalculationsTest {

	/*
	 * Testing Sun's calculations for SpaceWeather sub-solar point in a given time
	 */

	String[] dateTimes = new String[] { "2016-11-08T01:00:00Z", "2016-11-08T07:00:00Z", "2016-11-08T13:00:00Z",
			"2016-11-08T19:00:00Z" };
	double[][] latLongs = new double[][] { { -16.64, 160.98 }, { -16.71, 70.94 }, { -16.78, -19.05 },
			{ -16.86, -109.05 } };

	@Test
	public void test() {
		SolarCalc sc = new SolarCalc();

		for (int index = 0; index < dateTimes.length; index++) {
			ZonedDateTime dt = ZonedDateTime.parse(dateTimes[index]).withZoneSameInstant(ZoneId.of("UTC"));

			double solarPointLatitude = sc.calcSubSolarPointLatitude(dt);
			double solarPointLongitude = sc.calcSubSolarPointLongitude(dt);

			System.out.println(sc.getSubSolarLatLon(dt));

			// should be near -16.64 160.94 for 2016-11-08T01:00:00Z

			assertEquals(latLongs[index][0], solarPointLatitude, 0.1);
			assertEquals(latLongs[index][1], solarPointLongitude, 0.1);
		}

	}

	@Test
	public void test24Hour() {
		SolarCalc sc = new SolarCalc();
		ZonedDateTime dt = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
		for (int index = 0; index < 24; index++) {

			System.out.println(dt + " " + sc.getSubSolarLatLon(dt));
			dt = dt.plusHours(1);
		}

	}

	// @Test
	public void testNow() {
		SolarCalc sc = new SolarCalc();

		ZonedDateTime dt = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
		// System.out.println(dt);

		System.out.println(dt + " " + sc.getSubSolarLatLon(dt));

	}

	

}
