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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;

import wmo.WMOPrecipitationRegister;


/**Unit test to check all types of logging*/
public class LoggingTest {

	
	/**General log output*/
	@Test
	public void testMainLogger() {
		Logger log = LoggingClass.INSTANCE.getLoggerInstance();
		assertNotNull(log);
		
		log.debug("test DEBUG");
		log.error("test ERROR");
		log.info("test INFO");
	}
	
	
	/**Output for named logger NameSpaceMapper*/
	@Test
	public void testCustomClassLogger() {
		Logger log = LoggingClass.INSTANCE.getLoggerInstanceForClass(LoggingTest.class);
		assertNotNull(log);
		
		log.debug("test DEBUG");
		log.error("test ERROR");
		log.info("test INFO");
		
	}
	
	/**Output for the class implementing the LogWriter interface*/
	@Test
	public void testInterfaceLogger() {
		WMOPrecipitationRegister wmor = new WMOPrecipitationRegister();
		wmor.writeDebug("debug message");
		wmor.writeError("error message", new Exception("THIS IS TEST EXCEPTION. IT SHOULD BE HERE"));
		wmor.writeInfo("INFO message");
	}
	

}
