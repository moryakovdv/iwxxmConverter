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

import org.gamc.spmi.iwxxmConverter.common.ForecastSectionType;
import org.gamc.spmi.iwxxmConverter.general.TafForecastSection;
import org.gamc.spmi.iwxxmConverter.tac.TacMessageImpl;
import org.gamc.spmi.iwxxmConverter.tac.TacSectionImpl;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class TAFProbabilitySection extends TacSectionImpl implements TafForecastSection {

	private TafCommonWeatherSection commonWeatherSection = new TafCommonWeatherSection(false);

	DateTime dtAT = null;
	DateTime dtFM = null;
	DateTime dtTL = null;

	boolean isTempo = false;
	int probability=30;
	
	
	public TAFProbabilitySection(String initialTac,TacMessageImpl parent) {
		super(initialTac, parent);
		
	}

	public TafCommonWeatherSection getCommonWeatherSection() {
		return commonWeatherSection;
	}

	@Override
	public DateTime getDateTimeAT() {
		return dtAT;
	}

	@Override
	public DateTime getDateTimeFROM() {
		return dtFM;
	}

	@Override
	public DateTime getDateTimeTILL() {
		return dtTL;
	}

	@Override
	public void parseSection() throws TAFParsingException {
		StringBuffer innerTac = new StringBuffer(getInitialTacString());

		//DateTime parentDateTime = this.getParentIssuedDateTime();
/*
		dtAT = parseSectionDateTimeToken(TafParsingRegexp.tafForecastDateTimeAT, innerTac.toString(),
				parentDateTime);
		dtTL = parseSectionDateTimeToken(TafParsingRegexp.tafForecastDateTimeTL, innerTac.toString(),
				parentDateTime);
		dtFM = parseSectionDateTimeToken(TafParsingRegexp.tafForecastDateTimeFM, innerTac.toString(),
				parentDateTime);
*/
		commonWeatherSection.parseSection(innerTac);

	}
	@Override
	public ForecastSectionType getSectionType() {
		switch(getProbability()) {
			case 30:
			default:
				return isTempo()?ForecastSectionType.PROB30TEMPO:ForecastSectionType.PROB30;
			case 40:
				return isTempo()?ForecastSectionType.PROB40TEMPO:ForecastSectionType.PROB40;
		}
		
		 
	}

	@Override
	public Interval getTrendValidityInterval() {
		DateTime issuedTime = this.getParentIssuedDateTime();
		int issuedDay = issuedTime.getDayOfMonth();
		
		DateTime validFrom = DateTime.now();
		int validFromDay = this.getCommonWeatherSection().getValidityDayFrom();
		int validFromHour = this.getCommonWeatherSection().getValidityHourFrom();
		
		if (issuedDay == validFromDay) {
			validFrom = issuedTime.withTimeAtStartOfDay().plusHours(Integer.valueOf(validFromHour));
		}
		if (issuedDay<validFromDay) {
			validFrom = issuedTime.withDayOfMonth(validFromDay).withTimeAtStartOfDay().plusHours(Integer.valueOf(validFromHour));
		}
		if (issuedDay>validFromDay) {
			validFrom = issuedTime.plusMonths(1).withDayOfMonth(validFromDay).withTimeAtStartOfDay().plusHours(Integer.valueOf(validFromHour));
		}
		
		DateTime validTill =  DateTime.now();
		
		int validTillDay = this.getCommonWeatherSection().getValidityDayTo();
		int validTillHour = this.getCommonWeatherSection().getValidityHourTo();
		
		if (issuedDay == validTillDay) {
			validTill = issuedTime.withTimeAtStartOfDay().plusHours(Integer.valueOf(validTillHour));
		}
		if (issuedDay<validTillDay) {
			validTill = issuedTime.withDayOfMonth(validTillDay).withTimeAtStartOfDay().plusHours(Integer.valueOf(validTillHour));
		}
		if (issuedDay>validTillDay) {
			validTill = issuedTime.plusMonths(1).withDayOfMonth(validTillDay).withTimeAtStartOfDay().plusHours(Integer.valueOf(validTillHour));
		}
		 
		return  new Interval(validFrom,validTill);
	}

	public boolean isTempo() {
		return isTempo;
	}

	public void setTempo(boolean isTempo) {
		this.isTempo = isTempo;
	}

	public int getProbability() {
		return probability;
	}

	public void setProbability(int probability) {
		this.probability = probability;
	}
}
