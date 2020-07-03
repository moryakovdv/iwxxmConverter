package org.gamc.spmi.iwxxmConverter.sigmetconverter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import org.gamc.spmi.iwxxmConverter.common.CoordPoint;
import org.gamc.spmi.iwxxmConverter.common.DirectionFromLine;
import org.gamc.spmi.iwxxmConverter.common.Line;
import org.gamc.spmi.iwxxmConverter.tac.TacSectionImpl;
import org.joda.time.DateTime;

/**Description of sigmet's phenomenon forecasted time and position*/
public class SigmetForecastSection extends TacSectionImpl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4229832662872170289L;


	private SigmetHorizontalPhenomenonLocation horizontalLocation = new SigmetHorizontalPhenomenonLocation();
	private SigmetVerticalPhenomenonLocation verticalLocation = new SigmetVerticalPhenomenonLocation();
	private LinkedList<DirectionFromLine> areas = new LinkedList<DirectionFromLine>();

	
	public SigmetForecastSection(String initialTacSection) {
		super(initialTacSection);
		
	}

	private DateTime forecastedTime;
	

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

	public SigmetHorizontalPhenomenonLocation getHorizontalLocation() {
		return horizontalLocation;
	}

	public void setHorizontalLocation(SigmetHorizontalPhenomenonLocation horizontalPhenomenonLocation) {
		this.horizontalLocation = horizontalPhenomenonLocation;
	}

	public SigmetVerticalPhenomenonLocation getVerticalLocation() {
		return verticalLocation;
	}

	public void setVerticalLocation(SigmetVerticalPhenomenonLocation verticalPhenomenonLocation) {
		this.verticalLocation = verticalPhenomenonLocation;
	}
	
	
	
	
	
}
