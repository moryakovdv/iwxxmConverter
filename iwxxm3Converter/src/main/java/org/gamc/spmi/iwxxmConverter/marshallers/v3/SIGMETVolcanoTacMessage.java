package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.util.Optional;
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
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETTacMessage.Type;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SIGMETParsingException;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetForecastSection;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetHorizontalPhenomenonLocation;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetMovingSection;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetParsingRegexp;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetVerticalPhenomenonLocation;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetVolcanoForecastSection;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetVolcanoPhenomenonDescription;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription.Intensity;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription.ObservationType;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription.Severity;
import org.gamc.spmi.iwxxmConverter.tac.TacMessageImpl;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Implemetation of a SIGMET Tac message for meteorological sigmet (WS, not WV
 * or WC)
 * 
 * @author moryakov
 */
public class SIGMETVolcanoTacMessage extends TacMessageImpl {

	public enum Type {
		METEO, VOLCANO, CYCLONE;
	}

	private String sigmetDataType;
	private String issueRegion;
	private int bulletinNumber;
	private String disseminatingCentre;
	/* issuedDateTime */

	private String sigmetNumber;
	private Type sigmetType = Type.VOLCANO;

	private String cancelSigmetNumber;
	private DateTime cancelSigmetDateTimeFrom;
	private DateTime cancelSigmetDateTimeTo;

	private DateTime validFrom;
	private DateTime validTo;

	private String watchOffice;
	private String firCode;
	private String firName;
	private SigmetVolcanoForecastSection fSection;
	private SigmetVolcanoPhenomenonDescription phenomenonDescription;
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

	public SigmetVolcanoPhenomenonDescription getPhenomenonDescription() {
		return phenomenonDescription;
	}

	/** Description of phenomena */
	public void setPhenomenonDescription(SigmetVolcanoPhenomenonDescription phenomenonDescription) {
		this.phenomenonDescription = phenomenonDescription;
	}

	/** Horizontal location of phenomena */
	public SigmetHorizontalPhenomenonLocation getHorizontalLocation() {
		return horizontalLocation;
	}

	public void setHorizontalLocation(SigmetHorizontalPhenomenonLocation horizontalLocation) {
		this.horizontalLocation = horizontalLocation;
	}

	/** Vertical extension of phenomena */
	public SigmetVerticalPhenomenonLocation getVerticalLocation() {
		return verticalLocation;
	}

	public void setVerticalLocation(SigmetVerticalPhenomenonLocation verticalLocation) {
		this.verticalLocation = verticalLocation;
	}

	public SIGMETVolcanoTacMessage(String initialTacMessage) {
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
				this.setMessageIssueDateTime(
						IWXXM31Helpers.parseDateTimeToken(matcherBulletin.group("issuedDateTime")));
			} catch (ParsingException e) {
				throw new SIGMETParsingException("Check date and time");
			}

