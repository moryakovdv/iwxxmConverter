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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gamc.spmi.iwxxmConverter.common.IWXXM21Helpers;
import org.gamc.spmi.iwxxmConverter.common.MessageStatusType;
import org.gamc.spmi.iwxxmConverter.common.MessageType;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.gamc.spmi.iwxxmConverter.metarconverter.METARParsingException;
import org.gamc.spmi.iwxxmConverter.metarconverter.MetarParsingRegexp;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription.ObservationType;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription.Severity;
import org.gamc.spmi.iwxxmConverter.tac.TacMessageImpl;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Implemetation of a SIGMET Tac message for meteorological sigmet (WS, not WV or WC)
 * 
 * @author moryakov
 */
public class SIGMETTacMessage extends TacMessageImpl {

	public enum Type {
		METEO,VOLCANO,CYCLONE;
	}
	
	private String sigmetDataType;
	private String issueRegion;
	private int bulletinNumber;
	private String disseminatingCentre;
	/*issuedDateTime*/
	
	private String sigmetNumber;
    private Type sigmetType = Type.METEO;
	
	private DateTime validFrom;
	private DateTime validTo;
	
	private String watchOffice;
	private String firCode;
	private String firName;
	
	private SigmetPhenomenonDescription phenomenonDescription;
	private SigmetHorizontalPhenomenonLocation horizontalLocation;
	private SigmetVerticalPhenomenonLocation verticalLocation;
	
	public String getSigmetDataType() {
		return sigmetDataType;
	}

	public void setSigmetDataType(String sigmetDataType) {
		this.sigmetDataType = sigmetDataType;
	}

	public String getIssueRegion() {
		return issueRegion;
	}

	public void setIssueRegion(String issueRegion) {
		this.issueRegion = issueRegion;
	}

	public int getBulletinNumber() {
		return bulletinNumber;
	}

	public void setBulletinNumber(int bulletinNumber) {
		this.bulletinNumber = bulletinNumber;
	}

	public String getDisseminatingCentre() {
		return disseminatingCentre;
	}

	public void setDisseminatingCentre(String disseminatingCentre) {
		this.disseminatingCentre = disseminatingCentre;
	}

	
	
	public String getSigmetNumber() {
		return sigmetNumber;
	}

	public void setSigmetNumber(String sigmetNumber) {
		this.sigmetNumber = sigmetNumber;
	}

	public Type getSigmetType() {
		return sigmetType;
	}

	public void setSigmetType(Type sigmetType) {
		this.sigmetType = sigmetType;
	}

	public DateTime getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(DateTime validFrom) {
		this.validFrom = validFrom;
	}

	public DateTime getValidTo() {
		return validTo;
	}

	public void setValidTo(DateTime validTo) {
		this.validTo = validTo;
	}

	public String getWatchOffice() {
		return watchOffice;
	}

	public void setWatchOffice(String watchOffice) {
		this.watchOffice = watchOffice;
	}

	public String getFirCode() {
		return firCode;
	}

	public void setFirCode(String firCode) {
		this.firCode = firCode;
	}

	public String getFirName() {
		return firName;
	}

	public void setFirName(String firName) {
		this.firName = firName;
	}

	public SigmetPhenomenonDescription getPhenomenonDescription() {
		return phenomenonDescription;
	}

	public void setPhenomenonDescription(SigmetPhenomenonDescription phenomenonDescription) {
		this.phenomenonDescription = phenomenonDescription;
	}

	public SigmetHorizontalPhenomenonLocation getHorizontalLocation() {
		return horizontalLocation;
	}

	public void setHorizontalLocation(SigmetHorizontalPhenomenonLocation horizontalLocation) {
		this.horizontalLocation = horizontalLocation;
	}

	public SigmetVerticalPhenomenonLocation getVerticalLocation() {
		return verticalLocation;
	}

