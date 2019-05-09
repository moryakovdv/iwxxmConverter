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
package org.gamc.spmi.iwxxmConverter.marshallers.v2;

import java.util.regex.Pattern;

import org.gamc.spmi.iwxxmConverter.common.MessageStatusType;
import org.gamc.spmi.iwxxmConverter.common.MessageType;
import org.gamc.spmi.iwxxmConverter.speciconverter.SpeciParsingRegexp;


/**
 * Implementation of a SPECI Tac message based on METARTacMessage
 * 
 * @author moryakov
 */
public class SPECITacMessage extends METARTacMessage {

	
	public SPECITacMessage(String initialTac) {
		super(initialTac);
	
	}

	MessageStatusType messageStatusType = MessageStatusType.NORMAL;
	
	@Override
	public String getTacStartToken() {
		return "SPECI";
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.SPECI;
	}
	
	@Override
	public Pattern getHeaderPattern() {
		return SpeciParsingRegexp.speciHeader;
	}

}
