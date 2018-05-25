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


package org.gamc.spmi.iwxxmConverter.common;

/**Enum to describe possible message status when taken from TAC*/
public enum MessageStatusType {
	NORMAL(""), CORRECTION("COR"), AMENDMENT("AMD"),CANCEL("CNL"), MISSING("NIL");
	
	private String code; 
	
	private MessageStatusType(String code) {
		
		this.code=code;
	}
	
	public String getCode() {
		return code;
	}
	
	public static MessageStatusType fromString(String text) {
	    for (MessageStatusType b : MessageStatusType.values()) {
	    	System.out.println(b.getCode());
	      if (b.getCode().equalsIgnoreCase(text)) {
	        return b;
	      }
	    }
	    return NORMAL;
	}
	
	
}
