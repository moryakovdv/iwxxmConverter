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
package org.gamc.spmi.iwxxmConverter.airmetconverter;

import java.util.regex.Pattern;




/** Regexps for parsing AIRMET
 * airmet blocks: 
 * 	1) Location indicator of FIR(/UIR) or CTA;
	2)Name of the FIR(/UIR ) or CTA;
	3)Description of the phenomenon;
	4)Observed or Forecasted;
	5)Location of observation/forecast;
	6)Level (altitude) of the observation/forecast;
	7)Movement or Expected Movement;
	8)Changes in intensity;
	9)Forecasted position at end of AIRMET’s end of validity (optional)
 * 
 * */
public class AirmetParsingRegexp {

	/**WMO Bulletin header*/
	public final static Pattern airmetBulletinHeader = Pattern.compile("(?<airmetDataType>[A-Z]{2})(?<issueRegion>[A-Z]{2})(?<bulletinNumber>\\d{2})\\s+(?<disseminatingCentre>[A-Z]{4})\\s+(?<issuedDateTime>\\d{6})"); 
	
	/**AIRMET Mandatory header*/
	public final static Pattern airmetHeader = Pattern.compile(
			//"(?<icao>[A-Z]{4})\\s+(?<isAirmet>AIRMET)\\s+(?<airmetNumber>(([A-Z]+)?\\s*\\d+))\\s+(?<isValid>VALID)\\s+(?<ddf>\\d\\d)(?<hhf>\\d\\d)(?<mmf>\\d\\d)\\/(?<ddt>\\d\\d)(?<hht>\\d\\d)(?<mmt>\\d\\d)\\s+(?<watchOffice>[A-Z]{4})-?\\s*(?<firCode>[A-Z]{4})?\\s+((?<firName>.+)\\s+(FIR|CTA))+(?<uir>\\/UIR)?");
	       //"(?<icao>[A-Z]{4})\\s+(?<isAirmet>AIRMET)\\s+(?<airmetNumber>(([A-Z]+)?\\s*\\d+))\\s+(?<isValid>VALID)\\s+(?<dateFrom>\\d{6})\\/(?<dateTo>\\d{6})\\s+(?<watchOffice>[A-Z]{4})-?\\s*(?<firCode>[A-Z]{4})?\\s+((?<firName>.+)\\s+(FIR|CTA))+(?<uir>\\/UIR)?");
			"(?<icao>[A-Z]{4})\\s+(?<isAirmet>AIRMET)\\s+(?<airmetNumber>(([A-Z]+)?\\s*\\d+))\\s+(?<isValid>VALID)\\s+(?<dateFrom>\\d{6})\\/(?<dateTo>\\d{6})\\s+(?<watchOffice>[A-Z]{4})-?\\s*(?<firCode>[A-Z]{4})?\\s+((?<firName>.+)\\s+(FIR|CTA))+(?<uir>\\/UIR)?(?<isCancel>\\s*CNL\\s*AIRMET\\s*(?<cancelNumber>\\d)\\s*(?<cancelDateFrom>\\d{6})\\/(?<cancelDateTo>\\d{6}))?");
	
	public final static Pattern airmetCancel = Pattern.compile("");
	
	/**Pattern for phenomenas except VA,VC*/
	public final static Pattern airmetPhenomena = Pattern.compile("((?:\\s+|^)?(?<severity>ISOL|OBSC|SQL|EMBD|FRQ|SEV|HVY|MOD))\\s+(?<phenomena>.*?)\\s+(?<obsfcst>OBS|FCST)\\s+(?:AT\\s+(?<atTime>\\d{4})Z)?");
	public final static Pattern airmetPhenomenaTimestamp = Pattern
			.compile("(?<hh>\\d{2})(?<mm>\\d{2})");
	
	/**Pattern to determine entire fir*/
	public final static Pattern airmetEntireFir = Pattern.compile("ENTIRE FIR(?:\\/UIR)?");
	
	
	/**Pattern to determine polygon*/
	public final static Pattern airmetInPolygon = Pattern.compile("(?<isInPolygon>WI)\\s+(\\D+)");
	
	/**Pattern to extract coordinate point*/
	public final static Pattern airmetCoordPoint = Pattern.compile("(?<point>(?<latitude>N|S)(?<ladeg>\\d{2})(?<lamin>\\d{2})?\\s+(?<longitude>E|W)(?<lodeg>\\d{2,3})(?<lomin>\\d{2})?)");

