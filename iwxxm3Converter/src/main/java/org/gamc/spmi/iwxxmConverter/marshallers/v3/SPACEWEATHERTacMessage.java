package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.util.LinkedList;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gamc.spmi.iwxxmConverter.common.MessageStatusType;
import org.gamc.spmi.iwxxmConverter.common.MessageType;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SIGMETParsingException;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SigmetParsingRegexp;
import org.gamc.spmi.iwxxmConverter.tac.TacMessageImpl;
import org.gamc.spmi.ixwwmConverter.spaceweatherconverter.SPACEWEATHERParsingException;
import org.gamc.spmi.ixwwmConverter.spaceweatherconverter.SpaceWeatherEffectLocation;
import org.gamc.spmi.ixwwmConverter.spaceweatherconverter.SpaceWeatherParsingRegexp;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

/**Class describes the model of Space Weather(SWX) advisory object*/
/**
 * Sample SWX ADVISORY DTG: 20161108/0100Z SWXC: DONLON ADVISORY NR: 2016/2 SWX
 * EFFECT: HF COM MOD AND GNSS MOD NR RPLC : 2016/1 OBS SWX: 08/0100Z HNH HSH
 * E18000 - W18000 FCST SWX +6 HR: 08/0700Z HNH HSH E18000 - W18000 FCST SWX +12
 * HR: 08/1300Z HNH HSH E18000 - W18000 FCST SWX +18 HR: 08/1900Z HNH HSH E18000
 * - W18000 FCST SWX +24 HR: 09/0100Z NO SWX EXP RMK: LOW LVL GEOMAGNETIC
 * STORMING CAUSING INCREASED AURORAL ACT AND SUBSEQUENT MOD DEGRADATION OF GNSS
 * AND HF COM AVBL IN THE AURORAL ZONE. THIS STORMING EXP TO SUBSIDE IN THE FCST
 * PERIOD. SEE WWW.SPACEWEATHERPROVIDER.WEB NXT ADVISORY: NO FURTHER ADVISORIES
 *
 **/
public class SPACEWEATHERTacMessage extends TacMessageImpl {

	private MessageStatusType messageStatusType = MessageStatusType.NORMAL;
	private DateTime issued;
	private String issuingCenter;

	private String advisoryNumber;
	private String replaceNumber;

	private SpaceWeatherEffectLocation observedLocation;
	private TreeSet<SpaceWeatherEffectLocation> forecastedLocations = new TreeSet<SpaceWeatherEffectLocation>();
	private LinkedList<String> effects = new LinkedList<String>();

	private String remark;
	
	private DateTime nextAdvDateTime;

