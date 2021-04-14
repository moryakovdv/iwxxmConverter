package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.util.regex.Matcher;

import org.gamc.spmi.iwxxmConverter.common.CoordPoint;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SIGMETParsingException;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetForecastSection;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetHorizontalPhenomenonLocation;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetParsingRegexp;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetPhenomenonDescription;
import org.joda.time.DateTime;

/**
 * Implemetation of a SIGMET Tac message for meteorological sigmet (WS, not WV
 * or WC)
 * 
 * @author alex
 */
public class SIGMETTropicalTacMessage extends SIGMETTacMessage {

	private Type sigmetType = Type.CYCLONE;

	@Override
	public Type getSigmetType() {
		return sigmetType;
	}

	@Override
	public void setSigmetType(Type sigmetType) {
		this.sigmetType = sigmetType;
	}

	public SIGMETTropicalTacMessage(String initialTacMessage) {
		super(initialTacMessage);

	}

	/**Overrides method in common weather sigmet to extract cyclone center position along with name of the cyclone*/
	@Override
	protected StringBuffer fillAndRemovePhenomenaDescription(StringBuffer tac) throws SIGMETParsingException {

		Matcher matcherPhenomena = SigmetParsingRegexp.sigmetCyclonePhenomena.matcher(tac);
		if (matcherPhenomena.find()) {

			int lastIndex = matcherPhenomena.end();

			SigmetPhenomenonDescription phenom = new SigmetPhenomenonDescription(tac.substring(0, lastIndex));

			String phS = matcherPhenomena.group("phenomena");
			String name = matcherPhenomena.group("name");
			String cycloneCenterPosition = matcherPhenomena.group("position");

			if (cycloneCenterPosition != null) {
				Matcher matcherCenterPoint = SigmetParsingRegexp.sigmetCoordPoint.matcher(cycloneCenterPosition);
				if (matcherCenterPoint.find()) {

					String lat = matcherCenterPoint.group("latitude");
					String laDeg = matcherCenterPoint.group("ladeg");
					String laMin = matcherCenterPoint.group("lamin");
					String lon = matcherCenterPoint.group("longitude");
					String loDeg = matcherCenterPoint.group("lodeg");
					String loMin = matcherCenterPoint.group("lomin");
					CoordPoint center = new CoordPoint(RUMB_UNITS.valueOf(lat), Integer.parseInt(laDeg),
							Integer.parseInt(laMin), RUMB_UNITS.valueOf(lon), Integer.parseInt(loDeg),
							Integer.parseInt(loMin));
					getHorizontalLocation().setPoint(center);
				}

			}

			String obsTypeS = matcherPhenomena.group("obsfcst");
			String atTimeS = matcherPhenomena.group("atTime");

			phenom.setPhenomenon(phS);

			phenom.setPhenomenonGivenName(name);

			phenom.setPhenomenonObservation(SigmetPhenomenonDescription.ObservationType.valueOf(obsTypeS));

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

	@Override
	protected StringBuffer fillLocationSection(StringBuffer tac, SigmetHorizontalPhenomenonLocation location) {
		// TODO Auto-generated method stub
		return super.fillLocationSection(tac, location);
	}
	
	
	/**Overrides method in common weather sigmet to extract forecasted cyclone center position*/
	@Override
	protected StringBuffer fillAndRemoveForecastedLocation(StringBuffer tac) {
		Matcher matcherFcst = SigmetParsingRegexp.sigmetCycloneForecastSection.matcher(tac);
		int lastMatch = 0;
		int firstMatch = 0;
		while (matcherFcst.find()) {
			
			if (firstMatch == 0)
				firstMatch = matcherFcst.start();
			
			int lastIndex = matcherFcst.end();

			SigmetForecastSection fSection = new SigmetForecastSection(tac.substring(matcherFcst.start(), lastIndex));
			this.getPhenomenonDescription().getForecastSection().add(fSection);
			String time = matcherFcst.group("time");
			String location = matcherFcst.group("location");
			DateTime parentDateTime = this.getMessageIssueDateTime();

			DateTime dtAT = time == null ? parentDateTime
					: fSection.parseSectionDateTimeToken(SigmetParsingRegexp.sigmetPhenomenaTimestamp, time,
							parentDateTime);

			fSection.setForecastedTime(dtAT);
			
			
			
			
			fillLocationSection(new StringBuffer(location),fSection.getHorizontalLocation());
			lastMatch = matcherFcst.end();
		}
		
		tac.delete(firstMatch, lastMatch);

		return tac;
	}
	
	
	@Override
	protected StringBuffer fillWithinRadius(StringBuffer tac, SigmetHorizontalPhenomenonLocation location) {
		Matcher matcherRadius = SigmetParsingRegexp.sigmetCycloneWithinRadius.matcher(tac);
		if (matcherRadius.find()) {
			this.getHorizontalLocation().setWithinRadius(true);
			String radius = matcherRadius.group("radius");
			String units = matcherRadius.group("radiusUnit");

			location.setWideness(Integer.valueOf(radius));
			location.setWidenessUnits(LENGTH_UNITS.valueOf(units));
			location.setWithinRadius(true);

		}

		return tac;
	}

}
