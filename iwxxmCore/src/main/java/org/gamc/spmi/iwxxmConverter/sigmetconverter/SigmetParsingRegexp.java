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
package org.gamc.spmi.iwxxmConverter.sigmetconverter;

import java.util.regex.Pattern;




/** Regexps for parsing SIGMET
 * sigmet blocks: 
 * 	1) Location indicator of FIR(/UIR) or CTA;
	2)Name of the FIR(/UIR ) or CTA;
	3)Description of the phenomenon;
	4)Observed or Forecasted;
	5)Location of observation/forecast;
	6)Level (altitude) of the observation/forecast;
	7)Movement or Expected Movement;
	8)Changes in intensity;
	9)Forecasted position at end of SIGMET’s end of validity (optional)
 * 
 * */
public class SigmetParsingRegexp {

	/**WMO Bulletin header*/
	//public final static Pattern sigmetBulletinHeader = Pattern.compile("(?<sigmetDataType>[A-Z]{2})(?<issueRegion>[A-Z]{2})(?<bulletinNumber>\\d{2})(?<disseminatingCentre>[A-Z]{4})\\s+(?<issuedDateTime>\\d{6})"); 
	public final static Pattern sigmetBulletinHeader = Pattern.compile("(?<sigmetDataType>[A-Z]{2})(?<issueRegion>[A-Z]{2})(?<bulletinNumber>\\d{2})\\s*?(?<disseminatingCentre>[A-Z]{4})\\s+(?<issuedDateTime>\\d{6})");
	/**SIGMET Mandatory header*/
			//public final static Pattern sigmetHeader = Pattern.compile(
			//"(?<icao>[A-Z]{4})\\s+(?<isSigmet>SIGMET)\\s+(?<sigmetNumber>(([A-Z]+)?\\s*\\d+))\\s+(?<isValid>VALID)\\s+(?<ddf>\\d\\d)(?<hhf>\\d\\d)(?<mmf>\\d\\d)\\/(?<ddt>\\d\\d)(?<hht>\\d\\d)(?<mmt>\\d\\d)\\s+(?<watchOffice>[A-Z]{4})-?\\s*(?<firCode>[A-Z]{4})?\\s+((?<firName>.+)\\s+(FIR|CTA))+(?<uir>\\/UIR)?");
	       //"(?<icao>[A-Z]{4})\\s+(?<isSigmet>SIGMET)\\s+(?<sigmetNumber>(([A-Z]+)?\\s*\\d+))\\s+(?<isValid>VALID)\\s+(?<dateFrom>\\d{6})\\/(?<dateTo>\\d{6})\\s+(?<watchOffice>[A-Z]{4})-?\\s*(?<firCode>[A-Z]{4})?\\s+((?<firName>.+)\\s+(FIR|CTA))+(?<uir>\\/UIR)?");
			//"(?<icao>[A-Z]{4})\\s+(?<isSigmet>SIGMET)\\s+(?<sigmetNumber>(([A-Z]+)?\\s*\\d+))\\s+(?<isValid>VALID)\\s+(?<dateFrom>\\d{6})\\/(?<dateTo>\\d{6})\\s+(?<watchOffice>[A-Z]{4})(?:-|–)?\\s*(?<firCode>[A-Z]{4})?\\s+((?<firName>.+)\\s+(FIR|CTA))+(?<uir>\\/UIR)?(?<isCancel>\\s*CNL\\s*SIGMET\\s*(?<cancelNumber>\\d)\\s*(?<cancelDateFrom>\\d{6})\\/(?<cancelDateTo>\\d{6}))?");
	public final static Pattern sigmetHeader = Pattern.compile("(?<icao>[A-Z]{4})\\s+(?<isSigmet>SIGMET)\\s+(?<sigmetNumber>(([A-Z]+)?\\s*\\d+))\\s+(?<isValid>VALID)\\s+(?<dateFrom>\\d{6})\\/(?<dateTo>\\d{6})\\s+(?<watchOffice>[A-Z]{4})\\s*?(?:-|–)?\\s*(?<firCode>[A-Z]{4})?\\s+((?<firName>.*?)\\s+(FIR|CTA))(?<uir>\\/UIR)?(?<isCancel>\\s*CNL\\s*SIGMET\\s*(?<cancelNumber>\\d)\\s*(?<cancelDateFrom>\\d{6})\\/(?<cancelDateTo>\\d{6}))?"); 

