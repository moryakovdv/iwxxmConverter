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
package org.gamc.spmi.iwxxmConverter.tafconverter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;

import org.gamc.spmi.iwxxmConverter.common.AnnotationLocaliedName;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.general.CommonWeatherSection;
import org.gamc.spmi.iwxxmConverter.general.IWXXMHelpers;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.PRESSURE_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.joda.time.DateTime;
import org.mariuszgromada.math.mxparser.Expression;

/**
 * Embedded objects of this class are used to describe common weather condition
 * in METAR itself, BECMG or TEMPO sections
 */
public class TafCommonWeatherSection implements CommonWeatherSection {

	private String tacContent;

	boolean failWhenMandatorySectionMissed = true;
	@AnnotationLocaliedName(name = "Начало действия день")
	private Integer validityDayFrom;
	@AnnotationLocaliedName(name = "Начало действия часы")
	private Integer validityHourFrom;
	@AnnotationLocaliedName(name = "Конец действия день")
	private Integer validityDayTo;
	@AnnotationLocaliedName(name = "Конец действия часы")
	private Integer validityHourTo;
	@AnnotationLocaliedName(name = "Направление ветра")
	private Integer windDir;
	@AnnotationLocaliedName(name = "Скорость ветра")
	private Integer windSpeed;
	@AnnotationLocaliedName(name = "Порыв ветра")
	private Integer gustSpeed;
	@AnnotationLocaliedName(name = "Единица измерения скорости ветра")
	private SPEED_UNITS speedUnits = SPEED_UNITS.MPS;

	// if wind direction variables and speed>6m/s
	@AnnotationLocaliedName(name = "Изменение направления ветра от")
	private Integer windVariableFrom;
	@AnnotationLocaliedName(name = "Изменение направления ветра до")
	private Integer windVariableTo;

	// VRB if wind direction variables and speed<6m/s
	@AnnotationLocaliedName(name = "Наличие перменной")
	private boolean vrb;
	@AnnotationLocaliedName(name = "Скорость ветра vrb")
	private Integer windVrbSpeed;
	@AnnotationLocaliedName(name = "Единица измерения скорости ветра vrb")
	private SPEED_UNITS vrbSpeedUnits = SPEED_UNITS.MPS;
	
	@AnnotationLocaliedName(name = "Наличие CAVOK")
	private boolean cavok = false;
	@AnnotationLocaliedName(name = "Максимальное значение прогнозируемой температуры воздуха")
	private BigDecimal airTemperatureMax;
	@AnnotationLocaliedName(name = "Время прогнозируемой максимальной температуры воздуха")
	private DateTime airTemperatureMaxTime;
	@AnnotationLocaliedName(name = "Минимальное значение прогнозируемой температуры воздуха")
	private BigDecimal airTemperatureMin;
	@AnnotationLocaliedName(name = "Время прогнозируемой минимальной температуры воздуха")
	private DateTime airTemperatureMinTime;
	@AnnotationLocaliedName(name = "Давление QNH")
	private BigDecimal qnh;
	@AnnotationLocaliedName(name = "Единица измерения давления")
	private PRESSURE_UNITS qnhUnits = PRESSURE_UNITS.HECTOPASCALS;
	@AnnotationLocaliedName(name = "Преобладающая видимость")
	private Double prevailVisibility;
	@AnnotationLocaliedName(name = "Минимальная видимость")
	private Double minimumVisibility;
	@AnnotationLocaliedName(name = "Направление минимальной видимости")
	private RUMB_UNITS minimumVisibilityDirection;
	@AnnotationLocaliedName(name = "Единица измерения видимости")
	private LENGTH_UNITS visibilityUnits = LENGTH_UNITS.M;
	@AnnotationLocaliedName(name = "Список текущих погодных явлений")
	private LinkedList<String> currentWeather = new LinkedList<String>();
	@AnnotationLocaliedName(name = "Список секций вертикальной видимости")
	private LinkedList<TAFCloudSection> cloudSections = new LinkedList<TAFCloudSection>();

