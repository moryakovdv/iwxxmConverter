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

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gamc.spmi.iwxxmConverter.common.ForecastSectionType;
import org.gamc.spmi.iwxxmConverter.common.MessageStatusType;
import org.gamc.spmi.iwxxmConverter.common.MessageType;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.general.ForecastTimedSectionType;
import org.gamc.spmi.iwxxmConverter.general.TafForecastTimeSection;
import org.gamc.spmi.iwxxmConverter.tac.TacMessageImpl;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFBecomingSection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFParsingException;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFProbabilitySection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFRemarkSection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFTempoSection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFTimedATSection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFTimedFMSection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFTimedTLSection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TafCommonWeatherSection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TafParsingRegexp;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Implemetation of a TAF Tac message
 * 
 * @author moryakov
 */
public class TAFTacMessage extends TacMessageImpl {
	
	private MessageStatusType messageStatusType = MessageStatusType.NORMAL;
	
	private TafCommonWeatherSection commonWeatherSection = new TafCommonWeatherSection(true);

	private boolean noSignificantChanges = false;

	private LinkedList<TAFBecomingSection> becomingSections = new LinkedList<TAFBecomingSection>();
	
	private LinkedList<TAFTempoSection> tempoSections = new LinkedList<TAFTempoSection>();
	
	private LinkedList<TAFProbabilitySection> probabilitySections = new LinkedList<TAFProbabilitySection>();
	
	private LinkedList<TAFRemarkSection> remarkSections = new LinkedList<TAFRemarkSection>();
	private LinkedList<TafForecastTimeSection> timedSections = new LinkedList<TafForecastTimeSection>();

	public TAFTacMessage(String initialTac) {
		super(initialTac);
	}

	@Override
	public String getTacStartToken() {
		return "TAF";
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.TAF;
	}

	/**
	 * Performs parsing message applying regexps step-by-step, truncating processed
	 * groups from message 
	 * @throws TAFParsingException
	 */
	@Override
	public void parseMessage() throws TAFParsingException {

		StringBuffer tac = new StringBuffer(getInitialTacString());
		
		int lastIndex = 0;
		// start parse header
		Matcher matcher = getHeaderPattern().matcher(tac);
		if (matcher.lookingAt()) {

			String header = matcher.group("header");
			if (!header.equalsIgnoreCase(getTacStartToken()))
				throw new TAFParsingException("Not a valid "+getTacStartToken());

			this.setIcaoCode(matcher.group("icao"));

			String dt = matcher.group("datetime");
			try {
			this.setMessageIssueDateTime(IWXXM21Helpers.parseDateTimeToken(dt));
			}
			catch(ParsingException e) {
				throw new TAFParsingException("Check date and time section");
			}

			
			String changeIndicator = matcher.group("changeIndicator");
			if (changeIndicator!=null && (! changeIndicator.isEmpty())) {
				
				this.setMessageStatusType(MessageStatusType.fromString(changeIndicator));
				
			}
			
			lastIndex = matcher.end();
			tac.delete(0, lastIndex);

		} else
			throw new TAFParsingException("TAF mandatory header section is missed");

		tac = fillAndRemoveProbabilitySections(tac);
		tac = fillAndRemoveTrendTimeSections(tac);
		tac = fillAndRemoveTrendSections(tac);
		tac = findAndRemoveRMKSection(tac);

		tac = processCommonWeatherSection(tac);

		// if (this.rvrSections.size() > 0)

		// NOSIG
		matcher = TafParsingRegexp.tafNOSIGForecast.matcher(tac);
		if (matcher.matches()) {
			this.noSignificantChanges = true;
			lastIndex = matcher.end();
			tac.delete(0, lastIndex);
		}

	}
	/**Applying regexp to parse and remove probaility sections (e.g. PROB30 TEMPO)*/
	private StringBuffer fillAndRemoveProbabilitySections(StringBuffer tac) {
		Matcher matcher = TafParsingRegexp.tafProbabilitySection.matcher(tac);
		int start = 0, end = 0;
		while (matcher.find()) {
			if (start == 0)
				start = matcher.start();

			end = matcher.end();
			String ps = matcher.group("prob");
			String prsnt = matcher.group("percent");
			String isTempo = matcher.group("tempo");
			String sTs = matcher.group("trendSection");

			TAFProbabilitySection probSection = new TAFProbabilitySection(sTs, this);
			boolean isTempoSection = ((isTempo!=null) && (!isTempo.isEmpty()));
			probSection.setTempo(isTempoSection);
			
			if (prsnt!=null) {
				probSection.setProbability(Integer.valueOf(prsnt));
			}
			
			
			this.probabilitySections.add(probSection);
		}
		
		
		if (this.probabilitySections.size() > 0) {
			tac.delete(start, end);
		}

		return tac;
	}

