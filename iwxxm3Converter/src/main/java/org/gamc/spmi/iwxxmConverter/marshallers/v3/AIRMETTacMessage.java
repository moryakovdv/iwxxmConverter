package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gamc.spmi.iwxxmConverter.common.CoordPoint;
import org.gamc.spmi.iwxxmConverter.common.Coordinate;
import org.gamc.spmi.iwxxmConverter.common.Line;
import org.gamc.spmi.iwxxmConverter.common.MessageStatusType;
import org.gamc.spmi.iwxxmConverter.common.MessageType;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.AIRMETTacMessage.Type;
import org.gamc.spmi.iwxxmConverter.airmetconverter.DirectionFromLine;
import org.gamc.spmi.iwxxmConverter.airmetconverter.AIRMETParsingException;
import org.gamc.spmi.iwxxmConverter.airmetconverter.AirmetForecastSection;
import org.gamc.spmi.iwxxmConverter.airmetconverter.AirmetHorizontalPhenomenonLocation;
import org.gamc.spmi.iwxxmConverter.airmetconverter.AirmetMovingSection;
import org.gamc.spmi.iwxxmConverter.airmetconverter.AirmetParsingRegexp;
import org.gamc.spmi.iwxxmConverter.airmetconverter.AirmetPhenomenonDescription;
import org.gamc.spmi.iwxxmConverter.airmetconverter.AirmetVerticalPhenomenonLocation;
import org.gamc.spmi.iwxxmConverter.airmetconverter.AirmetPhenomenonDescription.Intensity;
import org.gamc.spmi.iwxxmConverter.airmetconverter.AirmetPhenomenonDescription.ObservationType;
import org.gamc.spmi.iwxxmConverter.airmetconverter.AirmetPhenomenonDescription.Severity;
import org.gamc.spmi.iwxxmConverter.tac.TacMessageImpl;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class AIRMETTacMessage  extends TacMessageImpl{

	public enum Type {
		METEO, VOLCANO, CYCLONE;
	}

	private String airmetDataType;
	private String issueRegion;
	private int bulletinNumber;
	private String disseminatingCentre;
	/* issuedDateTime */

	private String airmetNumber;
	private Type airmetType = Type.METEO;
	
	private String cancelAirmetNumber;
	private DateTime cancelAirmetDateTimeFrom;
	private DateTime cancelAirmetDateTimeTo;

	private DateTime validFrom;
	private DateTime validTo;

	private String watchOffice;
	private String firCode;
	private String firName;

	private AirmetPhenomenonDescription phenomenonDescription;
	private AirmetHorizontalPhenomenonLocation horizontalLocation = new AirmetHorizontalPhenomenonLocation();
	private AirmetVerticalPhenomenonLocation verticalLocation = new AirmetVerticalPhenomenonLocation();

	public String getAirmetDataType() {
		return airmetDataType;
	}

	public void setAirmetDataType(String airmetDataType) {
		this.airmetDataType = airmetDataType;
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

	public String getAirmetNumber() {
		return airmetNumber;
	}

	public void setAirmetNumber(String airmetNumber) {
		this.airmetNumber = airmetNumber;
	}

	public Type getAirmetType() {
		return airmetType;
	}

	public void setAirmetType(Type airmetType) {
		this.airmetType = airmetType;
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

	public AirmetPhenomenonDescription getPhenomenonDescription() {
		return phenomenonDescription;
	}
	
	/**Description of phenomena*/
	public void setPhenomenonDescription(AirmetPhenomenonDescription phenomenonDescription) {
		this.phenomenonDescription = phenomenonDescription;
	}

	/**Horizontal location of phenomena*/
	public AirmetHorizontalPhenomenonLocation getHorizontalLocation() {
		return horizontalLocation;
	}

	public void setHorizontalLocation(AirmetHorizontalPhenomenonLocation horizontalLocation) {
		this.horizontalLocation = horizontalLocation;
	}

	/**Vertical extension of phenomena*/
	public AirmetVerticalPhenomenonLocation getVerticalLocation() {
		return verticalLocation;
	}

	public void setVerticalLocation(AirmetVerticalPhenomenonLocation verticalLocation) {
		this.verticalLocation = verticalLocation;
	}

	public AIRMETTacMessage(String initialTacMessage) {
		super(initialTacMessage);

	}

	MessageStatusType messageStatusType = MessageStatusType.NORMAL;

	@Override
	public String getTacStartToken() {
		return "AIRMET";
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.AIRMET;
	}

	@Override
	public void parseMessage() throws AIRMETParsingException {
		StringBuffer tac = new StringBuffer(getInitialTacString());

		int lastIndex = 0;
		// parse bulletin header
		Matcher matcherBulletin = AirmetParsingRegexp.airmetBulletinHeader.matcher(tac);
		if (matcherBulletin.find()) {
			this.setAirmetDataType(matcherBulletin.group("airmetDataType"));
			this.setIssueRegion(matcherBulletin.group("issueRegion"));
			this.setBulletinNumber(Integer.parseInt(matcherBulletin.group("bulletinNumber")));
			this.setDisseminatingCentre(matcherBulletin.group("disseminatingCentre"));

			try {
				this.setMessageIssueDateTime(
						IWXXM31Helpers.parseDateTimeToken(matcherBulletin.group("issuedDateTime")));
			} catch (ParsingException e) {
				throw new AIRMETParsingException("Check date and time");
			}

			lastIndex = matcherBulletin.end();
			tac.delete(0, lastIndex);
		} else
			throw new AIRMETParsingException("Mandatory Bulletin header section is missed");

		// parsing airmet header
		Matcher matcher = getHeaderPattern().matcher(tac);
		if (matcher.find()) {

			String airmetHeader = matcher.group("isAirmet");
			if (!airmetHeader.equalsIgnoreCase(getTacStartToken()))
				throw new AIRMETParsingException("Not a valid " + getTacStartToken());

			this.setIcaoCode(matcher.group("icao"));
			this.setAirmetNumber(matcher.group("airmetNumber"));
			this.setWatchOffice(matcher.group("watchOffice"));
			this.setFirCode(matcher.group("firCode"));
			this.setFirName(matcher.group("firName"));
			try {
				this.setValidFrom(IWXXM31Helpers.parseDateTimeToken(matcher.group("dateFrom")));
				this.setValidTo(IWXXM31Helpers.parseDateTimeToken(matcher.group("dateTo")));
			} catch (ParsingException e) {
				throw new AIRMETParsingException("Check date and time in VALID sections");
			}
			
			//Check if airmet is CNL
			boolean isCancel = matcher.group("isCancel")!=null;
			if (isCancel) {
				this.setMessageStatusType(MessageStatusType.CANCEL);
				this.setCancelAirmetNumber(matcher.group("cancelNumber"));
				try {
					this.setCancelAirmetDateTimeFrom(IWXXM31Helpers.parseDateTimeToken(matcher.group("cancelDateFrom")));
					this.setCancelAirmetDateTimeTo(IWXXM31Helpers.parseDateTimeToken(matcher.group("cancelDateTo")));
				} catch (ParsingException e) {
					throw new AIRMETParsingException("Check date and time for CANCEL section");
				}
				return;
			}
			
			lastIndex = matcher.end();
			tac.delete(0, lastIndex);

		} else
			throw new AIRMETParsingException("Mandatory header section is missed in " + getTacStartToken());

		fillAndRemovePhenomenaDescription(tac);
		fillAndRemoveForecastedLocation(tac);
		fillLocationSection(tac);
		fillIntensity(tac);
		fillMovingSection(tac);
		fillLevel(tac);

	}

	protected StringBuffer fillAndRemovePhenomenaDescription(StringBuffer tac) throws AIRMETParsingException {

		Matcher matcherPhenomena = AirmetParsingRegexp.airmetPhenomena.matcher(tac);
		if (matcherPhenomena.find()) {

			int lastIndex = matcherPhenomena.end();

			AirmetPhenomenonDescription phenom = new AirmetPhenomenonDescription(tac.substring(0, lastIndex));
			String sevS = matcherPhenomena.group("severity");
			String phS = matcherPhenomena.group("phenomena");
			String obsTypeS = matcherPhenomena.group("obsfcst");
			String atTimeS = matcherPhenomena.group("atTime");

			if (sevS != null)
				phenom.setPhenomenonSeverity(Severity.valueOf(sevS));
			phenom.setPhenomenon(phS);
			phenom.setPhenomenonObservation(ObservationType.valueOf(obsTypeS));

			DateTime parentDateTime = this.getMessageIssueDateTime();

			DateTime dtAT = atTimeS == null ? parentDateTime
					: phenom.parseSectionDateTimeToken(AirmetParsingRegexp.airmetPhenomenaTimestamp, atTimeS,
							parentDateTime);
			phenom.setPhenomenonTimeStamp(dtAT);

			this.setPhenomenonDescription(phenom);

			tac.delete(0, lastIndex);
		} else {
			throw new AIRMETParsingException("No phenomena description found!");
		}

		return tac;
	}

	protected StringBuffer fillAndRemoveForecastedLocation(StringBuffer tac) {
		Matcher matcherFcst = AirmetParsingRegexp.airmetForecastSection.matcher(tac);
		if (matcherFcst.find()) {
			int lastIndex = matcherFcst.end();

			AirmetForecastSection fSection = new AirmetForecastSection(tac.substring(matcherFcst.start(), lastIndex));
			this.getPhenomenonDescription().setForecastSection(fSection);
			String time = matcherFcst.group("time");
			String location = matcherFcst.group("location");
			DateTime parentDateTime = this.getMessageIssueDateTime();

			DateTime dtAT = time == null ? parentDateTime
					: fSection.parseSectionDateTimeToken(AirmetParsingRegexp.airmetPhenomenaTimestamp, time,
							parentDateTime);

			fSection.setForecastedTime(dtAT);
			Matcher matcherFcstLocation = AirmetParsingRegexp.airmetOnePointLine.matcher(location);

			while (matcherFcstLocation.find()) {
				String azimuth = matcherFcstLocation.group("azimuth");
				String pointCoord = matcherFcstLocation.group("pointCoord");
				String pointDeg = matcherFcstLocation.group("deg");
				String pointMin = matcherFcstLocation.group("min");

				Line airmetLine = new Line(new Coordinate(RUMB_UNITS.valueOf(pointCoord), Integer.parseInt(pointDeg),
						pointMin.isEmpty() ? 0 : Integer.parseInt(pointMin)));
				DirectionFromLine dirLine = new DirectionFromLine(RUMB_UNITS.valueOf(azimuth), airmetLine);

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

		Matcher matcherRadius = AirmetParsingRegexp.airmetWithinRadius.matcher(tac);
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
			CoordPoint center = new CoordPoint(RUMB_UNITS.valueOf(lat), Integer.parseInt(laDeg),
					Integer.parseInt(laMin), RUMB_UNITS.valueOf(lon), Integer.parseInt(loDeg), Integer.parseInt(loMin));
			this.getHorizontalLocation().getPolygonPoints().add(center);

			this.getHorizontalLocation().setWideness(Integer.parseInt(radius));
			/** TODO: add center corridor line */
		}

		return tac;
	}

	protected StringBuffer fillIntensity(StringBuffer tac) {
		Matcher matcherIntensity = AirmetParsingRegexp.airmetIntensityChanges.matcher(tac);
		if (matcherIntensity.find()) {
			String intensity = matcherIntensity.group("intensity");

			this.getPhenomenonDescription().setIntencity(Intensity.valueOf(intensity));
			tac.delete(matcherIntensity.start(), matcherIntensity.end());
		}
		return tac;
	}

	protected StringBuffer fillLevel(StringBuffer tac) {

		Matcher matcherLevel = AirmetParsingRegexp.airmetLevel.matcher(tac);
		if (matcherLevel.find()) {

			AirmetVerticalPhenomenonLocation level = new AirmetVerticalPhenomenonLocation();

			boolean hasTop = matcherLevel.group("hastopfl") != null;
			boolean onSurface = matcherLevel.group("hassurface") != null;
			boolean hasBothFL = matcherLevel.group("hasbothfls") != null;
			boolean aboveFL = matcherLevel.group("above") != null;
			boolean belowFl = matcherLevel.group("below") !=null;

			level.setBottomMarginOnSurface(onSurface);
			level.setTopMarginAboveFl(aboveFL);
			level.setTopMarginBelowFl(belowFl);

			// top FL

			if (hasTop) {
				String topFL = matcherLevel.group("fl");
				Optional<Integer> oTop = Optional.of(Integer.valueOf(topFL));
				level.setTopFL(oTop);
			}

			// if on surface - set height

			if (onSurface) {
				String heightMeters = matcherLevel.group("heightMeters");
				String heightUnits = matcherLevel.group("units");
				Optional<Integer> oSurface = Optional.of(Integer.valueOf(heightMeters));
				level.setTopMarginMeters(oSurface);
				LENGTH_UNITS.valueOf(heightUnits);

			}

			if (hasBothFL) {
				String lowFL = matcherLevel.group("lowfl");
				String highFL = matcherLevel.group("highfl");

				// low
				Optional<Integer> oLowFL = Optional.of(Integer.valueOf(lowFL));
				level.setBottomFL(oLowFL);

				// high
				Optional<Integer> oHighFL = Optional.of(Integer.valueOf(highFL));
				level.setTopFL(oHighFL);
			}

			this.setVerticalLocation(level);
			tac.delete(matcherLevel.start(), matcherLevel.end());
		}

		return tac;
	}

	protected StringBuffer fillMovingSection(StringBuffer tac) {
		Matcher matcherMov = AirmetParsingRegexp.airmetMovement.matcher(tac);
		if (matcherMov.find()) {
			AirmetMovingSection movSection = new AirmetMovingSection(
					tac.substring(matcherMov.start(), matcherMov.end()));
			String stationery = matcherMov.group("isStationery");
			String direction = matcherMov.group("movDirection");
			String speed = matcherMov.group("movSpeed");
			String units = matcherMov.group("speedunits");
			boolean isMoving = stationery == null || stationery.isEmpty();

			movSection.setMoving(isMoving);

			if (isMoving) {
				movSection.setMovingDirection(RUMB_UNITS.valueOf(direction));
				if (speed != null && units != null) {
					movSection.setSpeedUnits(SPEED_UNITS.valueOf(units));
					movSection.setMovingSpeed(Integer.valueOf(speed));
				}
			}
			this.getPhenomenonDescription().setMovingSection(movSection);
			tac.delete(matcherMov.start(), matcherMov.end());
		}

		return tac;
	}

	/** check if it has WI flag and fill polygon coordinates */
	protected StringBuffer fillWithinPolygon(StringBuffer tac) {
		Matcher matcherWI = AirmetParsingRegexp.airmetInPolygon.matcher(tac);
		if (matcherWI.find()) {
			this.getHorizontalLocation().setInPolygon(true);
			int startIndex = matcherWI.start();

			Matcher matcherCoordPoint = AirmetParsingRegexp.airmetCoordPoint.matcher(tac);
			int lastMatch = 0;
			while (matcherCoordPoint.find()) {

				String lat = matcherCoordPoint.group("latitude");
				String laDeg = matcherCoordPoint.group("ladeg");
				String laMin = matcherCoordPoint.group("lamin");
				String lon = matcherCoordPoint.group("longitude");
				String loDeg = matcherCoordPoint.group("lodeg");
				String loMin = matcherCoordPoint.group("lomin");

				CoordPoint polygonApex = new CoordPoint(RUMB_UNITS.valueOf(lat), Integer.parseInt(laDeg),
						Integer.parseInt(laMin), RUMB_UNITS.valueOf(lon), Integer.parseInt(loDeg),
						Integer.parseInt(loMin));
				this.getHorizontalLocation().getPolygonPoints().add(polygonApex);

				lastMatch = matcherCoordPoint.end();

				// matcherCoordPoint.reset();
			}

			tac.delete(startIndex, lastMatch);

		}

		return tac;
	}

	/** Extract location when it is in corridor */
	protected StringBuffer fillWithinCorridor(StringBuffer tac) {
		Matcher matcherCorridor = AirmetParsingRegexp.airmetWithinCorridor.matcher(tac);
		if (matcherCorridor.find()) {
			this.getHorizontalLocation().setWithinCorridor(true);
			String range = matcherCorridor.group("range");
			this.getHorizontalLocation().setWideness(Integer.parseInt(range));
			/** TODO: add center corridor line */
		}

		return tac;
	}

	/** check if it has area described by lines e.g. N OF N2000 AND E OF E5555 */
	protected StringBuffer fillLineAreaLocation(StringBuffer tac) {

		Matcher matcherDirLine = AirmetParsingRegexp.airmetOnePointLine.matcher(tac);
		int lastMatch = 0;
		while (matcherDirLine.find()) {

			String azimuth = matcherDirLine.group("azimuth");
			String pointCoord = matcherDirLine.group("pointCoord");
			String pointDeg = matcherDirLine.group("deg");
			String pointMin = matcherDirLine.group("min");

			Line airmetLine = new Line(new Coordinate(RUMB_UNITS.valueOf(pointCoord), Integer.parseInt(pointDeg),
					pointMin.isEmpty() ? 0 : Integer.parseInt(pointMin)));
			DirectionFromLine dirLine = new DirectionFromLine(RUMB_UNITS.valueOf(azimuth), airmetLine);
			this.getHorizontalLocation().getDirectionsFromLines().add(dirLine);
			lastMatch = matcherDirLine.end();
		}
		tac.delete(0, lastMatch);

		return tac;
	}

	/** check if it has ENTIRE FIR/UIR flag */
	protected StringBuffer fillEntireFIRLocation(StringBuffer tac) {
		Matcher matcherFIR = AirmetParsingRegexp.airmetEntireFir.matcher(tac);
		if (matcherFIR.find()) {

			this.getHorizontalLocation().setEntireFIR(true);
			int lastIndex = matcherFIR.end();
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

		return new Interval(this.validFrom, this.validTo);
	}

	@Override
	public Pattern getHeaderPattern() {
		return AirmetParsingRegexp.airmetHeader;
	}

	public String getCancelAirmetNumber() {
		return cancelAirmetNumber;
	}

	public void setCancelAirmetNumber(String cancelAirmetNumber) {
		this.cancelAirmetNumber = cancelAirmetNumber;
	}

	public DateTime getCancelAirmetDateTimeFrom() {
		return cancelAirmetDateTimeFrom;
	}

	public void setCancelAirmetDateTimeFrom(DateTime cancelAirmetDateTimeFrom) {
		this.cancelAirmetDateTimeFrom = cancelAirmetDateTimeFrom;
	}

	public DateTime getCancelAirmetDateTimeTo() {
		return cancelAirmetDateTimeTo;
	}

	public void setCancelAirmetDateTimeTo(DateTime cancelAirmetDateTimeTo) {
		this.cancelAirmetDateTimeTo = cancelAirmetDateTimeTo;
	}
}