	public void setVerticalLocation(SigmetVerticalPhenomenonLocation verticalLocation) {
		this.verticalLocation = verticalLocation;
	}

	
		
	
	public SIGMETTacMessage(String initialTacMessage) {
		super(initialTacMessage);
	
	}

	MessageStatusType messageStatusType = MessageStatusType.NORMAL;
	
	@Override
	public String getTacStartToken() {
		return "SIGMET";
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.SIGMET;
	}
	
	@Override
	public void parseMessage() throws SIGMETParsingException {
		StringBuffer tac = new StringBuffer(getInitialTacString());
		

		int lastIndex = 0;
		// parse bulletin header
		Matcher matcherBulletin = SigmetParsingRegexp.sigmetBulletinHeader.matcher(tac);
		if (matcherBulletin.find()) {
			this.setSigmetDataType(matcherBulletin.group("sigmetDataType"));
			this.setIssueRegion(matcherBulletin.group("issueRegion"));
			this.setBulletinNumber(Integer.parseInt(matcherBulletin.group("bulletinNumber")));
			this.setDisseminatingCentre(matcherBulletin.group("disseminatingCentre"));
			
			this.setMessageIssueDateTime(IWXXM21Helpers.parseDateTimeToken(matcherBulletin.group("issuedDateTime")));
			
			lastIndex = matcherBulletin.end();
			tac.delete(0, lastIndex);
		} else
			throw new SIGMETParsingException("Mandatory Bulletin header section is missed");
		
		//parsing sigmet header
		Matcher matcher = getHeaderPattern().matcher(tac);
		if (matcher.find()) {

			String sigmetHeader = matcher.group("isSigmet");
			if (!sigmetHeader.equalsIgnoreCase(getTacStartToken()))
				throw new SIGMETParsingException("Not a valid "+getTacStartToken());

			this.setIcaoCode(matcher.group("icao"));
			this.setSigmetNumber(matcher.group("sigmetNumber"));
			this.setWatchOffice(matcher.group("watchOffice"));
			this.setFirCode(matcher.group("firCode"));
			this.setFirName(matcher.group("firName"));
			this.setValidFrom(IWXXM21Helpers.parseDateTimeToken(matcher.group("dateFrom")));
			this.setValidTo(IWXXM21Helpers.parseDateTimeToken(matcher.group("dateTo")));
			lastIndex = matcher.end();
			tac.delete(0, lastIndex);

		} else
			throw new SIGMETParsingException("Mandatory header section is missed in "+getTacStartToken());

		fillAndRemovePhenomenaDescription(tac);
		
	}
	
	protected StringBuffer fillAndRemovePhenomenaDescription(StringBuffer tac) { 
		
		Matcher matcherPhenomena = SigmetParsingRegexp.sigmetPhenomena.matcher(tac);
		if (matcherPhenomena.find()) {
			
			int lastIndex = matcherPhenomena.end();
			
			SigmetPhenomenonDescription phenom = new SigmetPhenomenonDescription(tac.substring(0, lastIndex));
			String sevS = matcherPhenomena.group("severity");
			String phS = matcherPhenomena.group("phenomena");
			String obsTypeS = matcherPhenomena.group("obsfcst");
			String atTimeS = matcherPhenomena.group("atTime");
			
			phenom.setPhenomenonSeverity(Severity.valueOf(sevS));
			phenom.setPhenomenon(phS);
			phenom.setPhenomenonObservation(ObservationType.valueOf(obsTypeS));
			
			DateTime parentDateTime = this.getMessageIssueDateTime();
			DateTime dtAT = phenom.parseSectionDateTimeToken(SigmetParsingRegexp.sigmetPhenomenaTimestamp, atTimeS, parentDateTime);
			
			phenom.setPhenomenonTimeStamp(dtAT);
			this.setPhenomenonDescription(phenom);
			
			tac.delete(0, lastIndex);
		}
		
		return tac;
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
		
		return new Interval(this.validFrom,this.validTo);
	}

	@Override
	public Pattern getHeaderPattern() {
		return SigmetParsingRegexp.sigmetHeader;
	}

}