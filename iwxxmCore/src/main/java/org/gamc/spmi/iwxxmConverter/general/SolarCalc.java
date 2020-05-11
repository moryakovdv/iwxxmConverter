package org.gamc.spmi.iwxxmConverter.general;

import org.joda.time.DateTime;

/** Helper class to perform Sun's calculations for SpaceWeather */
public class SolarCalc {

	/**
	 * equation of time in minutes returns time in minutes between mean and real Sun
	 */
	public double calcEquationOfTime(DateTime dt) {
		double W = Math.toRadians(360) / 365.24;
		double D = dt.getDayOfYear() - 1;
		double A = W * (D + 10);
		double wd = W * (D - 2);

		double B = A + (Math.toRadians(360) / Math.PI) * 0.0167 * Math.sin(wd);

		double l = Math.toRadians(23.44);

		double C = (A - Math.atan(Math.tan(B) / Math.cos(l))) / Math.PI;

		double result = (C - Math.rint(C));

		return result * 720; // 12 hours (720 minutes) to half-turn

	}

	/** Sun's declination for the given time **/
	public double calcSunDeclination(DateTime dt) {

		double D = dt.getDayOfYear() - 1;
		double part1 = Math.sin(Math.toRadians(0.98565 * (D + 2)));

		double part2 = Math.toRadians(1.914) * part1;
		double part3 = Math.toRadians(0.98565) * (D + 10);
		double partCos = 0.39779 * Math.cos(part3 + part2);

		double res = Math.asin(partCos);
		return Math.toDegrees(res * (-1.0));
	}

	/**
	 * get sub-solar point latitude
	 * 
	 * @param dt - DateTime to calc
	 * @return latitude in decimal coordinates
	 */
	public double calcSubSolarPointLatitude(DateTime dt) {
		return calcSunDeclination(dt);
	}

	/**
	 * get sub-solar point longitude
	 * 
	 * @param dt - DateTime to calc
	 * @return longitude in decimal coordinates
	 */
	public double calcSubSolarPointLongitude(DateTime dt) {

		double equation1 = calcEquationOfTime(dt);
		double diff = dt.getMinuteOfDay() + equation1;
		double deg = (diff / (60 * 24)) * 360;
		double solarPointLongitude = 180.0 - deg;
		return solarPointLongitude;

	}

	/**
	 * get sub-solar point longitude
	 * 
	 * @param dt - DateTime to calc
	 * @return String of lat lon in decimal coordinates
	 */
	public String getSubSolarLatLon(DateTime dt) {
		return String.format("%.2f %.2f", calcSubSolarPointLatitude(dt), calcSubSolarPointLongitude(dt));

	}

}