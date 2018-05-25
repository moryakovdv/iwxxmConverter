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
package org.gamc.spmi.iwxxmConverter.metarconverter;

import org.gamc.spmi.iwxxmConverter.common.ForecastSectionType;
import org.gamc.spmi.iwxxmConverter.general.MetarForecastSection;
import org.gamc.spmi.iwxxmConverter.tac.TacMessageImpl;
import org.gamc.spmi.iwxxmConverter.tac.TacSectionImpl;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**Describes base BECMG section in METAR*/
public class METARBecomingSection extends TacSectionImpl implements MetarForecastSection {

	private MetarCommonWeatherSection commonWeatherSection = new MetarCommonWeatherSection(false);
	
	DateTime dtAT=null;
	DateTime dtFM=null;
	DateTime dtTL=null;
	
	public METARBecomingSection(String initialTac, TacMessageImpl parent) {
		super(initialTac, parent);
		
	}

	@Override
	public MetarCommonWeatherSection getCommonWeatherSection() {
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
	public void parseSection() throws METARParsingException {
		StringBuffer innerTac = new StringBuffer(getInitialTacString());
		
		DateTime parentDateTime = this.getParentIssuedDateTime();
		/*
		dtAT = parseSectionDateTimeToken(MetarParsingRegexp.metarForecastDateTimeAT, innerTac.toString(), parentDateTime);
		dtTL = parseSectionDateTimeToken(MetarParsingRegexp.metarForecastDateTimeTL, innerTac.toString(), parentDateTime);
		dtFM = parseSectionDateTimeToken(MetarParsingRegexp.metarForecastDateTimeFM, innerTac.toString(), parentDateTime);
		*/
		
		commonWeatherSection.parseSection(innerTac);

		
	}

	@Override
	public ForecastSectionType getSectionType() {
		return ForecastSectionType.BECMG;
	}

	/**Validity interval for BCMG 2 hours by default*/
	@Override
	public Interval getTrendValidityInterval() {
		DateTime parentDateTime = this.getParentIssuedDateTime();
		DateTime parentDateTimeEnd = this.getParentIssuedDateTime().plusHours(2);
		
		return new Interval(parentDateTime,parentDateTimeEnd);
	}

}
