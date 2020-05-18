package org.gamc.spmi.iwxxmConverter.spaceweatherconverter;

import java.util.LinkedList;
import java.util.Optional;

import org.joda.time.DateTime;

/**Describes effects observed or forecasting location
 * 
 * OBS SWX:            08/0100Z HNH HSH E18000 - W18000
 * FCST SWX +6 HR:     08/0700Z HNH HSH E18000 - W18000
 * FCST SWX +12 HR:    08/1300Z HNH HSH E18000 - W18000
 * */
public class SpaceWeatherEffectLocation implements Comparable<SpaceWeatherEffectLocation>{
	private DateTime effectsDateTime;
	private LinkedList<String> hemiSpheres = new LinkedList<>();
	private String latStart;
	private String latEnd;
	private Optional<Integer> aboveFL;
	private boolean dayLightSide;
	private boolean nightSide;
	
	public DateTime getEffectsDateTime() {
		return effectsDateTime;
	}
	public void setEffectsDateTime(DateTime effectsDateTime) {
		this.effectsDateTime = effectsDateTime;
	}
	public LinkedList<String> getHemiSpheres() {
		return hemiSpheres;
	}
	public void setHemiSpheres(LinkedList<String> hemiSpheres) {
		this.hemiSpheres = hemiSpheres;
	}
	public String getLatStart() {
		return latStart;
	}
	public void setLatStart(String latStart) {
		this.latStart = latStart;
	}
	public String getLatEnd() {
		return latEnd;
	}
	public void setLatEnd(String latEnd) {
		this.latEnd = latEnd;
	}
	public Optional<Integer> getAboveFL() {
		return aboveFL;
	}
	public void setAboveFL(Optional<Integer> aboveFL) {
		this.aboveFL = aboveFL;
	}
	@Override
	public int compareTo(SpaceWeatherEffectLocation o) {
		// TODO Auto-generated method stub
		return effectsDateTime.compareTo(o.effectsDateTime);
	}
	public boolean isDayLightSide() {
		return dayLightSide;
	}
	public void setDayLightSide(boolean dayLightSide) {
		this.dayLightSide = dayLightSide;
	}
	public boolean isNightSide() {
		return nightSide;
	}
	public void setNightSide(boolean nightSide) {
		this.nightSide = nightSide;
	}
	
	
}
