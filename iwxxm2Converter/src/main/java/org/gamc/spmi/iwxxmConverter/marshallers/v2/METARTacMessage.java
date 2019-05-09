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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gamc.spmi.iwxxmConverter.common.ForecastSectionType;
import org.gamc.spmi.iwxxmConverter.common.MessageStatusType;
import org.gamc.spmi.iwxxmConverter.common.MessageType;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.general.ForecastTimedSectionType;
import org.gamc.spmi.iwxxmConverter.general.MetarForecastTimeSection;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARBecomingSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARParsingException;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARRVRSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARRemarkSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARRunwayStateSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARTempoSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARTimedATSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARTimedFMSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARTimedTLSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.MetarCommonWeatherSection;
import org.gamc.spmi.iwxxmConverter.metarconverter.MetarParsingRegexp;
import org.gamc.spmi.iwxxmConverter.metarconverter.RVROperator;
import org.gamc.spmi.iwxxmConverter.metarconverter.RVRVisibilityTendency;
import org.gamc.spmi.iwxxmConverter.tac.TacMessageImpl;
import org.joda.time.DateTime;
import org.joda.time.Interval;


/**
 * Implemetation of a METAR Tac message
 * 
 * @author moryakov
 */
public class METARTacMessage extends TacMessageImpl {

	
	MessageStatusType messageStatusType = MessageStatusType.NORMAL;
	
	private MetarCommonWeatherSection commonWeatherSection = new MetarCommonWeatherSection(true);

	private boolean windShearForAll = false;
	private boolean noSignificantChanges = false;

	private LinkedList<METARRVRSection> rvrSections = new LinkedList<METARRVRSection>();
	private LinkedList<METARRunwayStateSection> runwayStateSections = new LinkedList<METARRunwayStateSection>();
	private LinkedList<String> windShearSections = new LinkedList<String>();

	private LinkedList<METARBecomingSection> becomingSections = new LinkedList<METARBecomingSection>();
	private LinkedList<METARTempoSection> tempoSections = new LinkedList<METARTempoSection>();
	private LinkedList<METARRemarkSection> remarkSections = new LinkedList<METARRemarkSection>();
	
	private LinkedList<MetarForecastTimeSection> timedSections = new LinkedList<MetarForecastTimeSection>();
	
	public METARTacMessage(String initialTac) {
		super(initialTac);
	}

	@Override
	public String getTacStartToken() {
		return "METAR";
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.METAR;
	}