	/**Pattern to extract lines into collection*/
	public final static Pattern airmetLines = Pattern.compile("((N|NE|E|SE|S|SW|W|NW) OF LINE (?<pointStart>(?<latitudeStart>N|S)(?<ladegStart>\\d{2})(?<laminStart>\\d{2})?\\s+(?<longitudeStart>E|W))(?<lodegStart>\\d{2,3})(?<lominStart>\\d{2})?)\\s+(.)?\\s+(?<pointEnd>(?<latitudeEnd>N|S)(?<ladegEnd>\\d{2})(?<laminEnd>\\d{2})?\\s+(?<longitudeEnd>E|W)(?<lodegEnd>\\d{2,3})(?<lominEnd>\\d{2})?)");
	
	/**Pattern to find sectors */
	public final static Pattern airmetSectorPoints = Pattern.compile("((N|NE|E|SE|S|SW|W|NW) OF LINE (?<pointStart>(?<latitudeStart>N|S)(?<ladegStart>\\d{2})(?<laminStart>\\d{2})?\\s+(?<longitudeStart>E|W))(?<lodegStart>\\d{2,3})(?<lominStart>\\d{2})?)\\s+(.)?\\s+(?<pointEnd>(?<latitudeEnd>N|S)(?<ladegEnd>\\d{2})(?<laminEnd>\\d{2})?\\s+(?<longitudeEnd>E|W)(?<lodegEnd>\\d{2,3})(?<lominEnd>\\d{2})?)");
	
	/**Within corridor with distance of certain line*/
	public final static Pattern airmetWithinCorridor = Pattern.compile("(APRX|WTN)\\s+(?<range>\\d+)\\s?(KM|NM)\\s+(WID|OF)\\s+LINE\\s+(?:BTN|\\/)?");
	
	/**Whether phenomena moves or stands and intensity*/
	public final static Pattern airmetMovement = Pattern.compile("((?<isStationery>STNR)|(MOV\\s+(?<movDirection>[A-Z]{1,3})\\s+(?<movSpeed>\\d+)?(?<speedunits>KMH|KT)?))");
	
	/**Forecasted position of phenomenon*/
	public final static Pattern airmetForecastSection = Pattern.compile("FCST\\s+AT\\s+(?<time>\\d{4})Z\\s+(?<location>.*)");
	
	/**Extract single point direction e.g. N OF N2000 AND E OF E5555*/
	public final static Pattern airmetOnePointLine = Pattern.compile("(?<direction>(?<azimuth>N|NE|E|SE|S|SW|W|NW)\\s+OF\\s+(?<singlepointcoord>(?<pointCoord>N|S|E|W)(?<deg>\\d{2,3})(?<min>\\d{0,2})))");
	
	/**Vertical length (height) of the phenomena*/
	//public final static Pattern airmetLevel = Pattern.compile("(?<hastopfl>(?<top>TOP)\\s+(?<above>ABV)?\\s*FL(?<fl>\\d+))|(?<hassurface>(?<surface>SFC)\\/(FL(?<topfl>(\\d+))|(?<heightmeters>\\d+)M))|(?<hasbothfls>FL(?<lowfl>\\d+)\\/(?<highfl>\\d+))");
	public final static Pattern airmetLevel = Pattern.compile("(?<hastopfl>(?<top>TOP)\\s+(?<above>ABV)?(?<below>BLW)?\\s*FL(?<fl>\\d+))|(?<hassurface>(?<surface>SFC)\\/(FL(?<topfl>(\\d+))|(?<heightmeters>\\d+)(?<units>M|FT)))|(?<hasbothfls>FL(?<lowfl>\\d+)\\/(?<highfl>\\d+))");
	public final static Pattern airmetIntensityChanges=Pattern.compile("(?<intensity>INTSF|WKN|NC)");
	
	/**Radioactive cloud mentioned WITHIN some radius from point*/
	public final static Pattern airmetWithinRadius=Pattern.compile("(?:WI\\s+)(?<radius>\\d+)(?<radiusUnit>KM|NM)(?:\\s+OF\\s+)(?<point>(?<latitude>N|S)(?<ladeg>\\d{2})(?<lamin>\\d{2})?\\s+(?<longitude>E|W)(?<lodeg>\\d{2,3})(?<lomin>\\d{2})?)");
	
	
}