	/**
	 * If we parse TEMPO or BECMG sections, we can ask parser NOT to fail when some
	 * mandatory section are missed .
	 */
	public TafCommonWeatherSection(boolean failWhenMandatorySectionMissed) {

		this.failWhenMandatorySectionMissed = failWhenMandatorySectionMissed;

	}

	@Override
	public StringBuffer parseSection(StringBuffer tac) throws TAFParsingException {
		this.tacContent = tac.toString();

		// parsing validity times
		int lastIndex = 0;

		Matcher matcher = TafParsingRegexp.tafValidity.matcher(tac);
		if (matcher.find()) {
			String sTdf = matcher.group("tafDayFrom");
			String sThf = matcher.group("tafHourFrom");
			String sTdt = matcher.group("tafDayTo");
			String sTht = matcher.group("tafHourTo");

			if (sTdf != null)
				this.setValidityDayFrom(Integer.valueOf(sTdf));
			if (sThf != null)
				this.setValidityHourFrom(Integer.valueOf(sThf));
			if (sTdt != null)
				this.setValidityDayTo(Integer.valueOf(sTdt));
			if (sTht != null)
				this.setValidityHourTo(Integer.valueOf(sTht));

			lastIndex = matcher.end();
			tac.delete(0, lastIndex);

		}

		// parsing Winds
		boolean hasWindSection = false;

		matcher = TafParsingRegexp.tafWind.matcher(tac);
		if (matcher.find()) {
			String sWd = matcher.group("windDir");
			String sWs = matcher.group("windSpeed");
			String sWsU = matcher.group("speedUnits");

			String sGs = matcher.group("gustSpeed");
			String sGsU = matcher.group("gustSpeedUnits");

			if (sWd != null)
				this.setWindDir(Integer.valueOf(sWd));

			if (sWs != null)
				this.setWindSpeed(Integer.valueOf(sWs));

			if (sWsU != null)
				this.setSpeedUnits(SPEED_UNITS.valueOf(sWsU));

			if (sGs != null)
				this.setGustSpeed(Integer.valueOf(sGs));

			if (sGsU != null)
				this.setSpeedUnits(SPEED_UNITS.valueOf(sGsU));

			lastIndex = matcher.end();
			tac.delete(0, lastIndex);
			hasWindSection = true;
		}

		// wind variable
		matcher = TafParsingRegexp.tafWindVariable.matcher(tac);
		if (matcher.find()) {
			String sVf = matcher.group("variableFrom");
			String sVt = matcher.group("variableTo");

			this.setWindVariableFrom(Integer.valueOf(sVf));
			this.setWindVariableTo(Integer.valueOf(sVt));

			lastIndex = matcher.end();
			tac.delete(0, lastIndex);
			hasWindSection = true;
		}

		// has VRB?
		matcher = TafParsingRegexp.tafWindVRB.matcher(tac);
		if (matcher.find()) {
			String sVrbS = matcher.group("vrbWindSpeed");
			String sVrbU = matcher.group("vrbWindUnits");

			this.setVrb(true);
			// this.setWindSpeed(Integer.valueOf(sVrbS));
			this.setWindVrbSpeed(Integer.valueOf(sVrbS));
			this.setSpeedUnits(SPEED_UNITS.valueOf(sVrbU));

			lastIndex = matcher.end();
			tac.delete(0, lastIndex);
			hasWindSection = true;
		}

		if (!hasWindSection && failWhenMandatorySectionMissed)
			throw new TAFParsingException("TAF mandatory wind section is missed");

		// CAVOK?

		matcher = TafParsingRegexp.tafCavok.matcher(tac);
		if (matcher.find()) {

			this.setCavok(true);
			lastIndex = matcher.end();
			tac.delete(0, lastIndex);
		}

		// process visibility in two steps - find prevail and minimum visibility
		matcher = TafParsingRegexp.tafVisibility.matcher(tac);
		if (matcher.find()) {

			if (this.isCavok())
				throw new TAFParsingException("It is CAVOK");

			String sPv = matcher.group("visibility");
			boolean isStatuteMiles = false;
			if (sPv.endsWith("SM")) {
				this.setVisibilityUnits(LENGTH_UNITS.SM);
				sPv = sPv.replaceFirst("SM$", "");
				sPv = sPv.replaceFirst("P", "");
				isStatuteMiles = true;
			}

			if (sPv != null) {

				// parse statute miles, such as 1 1/4SM, 1/4SM, 4.5SM
				if (isStatuteMiles) {
					String sms = sPv.replaceFirst("\\s", "+");
					Expression fExp = new Expression(sms);
					double result = fExp.calculate();
					this.setPrevailVisibility(BigDecimal.valueOf(result).doubleValue());
				} else
					this.setPrevailVisibility(new BigDecimal(sPv).doubleValue());
			}

			lastIndex = matcher.end();
			tac.delete(0, lastIndex);
		}
		/*
		 * matcher.reset();
		 * 
		 * if (matcher.find() && matcher.start() <= 1) {
		 * 
		 * if (this.isCavok()) throw new TAFParsingException("It is CAVOK");
		 * 
		 * String sMv = matcher.group("visibility"); String sMVd =
		 * matcher.group("visibilityDirection"); if (sMv != null)
		 * this.setMinimumVisibility(Integer.valueOf(sMv)); if (sMVd != null)
		 * this.setMinimumVisibilityDirection(RUMB_UNITS.valueOf(sMVd));
		 * 
		 * lastIndex = matcher.end(); tac.delete(0, lastIndex); }
		 */
		// process precipitations

		matcher = TafParsingRegexp.tafPrecipitation.matcher(tac);
		while (matcher.find()) {

			String curWeather = matcher.group("weather");
			this.getCurrentWeather().add(curWeather);

			lastIndex = matcher.end();

		}

		if (this.getCurrentWeather().size() > 0)
			tac.delete(0, lastIndex);

		// processClouds
		matcher = TafParsingRegexp.tafClouds.matcher(tac);
		while (matcher.find()) {

			if (this.isCavok())
				throw new TAFParsingException("It is CAVOK");

			int start = matcher.start();
			int end = matcher.end();

			String cloudAmount = matcher.group("cloudAmount");

			String cloudHeight = matcher.group("cloudHeight");
			String cloudType = matcher.group("cloudType");
			TAFCloudSection cloudSec = new TAFCloudSection(tac.substring(start, end));

			cloudSec.setAmount(cloudAmount);

			/*
			 * // convert to hundreds of feets if (cloudAmount.equalsIgnoreCase("VV"))
			 * cloudSec.setVerticalVisibility(true);
			 */

			if (!cloudHeight.equalsIgnoreCase("///"))
				cloudSec.setHeight(Integer.valueOf(cloudHeight) * 100);

			cloudSec.setType(cloudType);

			this.getCloudSections().add(cloudSec);

			lastIndex = matcher.end();

		}

		if (this.getCloudSections().size() > 0)
			tac.delete(0, lastIndex);

		// Temperatures
		matcher = TafParsingRegexp.tafMaxAirTemp.matcher(tac);
		if (matcher.find()) {

			String sTmax = matcher.group("tempMax").replace("M", "-");
			this.setAirTemperatureMax(new BigDecimal(sTmax));

			String sTMaxDay = matcher.group("day");
			String sTMaxHour = matcher.group("hour");
			try {
				this.setAirTemperatureMaxTime(
						IWXXMHelpers.parseDateTimeToken(String.format("%s%s00", sTMaxDay, sTMaxHour)));
			} catch (ParsingException e) {
				throw new TAFParsingException("Check air temperature maximum time section");
			}

			lastIndex = matcher.end();
			tac.delete(0, lastIndex);

		}
		/*
		 * else if (failWhenMandatorySectionMissed) { throw new
		 * IllegalArgumentException("Wrong or missed temperature section"); }
		 */

		matcher = TafParsingRegexp.tafMinAirTemp.matcher(tac);
		if (matcher.find()) {

			String sTmin = matcher.group("tempMin").replace("M", "-");

			this.setAirTemperatureMin(new BigDecimal(sTmin));
			String sTMinDay = matcher.group("day");
			String sTMinHour = matcher.group("hour");
			try {
				this.setAirTemperatureMinTime(
						IWXXMHelpers.parseDateTimeToken(String.format("%s%s00", sTMinDay, sTMinHour)));
			} catch (ParsingException e) {
				throw new TAFParsingException("Check air temperature minimum time section");
			}

			lastIndex = matcher.end();
			tac.delete(0, lastIndex);

		}
		/*
		 * else if (failWhenMandatorySectionMissed) { throw new
		 * IllegalArgumentException("Wrong or missed temperature section"); }
		 */

		/*
		 * // QNH matcher = MetarParsingRegexp.metarQNH.matcher(tac); if
		 * (matcher.find()) {
		 * 
		 * String sQnh = matcher.group("qnh"); String sQnhU = matcher.group("qnhUnits");
		 * this.setQnhUnits( sQnhU.equalsIgnoreCase("A") ?
		 * PRESSURE_UNITS.INCH_OF_MERCURY : PRESSURE_UNITS.HECTOPASCALS);
		 * this.setQnh(new BigDecimal(sQnh)); lastIndex = matcher.end(); tac.delete(0,
		 * lastIndex);
		 * 
		 * } else if (failWhenMandatorySectionMissed) { throw new
		 * IllegalArgumentException("Wrong or missed QNH section"); }
		 */
		return tac;

	}