	/**
	 * Method for parsing the common weather section that may exists in TAF itself
	 * or its trend sections
	 */
	protected StringBuffer processCommonWeatherSection(StringBuffer tac) throws TAFParsingException {

		return this.commonWeatherSection.parseSection(tac);

	}

	/**
	 * Seeks for the BECMG, TEMPO sections in initial TAC message If found puts them
	 * into inner collections and removes from the message to avoid duplicate groups
	 * in visibility, precipitation etc..
	 */
	public StringBuffer fillAndRemoveTrendSections(StringBuffer tac) {

		Matcher matcher = TafParsingRegexp.tafForecastSection.matcher(tac);
		int start = 0, end = 0;
		while (matcher.find()) {
			if (start == 0)
				start = matcher.start();

			end = matcher.end();

			String sTt = matcher.group("trendType");
			String sTs = matcher.group("trendSection");

			ForecastSectionType sec = ForecastSectionType.valueOf(sTt);
			switch (sec) {
			case BECMG:
			default:
				TAFBecomingSection becmgSec = new TAFBecomingSection(sTs, this);
				this.becomingSections.add(becmgSec);
				break;

			case TEMPO:
				TAFTempoSection tempoSec = new TAFTempoSection(sTs, this);
				this.tempoSections.add(tempoSec);
				break;

			}

		}

		if (this.becomingSections.size() > 0 || this.tempoSections.size() > 0) {
			tac.delete(start, end);
		}

		return tac;
	}

	/**
	 * Seeks for the BECMG, TEMPO sections in initial TAC message If found puts them
	 * into inner collections and removes from the message to avoid duplicate groups
	 * in visibility, precipitation etc..
	 */
	public StringBuffer fillAndRemoveTrendTimeSections(StringBuffer tac) {

		Matcher matcher = TafParsingRegexp.tafTimedSection.matcher(tac);
		int start = 0, end = 0;
		while (matcher.find()) {
			if (start == 0)
				start = matcher.start();

			end = matcher.end();

			String sTt = matcher.group("trendTimeType");
			String sTs = matcher.group("trendTimeSection");

			ForecastTimedSectionType sec = ForecastTimedSectionType.valueOf(sTt);
			TafForecastTimeSection timeSec = null;
			switch (sec) {
			case AT:
				timeSec = new TAFTimedATSection(sTs, this);
				break;
			case FM:
				timeSec = new TAFTimedFMSection(sTs, this);
				break;
			case TL:
				timeSec = new TAFTimedTLSection(sTs, this);
				break;

			}
			if (timeSec!=null)
				this.timedSections.add(timeSec);
		}
		
		
		if (this.timedSections.size() > 0) {
			tac.delete(start, end);
		}

		return tac;
	}

