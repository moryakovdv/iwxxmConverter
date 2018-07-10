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
package org.gamc.spmi.iwxxmConverter.sigmetconverter;

import java.util.regex.Pattern;

import org.gamc.spmi.iwxxmConverter.common.MessageStatusType;
import org.gamc.spmi.iwxxmConverter.common.MessageType;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.gamc.spmi.iwxxmConverter.tac.TacMessageImpl;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Implemetation of a SIGMET Tac message
 * 
 * @author moryakov
 */
public class SIGMETTacMessage extends TacMessageImpl {

	public enum Severity {
		OBSCURED,SQALL,EMBEDDED,FREQUENT,SEVERE,HEAVY, NOTSET;
	}
	
	public enum ObservationType {
		FORECAST, OBSERVE, NOTSET;
	}
	
	private String firName;
	private String phenomenaName;
	private boolean isMoving=false;
	private String movingDirection;
	private int movingSpeed;
	private SPEED_UNITS speedUnits;
	private String fl;
	
	private Severity phenomenaSeverity=Severity.NOTSET;
	private ObservationType phenomenaObservation=ObservationType.NOTSET;
	
	
	
	public SIGMETTacMessage(String initialTacMessage) {
		super(initialTacMessage);
	
	}

	MessageStatusType messageStatusType = MessageStatusType.NORMAL;
	
	@Override
	public String getTacStartToken() {
		return "";
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.SIGMET;
	}
	
	@Override
	public void parseMessage() throws SIGMETParsingException {
		
		
		
	}

	@Override
	public MessageStatusType getMessageStatusType() {

		return messageStatusType;
	}

	public void setMessageStatusType(MessageStatusType messageStatusType) {
		this.messageStatusType = messageStatusType;
	}

	@Override
	public Interval getValidityInterval() {
		
		/**TODO - calculate sigmet validity!!*/
		DateTime start = getMessageIssueDateTime();
		DateTime end = getMessageIssueDateTime().plusMinutes(30);
		
		return new Interval(start,end);
	}

	@Override
	public Pattern getHeaderPattern() {
		return SigmetParsingRegexp.sigmetHeader;
	}

}