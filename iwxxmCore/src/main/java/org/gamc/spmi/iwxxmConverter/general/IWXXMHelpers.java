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

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.UUID;
import java.util.WeakHashMap;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Set of the helper functions. Provides creation of a common objects to use
 * during xml creation. 
 * Helps to reduce boiler-plate code. 
 * The functionality may
 * be extended to provide specific implementation for METAR, TAF, SIGMET etc..
 */
public class IWXXMHelpers {

		/**
	 * By default date time format include minutes. Override this in children if
	 * necessary
	 */
	public DateTimeFormatter getDateTimeFormat() {
		return DateTimeFormat.forPattern("yyyyMMddHHmm");
	}

	/** ISO format without milliseconds */
	public DateTimeFormatter getDateTimeISOFormat() {
		return ISODateTimeFormat.dateTimeNoMillis();
	}

	
	/** Helper function to parse dateTime */
	public static DateTime parseDateTimeToken(String dtToken) throws ParsingException {

		try {
		DateTime dtNow = DateTime.now();
		int year = dtNow.getYear();
		int month = dtNow.getMonthOfYear();

		int day = Integer.valueOf(dtToken.substring(0, 2));
		int hour = Integer.valueOf(dtToken.substring(2, 4));
		int minute = Integer.valueOf(dtToken.substring(4, 6));

		if (hour>23) {
			day++;
			hour=0;
		}
		
		int daysInMonth = dtNow.dayOfMonth().getMaximumValue();

		if (day > daysInMonth)
			month--;

		
		
		DateTime dtIssued = new DateTime(year, month, day, hour, minute, DateTimeZone.UTC);

		return dtIssued;
		}
		catch(Exception e) {
			throw new ParsingException("Error occured on DateTime parsing");
		}

	}

	/** UUID V3 generating Helper */
	public String generateUUIDv3(String strId) {

		try {
			return UUID.nameUUIDFromBytes(strId.getBytes("UTF-8")).toString();
		} catch (UnsupportedEncodingException e) {
			return "uuid."+UUID.randomUUID().toString();
		}
	}
	
	
	private WeakHashMap<String, UUID> hash = new WeakHashMap<>();
	
	/**Generate V4 or find uuid in temporal storage*/
	public String generateUUIDv4(String key) {
		UUID hashU = hash.get(key);
		if (hashU==null) {
			hashU = UUID.randomUUID();
			hash.put(key, hashU);
		}
		
		return "uuid."+hashU.toString();
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}


	private Locale locale = Locale.US;
	
	
	

	
}
