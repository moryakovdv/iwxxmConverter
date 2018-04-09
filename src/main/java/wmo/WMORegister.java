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
package wmo;

import java.util.TreeMap;

import common.LogWriter;
import common.LoggingClass;

/**Base class for parsing WMO XML Registers.
 * Each inheritor should inplement parsing method and method for query WMO url by code
 * Resource directory stores XML-files dowloaded from WMO 
 * 
 * @author moryakov
 * */
public abstract class WMORegister implements LogWriter {
	
	/**Parse XML file and populate TreeMap storage*/
	public abstract void parseWMOXml();
	
	/**Get All content of a registry as a Map of elements*/
	public abstract TreeMap<?,?> getContent();
	
	/**Get particular URL for given code*/
	public abstract String getWMOUrlByCode(Object code);
	
	@Override
	public void writeInfo(String message) {
		LoggingClass.INSTANCE.getLoggerInstanceForClass(this.getClass()).info(message);
		
	}
	
	@Override
	public void writeDebug(String message) {
		LoggingClass.INSTANCE.getLoggerInstanceForClass(this.getClass()).debug(message);
		
	}
	
	@Override
	public void writeError(String message, Throwable err) {
		LoggingClass.INSTANCE.getLoggerInstanceForClass(this.getClass()).error(message,err);
		
	}

}