	public final static Pattern sigmetCancel = Pattern.compile("");
	
	/**Pattern for phenomenas except VA,VC*/
	public final static Pattern sigmetPhenomena = Pattern.compile("((?:\\s+|^)?(?<severity>ISOL|OBSC|SQL|EMBD|FRQ|SEV|HVY))?\\s+(?<phenomena>.*?)\\s+(?<obsfcst>OBS|FCST)\\s+(?:AT\\s+(?<atTime>\\d{4})Z)?");
	
	/**Pattern for TC*/
	public final static Pattern sigmetCyclonePhenomena = Pattern.compile("((?:\\s+|^)?(?<phenomena>TC))\\s+?(?<name>(.)*)?\\s+?(?<position>(?:PSN)(.*)(?:CB))\\s+(?<obsfcst>OBS|FCST)\\s+(?:AT\\s+(?<atTime>\\d{4})Z)?");

	
	public final static Pattern sigmetPhenomenaTimestamp = Pattern
			.compile("(?<hh>\\d{2})(?<mm>\\d{2})");
	public final static Pattern sigmetType = Pattern.compile("(?<sigmetType>\\sTC|VA\\s)");
	/**Pattern to determine entire fir*/
	public final static Pattern sigmetEntireFir = Pattern.compile("ENTIRE FIR(?:\\/UIR)?");
	
	
	/**Pattern to determine polygon*/
	public final static Pattern sigmetInPolygon = Pattern.compile("(?<isInPolygon>WI)\\s+(\\D+)");
	//public final static Pattern sigmetInPolygonVolcano = Pattern.compile("(?<isInPolygon>WI|)\\s+(\\D+)");
	public final static Pattern sigmetInPolygonVolcano = Pattern.compile("(?<isInPolygon>WI)\\s+(\\D+)"); 

	
	
	
	/**Pattern to extract coordinate point*/
	public final static Pattern sigmetCoordPoint = Pattern.compile("(?<point>(?<latitude>N|S)(?<ladeg>\\d{2})(?<lamin>\\d{2})?\\s+(?<longitude>E|W)(?<lodeg>\\d{3})(?<lomin>\\d{0,2})?)");

	/**Pattern to extract lines into collection*/
	public final static Pattern sigmetLines = Pattern.compile("((N|NE|E|SE|S|SW|W|NW) OF LINE (?<pointStart>(?<latitudeStart>N|S)(?<ladegStart>\\d{2})(?<laminStart>\\d{0,2})?\\s+(?<longitudeStart>E|W))(?<lodegStart>\\d{3})(?<lominStart>\\d{0,2})?)\\s+(.)?\\s+(?<pointEnd>(?<latitudeEnd>N|S)(?<ladegEnd>\\d{2})(?<laminEnd>\\d{0,2})?\\s+(?<longitudeEnd>E|W)(?<lodegEnd>\\d{3})(?<lominEnd>\\d{0,2})?)");
	
	/**Extract single point direction e.g. N OF N2000 AND E OF E05555*/
	//public final static Pattern sigmetOnePointLine = Pattern.compile("(?<direction>(?<azimuth>N|NE|E|SE|S|SW|W|NW)\\s+OF\\s+(?<singlepointcoord>(?<pointCoord>N|S|E|W)(?<deg>\\d{3}?)(?<min>\\d{0,2})))");
	public final static Pattern sigmetOnePointLine = Pattern.compile("(?<azimuth>N|NE|E|SE|S|SW|W|NW)\\s+OF\\s+(?<singlepointcoord>(?<pointCoordLat>N|S)(?<degLat>\\d{2}?)(?<minLat>\\d{0,2})|(?<pointCoordLong>E|W)(?<degLong>\\d{3}?)(?<minLong>\\d{0,2}))");
	
