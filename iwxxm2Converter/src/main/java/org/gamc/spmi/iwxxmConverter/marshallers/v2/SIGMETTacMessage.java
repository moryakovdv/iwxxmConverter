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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gamc.spmi.iwxxmConverter.common.CoordPoint;
import org.gamc.spmi.iwxxmConverter.common.Coordinate;
import org.gamc.spmi.iwxxmConverter.common.DirectionFromLine;
import org.gamc.spmi.iwxxmConverter.common.Line;
import org.gamc.spmi.iwxxmConverter.common.MessageStatusType;
import org.gamc.spmi.iwxxmConverter.common.MessageType;
import org.gamc.spmi.iwxxmConverter.common.StringConstants;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SIGMETParsingException;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetForecastSection;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetHorizontalPhenomenonLocation;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetMovingSection;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetParsingRegexp;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription.Intensity;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription.ObservationType;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription.Severity;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetVerticalPhenomenonLocation;
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
	private SigmetHorizontalPhenomenonLocation horizontalLocation = new SigmetHorizontalPhenomenonLocation();
	private SigmetVerticalPhenomenonLocation verticalLocation = new SigmetVerticalPhenomenonLocation(); 
	
	
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
			
			try {
				this.setMessageIssueDateTime(IWXXM21Helpers.parseDateTimeToken(matcherBulletin.group("issuedDateTime")));
			}
			catch(ParsingException e) {
				throw new SIGMETParsingException("Check date and time");
			}
			
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
			try {
				this.setValidFrom(IWXXM21Helpers.parseDateTimeToken(matcher.group("dateFrom")));
				this.setValidTo(IWXXM21Helpers.parseDateTimeToken(matcher.group("dateTo")));
			}
			catch(ParsingException e) {
				throw new SIGMETParsingException("Check date and time in VALID sections");
			}
			lastIndex = matcher.end();
			tac.delete(0, lastIndex);

		} else
			throw new SIGMETParsingException("Mandatory header section is missed in "+getTacStartToken());

		fillAndRemovePhenomenaDescription(tac);
		fillAndRemoveForecastedLocation(tac);
		fillLocationSection(tac);
		fillIntensity(tac);
		fillMovingSection(tac);
		fillLevel(tac);
		
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
			
			if (sevS!=null)
				phenom.setPhenomenonSeverity(Severity.valueOf(sevS));
			phenom.setPhenomenon(phS);
			phenom.setPhenomenonObservation(ObservationType.valueOf(obsTypeS));
			
			DateTime parentDateTime = this.getMessageIssueDateTime();
			
			DateTime dtAT = atTimeS==null?parentDateTime:phenom.parseSectionDateTimeToken(SigmetParsingRegexp.sigmetPhenomenaTimestamp, atTimeS, parentDateTime);
			phenom.setPhenomenonTimeStamp(dtAT);

			this.setPhenomenonDescription(phenom);
			
			tac.delete(0, lastIndex);
		}
		
		return tac;
	}
	
	protected StringBuffer fillAndRemoveForecastedLocation(StringBuffer tac) {
		Matcher matcherFcst = SigmetParsingRegexp.sigmetForecastSection.matcher(tac);
		if (matcherFcst.find()) {
			int lastIndex = matcherFcst.end();
			
			SigmetForecastSection fSection = new SigmetForecastSection(tac.substring(matcherFcst.start(),lastIndex));
			this.getPhenomenonDescription().setForecastSection(fSection);
			String time = matcherFcst.group("time");
			String location = matcherFcst.group("location");
			DateTime parentDateTime = this.getMessageIssueDateTime();
			
			DateTime dtAT = time==null?parentDateTime:fSection.parseSectionDateTimeToken(SigmetParsingRegexp.sigmetPhenomenaTimestamp, time, parentDateTime);
			
			fSection.setForecastedTime(dtAT);
			Matcher matcherFcstLocation = SigmetParsingRegexp.sigmetOnePointLine.matcher(location);
			
			while(matcherFcstLocation.find()) {
				String azimuth = matcherFcstLocation.group("azimuth");
				String pointCoordLat = matcherFcstLocation.group("pointCoordLat");
				String pointDegLat = matcherFcstLocation.group("degLat");
				String pointMinLat = matcherFcstLocation.group("minLat");
				
				String pointCoordLong = matcherFcstLocation.group("pointCoordLong");
				String pointDegLong = matcherFcstLocation.group("degLong");
				String pointMinLong = matcherFcstLocation.group("minLong");

				String pointCoord = StringConstants.coalesce(pointCoordLat,pointCoordLong);
				String pointDeg = StringConstants.coalesce(pointDegLat,pointDegLong);
				String pointMin = StringConstants.coalesce(pointMinLat,pointMinLong);
			
				Line sigmetLine = new Line(new Coordinate(RUMB_UNITS.valueOf(pointCoord),Integer.parseInt(pointDeg),pointMin.isEmpty()?0:Integer.parseInt(pointMin)));
				DirectionFromLine dirLine = new DirectionFromLine(RUMB_UNITS.valueOf(azimuth),sigmetLine);
				
				fSection.getAreas().add(dirLine);
			}
			
			tac.delete(matcherFcst.start(), lastIndex);
		}
			
		
		
		return tac;
	}
	
	protected StringBuffer fillLocationSection(StringBuffer tac) {
		
		fillEntireFIRLocation(tac);
		if (this.getHorizontalLocation().isEntireFIR())
			return tac; // not necessary to check location further
		
		fillWithinPolygon(tac);
		if (this.getHorizontalLocation().isInPolygon())
			return tac; // not necessary to check location further
		
		fillWithinCorridor(tac);
		if (this.getHorizontalLocation().isWithinCorridor()) 
			return tac; // not necessary to check location further
		
		fillWithinRadius(tac);
		if (this.getHorizontalLocation().isWithinRadius())
			return tac;
		
		fillLineAreaLocation(tac);
		
		return tac;
	}
	
	protected StringBuffer fillWithinRadius(StringBuffer tac) {
		
		Matcher matcherRadius = SigmetParsingRegexp.sigmetWithinRadius.matcher(tac);
		if (matcherRadius.find()) {
			this.getHorizontalLocation().setWithinRadius(true);
			String radius = matcherRadius.group("radius");
			String units = matcherRadius.group("radiusUnit");
			
			String lat = matcherRadius.group("latitude");
			String laDeg = matcherRadius.group("ladeg");
			String laMin = matcherRadius.group("lamin");
			String lon = matcherRadius.group("longitude");
			String loDeg = matcherRadius.group("lodeg");
			String loMin = matcherRadius.group("lomin");
			this.getHorizontalLocation().setWideness(Integer.valueOf(radius));
			this.getHorizontalLocation().setWidenessUnits(LENGTH_UNITS.valueOf(units));
			CoordPoint center = new CoordPoint(RUMB_UNITS.valueOf(lat), Integer.parseInt(laDeg), Integer.parseInt(laMin), RUMB_UNITS.valueOf(lon), Integer.parseInt(loDeg), Integer.parseInt(loMin));
			this.getHorizontalLocation().getPolygonPoints().add(center);

			
			this.getHorizontalLocation().setWideness(Integer.parseInt(radius));
			/**TODO: add center corridor line*/
		}
		
		
		return tac;
	}
	
	protected StringBuffer fillIntensity(StringBuffer tac) {
		Matcher matcherIntensity = SigmetParsingRegexp.sigmetIntensityChanges.matcher(tac);
		if (matcherIntensity.find()) {
			String intensity = matcherIntensity.group("intensity");
			this.getPhenomenonDescription().setIntencity(Intensity.valueOf(intensity));
			tac.delete(matcherIntensity.start(),matcherIntensity.end());
		}
		return tac;
	}
	
	protected StringBuffer fillLevel(StringBuffer tac) {
		
		Matcher matcherLevel = SigmetParsingRegexp.sigmetLevel.matcher(tac);
		if (matcherLevel.find()) {
			
			
		
		}
		
		return tac;
	}
	
	protected StringBuffer fillMovingSection(StringBuffer tac) {
		Matcher matcherMov = SigmetParsingRegexp.sigmetMovement.matcher(tac);
		if (matcherMov.find()) {
			SigmetMovingSection movSection = new SigmetMovingSection(tac.substring(matcherMov.start(),matcherMov.end()));
			String stationery = matcherMov.group("isStationery");
			String direction = matcherMov.group("movDirection");
			String speed = matcherMov.group("movSpeed");
			String units = matcherMov.group("speedunits");
			boolean isMoving = stationery==null||stationery.isEmpty();
			
			movSection.setMoving(isMoving);
			
			if (isMoving) {
				movSection.setMovingDirection(RUMB_UNITS.valueOf(direction));
				movSection.setSpeedUnits(SPEED_UNITS.valueOf(units));
				movSection.setMovingSpeed(Integer.valueOf(speed));
		
			}
			this.getPhenomenonDescription().setMovingSection(movSection);
			tac.delete(matcherMov.start(),matcherMov.end());
		}
		
		return tac;
	}
	
	/**check if it has WI flag and fill polygon coordinates*/
	protected StringBuffer fillWithinPolygon(StringBuffer tac) {
		Matcher matcherWI = SigmetParsingRegexp.sigmetInPolygon.matcher(tac);
		if (matcherWI.find()) {
			this.getHorizontalLocation().setInPolygon(true);
			int startIndex = matcherWI.start();
			
			Matcher matcherCoordPoint = SigmetParsingRegexp.sigmetCoordPoint.matcher(tac);
			int lastMatch = 0;
			while(matcherCoordPoint.find()) {
			
				String lat = matcherCoordPoint.group("latitude");
				String laDeg = matcherCoordPoint.group("ladeg");
				String laMin = matcherCoordPoint.group("lamin");
				String lon = matcherCoordPoint.group("longitude");
				String loDeg = matcherCoordPoint.group("lodeg");
				String loMin = matcherCoordPoint.group("lomin");
				
				CoordPoint polygonApex = new CoordPoint(RUMB_UNITS.valueOf(lat), Integer.parseInt(laDeg), Integer.parseInt(laMin), RUMB_UNITS.valueOf(lon), Integer.parseInt(loDeg), Integer.parseInt(loMin));
				this.getHorizontalLocation().getPolygonPoints().add(polygonApex);

				lastMatch = matcherCoordPoint.end();
			
				//matcherCoordPoint.reset();
			}
		
			tac.delete(startIndex, lastMatch);
		
		}
		
		return tac;
	}
	
	/**Extract location when it is in corridor*/
	protected StringBuffer fillWithinCorridor(StringBuffer tac) {
		Matcher matcherCorridor = SigmetParsingRegexp.sigmetWithinCorridor.matcher(tac);
		if (matcherCorridor.find()) {
			this.getHorizontalLocation().setWithinCorridor(true);
			String range = matcherCorridor.group("range");
			this.getHorizontalLocation().setWideness(Integer.parseInt(range));
			/**TODO: add center corridor line*/
		}
		
		return tac;
	}
	
	/**check if it has area described by lines e.g. N OF N2000 AND E OF E5555*/
	protected StringBuffer fillLineAreaLocation(StringBuffer tac) {
		
		Matcher matcherDirLine = SigmetParsingRegexp.sigmetOnePointLine.matcher(tac);
		int lastMatch = 0;
		while (matcherDirLine.find()) {
			
			String azimuth = matcherDirLine.group("azimuth");
			String pointCoordLat = matcherDirLine.group("pointCoordLat");
			String pointDegLat = matcherDirLine.group("degLat");
			String pointMinLat = matcherDirLine.group("minLat");
			
			String pointCoordLong = matcherDirLine.group("pointCoordLong");
			String pointDegLong = matcherDirLine.group("degLong");
			String pointMinLong = matcherDirLine.group("minLong");

			String pointCoord = StringConstants.coalesce(pointCoordLat,pointCoordLong);
			String pointDeg = StringConstants.coalesce(pointDegLat,pointDegLong);
			String pointMin = StringConstants.coalesce(pointMinLat,pointMinLong);		
		
			Line sigmetLine = new Line(new Coordinate(RUMB_UNITS.valueOf(pointCoord),Integer.parseInt(pointDeg),pointMin.isEmpty()?0:Integer.parseInt(pointMin)));
			DirectionFromLine dirLine = new DirectionFromLine(RUMB_UNITS.valueOf(azimuth),sigmetLine);
			this.getHorizontalLocation().getDirectionsFromLines().add(dirLine);
			lastMatch = matcherDirLine.end();
		}
		tac.delete(0, lastMatch);
		
		return tac;
	}
	
	/**check if it has ENTIRE FIR/UIR flag*/
	protected StringBuffer fillEntireFIRLocation(StringBuffer tac) {
		Matcher matcherFIR = SigmetParsingRegexp.sigmetEntireFir.matcher(tac);
		if (matcherFIR.find()) {
			
			this.getHorizontalLocation().setEntireFIR(true);
			int lastIndex=matcherFIR.end();
			tac.delete(0, lastIndex);
			return tac;
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