	/**
	 * Seeks for the RMK section in initial TAC message. If found puts it into inner
	 * collections and removes from the message to avoid duplicate groups in
	 * visibility, precipitation etc..
	 */
	public StringBuffer findAndRemoveRMKSection(StringBuffer tac) {

		Matcher matcher = TafParsingRegexp.tafRMKSection.matcher(tac);
		int start = 0, end = 0;
		while (matcher.find()) {
			if (start == 0)
				start = matcher.start();

			end = matcher.end();

			String sRmk = matcher.group("rmk");

			TAFRemarkSection sec = new TAFRemarkSection(sRmk, this);
			this.remarkSections.add(sec);

		}

		if (this.remarkSections.size() > 0) {
			tac.delete(start, end);
		}

		return tac;
	}

	/**Get list of all sections marked BCMG*/
	public LinkedList<TAFBecomingSection> getBecomingSections() {
		return becomingSections;
	}

	/**Get list of all sections marked TEMPO*/
	public LinkedList<TAFTempoSection> getTempoSections() {
		return tempoSections;
	}

	public boolean isNoSignificantChanges() {
		return noSignificantChanges;
	}

	/**Get section marked RMK*/
	public LinkedList<TAFRemarkSection> getRemarkSections() {
		return remarkSections;
	}

	public TafCommonWeatherSection getCommonWeatherSection() {
		return commonWeatherSection;
	}

	/**Get list of all sections marked FM,AT,TL*/
	public LinkedList<TafForecastTimeSection> getTimedSections() {
		return timedSections;
	}
	
	
	
	@Override
	/**Calculates validity interval*/
	public Interval getValidityInterval() {
		DateTime issuedTime = this.getMessageIssueDateTime();
		int issuedDay = issuedTime.getDayOfMonth();
		
		DateTime validFrom = DateTime.now();
		int validFromDay = this.getCommonWeatherSection().getValidityDayFrom();
		int validFromHour = this.getCommonWeatherSection().getValidityHourFrom();
		
		if (issuedDay == validFromDay) {
			validFrom = issuedTime.withTimeAtStartOfDay().plusHours(Integer.valueOf(validFromHour));
		}
		if (issuedDay<validFromDay) {
			validFrom = issuedTime.withDayOfMonth(validFromDay).withTimeAtStartOfDay().plusHours(Integer.valueOf(validFromHour));
		}
		if (issuedDay>validFromDay) {
			validFrom = issuedTime.plusMonths(1).withDayOfMonth(validFromDay).withTimeAtStartOfDay().plusHours(Integer.valueOf(validFromHour));
		}
		
		DateTime validTill =  DateTime.now();
		
		int validTillDay = this.getCommonWeatherSection().getValidityDayTo();
		int validTillHour = this.getCommonWeatherSection().getValidityHourTo();
		
		if (issuedDay == validTillDay) {
			validTill = issuedTime.withTimeAtStartOfDay().plusHours(Integer.valueOf(validTillHour));
		}
		if (issuedDay<validTillDay) {
			validTill = issuedTime.withDayOfMonth(validTillDay).withTimeAtStartOfDay().plusHours(Integer.valueOf(validTillHour));
		}
		if (issuedDay>validTillDay) {
			validTill = issuedTime.plusMonths(1).withDayOfMonth(validTillDay).withTimeAtStartOfDay().plusHours(Integer.valueOf(validTillHour));
		}
		 
		return  new Interval(validFrom,validTill);
	}


	@Override
	/**Get status of the message - common, amendment, cancellation, correction*/
	public MessageStatusType getMessageStatusType() {
		return messageStatusType;
	}

	public void setMessageStatusType(MessageStatusType messageStatusType) {
		this.messageStatusType = messageStatusType;
	}
	
	@Override
	public Pattern getHeaderPattern() {
		return TafParsingRegexp.tafHeader;
	}

	public LinkedList<TAFProbabilitySection> getProbabilitySections() {
		return probabilitySections;
	}

	public void setProbabilitySections(LinkedList<TAFProbabilitySection> probabilitySections) {
		this.probabilitySections = probabilitySections;
	}

}
