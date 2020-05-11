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
package org.gamc.spmi.ixwwmConverter.spaceweatherconverter;

import java.util.regex.Pattern;




/** Regexps for parsing SPACE WEATHER ADVISORY
 * 
 * 
 * */
public class SpaceWeatherParsingRegexp {

	/**SWX Mandatory header*/
	public final static Pattern spaceWeatherHeader = Pattern.compile(
	       "SWX\\s+ADVISORY");
	
	public final static Pattern timeStamp = Pattern.compile("(?<year>\\d{4})(?<month>\\d{2})(?<day>\\d{2})\\/(?<hour>\\d{2})(?<minute>\\d{2})Z");  
	
	/**Pattern for date time of creation*/
	public final static Pattern spaceWeatherDateTimeGenerated = Pattern.compile("DTG\\s*:\\s+(?<year>\\d{4})(?<month>\\d{2})(?<day>\\d{2})\\/(?<hour>\\d{2})(?<minute>\\d{2})Z");
	
	/**issued by*/
	public final static Pattern spaceWeatherCenter = Pattern.compile("SWXC\\s*:\\s+(?<center>.*)\\s*");
	
	/**advisory number*/
	public final static Pattern spaceWeatherAdvisoryNumber = Pattern.compile("ADVISORY\\sNR\\s*:\\s+(?<advisoryNumber>.*)\\s*");
	
	/**advisory replacing number*/
	public final static Pattern spaceWeatherAdvisoryReplacingNumber = Pattern.compile("NR RPLC\\s*:\\s+(?<advisoryReplaceNumber>.*)\\s*");
	
	/**effects*/
	public final static Pattern spaceWeatherEffects = Pattern.compile("(?:SWX\\s+EFFECT:)\\s*(?<effects>.*)\\s*");
	
	/**Observation area and time*/
	public final static Pattern spaceWeatherObserveArea = Pattern.compile("(?:OBS\\s+SWX:)\\s*(?<day>\\d\\d)\\/(?<hour>\\d\\d)(?<minute>\\d\\d)Z?\\s+(?<daylight>DAYLIGHT|DAY\\s+SIDE)?(?<nightlight>NIGHT\\s+SIDE)?(?<hemi1>HNH|HSH|MNH|MSH|EQN|EQS)?\\s*(?<hemi2>HNH|HSH|MNH|MSH|EQN|EQS)?\\s*(?<latStart>(E|W)(\\d{2,5}))?\\s*-?\\s*(?<latEnd>(E|W)(\\d{2,5}))?(?:\\s+ABV\\s+FL(?<fl>\\d{2,3}))?");
	
	/**Forecasting areas and times*/
	public final static Pattern spaceWeatherForecastArea = Pattern.compile("(?:FCST\\s+SWX\\s+\\+(?<forecastHour>\\d{1,2})\\s+HR\\s*:)\\s*(?<day>\\d\\d)\\/(?<hour>\\d\\d)(?<minute>\\d\\d)Z?\\s+(?<daylight>DAYLIGHT|DAY\\s+SIDE)?(?<nightlight>NIGHT\\s+SIDE)?(?<hemi1>HNH|HSH|MNH|MSH|EQN|EQS)?\\s*(?<hemi2>HNH|HSH|MNH|MSH|EQN|EQS)?\\s*(?<latStart>(E|W)(\\d{2,5}))?\\s*-?\\s*(?<latEnd>(E|W)(\\d{2,5}))?(?:\\s+ABV\\s+FL(?<fl>\\d{2,3}))?(?<notExpected>NO\\s+SWX\\s+EXP)?");
	
	/**Remark description**/
	public final static Pattern spaceWeatherRemark = Pattern.compile("RMK\\s*:\\s+(?<remark>.+)(?=\r|\n|NXT|$)");
	
	/**next advisory if mentioned
	 * Otherwise should lead to  <iwxxm:nextAdvisoryTime nilReason="http://codes.wmo.int/common/nil/inapplicable"/>*/
	public final static Pattern spaceWeatherNextAdvisory = Pattern.compile("NXT\\s+ADVISORY\\S*:\\s+(?<nextAdv>.*)");
	
	
	
	
}
