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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

public class TacSectionImpl implements TacSection {

	@JsonBackReference
	private TacMessageImpl parentMessage;
	
	private String initialTacSection;
	
	
	
	/**Constructor to be overridden in children
	 * @param initialTacSection string representation of a TAC message
	 * @param parent - parent TAC message object this section belongs to
	 * */
	public TacSectionImpl(String initialTacSection, TacMessageImpl parent) {
		this.initialTacSection=initialTacSection;
		this.parentMessage = parent;
	} 
	
	/**Constructor to be overridden in children
	 * @param initialTacSection string representation of a TAC message
	 */
	public TacSectionImpl(String initialTacSection) {
		this.initialTacSection=initialTacSection;
	}
	
	@Override
	public String getInitialTacString() {
		return initialTacSection;
	}
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	public DateTime getParentIssuedDateTime() {
		if (parentMessage==null)
			return new DateTime();
		return parentMessage.getMessageIssueDateTime();
	}

	
	
	
	/**Helper method for create datetimes from initial parent dateTime and string tokens, such as AT1800, TL1900, FM1200*/
	public DateTime parseSectionDateTimeToken(Pattern dtPattern, String token, DateTime initialDateTime) {
		
		DateTime result = null;
		Matcher dtMatcher = dtPattern.matcher(token);
		
		if (dtMatcher.find()) {
			String dd =null;
			
			if (dtMatcher.groupCount()>2)
				dd = dtMatcher.group("dd");
			
			String hh = dtMatcher.group("hh");
			String mm = dtMatcher.group("mm");
			
			int hhi = Integer.valueOf(hh);
			int mmi = Integer.valueOf(mm);
			
			int ddi=0;
			
			if (hhi>23) {
				hhi=0;
				ddi=1;
			}
			
			if (dd==null) {
				
				result = initialDateTime.withTimeAtStartOfDay().plusDays(ddi).plusHours(Integer.valueOf(hhi)).plusMinutes(mmi);
			}
			else {
				int initDay = initialDateTime.getDayOfMonth();
				int secDay = Integer.valueOf(dd)+ddi;
				
				if (initDay == secDay) {
					result = initialDateTime.withTimeAtStartOfDay().plusHours(Integer.valueOf(hh)).plusMinutes(Integer.valueOf(mm));
				}
				if (initDay<secDay) {
					result = initialDateTime.withDayOfMonth(secDay).withTimeAtStartOfDay().plusHours(Integer.valueOf(hh)).plusMinutes(Integer.valueOf(mm));
				}
				if (initDay>secDay) {
					result = initialDateTime.plusMonths(1).withDayOfMonth(secDay).withTimeAtStartOfDay().plusHours(Integer.valueOf(hh)).plusMinutes(Integer.valueOf(mm));
				}
				
			}
			
		}
		return result;
	}

	public TacMessageImpl getParentMessage() {
		return parentMessage;
	}

		
	
}