	/**
	 * Performs parsing message applying regexps step-by-step, truncating
	 * processed groups from message /* @throws METARParsingException
	 */
	@Override
	public void parseMessage() throws METARParsingException {

		StringBuffer tac = new StringBuffer(getInitialTacString());
		// seek trend forecast and RMK sections to process them later.

		int lastIndex = 0;
		// start parse header
		Matcher matcher = getHeaderPattern().matcher(tac);
		if (matcher.lookingAt()) {

			String header = matcher.group("header");
			if (!header.equalsIgnoreCase(getTacStartToken()))
				throw new METARParsingException("Not a valid "+getTacStartToken());

			this.setIcaoCode(matcher.group("icao"));

			String dt = matcher.group("datetime");
			try {
				this.setMessageIssueDateTime(IWXXM21Helpers.parseDateTimeToken(dt));
			}
			catch(ParsingException e) {
				throw new METARParsingException("Check date and time");
			}
			
			String changeIndicator = matcher.group("changeIndicator");
			if (changeIndicator!=null && (! changeIndicator.isEmpty())) {
				
				this.setMessageStatusType(MessageStatusType.fromString(changeIndicator));
				
			}
			
			
			lastIndex = matcher.end();
			tac.delete(0, lastIndex);

		} else
			throw new METARParsingException("Mandatory header section is missed in "+getTacStartToken());

		tac = fillAndRemoveTrendTimeSections(tac);
		tac = fillAndRemoveTrendSections(tac);
		tac = findAndRemoveRMKSection(tac);

				
		// process RVR

		matcher = MetarParsingRegexp.metarRVR.matcher(tac);

		while (matcher.find()) {
			
			int startIndex=matcher.start();
			lastIndex = matcher.end();
			
			String sRWDesignator = matcher.group("runwayDesignator");
			String sRvr = matcher.group("rvr");
			String sTendency = matcher.group("tendency");
			String sRvrOper = matcher.group("rvrOperator");
			String sUnits = matcher.group("units");

			METARRVRSection rvrSection = new METARRVRSection(tac.substring(startIndex , lastIndex),this);
			rvrSection.setRvrDesignator(sRWDesignator);
			rvrSection.setRvrValue(Integer.valueOf(sRvr));
			if (sTendency!=null && sTendency.length() > 0)
				rvrSection.setTendency(RVRVisibilityTendency.valueOf(sTendency));
				
			if (sRvrOper!=null)
				rvrSection.setOperator(RVROperator.valueOf(sRvrOper));
			
			if (sUnits!=null && sUnits.equalsIgnoreCase("FT")) {
				
				rvrSection.setUnits(LENGTH_UNITS.FT);
			}
			
			this.rvrSections.add(rvrSection);

			tac.delete(startIndex, lastIndex);
			matcher.reset();
		}
		
		matcher = MetarParsingRegexp.metarRunwayState.matcher(tac);

		while (matcher.find()) {
			
			int startIndex=matcher.start();
			lastIndex = matcher.end();
			
			
			String sRWDesignator = matcher.group("rwCode");
			String sRWCleared = matcher.group("cleared");
			
			String sType = matcher.group("depositType");
			String sContamination = matcher.group("contamination");
			String sDepth = matcher.group("depth");
			String sFriction = matcher.group("friction");

			METARRunwayStateSection rwsSection = new METARRunwayStateSection(tac.substring(startIndex , lastIndex));
			if (sRWDesignator.equalsIgnoreCase("88"))
				rwsSection.setApplicableForAllRunways(true);
			
			rwsSection.setRvrDesignator(sRWDesignator);
			
			rwsSection.setCleared(sRWCleared!=null);
			
			
			if (sType!=null && ! sType.equalsIgnoreCase("/"))
				rwsSection.setType( Optional.of(Integer.valueOf(sType)));
			
			if (sContamination!=null && ! sContamination.equalsIgnoreCase("/"))
				rwsSection.setContamination(Optional.of(Integer.valueOf(sContamination)));
			
			if (sDepth!=null && ! sDepth.equalsIgnoreCase("//"))
				rwsSection.setDepositDepth(Optional.of(Integer.valueOf(sDepth)));
			
			if (sFriction!=null && ! sFriction.equalsIgnoreCase("//"))
				rwsSection.setFriction(Optional.of(Integer.valueOf(sFriction)));
			
			this.runwayStateSections.add(rwsSection);

			tac.delete(startIndex, lastIndex);
			matcher.reset();
			
		}
		

		//if (this.rvrSections.size() > 0)
		
		tac = processCommonWeatherSection(tac);

		// Wind shear for all runways
		matcher = MetarParsingRegexp.metarWindShearAll.matcher(tac);
		if (matcher.find()) {
			windShearForAll = true;
			lastIndex = matcher.end();
			tac.delete(0, lastIndex);
		}

		
		
		// Wind shear for some runways
		matcher = MetarParsingRegexp.metarWindShearRunway.matcher(tac);
		while (matcher.find()) {
			String rw = matcher.group("wsRunway");
			this.windShearSections.add(rw);

			lastIndex = matcher.end();
		}

		if (this.windShearSections.size() > 0) {
			tac.delete(0, lastIndex);
		}

		// NOSIG
		matcher = MetarParsingRegexp.metarNOSIGForecast.matcher(tac);
		if (matcher.matches()) {
			this.noSignificantChanges = true;
			lastIndex = matcher.end();
			tac.delete(0, lastIndex);
		}
		
		if (this.isNoSignificantChanges()) {
			if (this.getBecomingSections().size()>0 || this.getTimedSections().size()>0) {
				throw new METARParsingException("NOSIG messed with trend sections!");
			}
		}

	}

