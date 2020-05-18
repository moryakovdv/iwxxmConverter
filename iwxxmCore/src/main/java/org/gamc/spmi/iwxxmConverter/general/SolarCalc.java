/**
 * Copyright (C) 2018 Dmitry Moryakov, Main aeronautical meteorological center, Moscow, Russia
 * moryakovdv[at]gmail[dot]com
 *
 * Code with precise astronomical calculations (based on Julian time) was taken 
 * from https://github.com/LocusEnergy/solar-calculations (Copyright (c) 2015 Locus Energy)
 * and adopted for java-8 time library
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
package org.gamc.spmi.iwxxmConverter.general;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

import org.joda.time.DateTime;

/** Helper class to perform Sun's calculations for SpaceWeather */
public class SolarCalc {

	/**
	 * equation of time in minutes returns time in minutes between mean and real Sun
	 * if accuracy is set to true uses more precise calculation with Julian dates
	 */
	public double calcEquationOfTime(ZonedDateTime dt, boolean accurate) {

		if (accurate)
			return calcEquationOfTime(dt);

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

	private double calcEquationOfTime(ZonedDateTime dt) {
		double timeJulian = calcTimeJulian(dt);
		double epsilon = calcObliquityCorrection(timeJulian);
		double l0 = calcGeomMeanLongSun(timeJulian);
		double e = calcEccentricityEarthOrbit(timeJulian);
		double m = calcGeomMeanAnomalySun(timeJulian);

		double y = Trig.tanD(epsilon / 2);
		y *= y;
		double sin2l0 = Trig.sinD(2 * l0);
		double sinm = Trig.sinD(m);
		double cos2l0 = Trig.cosD(2 * l0);
		double sin4l0 = Trig.sinD(4 * l0);
		double sin2m = Trig.sinD(2 * m);
		double eqTime = y * sin2l0 - 2 * e * sinm + 4 * e * y * sinm * cos2l0 - 0.5 * y * y * sin4l0
				- 1.25 * e * e * sin2m;
		double output = Math.toDegrees(eqTime) * 4;
		return output;
	}

	private double calcEccentricityEarthOrbit(double timeJulian) {
		double output = 0.016708634 - timeJulian * (0.000042037 + 0.0000001267 * timeJulian);
		return output;
	}

	/** Sun's declination for the given time **/
	/** accurate flag switches calculation to julian time, which is more accurate */
	public double calcSunDeclination(ZonedDateTime dt, boolean accurate) {

		if (accurate)
			return calcSunDeclination(dt);

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
	public double calcSubSolarPointLatitude(ZonedDateTime dt) {
		return calcSunDeclination(dt, true);
	}

	/**
	 * get sub-solar point longitude
	 * 
	 * @param dt - DateTime to calc
	 * @return longitude in decimal coordinates
	 */
	public double calcSubSolarPointLongitude(ZonedDateTime dt) {

		double equation1 = calcEquationOfTime(dt);
		double diff = dt.get(ChronoField.MINUTE_OF_DAY) + equation1;
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
	public String getSubSolarLatLon(ZonedDateTime dt) {
		return String.format("%.2f %.2f", calcSubSolarPointLatitude(dt), calcSubSolarPointLongitude(dt));

	}

	/** calculates solar declination for given time */
	public double calcSunDeclination(ZonedDateTime dt) {
		double timeJulian = calcTimeJulian(dt);
		double e = calcObliquityCorrection(timeJulian);

		double lambda = calcSunApparentLong(timeJulian);
		double sint = Trig.sinD(e) * Trig.sinD(lambda);
		double output = Math.toDegrees(Math.asin(sint));
		return output;

	}

	/** calculates Julian time */
	public double calcTimeJulian(ZonedDateTime dt) {
		double jd = calcJulianDate(dt);
		double time = calcTimeDecimal(dt);

		double output = (jd + (time) / 24 - 2451545) / 36525;
		return output;
	}

	/** Julian date */
	private double calcJulianDate(ZonedDateTime dt) {
		int year = dt.getYear();
		int month = dt.getMonthValue();
		int day = dt.getDayOfMonth();

		if (month <= 2) {
			year--;
			month += 12;
		}

		double a = Math.floor(year / 100);
		double b = 2 - a + Math.floor(a / 4);
		double output = Math.floor(365.25 * (year + 4716)) + Math.floor(30.6001 * (month + 1)) + day + b - 1524.5;
		return output;
	}

	/** decimal fraction for time */
	private double calcTimeDecimal(ZonedDateTime dt) {

		return dt.getHour() + dt.getMinute() / 60.0 + dt.getSecond() / 3600.0;
	}

	
	private double calcObliquityCorrection(double timeJulian) {
		double e0 = calcMeanObliquityOfEcliptic(timeJulian);
		double omega = 125.04 - 1934.136 * timeJulian;
		double output = e0 + 0.00256 * Trig.cosD(omega);
		return output;
	}

	private double calcMeanObliquityOfEcliptic(double timeJulian) {
		double seconds = 21.448 - timeJulian * (46.8150 + timeJulian * (0.00059 - timeJulian * (0.001813)));
		double output = 23 + (26 + seconds / 60) / 60;
		return output;
	}

	private double calcSunApparentLong(double timeJulian) {
		double o = calcSunTrueLong(timeJulian);
		double omega = 125.04 - 1934.136 * timeJulian;
		double output = o - 0.00569 - 0.00478 * Trig.sinD(omega);
		return output;
	}

	private double calcSunTrueLong(double timeJulian) {
		double output = calcGeomMeanLongSun(timeJulian) + calcSunEqOfCenter(timeJulian);
		return output;
	}

	private double calcGeomMeanLongSun(double timeJulian) {
		double output = 280.46646 + timeJulian * (36000.76983 + 0.0003032 * timeJulian);
		while (output > 360)
			output -= 360;
		while (output < 0)
			output += 360;
		return output;
	}

	private double calcSunEqOfCenter(double timeJulian) {
		double m = calcGeomMeanAnomalySun(timeJulian);
		double output = Trig.sinD(m) * (1.914602 - timeJulian * (0.004817 + 0.000014 * timeJulian))
				+ Trig.sinD(m * 2) * (0.019993 - 0.000101 * timeJulian) + Trig.sinD(m * 3) * 0.000289;
		return output;
	}

	private double calcGeomMeanAnomalySun(double timeJulian) {
		double output = 357.52911 + timeJulian * (35999.05029 - 0.0001537 * timeJulian);
		return output;
	}

	/** Helper trogonometric class **/
	public static class Trig {

		public static double sinD(double angleDeg) {
			return Math.sin(Math.toRadians(angleDeg));
		}

		public static double cosD(double angleDeg) {
			return Math.cos(Math.toRadians(angleDeg));
		}

		public static double tanD(double angleDeg) {
			return Math.tan(Math.toRadians(angleDeg));
		}

	}

}