	public SPACEWEATHERTacMessage(String initialTacMessage) {
		super(initialTacMessage);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTacStartToken() {
		return "SWX ADVISORY";
	}

	@Override
	public MessageType getMessageType() {

		return MessageType.SPACEWEATHER;
	}

	@Override
	public MessageStatusType getMessageStatusType() {
		return messageStatusType;
	}

	@Override
	public void parseMessage() throws ParsingException {
		StringBuffer tac = new StringBuffer(getInitialTacString());

		int lastIndex = 0;

		// check whether valid header presents
		Matcher matcherHeader = SpaceWeatherParsingRegexp.spaceWeatherHeader.matcher(tac);
		if (!matcherHeader.find())
			throw new SPACEWEATHERParsingException("Mandatory header section is missed");

		lastIndex = matcherHeader.end();
		tac.delete(matcherHeader.start(), lastIndex);

		// extract time of issuing
		Matcher matcherDtGenerated = SpaceWeatherParsingRegexp.spaceWeatherDateTimeGenerated.matcher(tac);
		if (matcherDtGenerated.find()) {
			String yearS = matcherDtGenerated.group("year");
			String monthS = matcherDtGenerated.group("month");
			String dayS = matcherDtGenerated.group("day");
			String hourS = matcherDtGenerated.group("hour");
			String minuteS = matcherDtGenerated.group("minute");
			this.issued = new DateTime(Integer.valueOf(yearS), Integer.valueOf(monthS), Integer.valueOf(dayS),
					Integer.valueOf(hourS), Integer.valueOf(minuteS), DateTimeZone.UTC);
			lastIndex = matcherDtGenerated.end();
			tac.delete(matcherDtGenerated.start(), lastIndex);
		}

		// extract issuing center
		Matcher matcherIssuingCenter = SpaceWeatherParsingRegexp.spaceWeatherCenter.matcher(tac);
		if (matcherIssuingCenter.find()) {
			this.issuingCenter = matcherIssuingCenter.group("center");
			lastIndex = matcherIssuingCenter.end();
			tac.delete(matcherIssuingCenter.start(), lastIndex);
		}

		// extract advisory number
		Matcher matcherNumber = SpaceWeatherParsingRegexp.spaceWeatherAdvisoryNumber.matcher(tac);
		if (matcherNumber.find()) {
			this.advisoryNumber = matcherNumber.group("advisoryNumber");
			lastIndex = matcherNumber.end();
			tac.delete(matcherNumber.start(), lastIndex);
		}
		// extract replace number
		Matcher matcherReplacingNumber = SpaceWeatherParsingRegexp.spaceWeatherAdvisoryReplacingNumber.matcher(tac);
		if (matcherReplacingNumber.find()) {
			this.replaceNumber = matcherReplacingNumber.group("advisoryReplaceNumber");
			lastIndex = matcherReplacingNumber.end();
			tac.delete(matcherReplacingNumber.start(), lastIndex);
		}
		
		// extract effects
		tac = parseEffects(tac);
		
		//fill observe section
		tac = parseObservationSection(tac);
		tac = parseForecastedSections(tac);
		
		Matcher matcherRemark = SpaceWeatherParsingRegexp.spaceWeatherRemark.matcher(tac);
		if (matcherRemark.find()) {
			String rmk = matcherRemark.group("remark");
			this.setRemark(rmk);
			lastIndex = matcherRemark.end();
			tac.delete(matcherRemark.start(), lastIndex);
		}
		
		
		Matcher matcherNextAdv = SpaceWeatherParsingRegexp.spaceWeatherNextAdvisory.matcher(tac);
		if (matcherNextAdv.find()) {
			
			
			
			
			 
			String nextAdvDatetime = matcherNextAdv.group("nextAdv");
			if (nextAdvDatetime.equalsIgnoreCase("NO FURTHER ADVISORIES")) {
				return;
			}
				
			Matcher matcherNextAdvDateTime = SpaceWeatherParsingRegexp.timeStamp.matcher(nextAdvDatetime);
			
			String yearS = matcherNextAdvDateTime.group("year");
			String monthS = matcherNextAdvDateTime.group("month");
			String dayS = matcherNextAdvDateTime.group("day");
			String hourS = matcherNextAdvDateTime.group("hour");
			String minuteS = matcherNextAdvDateTime.group("minute");
			 this.nextAdvDateTime = new DateTime(Integer.valueOf(yearS), Integer.valueOf(monthS), Integer.valueOf(dayS),
					Integer.valueOf(hourS), Integer.valueOf(minuteS), DateTimeZone.UTC);
			 
			 
			 
		
			 lastIndex = matcherNextAdvDateTime.end();
			tac.delete(matcherNextAdvDateTime.start(), lastIndex);
		}
	}
	
	/***parse and fill the list of effects*/
	private StringBuffer parseEffects(StringBuffer tac) {
		Matcher matcherEffects = SpaceWeatherParsingRegexp.spaceWeatherEffects.matcher(tac);
		this.effects=new LinkedList<String>();
		if (matcherEffects.find()) {
			String effects = matcherEffects.group("effects");
			String[] splitted = effects.split("\\s*AND\\s*");
			for(String effect:splitted) {
				String eff = effect.trim().replaceAll("\\s+", "_");
				this.effects.add(eff);
			}
			
			
			int lastIndex = matcherEffects.end();
			tac.delete(matcherEffects.start(), lastIndex);
		}
		return tac;
	}
	
	
	
	
	/**parse and fill observation section - time and location*/
	private StringBuffer parseObservationSection(StringBuffer tac) {
		Matcher matcherObservation = SpaceWeatherParsingRegexp.spaceWeatherObserveArea.matcher(tac);
		
		if (matcherObservation.find()) {
			String dayS = matcherObservation.group("day");
			String hourS = matcherObservation.group("hour");
			String minuteS = matcherObservation.group("minute");
			
			String daylight = matcherObservation.group("daylight");
			
			String hemi1 = matcherObservation.group("hemi1");
			
			String hemi2 = matcherObservation.group("hemi2");
			
			String latStart = matcherObservation.group("latStart");
			String latEnd = matcherObservation.group("latEnd");
			
			String fl = matcherObservation.group("fl");
			
			SpaceWeatherEffectLocation observeLocation = new SpaceWeatherEffectLocation();
			
			int dayObs = Integer.valueOf(dayS);
			int hourObs = Integer.valueOf(hourS);
			int minuteObs = Integer.valueOf(minuteS);
			
			DateTime obsDateTime = new DateTime(this.getIssued().getYear(),this.getIssued().getMonthOfYear(),dayObs,hourObs,minuteObs,DateTimeZone.UTC);;
			if (dayObs<this.getIssued().getDayOfYear()) {
				obsDateTime = this.getIssued().plusHours(24).withHourOfDay(hourObs).withMinuteOfHour(minuteObs);
			}
			
			if (hemi1!=null)
				observeLocation.getHemiSpheres().add(hemi1);
			if (hemi2!=null)
				observeLocation.getHemiSpheres().add(hemi2);
			
			observeLocation.setLatStart(latStart);
			observeLocation.setLatEnd(latEnd);
			
				
			observeLocation.setDayLightSide(daylight!=null);
			observeLocation.setEffectsDateTime(obsDateTime);
			observeLocation.setAboveFL(fl==null?Optional.empty():Optional.of(Integer.valueOf(fl)));
			this.setObservedLocation(observeLocation);
			
			int lastIndex = matcherObservation.end();
			tac.delete(matcherObservation.start(), lastIndex);
		}
		return tac;
	}
	
	/**parse and fill forecasted sections - time and location*/
	private StringBuffer parseForecastedSections(StringBuffer tac) {
		Matcher matcherForecast = SpaceWeatherParsingRegexp.spaceWeatherForecastArea.matcher(tac);
		
		while (matcherForecast.find()) {
			
			String forecastHours = matcherForecast.group("forecastHour");
			int hoursToAdd = Integer.valueOf(forecastHours);
			
			String daylight = matcherForecast.group("daylight");
			
			String hemi1 = matcherForecast.group("hemi1");
			
			String hemi2 = matcherForecast.group("hemi2");
			
			String latStart = matcherForecast.group("latStart");
			String latEnd = matcherForecast.group("latEnd");
			
			String fl = matcherForecast.group("fl");
			
			SpaceWeatherEffectLocation forecastLocation = new SpaceWeatherEffectLocation();
			
			
			DateTime forecastDateTime = this.getObservedLocation().getEffectsDateTime().plusHours(hoursToAdd);
			forecastLocation.setEffectsDateTime(forecastDateTime);
			
			if (hemi1!=null)
				forecastLocation.getHemiSpheres().add(hemi1);
			if (hemi2!=null)
				forecastLocation.getHemiSpheres().add(hemi2);
			
			forecastLocation.setLatStart(latStart);
			forecastLocation.setLatEnd(latEnd);
			
				
			forecastLocation.setDayLightSide(daylight!=null);
			forecastLocation.setAboveFL(fl==null?Optional.empty():Optional.of(Integer.valueOf(fl)));
			this.forecastedLocations.add(forecastLocation);
			
			int lastIndex = matcherForecast.end();
			tac = tac.delete(matcherForecast.start(), lastIndex);
			matcherForecast.reset();
		}
		
		
	
		return tac;
	}
	
	@Override
	public Interval getValidityInterval() {
		if (this.getObservedLocation() == null)
			return null;
		if (forecastedLocations == null)
			return new Interval(this.getObservedLocation().getEffectsDateTime(),
					this.getObservedLocation().getEffectsDateTime().plusHours(24));

		return new Interval(this.getObservedLocation().getEffectsDateTime(),
				forecastedLocations.last().getEffectsDateTime());

	}

	@Override
	public Pattern getHeaderPattern() {
		return SpaceWeatherParsingRegexp.spaceWeatherHeader;
	}

	public DateTime getIssued() {
		return issued;
	}

	public void setIssued(DateTime issued) {
		this.issued = issued;
	}

	public String getIssuingCenter() {
		return issuingCenter;
	}

	public void setIssuingCenter(String issuingCenter) {
		this.issuingCenter = issuingCenter;
	}

	public String getAdvisoryNumber() {
		return advisoryNumber;
	}

	public void setAdvisoryNumber(String advisoryNumber) {
		this.advisoryNumber = advisoryNumber;
	}

	public String getReplaceNumber() {
		return replaceNumber;
	}

	public void setReplaceNumber(String replaceNumber) {
		this.replaceNumber = replaceNumber;
	}

	public SpaceWeatherEffectLocation getObservedLocation() {
		return observedLocation;
	}

	public void setObservedLocation(SpaceWeatherEffectLocation observedLocation) {
		this.observedLocation = observedLocation;
	}

	public TreeSet<SpaceWeatherEffectLocation> getForecastedLocations() {
		return forecastedLocations;
	}

	public void setForecastedLocations(TreeSet<SpaceWeatherEffectLocation> forecastedLocations) {
		this.forecastedLocations = forecastedLocations;
	}

	public LinkedList<String> getEffects() {
		return effects;
	}

	public void setEffects(LinkedList<String> effects) {
		this.effects = effects;
	}

	public DateTime getNextAdvDateTime() {
		return nextAdvDateTime;
	}

	public void setNextAdvDateTime(DateTime nextAdvDateTime) {
		this.nextAdvDateTime = nextAdvDateTime;
	}

	public void setMessageStatusType(MessageStatusType messageStatusType) {
		this.messageStatusType = messageStatusType;
	}
	
	public boolean hasNextAdvisory() {
		return this.nextAdvDateTime!=null;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
