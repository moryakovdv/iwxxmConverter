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


package org.gamc.spmi.iwxxmConverter.general;

import org.gamc.spmi.iwxxmConverter.common.ForecastSectionType;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.tac.TacMessage;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**Interface to represent trend sections in messages, such as TEMPO or BECMG AT etc.*/
public interface ForecastSection {
	
	/**DateTime WHEN phenomenas should have place*/
	DateTime getDateTimeAT();
	
	/**DateTime FROM phenomenas should have place*/
	DateTime getDateTimeFROM();
	
	/**DateTime TILL phenomenas should have place*/
	DateTime getDateTimeTILL();

	/**See {@link CommonWeatherSection}*/
	CommonWeatherSection getCommonWeatherSection();
	
	/**See {@link ForecastSectionType}*/
	ForecastSectionType getSectionType();
	
	/**Each section has its own rules to calculate validity period*/
	Interval getTrendValidityInterval();
	
	/**Link to a parent message the section belongs to*/
	TacMessage getParentMessage();
	
	/**Prsing method to be implemented in classes to perform TAC parsing*/
	void parseSection() throws ParsingException;
}
