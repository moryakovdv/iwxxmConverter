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

import java.util.regex.Pattern;

/**List of regular expressions using for TAC METAR parsing*/
public class MetarParsingRegexp {
	
	public final static Pattern metarHeader = Pattern
			//.compile("(?<header>^METAR)\\s+(?<icao>[A-Z]{4})\\s+(?<datetime>\\d{6})(?<zulu>Z){0,1}");
			.compile("(?<header>^METAR)\\s+((?<changeIndicator>COR)(?:\\s)){0,1}(?<icao>[A-Z]{4})\\s+(?<datetime>\\d{6})(?<zulu>Z){0,1}");
	public final static Pattern metarWind = Pattern.compile(
			"(?<windDir>\\d{3})(?<windSpeed>\\d{2})((?<speedUnits>[A-Z]{2,3})|G(?<gustSpeed>\\d{2,3})(?<gustSpeedUnits>[A-Z]{2,3}))");
	public final static Pattern metarWindVariable = Pattern
			.compile("((?<variableFrom>\\d{3})V(?<variableTo>\\d{3}))");
	public final static Pattern metarWindVRB = Pattern
			.compile("VRB(?<vrbWindSpeed>\\d{2,3})(?<vrbWindUnits>[A-Z]{2,3})");

	public final static Pattern metarCavok = Pattern.compile("(?:CAVOK)");

	public final static Pattern metarVisibility = Pattern
			.compile("[^Q|A|TL|AT|FM|\\/](?<visibility>\\d{4}|\\d+SM|\\d+\\/\\d+SM)(?<visibilityDirection>[A-Z]{0,2})");

	public final static Pattern metarRVR = Pattern.compile("R(?<runwayDesignator>\\d{2,3}(?:L|R){0,1})\\/(?<rvrOperator>M|P){0,1}(?<rvr>\\d{2,4})((?<tendency>U|N|P|D)|(?<units>FT)|\\s){1}");

	public final static Pattern metarPrecipitation = Pattern
			.compile("(?:^|\\s)(?!RE|METAR|NOSIG|WS|ALL|RWY|RMK)(?<weather>(?:\\+|-|VC){0,1}[A-Z]{2,6})(?=\\s)");

	public final static Pattern metarClouds = Pattern
			.compile("(?<cloudAmount>[A-Z]{2,3})(?<cloudHeight>\\d{3}|\\/{3})(?<cloudType>[A-Z]{2,3}){0,1}(?:\\s|\\=|$)");

	public final static Pattern metarAirTemp = Pattern
			.compile("((?<tempAir>M{0,1}\\d{2})\\/(?<dewPoint>M{0,1}\\d{2}))\\s");
	public final static Pattern metarQNH = Pattern.compile("(?<=(?<qnhUnits>Q|A))(?<qnh>(\\d{4}))");

	public final static Pattern metarWindShearAll = Pattern.compile("WS ALL RWY");
	public final static Pattern metarWindShearRunway = Pattern.compile("WS\\sRWY(?<wsRunway>\\d{2,3}L|R{0,1})");

	//public final static Pattern metarRunwayState = Pattern.compile("R(?<rwCode>\\d\\d(?:L|R){0,1})\\/(?<type>\\d|\\/)(?<contamination>\\d|\\/)(?<depth>\\d{2}|\\/{2})(?<friction>\\d{2}|\\/{2})");
	
	public final static Pattern metarRunwayState = Pattern.compile("(R?)(?<rwCode>\\d\\d(?:L|R){0,1})\\/((?<cleared>CLRD)|((?<depositType>\\d|\\/)(?<contamination>\\d|\\/)(?<depth>\\d{2}|\\/{2})))(?<friction>\\d{2}|\\/{2})");
	
	public final static Pattern metarRecentWeather = Pattern.compile("(?:RE)(?<recentWeather>[A-Z]{2,6})");

	
	
	public final static Pattern metarForecastSection = Pattern
			.compile("(?<trendType>BECMG|TEMPO)(?<trendSection>(?:(?!BECMG|TEMPO|RMK|NOSIG|AT|FM|TL).|\\s)*)");
	
	public final static Pattern metarTimedSection = Pattern
			//.compile("(?<trendTimeType>AT|FM|TL)(?<trendTimeSection>(?:(?!BECMG|TEMPO|RMK|NOSIG|=).|\\s)*)");
			//.compile("<trendTimeType>AT|FM|TL)(?<trendTimeStamp>\\d{6})(?<trendTimeSectionBody>(?:(?!AT|FM|TL|BECMG|TEMPO|RMK|NOSIG|=).|\\s)*)");
			//.compile("(?<trendTimeType>AT|FM|TL)(?<trendTimeSection>\\d{6}(?:(?!AT|FM|TL|BECMG|TEMPO|RMK|NOSIG|=).|\\s)*)");
			.compile("(?<trendTimeType>AT|FM|TL)(?<trendTimeSection>\\d{4}(?:(?!AT|FM|TL|BECMG|TEMPO|RMK|NOSIG|=).|\\s)*)");
	
	
	public final static Pattern metarRMKSection = Pattern.compile("(?<=RMK)(?<rmk>(?:(?!BECMG|TEMPO|RMK|NOSIG).|\\s)*)");

	public final static Pattern metarNOSIGForecast = Pattern.compile("NOSIG");
	
	
	public final static Pattern metarBecomingTimestamp = Pattern
			.compile("(?<hh>\\d{2})(?<mm>\\d{2}).*");

	/*
	public final static Pattern metarForecastDateTimeAT = Pattern.compile("(?<=AT)(?<hh>\\d{2})(?<mm>\\d{2})");
	public final static Pattern metarForecastDateTimeFM = Pattern.compile("(?<=FM)(?<hh>\\d{2})(?<mm>\\d{2})");
	public final static Pattern metarForecastDateTimeTL = Pattern.compile("(?<=TL)(?<hh>\\d{2})(?<mm>\\d{2})");
	*/
	

}