	public Integer getWindDir() {
		return windDir;
	}

	public void setWindDir(Integer windDir) {
		this.windDir = windDir;
	}

	public Integer getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(Integer windSpeed) {
		this.windSpeed = windSpeed;
	}

	public Integer getGustSpeed() {
		return gustSpeed;
	}

	public void setGustSpeed(Integer gustSpeed) {
		this.gustSpeed = gustSpeed;
	}

	public SPEED_UNITS getSpeedUnits() {
		return speedUnits;
	}

	public void setSpeedUnits(SPEED_UNITS speedUnits) {
		this.speedUnits = speedUnits;
	}

	public Integer getWindVariableFrom() {
		return windVariableFrom;
	}

	public void setWindVariableFrom(Integer windVariableFrom) {
		this.windVariableFrom = windVariableFrom;
	}

	public Integer getWindVariableTo() {
		return windVariableTo;
	}

	public void setWindVariableTo(Integer windVariableTo) {
		this.windVariableTo = windVariableTo;
	}

	public boolean isVrb() {
		return vrb;
	}

	public void setVrb(boolean vrb) {
		this.vrb = vrb;
	}

	public Integer getWindVrbSpeed() {
		return windVrbSpeed;
	}

	public void setWindVrbSpeed(Integer windVrbSpeed) {
		this.windVrbSpeed = windVrbSpeed;
	}

