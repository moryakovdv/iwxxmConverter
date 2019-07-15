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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.gamc.spmi.iwxxmConverter.common.AnnotationLocaliedName;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/** Base implementation of a TacMessage object */
public abstract class TacMessageImpl implements TacMessage {

	private String icaoCode;
	private DateTime messageIssueDateTime;

	private String initialTacMessage;

	/**
	 * Constructor to be overridden in children
	 * 
	 * @param initialTacMessage string representation of a TAC message
	 */
	public TacMessageImpl(String initialTacMessage) {
		this.initialTacMessage = initialTacMessage;
	}

	@Override
	public String getInitialTacString() {
		return initialTacMessage;
	}

	/** @return airport icao code */
	public String getIcaoCode() {
		return icaoCode;
	}

	/**
	 * set airport icao code
	 * 
	 * @param icaoCode valid ICAO identifier
	 */
	public void setIcaoCode(String icaoCode) {
		this.icaoCode = icaoCode;
	}

	/** @return Message issue date and time */
	public DateTime getMessageIssueDateTime() {
		return messageIssueDateTime;
	}

	/**
	 * Sets Message issue date and time
	 * 
	 * @param messageIssueDateTime - date and time
	 **/
	public void setMessageIssueDateTime(DateTime messageIssueDateTime) {
		this.messageIssueDateTime = messageIssueDateTime;
	}

	@Override
	public abstract void parseMessage() throws ParsingException;

	public abstract Interval getValidityInterval();

	public abstract Pattern getHeaderPattern();

	

}
