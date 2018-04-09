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
import general.MetarForecastTimeSection;
import tac.TacMessageImpl;
import tac.TacSectionImpl;

/**Describes BECMG TL section in METAR*/
public class METARTimedTLSection extends TacSectionImpl implements MetarForecastTimeSection {

	private MetarCommonWeatherSection commonWeatherSection = new MetarCommonWeatherSection(false);
	
	DateTime dtAT=null;
	DateTime dtFM=null;
	DateTime dtTL=null;
	
	public METARTimedTLSection(String initialTac, TacMessageImpl parent) {
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
		
		//dtAT = parseSectionDateTimeToken(MetarParsingRegexp.metarForecastDateTimeAT, innerTac.toString(), parentDateTime);
		dtTL = parseSectionDateTimeToken(MetarParsingRegexp.metarBecomingTimestamp, innerTac.toString(), parentDateTime);
		//dtFM = parseSectionDateTimeToken(MetarParsingRegexp.metarForecastDateTimeFM, innerTac.toString(), parentDateTime);
		
		commonWeatherSection.parseSection(innerTac);

		
	}

	@Override
	public ForecastSectionType getSectionType() {
		return ForecastSectionType.BECMG;
	}

	/**Validity period from issue time to TL marker*/
	@Override
	public Interval getTrendValidityInterval() {
		DateTime parentDateTime = this.getParentIssuedDateTime();
		return new Interval(parentDateTime,dtTL);
	}

}
