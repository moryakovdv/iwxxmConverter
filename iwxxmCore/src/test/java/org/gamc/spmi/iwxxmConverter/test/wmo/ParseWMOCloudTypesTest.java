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

import java.util.TreeMap;

import org.gamc.spmi.iwxxmConverter.wmo.WMOCloudTypeRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegisterDescription;
import org.junit.Test;

/**Test for parsing WMO clouds types  registry*/
public class ParseWMOCloudTypesTest {

	@Test
	public void parseRegister() {
		WMOCloudTypeRegister cloudsRegister = new WMOCloudTypeRegister();
		cloudsRegister.parseWMOXml();
		assertTrue(cloudsRegister.getContent().size()>0);
		System.out.println(cloudsRegister.getContent());
		TreeMap<Integer,WMORegisterDescription> cnt = cloudsRegister.getContent();
		
		 assertTrue(cloudsRegister.getWMOUrlByCode(9).equalsIgnoreCase("http://codes.wmo.int/bufr4/codeflag/0-20-012/9"));
		
	}

}
