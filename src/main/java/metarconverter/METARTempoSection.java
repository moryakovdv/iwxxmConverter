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
package metarconverter;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import common.ForecastSectionType;
import exceptions.ParsingException;
import general.MetarForecastSection;
import tac.TacMessageImpl;
import tac.TacSectionImpl;

/**Describes trernd TEMPO section in METAR*/
public class METARTempoSection extends TacSectionImpl implements MetarForecastSection {

	private MetarCommonWeatherSection commonWeatherSection = new MetarCommonWeatherSection(false);

	DateTime dtAT = null;
	DateTime dtFM = null;
	DateTime dtTL = null;

	public METARTempoSection(String initialTac, TacMessageImpl parent) {
		super(initialTac, parent);
		
	}

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
	public void parseSection() throws ParsingException {
		StringBuffer innerTac = new StringBuffer(getInitialTacString());

/*
		dtAT = parseSectionDateTimeToken(MetarParsingRegexp.metarForecastDateTimeAT, innerTac.toString(),
				parentDateTime);
		dtTL = parseSectionDateTimeToken(MetarParsingRegexp.metarForecastDateTimeTL, innerTac.toString(),
				parentDateTime);
		dtFM = parseSectionDateTimeToken(MetarParsingRegexp.metarForecastDateTimeFM, innerTac.toString(),
				parentDateTime);
*/
		commonWeatherSection.parseSection(innerTac);

	}
	@Override
	public ForecastSectionType getSectionType() {
		return ForecastSectionType.TEMPO;
	}

	@Override
	public Interval getTrendValidityInterval() {
		DateTime parentDateTime = this.getParentIssuedDateTime();
		DateTime parentDateTimeEnd = this.getParentIssuedDateTime().plusMinutes(30);
		return new Interval(parentDateTime, parentDateTimeEnd);
	}
}
