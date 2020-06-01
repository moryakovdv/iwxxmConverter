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

package org.gamc.spmi.iwxxmConverter.common;

/**
 * Class to store string constants
 * 
 * @author moryakov
 */
public class StringConstants {

	public static final String WMO_49_2_METCE_METAR = "WMO No. 49 Volume 2 Meteorological Service for International Air Navigation APPENDIX 3 TECHNICAL SPECIFICATIONS RELATED TO METEOROLOGICAL OBSERVATIONS AND REPORTS";
	public static final String WMO_49_2_METCE_TAF = "WMO No. 49 Volume 2 Meteorological Service for International Air Navigation APPENDIX 5 TECHNICAL SPECIFICATIONS RELATED TO FORECASTS";
	public static final String WMO_METAR_OBSERVED_PROPERTY_TITLE = "Observed properties for Meteorological Aerodrome Observation Reports (METAR and SPECI)";
	public static final String WMO_TAF_OBSERVED_PROPERTY_TITLE = "Observed properties for Meteorological Aerodrome Observation Reports (METAR and SPECI)";
	public static final String SUN_JAXB_NAMESPACE_MAPPING_PROPERTY_NAME = "com.sun.xml.bind.namespacePrefixMapper"; //com.sun.xml.internal.bind.namespacePrefixMapper";
	public static final String NO_SIGNIFICANT_CHANGES = "http://codes.wmo.int/common/nil/nothingOfOperationalSignificance";
	public static final String NO_SIGNIFICANT_WEATHER_CHANGES = "NSW";
	
	public static final String coalesce(String... strings) {
		 for (String s : strings) {
	            if (!isNullOrEmpty(s)) {
	                return s;
	            }
	        }
	        return "";
		
	}
	
	private static boolean isNullOrEmpty(String s) {
		return s==null || s.isEmpty();
		
	}
	
	
}
