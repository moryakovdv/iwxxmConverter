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


package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;

public class ConverterFactory {
	
	
	/**Factory produces converter for message using the start token of the input string*/
	public final static Pattern messageTypePattern = Pattern.compile("(?<messageType>METAR|TAF|SPECI|SIGMET|AIRMET|SWX(?=\\s+ADVISORY))");   
	
	public static TacConverter<?,?,IWXXM31Helpers> createForTac(String inputTac) throws ParsingException {
	
		Matcher m = messageTypePattern.matcher(inputTac);
		if (!m.find()) {
			throw new ParsingException("Can not determine message type");
		}
		
		String messageType = m.group("messageType").toUpperCase();
		
		switch(messageType) {
		case "METAR":
			return new METARConverterV3();	
			
		case "SPECI":
			return new SPECIConverterV3();	
		
		case "TAF":
			return new TAFConverterV3();	
			
		case "SIGMET":
			return new SIGMETConverterV3();	
			
		case "AIRMET":
			return new AIRMETConverterV3();	
			
		case "SWX":
			return new SPACEWEATHERConverterV3();	
			
		default:
				throw new ParsingException("Can not determine message type");
			
		}
	}
	 

}
