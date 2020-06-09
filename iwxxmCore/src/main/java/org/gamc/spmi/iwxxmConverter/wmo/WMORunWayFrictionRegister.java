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

/**Class for parsing WMO contamination codes (0-20-087)
 * @see WMORegister
 * 
 * @author moryakov*/
public class WMORunWayFrictionRegister implements WMORegister<Integer> {

	private static final String registerFileName = "codes.wmo.int-bufr4-codeflag-0-20-089.rdf";
	
	ConcurrentHashMap<Integer, WMORegisterDescription> wmoFrictionCodes = new ConcurrentHashMap<Integer, WMORegisterDescription>();
	
	public WMORunWayFrictionRegister() {

	}

	private Locale locale = Locale.US;

	public WMORunWayFrictionRegister(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}
	
	@Override
	public ConcurrentHashMap<Integer, WMORegisterDescription> getContent() {
		// TODO Auto-generated method stub
		return wmoFrictionCodes;
	}

	@Override
	public void putToContent(String wmoCode, WMORegisterDescription description) {
		this.wmoFrictionCodes.put(Integer.valueOf(wmoCode), description);
	}
	
	@Override
	public String getRegisterFileName() {

		return registerFileName;
	}

}
