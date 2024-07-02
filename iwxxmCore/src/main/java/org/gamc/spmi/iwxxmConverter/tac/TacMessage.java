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
package org.gamc.spmi.iwxxmConverter.tac;

import org.gamc.spmi.iwxxmConverter.common.MessageStatusType;
import org.gamc.spmi.iwxxmConverter.common.MessageType;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**General interface for TAC messages*/
public interface TacMessage {

	/**@return the initial TAC*/
	String getInitialTacString();
	
	/**@return the start token to recognize message type*/
	String getTacStartToken(); 
	
	/**@return the {@link org.gamc.spmi.iwxxmConverter.common.MessageType} for this message*/
	MessageType getMessageType();
	
	MessageStatusType getMessageStatusType();
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	DateTime getMessageIssueDateTime();
	
	/**ICAO code*/
	String getIcaoCode();

	/**Perform the parsing from initial tac string*/
	void parseMessage() throws ParsingException;
	
}
