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

import java.util.function.BiConsumer;

import org.gamc.spmi.iwxxmConverter.wmo.WMOCloudRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMONilReasonRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegisterDescription;
import org.gamc.spmi.iwxxmConverter.wmo.WMOSigConvectiveCloudTypeRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMOSigWXRegister;
import org.gamc.spmi.iwxxmConverter.wmo.WMOSpaceWeatherRegister;
import org.junit.Test;

/** Test for parsing WMO NilReasons */
public class ParseWMONilReasonTest {

	@Test
	public void parseRegister() {
		WMONilReasonRegister phRegister = new WMONilReasonRegister();
		phRegister.parseWMOXml();
		assertTrue(phRegister.getContent().size() > 0);
		phRegister.getContent().forEach(prn);

		assertTrue(phRegister.getWMOUrlByCode("unknown").equalsIgnoreCase("http://codes.wmo.int/common/nil/unknown"));

	}

	BiConsumer<String, WMORegisterDescription> prn = new BiConsumer<String, WMORegisterDescription>() {

		@Override
		public void accept(String arg0, WMORegisterDescription arg1) {
			System.out.println(arg0);

		}
	};

}
