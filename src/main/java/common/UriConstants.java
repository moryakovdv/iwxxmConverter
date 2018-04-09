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


package common;

/**
 * Common used URIs
 * 
 * @author moryakov
 */
public class UriConstants {

	/**Schemas list for header in XML*/
	public static final String GLOBAL_SCHEMAS_LOCATION = "http://icao.int/iwxxm/2.1 http://schemas.wmo.int/iwxxm/2.1/iwxxm.xsd http://www.aixm.aero/schema/5.1.1 http://www.aixm.aero/schema/5.1.1/AIXM_Features.xsd http://def.wmo.int/metce/2013 http://schemas.wmo.int/metce/1.2/metce.xsd http://www.opengis.net/om/2.0 http://schemas.opengis.net/om/2.0/observation.xsd http://www.opengis.net/sampling/2.0 http://schemas.opengis.net/sampling/2.0/samplingFeature.xsd http://www.opengis.net/samplingSpatial/2.0 http://schemas.opengis.net/samplingSpatial/2.0/spatialSamplingFeature.xsd";

	/** Причина отсутствия значения */
	public static final String NIL_REASON_NOSIG = "http://codes.wmo.int/common/nil/nothingOfOperationalSignificance";

	/** Common URIs */
	
	
	public static final String OBSERVATION_TYPE_METAR = "http://codes.wmo.int/49-2/observation-type/IWXXM/2.1/MeteorologicalAerodromeObservation";
	public static final String OBSERVED_PROPERTY_METAR = "http://codes.wmo.int/49-2/observable-property/MeteorologicalAerodromeObservation";
	
	public static final String OBSERVATION_TYPE_TAF = "http://codes.wmo.int/49-2/observation-type/IWXXM/2.1/MeteorologicalAerodromeForecast";
	public static final String OBSERVED_PROPERTY_TAF = "http://codes.wmo.int/49-2/observable-property/MeteorologicalAerodromeForecast";

	/** OPEN-GIS Sampling point */
	public static final String GIS_SAMPLING_FEATURE = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint";

}