			lastIndex = matcherBulletin.end();
			tac.delete(0, lastIndex);
		} else
			throw new SIGMETParsingException("Mandatory Bulletin header section is missed");

		// parsing sigmet header
		Matcher matcher = getHeaderPattern().matcher(tac);
		if (matcher.find()) {

			String sigmetHeader = matcher.group("isSigmet");
			if (!sigmetHeader.equalsIgnoreCase(getTacStartToken()))
				throw new SIGMETParsingException("Not a valid " + getTacStartToken());

			this.setIcaoCode(matcher.group("icao"));
			this.setSigmetNumber(matcher.group("sigmetNumber"));
			this.setWatchOffice(matcher.group("watchOffice"));
			this.setFirCode(matcher.group("firCode"));
			this.setFirName(matcher.group("firName"));
			try {
				this.setValidFrom(IWXXM31Helpers.parseDateTimeToken(matcher.group("dateFrom")));
				this.setValidTo(IWXXM31Helpers.parseDateTimeToken(matcher.group("dateTo")));
			} catch (ParsingException e) {
				throw new SIGMETParsingException("Check date and time in VALID sections");
			}

			// Check if sigmet is CNL
			boolean isCancel = matcher.group("isCancel") != null;
			if (isCancel) {
				this.setMessageStatusType(MessageStatusType.CANCEL);
				this.setCancelSigmetNumber(matcher.group("cancelNumber"));
				try {
					this.setCancelSigmetDateTimeFrom(
							IWXXM31Helpers.parseDateTimeToken(matcher.group("cancelDateFrom")));
					this.setCancelSigmetDateTimeTo(IWXXM31Helpers.parseDateTimeToken(matcher.group("cancelDateTo")));
				} catch (ParsingException e) {
					throw new SIGMETParsingException("Check date and time for CANCEL section");
				}
				return;
			}

			lastIndex = matcher.end();
			tac.delete(0, lastIndex);

		} else
			throw new SIGMETParsingException("Mandatory header section is missed in " + getTacStartToken());

		fillAndRemovePhenomenaDescription(tac);
		fillAndRemoveForecastedLocation(tac);
		fillLocationSection(tac);
		fillIntensity(tac);
		fillMovingSection(tac);
		fillLevel(tac);

	}

	protected StringBuffer fillAndRemovePhenomenaDescription(StringBuffer tac) throws SIGMETParsingException {

		Matcher matcherPhenomena = SigmetParsingRegexp.sigmetPhenomena.matcher(tac);
		if (matcherPhenomena.find()) {

			int lastIndex = matcherPhenomena.end();
			/*
			 * Matcher sigmetMachType = SigmetParsingRegexp.sigmetType.matcher(tac); if
			 * (sigmetMachType.find()) { while (sigmetMachType.find()) { switch
			 * (sigmetMachType.group("sigmetType").trim()) { case "TC": sigmetType =
			 * Type.CYCLONE; break; case "VA": sigmetType = Type.VOLCANO; break; default:
			 * sigmetType = Type.METEO; break; } } } else { sigmetType = Type.METEO; }
			 */
			SigmetVolcanoPhenomenonDescription phenom = new SigmetVolcanoPhenomenonDescription(
					tac.substring(0, lastIndex));
			String sevS = matcherPhenomena.group("severity");
			String phS = matcherPhenomena.group("phenomena");
			String obsTypeS = matcherPhenomena.group("obsfcst");
			String atTimeS = matcherPhenomena.group("atTime");

			if (sevS != null)
				phenom.setPhenomenonSeverity(SigmetVolcanoPhenomenonDescription.Severity.valueOf(sevS));
			phenom.setPhenomenon(phS);
			phenom.setPhenomenonObservation(SigmetVolcanoPhenomenonDescription.ObservationType.valueOf(obsTypeS));

			DateTime parentDateTime = this.getMessageIssueDateTime();

			DateTime dtAT = atTimeS == null ? parentDateTime
					: phenom.parseSectionDateTimeToken(SigmetParsingRegexp.sigmetPhenomenaTimestamp, atTimeS,
							parentDateTime);
			phenom.setPhenomenonTimeStamp(dtAT);

			this.setPhenomenonDescription(phenom);

			tac.delete(0, lastIndex);
		} else
			throw new SIGMETParsingException("No phenomena description found!");

		return tac;
	}

	protected StringBuffer fillAndRemoveForecastedLocation(StringBuffer tac) {
		Matcher matcherFcst = SigmetParsingRegexp.sigmetForecastSection.matcher(tac);
		if (matcherFcst.find()) {
			int lastIndex = matcherFcst.end();
			StringBuffer foracastgr = new StringBuffer(matcherFcst.group());
			fSection = new SigmetVolcanoForecastSection(tac.substring(matcherFcst.start(), lastIndex));
			this.getPhenomenonDescription().setForecastSection(fSection);
			String time = matcherFcst.group("time");
			String location = matcherFcst.group("location");
			DateTime parentDateTime = this.getMessageIssueDateTime();

			DateTime dtAT = time == null ? parentDateTime
					: fSection.parseSectionDateTimeToken(SigmetParsingRegexp.sigmetPhenomenaTimestamp, time,
							parentDateTime);

			fSection.setForecastedTime(dtAT);
			tac.delete(matcherFcst.start(), lastIndex);
			fillForecastLocationSection(foracastgr);
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

		fillZigZagLine(tac);

		fillLineAreaLocation(tac);
		fillMultiLineLocation(tac);

		fillOneCoordinatePoint(tac);

		return tac;
	}

	protected StringBuffer fillForecastLocationSection(StringBuffer tac) {

		fillForecastEntireFIRLocation(tac);
		if (this.getfSection().getHorizontalLocation().isEntireFIR())
			return tac; // not necessary to check location further

		fillForecastWithinPolygon(tac);
		if (this.getfSection().getHorizontalLocation().isInPolygon())
			return tac; // not necessary to check location further

		fillForecastWithinCorridor(tac);
		if (this.getfSection().getHorizontalLocation().isWithinCorridor())
			return tac; // not necessary to check location further

		fillForecastWithinRadius(tac);
		if (this.getfSection().getHorizontalLocation().isWithinRadius())
			return tac;

		fillForecastZigZagLine(tac);

		fillForecastLineAreaLocation(tac);
		fillForecastMultiLineLocation(tac);

		fillForecastOneCoordinatePoint(tac);

		return tac;
	}

	protected StringBuffer fillOneCoordinatePoint(StringBuffer tac) {
		Matcher matcherCoordPoint = SigmetParsingRegexp.sigmetCoordPoint.matcher(tac);
		int lastMatch = 0;
		if (matcherCoordPoint.find()) {
			int startIndex = matcherCoordPoint.start();
			String lat = matcherCoordPoint.group("latitude");
			String laDeg = matcherCoordPoint.group("ladeg");
			String laMin = matcherCoordPoint.group("lamin");
			String lon = matcherCoordPoint.group("longitude");
			String loDeg = matcherCoordPoint.group("lodeg");
			String loMin = matcherCoordPoint.group("lomin");

			CoordPoint point = new CoordPoint(RUMB_UNITS.valueOf(lat), Integer.parseInt(laDeg),
					laMin != null ? Integer.parseInt(laMin) : 0, RUMB_UNITS.valueOf(lon),
					loDeg == null || loDeg.isEmpty() ? 0 : Integer.parseInt(loDeg),
					loMin == null || loMin.isEmpty() ? 0 : Integer.parseInt(loMin));

			this.getHorizontalLocation().setSinglePoint(true);
			this.getHorizontalLocation().setPoint(point);
			lastMatch = matcherCoordPoint.end();
			tac.delete(startIndex, lastMatch);
		}

		return tac;

	}

	protected StringBuffer fillForecastOneCoordinatePoint(StringBuffer tac) {
		Matcher matcherCoordPoint = SigmetParsingRegexp.sigmetCoordPoint.matcher(tac);
		int lastMatch = 0;
		if (matcherCoordPoint.find()) {
			int startIndex = matcherCoordPoint.start();
			String lat = matcherCoordPoint.group("latitude");
			String laDeg = matcherCoordPoint.group("ladeg");
			String laMin = matcherCoordPoint.group("lamin");
			String lon = matcherCoordPoint.group("longitude");
			String loDeg = matcherCoordPoint.group("lodeg");
			String loMin = matcherCoordPoint.group("lomin");

			CoordPoint point = new CoordPoint(RUMB_UNITS.valueOf(lat), Integer.parseInt(laDeg),
					laMin != null ? Integer.parseInt(laMin) : 0, RUMB_UNITS.valueOf(lon),
					loDeg == null || loDeg.isEmpty() ? 0 : Integer.parseInt(loDeg),
					loMin == null || loMin.isEmpty() ? 0 : Integer.parseInt(loMin));

			this.getHorizontalLocation().setSinglePoint(true);
			this.getHorizontalLocation().setPoint(point);
			lastMatch = matcherCoordPoint.end();
			tac.delete(startIndex, lastMatch);
		}

		return tac;

	}

	protected StringBuffer fillZigZagLine(StringBuffer tac) {
		Matcher matcherZigZag = SigmetParsingRegexp.sigmetMultiPointLine.matcher(tac);

		if (matcherZigZag.find()) {

			int startIndex = matcherZigZag.start();
			String azimuth = matcherZigZag.group("azimuth");

			Matcher matcherCoordPoint = SigmetParsingRegexp.sigmetCoordPoint.matcher(tac);
			int lastMatch = 0;
			Line sigmetZigZagLine = new Line();

			while (matcherCoordPoint.find()) {

				String lat = matcherCoordPoint.group("latitude");
				String laDeg = matcherCoordPoint.group("ladeg");
				String laMin = matcherCoordPoint.group("lamin");
				String lon = matcherCoordPoint.group("longitude");
				String loDeg = matcherCoordPoint.group("lodeg");
				String loMin = matcherCoordPoint.group("lomin");

				CoordPoint linePoint = new CoordPoint(RUMB_UNITS.valueOf(lat), Integer.parseInt(laDeg),
						Integer.parseInt(laMin), RUMB_UNITS.valueOf(lon), Integer.parseInt(loDeg),
						Integer.parseInt(loMin));

				sigmetZigZagLine.addPoint(linePoint);

				lastMatch = matcherCoordPoint.end();

			}
			this.getHorizontalLocation().getDirectionsFromLines()
					.add(new DirectionFromLine(RUMB_UNITS.valueOf(azimuth), sigmetZigZagLine));

			tac.delete(startIndex, lastMatch);
		}

		return tac;
	}

	protected StringBuffer fillForecastZigZagLine(StringBuffer tac) {
		Matcher matcherZigZag = SigmetParsingRegexp.sigmetMultiPointLine.matcher(tac);

		if (matcherZigZag.find()) {

			int startIndex = matcherZigZag.start();
			String azimuth = matcherZigZag.group("azimuth");

			Matcher matcherCoordPoint = SigmetParsingRegexp.sigmetCoordPoint.matcher(tac);
			int lastMatch = 0;
			Line sigmetZigZagLine = new Line();

			while (matcherCoordPoint.find()) {

				String lat = matcherCoordPoint.group("latitude");
				String laDeg = matcherCoordPoint.group("ladeg");
				String laMin = matcherCoordPoint.group("lamin");
				String lon = matcherCoordPoint.group("longitude");
				String loDeg = matcherCoordPoint.group("lodeg");
				String loMin = matcherCoordPoint.group("lomin");

				CoordPoint linePoint = new CoordPoint(RUMB_UNITS.valueOf(lat), Integer.parseInt(laDeg),
						Integer.parseInt(laMin), RUMB_UNITS.valueOf(lon), Integer.parseInt(loDeg),
						Integer.parseInt(loMin));

				sigmetZigZagLine.addPoint(linePoint);

				lastMatch = matcherCoordPoint.end();

			}
			this.getfSection().getHorizontalLocation().getDirectionsFromLines()
					.add(new DirectionFromLine(RUMB_UNITS.valueOf(azimuth), sigmetZigZagLine));

			tac.delete(startIndex, lastMatch);
		}

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
			CoordPoint center = new CoordPoint(RUMB_UNITS.valueOf(lat), Integer.parseInt(laDeg),
					Integer.parseInt(laMin), RUMB_UNITS.valueOf(lon), Integer.parseInt(loDeg), Integer.parseInt(loMin));
			this.getHorizontalLocation().setPoint(center);

			this.getHorizontalLocation().setWideness(Integer.parseInt(radius));
			/** TODO: add center corridor line */
		}

		return tac;
	}

	protected StringBuffer fillForecastWithinRadius(StringBuffer tac) {

		Matcher matcherRadius = SigmetParsingRegexp.sigmetWithinRadius.matcher(tac);
		if (matcherRadius.find()) {
			this.getfSection().getHorizontalLocation().setWithinRadius(true);
			String radius = matcherRadius.group("radius");
			String units = matcherRadius.group("radiusUnit");

			String lat = matcherRadius.group("latitude");
			String laDeg = matcherRadius.group("ladeg");
			String laMin = matcherRadius.group("lamin");
			String lon = matcherRadius.group("longitude");
			String loDeg = matcherRadius.group("lodeg");
			String loMin = matcherRadius.group("lomin");
			this.getfSection().getHorizontalLocation().setWideness(Integer.valueOf(radius));
			this.getfSection().getHorizontalLocation().setWidenessUnits(LENGTH_UNITS.valueOf(units));
			CoordPoint center = new CoordPoint(RUMB_UNITS.valueOf(lat), Integer.parseInt(laDeg),
					Integer.parseInt(laMin), RUMB_UNITS.valueOf(lon), Integer.parseInt(loDeg), Integer.parseInt(loMin));
			this.getfSection().getHorizontalLocation().setPoint(center);

			this.getfSection().getHorizontalLocation().setWideness(Integer.parseInt(radius));
			/** TODO: add center corridor line */
		}

		return tac;
	}

	protected StringBuffer fillIntensity(StringBuffer tac) {
		Matcher matcherIntensity = SigmetParsingRegexp.sigmetIntensityChanges.matcher(tac);
		if (matcherIntensity.find()) {
			String intensity = matcherIntensity.group("intensity");

			this.getPhenomenonDescription()
					.setIntencity(SigmetVolcanoPhenomenonDescription.Intensity.valueOf(intensity));
			tac.delete(matcherIntensity.start(), matcherIntensity.end());
		}
		return tac;
	}

	protected StringBuffer fillLevel(StringBuffer tac) {

		Matcher matcherLevel = SigmetParsingRegexp.sigmetLevel.matcher(tac);
		if (matcherLevel.find()) {

			SigmetVerticalPhenomenonLocation level = new SigmetVerticalPhenomenonLocation();

			boolean hasTop = matcherLevel.group("hastopfl") != null;
			boolean onSurface = matcherLevel.group("hassurface") != null;
			boolean hasBothFL = matcherLevel.group("hasbothfls") != null;
			boolean aboveFL = matcherLevel.group("above") != null;
			boolean belowFl = matcherLevel.group("below") != null;

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
				String heightMeters = matcherLevel.group("heightmeters");
				String heightUnits = matcherLevel.group("units");
				if (heightMeters != null) {
					Optional<Integer> oSurface = Optional.of(Integer.valueOf(heightMeters));
					level.setTopMarginMeters(oSurface);
					LENGTH_UNITS.valueOf(heightUnits);
				}
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
		Matcher matcherMov = SigmetParsingRegexp.sigmetMovement.matcher(tac);
		if (matcherMov.find()) {
			SigmetMovingSection movSection = new SigmetMovingSection(
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
		Matcher matcherWI = SigmetParsingRegexp.sigmetInPolygonVolcano.matcher(tac);
		if (matcherWI.find()) {
			this.getHorizontalLocation().setInPolygon(true);
			int startIndex = matcherWI.start();

			Matcher matcherCoordPoint = SigmetParsingRegexp.sigmetCoordPoint.matcher(tac);
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

	protected StringBuffer fillForecastWithinPolygon(StringBuffer tac) {
		Matcher matcherWI = SigmetParsingRegexp.sigmetInPolygonVolcano.matcher(tac);
		if (matcherWI.find()) {
			this.getfSection().getHorizontalLocation().setInPolygon(true);
			int startIndex = matcherWI.start();

			Matcher matcherCoordPoint = SigmetParsingRegexp.sigmetCoordPoint.matcher(tac);
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
				this.getfSection().getHorizontalLocation().getPolygonPoints().add(polygonApex);

				lastMatch = matcherCoordPoint.end();

				// matcherCoordPoint.reset();
			}

			tac.delete(startIndex, lastMatch);

		}

		return tac;
	}

	/** Extract location when it is in corridor */
	protected StringBuffer fillWithinCorridor(StringBuffer tac) {
		Matcher matcherCorridor = SigmetParsingRegexp.sigmetWithinCorridor.matcher(tac);
		if (matcherCorridor.find()) {
			this.getHorizontalLocation().setWithinCorridor(true);
			String range = matcherCorridor.group("range");
			this.getHorizontalLocation().setWideness(Integer.parseInt(range));
			/** TODO: add center corridor line */
		}

		return tac;
	}

	/** Extract location when it is in corridor */
	protected StringBuffer fillForecastWithinCorridor(StringBuffer tac) {
		Matcher matcherCorridor = SigmetParsingRegexp.sigmetWithinCorridor.matcher(tac);
		if (matcherCorridor.find()) {
			this.getfSection().getHorizontalLocation().setWithinCorridor(true);
			String range = matcherCorridor.group("range");
			this.getfSection().getHorizontalLocation().setWideness(Integer.parseInt(range));
			/** TODO: add center corridor line */
		}

		return tac;
	}

	/**
	 * Extract location with multilines example: N OF LINE N5100 E03520 - N5017
	 * E04200 AND S OF LINE N5400 E03150 - N5440 E04400
	 **/
	protected StringBuffer fillMultiLineLocation(StringBuffer tac) {
		Matcher matcherDirLine = SigmetParsingRegexp.sigmetMultiLine.matcher(tac);
		int lastMatch = 0;
		while (matcherDirLine.find()) {

			String lineAzimuth = matcherDirLine.group("azimuth");

			String latitudeStart = matcherDirLine.group("latStart");
			String latStartDeg = matcherDirLine.group("latStartDeg");
			String latStartMin = matcherDirLine.group("latStartMin");

			String longitudeStart = matcherDirLine.group("longStart");
			String longStartDeg = matcherDirLine.group("longStartDeg");
			String longStartMin = matcherDirLine.group("longStartMIn");

			String latitudeEnd = matcherDirLine.group("latEnd");
			String latEndDeg = matcherDirLine.group("latEndDeg");
			String latEndMin = matcherDirLine.group("latEndMin");

			String longitudeEnd = matcherDirLine.group("longEnd");
			String longEndDeg = matcherDirLine.group("longEndDeg");
			String longEndMin = matcherDirLine.group("longEndMin");

			CoordPoint startPoint = new CoordPoint(RUMB_UNITS.valueOf(latitudeStart), Integer.valueOf(latStartDeg),
					Integer.valueOf(latStartMin),

					RUMB_UNITS.valueOf(longitudeStart), Integer.valueOf(longStartDeg), Integer.valueOf(longStartMin));

			CoordPoint endPoint = new CoordPoint(RUMB_UNITS.valueOf(latitudeEnd), Integer.valueOf(latEndDeg),
					Integer.valueOf(latEndMin),

					RUMB_UNITS.valueOf(longitudeEnd), Integer.valueOf(longEndDeg), Integer.valueOf(longEndMin));
			Line line = new Line();
			line.setStartPoint(startPoint);
			line.setEndPoint(endPoint);
			DirectionFromLine dirLine = new DirectionFromLine(RUMB_UNITS.valueOf(lineAzimuth), line);

			this.getHorizontalLocation().getDirectionsFromLines().add(dirLine);
			lastMatch = matcherDirLine.end();
		}
		tac.delete(0, lastMatch);

		return tac;
	}

	protected StringBuffer fillForecastMultiLineLocation(StringBuffer tac) {
		Matcher matcherDirLine = SigmetParsingRegexp.sigmetMultiLine.matcher(tac);
		int lastMatch = 0;
		while (matcherDirLine.find()) {

			String lineAzimuth = matcherDirLine.group("azimuth");

			String latitudeStart = matcherDirLine.group("latStart");
			String latStartDeg = matcherDirLine.group("latStartDeg");
			String latStartMin = matcherDirLine.group("latStartMin");

			String longitudeStart = matcherDirLine.group("longStart");
			String longStartDeg = matcherDirLine.group("longStartDeg");
			String longStartMin = matcherDirLine.group("longStartMIn");

			String latitudeEnd = matcherDirLine.group("latEnd");
			String latEndDeg = matcherDirLine.group("latEndDeg");
			String latEndMin = matcherDirLine.group("latEndMin");

			String longitudeEnd = matcherDirLine.group("longEnd");
			String longEndDeg = matcherDirLine.group("longEndDeg");
			String longEndMin = matcherDirLine.group("longEndMin");

			CoordPoint startPoint = new CoordPoint(RUMB_UNITS.valueOf(latitudeStart), Integer.valueOf(latStartDeg),
					Integer.valueOf(latStartMin),

					RUMB_UNITS.valueOf(longitudeStart), Integer.valueOf(longStartDeg), Integer.valueOf(longStartMin));

			CoordPoint endPoint = new CoordPoint(RUMB_UNITS.valueOf(latitudeEnd), Integer.valueOf(latEndDeg),
					Integer.valueOf(latEndMin),

					RUMB_UNITS.valueOf(longitudeEnd), Integer.valueOf(longEndDeg), Integer.valueOf(longEndMin));
			Line line = new Line();
			line.setStartPoint(startPoint);
			line.setEndPoint(endPoint);
			DirectionFromLine dirLine = new DirectionFromLine(RUMB_UNITS.valueOf(lineAzimuth), line);

			this.getfSection().getHorizontalLocation().getDirectionsFromLines().add(dirLine);
			lastMatch = matcherDirLine.end();
		}
		tac.delete(0, lastMatch);

		return tac;
	}

	/** check if it has area described by lines e.g. N OF N2000 AND E OF E5555 */
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

			String pointCoord = StringConstants.coalesce(pointCoordLat, pointCoordLong);
			String pointDeg = StringConstants.coalesce(pointDegLat, pointDegLong);
			String pointMin = StringConstants.coalesce(pointMinLat, pointMinLong);

			Line sigmetLine = new Line(new Coordinate(RUMB_UNITS.valueOf(pointCoord), Integer.parseInt(pointDeg),
					pointMin.isEmpty() ? 0 : Integer.parseInt(pointMin)));
			DirectionFromLine dirLine = new DirectionFromLine(RUMB_UNITS.valueOf(azimuth), sigmetLine);
			this.getHorizontalLocation().getDirectionsFromLines().add(dirLine);
			lastMatch = matcherDirLine.end();
		}
		tac.delete(0, lastMatch);

		return tac;
	}

	/** check if it has area described by lines e.g. N OF N2000 AND E OF E5555 */
	protected StringBuffer fillForecastLineAreaLocation(StringBuffer tac) {

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

			String pointCoord = StringConstants.coalesce(pointCoordLat, pointCoordLong);
			String pointDeg = StringConstants.coalesce(pointDegLat, pointDegLong);
			String pointMin = StringConstants.coalesce(pointMinLat, pointMinLong);

			Line sigmetLine = new Line(new Coordinate(RUMB_UNITS.valueOf(pointCoord), Integer.parseInt(pointDeg),
					pointMin.isEmpty() ? 0 : Integer.parseInt(pointMin)));
			DirectionFromLine dirLine = new DirectionFromLine(RUMB_UNITS.valueOf(azimuth), sigmetLine);
			this.getfSection().getHorizontalLocation().getDirectionsFromLines().add(dirLine);
			lastMatch = matcherDirLine.end();
		}
		tac.delete(0, lastMatch);

		return tac;
	}

	/** check if it has ENTIRE FIR/UIR flag */
	protected StringBuffer fillEntireFIRLocation(StringBuffer tac) {
		Matcher matcherFIR = SigmetParsingRegexp.sigmetEntireFir.matcher(tac);
		if (matcherFIR.find()) {

			this.getHorizontalLocation().setEntireFIR(true);
			int lastIndex = matcherFIR.end();
			tac.delete(0, lastIndex);
			return tac;
		}

		return tac;

	}

	/** check if it has ENTIRE FIR/UIR flag */

	protected StringBuffer fillForecastEntireFIRLocation(StringBuffer tac) {
		Matcher matcherFIR = SigmetParsingRegexp.sigmetEntireFir.matcher(tac);
		if (matcherFIR.find()) {

			this.getfSection().getHorizontalLocation().setEntireFIR(true);
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
		return SigmetParsingRegexp.sigmetHeader;
	}

	public String getCancelSigmetNumber() {
		return cancelSigmetNumber;
	}

	public void setCancelSigmetNumber(String cancelSigmetNumber) {
		this.cancelSigmetNumber = cancelSigmetNumber;
	}

	public DateTime getCancelSigmetDateTimeFrom() {
		return cancelSigmetDateTimeFrom;
	}

	public void setCancelSigmetDateTimeFrom(DateTime cancelSigmetDateTimeFrom) {
		this.cancelSigmetDateTimeFrom = cancelSigmetDateTimeFrom;
	}

	public DateTime getCancelSigmetDateTimeTo() {
		return cancelSigmetDateTimeTo;
	}

	public void setCancelSigmetDateTimeTo(DateTime cancelSigmetDateTimeTo) {
		this.cancelSigmetDateTimeTo = cancelSigmetDateTimeTo;
	}

	public SigmetVolcanoForecastSection getfSection() {
		return fSection;
	}

	public void setfSection(SigmetVolcanoForecastSection fSection) {
		this.fSection = fSection;
	}

}
