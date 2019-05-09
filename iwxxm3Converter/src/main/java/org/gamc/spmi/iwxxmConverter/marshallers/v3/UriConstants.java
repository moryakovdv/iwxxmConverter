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


package org.gamc.spmi.iwxxmConverter.marshallers.v3;

/**
 * Common used URIs
 * 
 * @author moryakov
 */
public class UriConstants {

	/**Schemas list for header in XML*/
	public static final String GLOBAL_SCHEMAS_LOCATION = "http://icao.int/iwxxm/3.0 http://def.wmo.int/metce/2013 http://def.wmo.int/opm/2013 http://www.aixm.aero/schema/5.1.1 http://www.opengis.net/gml/3.2 http://www.opengis.net/om/2.0 http://www.isotc211.org/2005/gco http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gsr http://www.isotc211.org/2005/gss http://www.isotc211.org/2005/gts http://www.w3.org/1999/xlink";

	/** Причина отсутствия значения */
	public static final String NIL_REASON_NOSIG = "http://codes.wmo.int/common/nil/nothingOfOperationalSignificance";

	/** Common URIs */
	
	
	public static final String OBSERVATION_TYPE_METAR = ""; //"http://codes.wmo.int/49-2/observation-type/IWXXM/3.0/MeteorologicalAerodromeObservation";
	public static final String OBSERVED_PROPERTY_METAR = "http://codes.wmo.int/49-2/observable-property/MeteorologicalAerodromeObservation";
	
	public static final String OBSERVATION_TYPE_TAF = ""; //"http://codes.wmo.int/49-2/observation-type/IWXXM/3.0/MeteorologicalAerodromeForecast";
	public static final String OBSERVED_PROPERTY_TAF = "http://codes.wmo.int/49-2/observable-property/MeteorologicalAerodromeForecast";

	/** OPEN-GIS Sampling point */
	public static final String GIS_SAMPLING_FEATURE = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint";

}
