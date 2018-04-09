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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**This singleton provides general and custom log capabilities
 * 
 * @author moryakov
 * */
public enum LoggingClass {
	
	INSTANCE;
	
	private final static String LOGGERNAME = "IWXXMConverter";
    private Logger mainLogger;
    
  
    /**Get general logger instance*/
    public Logger getLoggerInstance() {
    	if (mainLogger==null) {
    		mainLogger = LoggerFactory.getLogger(LOGGERNAME);
    	}
    	return mainLogger;
    }

    /**Get custom logger instance for a given class*/
    public Logger getLoggerInstanceForClass(Class<?> cls) {
    	return LoggerFactory.getLogger(cls);
    }

}
