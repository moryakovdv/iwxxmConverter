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

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**Class for parsing WMO Cloud codes
 * @see WMORegister
 * 
 * @author moryakov*/
public class WMOCloudTypeRegister implements WMORegister<Integer> {

	private static final String registerFileName = "codes.wmo.int-49-2-SigConvectiveCloudType.rdf";
	
	ConcurrentHashMap<Integer, WMORegisterDescription> wmoCloudTypeCodes = new ConcurrentHashMap<Integer, WMORegisterDescription>();
	public static final int missingCode = 63; 
	
	
	@Override
	public ConcurrentHashMap<Integer, WMORegisterDescription> getContent() {
		
		return wmoCloudTypeCodes;
	}

	public WMOCloudTypeRegister() {
		
	}
	
	private Locale locale = Locale.US;
	public WMOCloudTypeRegister(Locale locale) {
		this.locale = locale;
	}
	
	
	@Override
	public Locale getLocale() {
		return locale;
	}
	
	
	/**Returns integer code for string cloud type representation,*/
	public Integer getCloudTypeByStringCode(String strType) {
		
		switch(strType) {
			case "CB":
				return 9;
			case "TCU":
				return 32;
			default:
				return missingCode;
		}
		
		 
	}

	@Override
	public String getRegisterFileName() {

		return registerFileName;
	}

	@Override
	public void putToContent(String wmoCode, WMORegisterDescription description) {
		this.wmoCloudTypeCodes.put(Integer.valueOf(wmoCode), description);
	}
	

}