	/**
	 * Method for parsing the common weather section that may exists in METAR
	 * itself or its trend sections
	 */
	protected StringBuffer processCommonWeatherSection(StringBuffer tac) throws METARParsingException {

		return this.commonWeatherSection.parseSection(tac);

		
	}

	/**
	 * Seeks for the BECMG, TEMPO sections in initial TAC message If found puts
	 * them into inner collections and removes from the message to avoid
	 * duplicate groups in visibility, precipitation etc..
	 */
	public StringBuffer fillAndRemoveTrendSections(StringBuffer tac) {

		Matcher matcher = MetarParsingRegexp.metarForecastSection.matcher(tac);
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
				METARBecomingSection becmgSec = new METARBecomingSection(sTs,this);
				this.becomingSections.add(becmgSec);
				break;

			case TEMPO:
				METARTempoSection tempoSec = new METARTempoSection(sTs,this);
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

		Matcher matcher = MetarParsingRegexp.metarTimedSection.matcher(tac);
		int start = 0, end = 0;
		while (matcher.find()) {
			if (start == 0)
				start = matcher.start();

			end = matcher.end();

			String sTt = matcher.group("trendTimeType");
			String sTs = matcher.group("trendTimeSection");

			ForecastTimedSectionType sec = ForecastTimedSectionType.valueOf(sTt);
			MetarForecastTimeSection timeSec = null;
			switch (sec) {
			case AT:
				timeSec = new METARTimedATSection(sTs, this);
				break;
			case FM:
				timeSec = new METARTimedFMSection(sTs, this);
				break;
			case TL:
				timeSec = new METARTimedTLSection(sTs, this);
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
	 * Seeks for the RMK section in initial TAC message.
	 * If found puts it into inner collections and removes from the message to avoid
	 * duplicate groups in visibility, precipitation etc..
	 * */
	public StringBuffer findAndRemoveRMKSection(StringBuffer tac) {

		Matcher matcher = MetarParsingRegexp.metarRMKSection.matcher(tac);
		int start = 0, end = 0;
		while (matcher.find()) {
			if (start == 0)
				start = matcher.start();

			end = matcher.end();

			String sRmk = matcher.group("rmk");

			METARRemarkSection sec = new METARRemarkSection(sRmk,this);
			this.remarkSections.add(sec);

		}

		if (this.remarkSections.size() > 0) {
			tac.delete(start, end);
		}

		return tac;
	}

	public LinkedList<METARBecomingSection> getBecomingSections() {
		return becomingSections;
	}

	public LinkedList<METARTempoSection> getTempoSections() {
		return tempoSections;
	}

	public LinkedList<METARRVRSection> getRvrSections() {
		return rvrSections;
	}

	public boolean isWindShearForAll() {
		return windShearForAll;
	}

	
	public LinkedList<String> getWindShearSections() {
		return windShearSections;
	}

	public boolean isNoSignificantChanges() {
		return noSignificantChanges;
	}

	public LinkedList<METARRemarkSection> getRemarkSections() {
		return remarkSections;
	}

	public MetarCommonWeatherSection getCommonWeatherSection() {
		return commonWeatherSection;
	}

	public LinkedList<METARRunwayStateSection> getRunwayStateSections() {
		return runwayStateSections;
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
		DateTime start = getMessageIssueDateTime();
		DateTime end = getMessageIssueDateTime().plusMinutes(30);
		
		return new Interval(start,end);
	}

	@Override
	public Pattern getHeaderPattern() {
		return MetarParsingRegexp.metarHeader;
	}

	public LinkedList<MetarForecastTimeSection> getTimedSections() {
		return timedSections;
	}

	public void setTimedSections(LinkedList<MetarForecastTimeSection> timedSections) {
		this.timedSections = timedSections;
	}

}
