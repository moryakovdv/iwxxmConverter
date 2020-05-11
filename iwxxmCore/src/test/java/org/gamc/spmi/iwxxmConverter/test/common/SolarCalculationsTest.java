package org.gamc.spmi.iwxxmConverter.test.common;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.TimeZone;

import org.gamc.spmi.iwxxmConverter.general.SolarCalc;
import org.gamc.spmi.iwxxmConverter.general.SolarCalculations;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

public class SolarCalculationsTest {

	/*
	 * Testing Sun's calculations for SpaceWeather sub-solar point in a given time
	 */

	String[] dateTimes = new String[] { "2016-11-08T01:00:00Z", "2016-11-08T07:00:00Z", "2016-11-08T13:00:00Z",
			"2016-11-08T19:00:00Z" };
	double[][] latLongs = new double[][] { 
		{ -16.64, 160.98 }, 
		{ -16.71, 70.94 }, 
		{ -16.78, -19.05},
		{ -16.86, -109.05}
	};

	//@Test
	public void test() {
		SolarCalc sc = new SolarCalc();
		for (int index = 0; index < dateTimes.length; index++) {
			DateTime dt = DateTime.parse(dateTimes[index]).withZone(DateTimeZone.UTC);
			//System.out.println(dt);

			double decl = sc.calcSunDeclination(dt);
			//System.out.println(decl);
			double equation1 = sc.calcEquationOfTime(dt); // minutes between real sun and mean sun

			//System.out.println(equation1);

			double solarPointLatitude = sc.calcSubSolarPointLatitude(dt);
			double solarPointLongitude = sc.calcSubSolarPointLongitude(dt);

			System.out.println(sc.getSubSolarLatLon(dt));

			// should be near -16.64 160.94 for 2016-11-08T01:00:00Z
			
			assertEquals(solarPointLatitude, latLongs[index][0], 0.5);
			assertEquals(solarPointLongitude, latLongs[index][1], 0.5);
		}

	}
	
	@Test
	public void test24Hour() {
		SolarCalc sc = new SolarCalc();
		for (int index = 0; index < 24; index++) {
			DateTime dt = DateTime.now().withZone(DateTimeZone.UTC).withTimeAtStartOfDay().plusHours(index);
			//System.out.println(dt);

			double decl = sc.calcSunDeclination(dt);
			//System.out.println(decl);
			double equation1 = sc.calcEquationOfTime(dt); // minutes between real sun and mean sun

			//System.out.println(equation1);

		

			System.out.println(dt +" "+sc.getSubSolarLatLon(dt));

		
		}

	}
	@Test
	public void testDeclination() {
		SolarCalculations sc = new SolarCalculations();
		for (int index = 0; index < 24; index++) {
			DateTime dt = DateTime.now().withZone(DateTimeZone.UTC).withTimeAtStartOfDay().plusHours(index);
		
			System.out.println(sc.calcSolarDeclination(dt.toGregorianCalendar()));
			
		}
		
	}

}
