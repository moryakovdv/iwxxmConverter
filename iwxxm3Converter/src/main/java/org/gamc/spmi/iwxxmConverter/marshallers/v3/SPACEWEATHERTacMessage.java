package org.gamc.spmi.iwxxmConverter.marshallers.v3;

import java.util.LinkedList;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;


import org.gamc.spmi.iwxxmConverter.common.MessageStatusType;
import org.gamc.spmi.iwxxmConverter.common.MessageType;
import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.tac.TacMessageImpl;
import org.gamc.spmi.ixwwmConverter.spaceweatherconverter.SpaceWeatherEffectLocation;
import org.gamc.spmi.ixwwmConverter.spaceweatherconverter.SpaceWeatherParsingRegexp;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**Class describes the model of Space Weather(SWX) advisory object*/
/**Sample
* SWX ADVISORY
* DTG:                20161108/0100Z 
* SWXC:               DONLON
* ADVISORY NR:        2016/2
* SWX EFFECT:         HF COM MOD AND GNSS MOD 
* NR RPLC :           2016/1
* OBS SWX:            08/0100Z HNH HSH E18000 - W18000 
* FCST SWX +6 HR:     08/0700Z HNH HSH E18000 - W18000
* FCST SWX +12 HR:    08/1300Z HNH HSH E18000 - W18000
* FCST SWX +18 HR:    08/1900Z HNH HSH E18000 - W18000
* FCST SWX +24 HR:    09/0100Z NO SWX EXP
* RMK:                LOW LVL GEOMAGNETIC STORMING CAUSING INCREASED AURORAL ACT AND SUBSEQUENT MOD DEGRADATION OF GNSS AND HF COM AVBL IN THE AURORAL ZONE. THIS STORMING EXP TO SUBSIDE IN THE FCST PERIOD. SEE WWW.SPACEWEATHERPROVIDER.WEB 
* NXT ADVISORY:       NO FURTHER ADVISORIES
 *
 * **/
public class SPACEWEATHERTacMessage extends TacMessageImpl {

	
	
	
	private MessageStatusType messageStatusType = MessageStatusType.NORMAL;
	private DateTime  issued;
	private String issuingCenter;
	
	private String advisoryNumber;
	private String replaceNumber;
	
	
	private SpaceWeatherEffectLocation observedLocation;
	private TreeSet<SpaceWeatherEffectLocation> forecastedLocations;
	private LinkedList<String> effects;
	
	
	
	private DateTime  nextAdvDateTime;
	
	
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public Interval getValidityInterval() {
		// TODO Auto-generated method stub
		return null;
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

}