	public SPEED_UNITS getVrbSpeedUnits() {
		return vrbSpeedUnits;
	}

	public void setVrbSpeedUnits(SPEED_UNITS vrbSpeedUnits) {
		this.vrbSpeedUnits = vrbSpeedUnits;
	}

	public BigDecimal getQnh() {
		return qnh;
	}

	public void setQnh(BigDecimal qnh) {
		this.qnh = qnh;
	}

	public PRESSURE_UNITS getQnhUnits() {
		return qnhUnits;
	}

	public void setQnhUnits(PRESSURE_UNITS qnhUnits) {
		this.qnhUnits = qnhUnits;
	}

	public Double getPrevailVisibility() {
		return prevailVisibility;
	}

	public void setPrevailVisibility(Double prevailVisibility) {
		this.prevailVisibility = prevailVisibility;
	}

	public Double getMinimumVisibility() {
		return minimumVisibility;
	}

	public void setMinimumVisibility(Double minimumVisibility) {
		this.minimumVisibility = minimumVisibility;
	}

	public RUMB_UNITS getMinimumVisibilityDirection() {
		return minimumVisibilityDirection;
	}

	public void setMinimumVisibilityDirection(RUMB_UNITS minimumVisibilityDirection) {
		this.minimumVisibilityDirection = minimumVisibilityDirection;
	}

