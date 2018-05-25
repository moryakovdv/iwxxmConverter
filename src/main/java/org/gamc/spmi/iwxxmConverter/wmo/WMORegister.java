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
package org.gamc.spmi.iwxxmConverter.wmo;

import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**Base interface for parsing WMO XML Registers.
 * Each class should inplement parsing method and method for query WMO url by code
 * Resource directory stores XML-files dowloaded from WMO 
 * 
 * @author moryakov
 * */
public interface WMORegister {
	
	Logger registerLogger = LoggerFactory.getLogger(WMORegister.class);
	
	/**Parse XML file and populate TreeMap storage*/
	void parseWMOXml();
	
	/**Get All content of a registry as a Map of elements*/
	 TreeMap<?,?> getContent();
	
	/**Get particular URL for given code*/
	  String getWMOUrlByCode(Object code);
	
	

}
