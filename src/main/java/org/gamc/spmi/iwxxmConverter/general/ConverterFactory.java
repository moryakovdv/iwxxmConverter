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


package org.gamc.spmi.iwxxmConverter.general;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARConverter;
import org.gamc.spmi.iwxxmConverter.speciconverter.SPECIConverter;
import org.gamc.spmi.iwxxmConverter.tac.TacConverter;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFConverter;

public class ConverterFactory {
	
	
	/**Factory produces converter for message using the start token of the input string*/
	public final static Pattern StartTokenPattern = Pattern.compile("(?<firstToken>\\w+).+");   
	
	public static TacConverter<?,?> createForTac(String inputTac) throws ParsingException {
	
		Matcher m = StartTokenPattern.matcher(inputTac);
		if (!m.find()) {
			throw new ParsingException("Start token is missed,unknown or  not implemented yet");
		}
		
		String startToken = m.group("firstToken").toUpperCase();
		
		switch(startToken) {
		case "METAR":
			return new METARConverter();	
			
		case "SPECI":
			return new SPECIConverter();	
		
		case "TAF":
			return new TAFConverter();	
			
		default:
				throw new ParsingException("Start token is missed,unknown or  not implemented yet");
			
		}
	}
	

}