	public LENGTH_UNITS getVisibilityUnits() {
		return visibilityUnits;
	}

	public void setVisibilityUnits(LENGTH_UNITS visibilityUnits) {
		this.visibilityUnits = visibilityUnits;
	}

	public LinkedList<String> getCurrentWeather() {
		return currentWeather;
	}

	public void setCurrentWeather(LinkedList<String> currentWeather) {
		this.currentWeather = currentWeather;
	}

	public LinkedList<TAFCloudSection> getCloudSections() {
		return cloudSections;
	}

	public boolean isCavok() {
		return cavok;
	}

	public void setCavok(boolean cavok) {
		this.cavok = cavok;
	}

	public BigDecimal getAirTemperatureMax() {
		return airTemperatureMax;
	}

	public void setAirTemperatureMax(BigDecimal airTemperatureMax) {
		this.airTemperatureMax = airTemperatureMax;
	}

	public BigDecimal getAirTemperatureMin() {
		return airTemperatureMin;
	}

	public void setAirTemperatureMin(BigDecimal airTemperatureMin) {
		this.airTemperatureMin = airTemperatureMin;
	}

	public Integer getValidityDayFrom() {
		return validityDayFrom;
	}

	public void setValidityDayFrom(Integer validityDayFrom) {
		this.validityDayFrom = validityDayFrom;
	}

	public Integer getValidityHourFrom() {
		return validityHourFrom;
	}

	public void setValidityHourFrom(Integer validityHourFrom) {
		this.validityHourFrom = validityHourFrom;
	}

	public Integer getValidityDayTo() {
		return validityDayTo;
	}

	public void setValidityDayTo(Integer validityDayTo) {
		this.validityDayTo = validityDayTo;
	}

	public Integer getValidityHourTo() {
		return validityHourTo;
	}

	public void setValidityHourTo(Integer validityHourTo) {
		this.validityHourTo = validityHourTo;
	}

	public DateTime getAirTemperatureMaxTime() {
		return airTemperatureMaxTime;
	}

	public void setAirTemperatureMaxTime(DateTime airTemperatureMaxTime) {
		this.airTemperatureMaxTime = airTemperatureMaxTime;
	}

	public DateTime getAirTemperatureMinTime() {
		return airTemperatureMinTime;
	}

	public void setAirTemperatureMinTime(DateTime airTemperatureMinTime) {
		this.airTemperatureMinTime = airTemperatureMinTime;
	}

	HashMap<String, Object> hashNames = new HashMap<String, Object>();

	public HashMap<String, Object> getHashNames() {
		return hashNames;
	}

	public void setHashNames(HashMap<String, Object> hashNames) {
		this.hashNames = hashNames;
	}

	public HashMap<String, Object> getLocalizedNameValues() {

		Field[] flds = this.getClass().getDeclaredFields();
		for (Field f : flds) {

			AnnotationLocaliedName ann = f.getAnnotation(AnnotationLocaliedName.class);
			if (ann != null) {

				String lname = ann.name();
				try {
					f.setAccessible(true);
					Object fieldValue = f.get(this);
					hashNames.put(lname, fieldValue);
					f.setAccessible(false);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return hashNames;

	}

}
