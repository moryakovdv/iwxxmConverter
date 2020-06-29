package org.gamc.spmi.iwxxmConverter.sigmetconverter;

import java.io.Serializable;
import java.util.LinkedList;

import org.gamc.spmi.iwxxmConverter.common.CoordPoint;
import org.gamc.spmi.iwxxmConverter.common.Coordinate;
import org.gamc.spmi.iwxxmConverter.common.DirectionFromLine;
import org.gamc.spmi.iwxxmConverter.tac.TacMessageImpl;
import org.gamc.spmi.iwxxmConverter.tac.TacSectionImpl;
import org.ietf.jgss.Oid;
import org.joda.time.DateTime;

public class SigmetVolcanoForecastSection extends TacSectionImpl implements Serializable {
	
	private SigmetVolcanoHorizontalPhenomenonLocation horizontalLocation = new SigmetVolcanoHorizontalPhenomenonLocation();
	
	private SigmetVolcanoVerticalPhenomenonLocation verticalLocation = new SigmetVolcanoVerticalPhenomenonLocation();

	public SigmetVolcanoHorizontalPhenomenonLocation getHorizontalLocation() {
		return horizontalLocation;
	}

	public void setHorizontalLocation(SigmetVolcanoHorizontalPhenomenonLocation horizontalLocation) {
		this.horizontalLocation = horizontalLocation;
	}

	public SigmetVolcanoVerticalPhenomenonLocation getVerticalLocation() {
		return verticalLocation;
	}

	public void setVerticalLocation(SigmetVolcanoVerticalPhenomenonLocation verticalLocation) {
		this.verticalLocation = verticalLocation;
	}

	public SigmetVolcanoForecastSection(String initialTacSection) {
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