	public final static Pattern sigmetVolcanoPoint = Pattern.compile("(?<azimuth>N|NE|E|SE|S|SW|W|NW)\\s+OF\\s+(?<singlepointcoord>(?<pointCoordLat>N|S)(?<degLat>\\d{2}?)(?<minLat>\\d{0,2})|(?<pointCoordLong>E|W)(?<degLong>\\d{3}?)(?<minLong>\\d{0,2}))");
	
	/**Pattern to find several lines (S OF LINE ... AND N OF LINE.... AND ...)*/
	//public final static Pattern sigmetMultiLine = Pattern.compile("((?<azimuth>N|NE|E|SE|S|SW|W|NW)(?:\\s+OF\\s+LINE\\s+)(?<pointStart>(?<latStart>N|S)(?<latStartDeg>\\d{2})(?<latStartMin>\\d{0,2})?\\s+(?<longStart>E|W))(?<longStartDeg>\\d{3})(?<longStartMIn>\\d{0,2})?)\\s+(.)?\\s+(?<pointEnd>(?<latEnd>N|S)(?<latEndDeg>\\d{2})(?<latEndMin>\\d{0,2})?\\s+(?<longEnd>E|W)(?<longEndDeg>\\d{3})(?<longEndMin>\\d{0,2})?)");
	public final static Pattern sigmetMultiLine = Pattern.compile("(?:\\s+AND\\s+)?((?<azimuth>N|NE|E|SE|S|SW|W|NW)(?:\\s+OF\\s+LINE\\s+)(?<pointStart>(?<latStart>N|S)(?<latStartDeg>\\d{2})(?<latStartMin>\\d{0,2})?\\s+(?<longStart>E|W))(?<longStartDeg>\\d{3})(?<longStartMIn>\\d{0,2})?)\\s+(.)?\\s+(?<pointEnd>(?<latEnd>N|S)(?<latEndDeg>\\d{2})(?<latEndMin>\\d{0,2})?\\s+(?<longEnd>E|W)(?<longEndDeg>\\d{3})(?<longEndMin>\\d{0,2})?)");
	
	/**Find ZigZag (multipoint) line e.g. E OF LINE S2127 W06840 - S2320 W06803 - S2442 W06846 */
	//public final static Pattern sigmetMultiPointLine = Pattern.compile("(?<azimuth>N|NE|E|SE|S|SW|W|NW)\\s+OF\\s+LINE\\s+(?<point>N|S(\\d{2})(\\d{2})\\s+(E|W)(\\d{3})(\\d{0,2}))(\\s*-\\s*)(?&point)(\\s*-\\s*)(?&point)?(\\s*-\\s*)?(?&point)");
	public final static Pattern sigmetMultiPointLine = Pattern.compile("(?<azimuth>N|NE|E|SE|S|SW|W|NW) OF LINE (N|S(?:\\d{2})(?:\\d{2})\\s+(?:E|W)(?:\\d{3})(?:\\d{0,2}))(?:\\s*-\\s*)?(\\2)?(?:\\s*-\\s*)?(\\2)?(?:\\s*-\\s*)?(\\2)?");

	
	/**Within corridor with distance of certain line*/
	public final static Pattern sigmetWithinCorridor = Pattern.compile("(APRX|WTN)\\s+(?<range>\\d+)\\s?(KM|NM)\\s+(WID|OF)\\s+LINE\\s+(?:BTN|\\/)?");
	
	/**Whether phenomena moves or stands and intensity*/
	public final static Pattern sigmetMovement = Pattern.compile("((?<isStationery>STNR)|(MOV\\s+(?<movDirection>[A-Z]{1,3})\\s+(?<movSpeed>\\d+)?(?<speedunits>KMH|KT)?))");
	
