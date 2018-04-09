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
package tafconverter;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import common.ForecastSectionType;
import general.TafForecastSection;
import tac.TacMessageImpl;
import tac.TacSectionImpl;

public class TAFBecomingSection extends TacSectionImpl implements TafForecastSection {

	private TafCommonWeatherSection commonWeatherSection = new TafCommonWeatherSection(false);
	
	public TAFBecomingSection(String initialTac, TacMessageImpl parent) {
		super(initialTac, parent);
		
	}

	public TafCommonWeatherSection getCommonWeatherSection() {
		return commonWeatherSection;
	}
	
	
	@Override
	public void parseSection() throws TAFParsingException {
		StringBuffer innerTac = new StringBuffer(getInitialTacString());
		
		/*
		DateTime parentDateTime = this.getParentIssuedDateTime();
		
		dtAT = parseSectionDateTimeToken(TafParsingRegexp.tafForecastDateTimeAT, innerTac.toString(), parentDateTime);
		dtTL = parseSectionDateTimeToken(TafParsingRegexp.tafForecastDateTimeTL, innerTac.toString(), parentDateTime);
		dtFM = parseSectionDateTimeToken(TafParsingRegexp.tafForecastDateTimeFM, innerTac.toString(), parentDateTime);
		*/
		
		commonWeatherSection.parseSection(innerTac);

		
	}

	@Override
	public DateTime getDateTimeAT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DateTime getDateTimeFROM() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DateTime getDateTimeTILL() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ForecastSectionType getSectionType() {
		return ForecastSectionType.BECMG;
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
}
