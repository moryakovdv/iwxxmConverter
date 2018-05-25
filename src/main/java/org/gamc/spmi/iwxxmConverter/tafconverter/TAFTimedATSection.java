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
import org.gamc.spmi.iwxxmConverter.general.TafForecastTimeSection;
import org.gamc.spmi.iwxxmConverter.tac.TacMessageImpl;
import org.gamc.spmi.iwxxmConverter.tac.TacSectionImpl;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class TAFTimedATSection extends TacSectionImpl implements TafForecastTimeSection {

	private TafCommonWeatherSection commonWeatherSection = new TafCommonWeatherSection(false);
	
	
	DateTime dtAT=null;
	DateTime dtFM=null;
	DateTime dtTL=null;
	
	public TAFTimedATSection(String initialTac, TacMessageImpl parent) {
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
		
		DateTime parentDateTime = this.getParentIssuedDateTime();
		dtAT = parseSectionDateTimeToken(TafParsingRegexp.tafBecomingTimestamp, innerTac.toString(), parentDateTime);
		/*
		
		dtTL = parseSectionDateTimeToken(TafParsingRegexp.tafForecastDateTimeTL, innerTac.toString(), parentDateTime);
		dtFM = parseSectionDateTimeToken(TafParsingRegexp.tafForecastDateTimeFM, innerTac.toString(), parentDateTime);
		*/
		
		commonWeatherSection.parseSection(innerTac);

		
	}

	@Override
	public ForecastSectionType getSectionType() {
		return ForecastSectionType.BECMG;
	}

	@Override
	public Interval getTrendValidityInterval() {
		
		return new Interval(dtAT,dtAT);
	}
	

}