	/**Forecasted position of phenomenon*/
	//public final static Pattern sigmetForecastSection = Pattern.compile("(FCST\\s+AT\\s+|FCST\\s+)(?<time>\\d{4})Z\\s+(?<location>.*)");
	public final static Pattern sigmetForecastSection = Pattern.compile("FCST(?:\\s+AT\\s+)?(?<time>\\d{4}Z)?\\s+(?<location>(.|\\s)+)");
	
	public final static Pattern sigmetCycloneForecastSection = Pattern.compile("FCST(?:\\s+AT\\s+)?(?<time>\\d{4}Z)?\\s+TC\\s+CEN(T)?(E)?R(E)?\\s+PSN\\s+(?<location>(.|\\s)+)");
	
	/**Vertical length (height) of the phenomena*/
	//public final static Pattern sigmetLevel = Pattern.compile("(?<hastopfl>(?<top>TOP)\\s+(?<above>ABV)?\\s*FL(?<fl>\\d+))|(?<hassurface>(?<surface>SFC)\\/(FL(?<topfl>(\\d+))|(?<heightmeters>\\d+)M))|(?<hasbothfls>FL(?<lowfl>\\d+)\\/(?<highfl>\\d+))");
	public final static Pattern sigmetLevel = Pattern.compile("(?<hastopfl>(?<top>TOP)\\s+(?<above>ABV)?(?<below>BLW)?\\s*FL(?<fl>\\d+))|(?<hassurface>(?<surface>SFC)\\/(FL(?<topfl>(\\d+))|(?<heightmeters>\\d+)(?<units>M|FT)))|(?<hasbothfls>FL(?<lowfl>\\d+)\\/(?<highfl>\\d+))");
	public final static Pattern sigmetIntensityChanges=Pattern.compile("(?<intensity>INTSF|WKN|NC)");
	
	/**Radioactive cloud mentioned WITHIN some radius from point*/
	public final static Pattern sigmetWithinRadius=Pattern.compile("(?:WI\\s+)(?<radius>\\d+)(?<radiusUnit>KM|NM)(?:\\s+OF\\s+)(?<point>(?<latitude>N|S)(?<ladeg>\\d{2})(?<lamin>\\d{0,2})?\\s+(?<longitude>E|W)(?<lodeg>\\d{3})(?<lomin>\\d{0,2})?)");
	
	/**TC radius from center point*/
	public final static Pattern sigmetCycloneWithinRadius=Pattern.compile("(?:WI\\s+)(?<radius>\\d+)(?<radiusUnit>KM|NM)(?:\\s+OF\\s+TC\\s+CENTRE)");

	
	
	/***Alex*/
	public final static Pattern sigmetVolcanicPhenomena = Pattern.compile("((?:\\s+|^)?(?<phenomena>VA\\s+ERUPTION))\\s+?(?<name>(.)*)?\\s+?(?<position>(?:PSN)(.*)(?:VA\\sCLD))\\s+(?<obsfcst>OBS|FCST)\\s+(?:AT\\s+(?<atTime>\\d{4})Z)?");
	public final static Pattern sigmetCycloneSection = Pattern.compile("FCST(?:\\s+AT\\s+)?(?<time>\\d{4}Z)?\\s+TC\\s+CEN(T)?(E)?R(E)?\\s+PSN\\s+(?<location>(.|\\s)+)");  
	public final static Pattern sigmetVolcanoForecastSection = Pattern.compile("FCST(?:\\s+AT\\s+)?(?<time>\\d{4}Z)?\\s+(?<location>(.(?!FCST))+)");
	public final static Pattern sigmetVolcanoWithinRadius=Pattern.compile("(?:WI\\s+)(?<radius>\\d+)(?<radiusUnit>KM|NM)"); 
}
