package org.gamc.spmi.iwxxmConverter.airmetconverter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import org.gamc.spmi.iwxxmConverter.common.CoordPoint;
import org.gamc.spmi.iwxxmConverter.common.Line;
import org.gamc.spmi.iwxxmConverter.tac.TacSectionImpl;
import org.joda.time.DateTime;

/**Description of sigmet's phenomenon forecasted time and position*/
public class AirmetForecastSection extends TacSectionImpl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4229832662872170289L;

	public AirmetForecastSection(String initialTacSection) {
		super(initialTacSection);
		
	}

	private DateTime forecastedTime;
	
	private LinkedList<DirectionFromLine> areas = new LinkedList<DirectionFromLine>();

	public DateTime getForecastedTime() {
		return forecastedTime;
	}

	public void setForecastedTime(DateTime forecastedTime) {
		this.forecastedTime = forecastedTime;
	}

	public LinkedList<DirectionFromLine> getAreas() {
		return areas;
	}

	public void setAreas(LinkedList<DirectionFromLine> areas) {
		this.areas = areas;
	}
	
	
	
	
}
