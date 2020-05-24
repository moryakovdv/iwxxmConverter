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
package org.gamc.spmi.iwxxmConverter.test.wmo;

import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.gamc.spmi.iwxxmConverter.wmo.WMOPrecipitationRegister;
import org.junit.Test;

/**Test for parsing WMO precipitation registry*/
public class ParseWMOPreciptationsTest {

	@Test
	public void parseRegister() {
		WMOPrecipitationRegister precipRegister = new WMOPrecipitationRegister();
		precipRegister.parseWMOXml();
		assertTrue(precipRegister.getContent().size()>0);
		
		assertTrue(precipRegister.getWMOUrlByCode("+TSSNGS").equalsIgnoreCase("http://codes.wmo.int/306/4678/+TSSNGS"));
		
		
	}
	
	@Test
	public void parseLocalizedRegister() {
		WMOPrecipitationRegister precipRegister = new WMOPrecipitationRegister(Locale.forLanguageTag("ru-RU"));
		precipRegister.parseWMOXml();
		assertTrue(precipRegister.getContent().size()>0);
		
		assertTrue(precipRegister.getWMOUrlByCode("+SHGRRA").equalsIgnoreCase("http://codes.wmo.int/306/4678/+SHGRRA"));
		assertTrue(precipRegister.getContent().get("+SHSNGR").getLabel().equalsIgnoreCase("Сильный ливневой снег с градом"));
		
		
		
		
	}
	

}
