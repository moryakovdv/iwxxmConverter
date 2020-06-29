package org.gamc.spmi.iwxxmConverter.sigmetconverter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;

import org.gamc.spmi.iwxxmConverter.common.CoordPoint;
import org.gamc.spmi.iwxxmConverter.common.DirectionFromLine;
import org.gamc.spmi.iwxxmConverter.common.Line;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;


/**Describes location of the sigmet phenomenon*/
public class SigmetVolcanoHorizontalPhenomenonLocation implements Serializable {

	


	/**
	 * 
	 */
	private static final long serialVersionUID = -4129364922665073405L;

	
	
	/**Phenomenon reported for entire FIR/UIR/CTA*/
	private boolean entireFIR = false;
	
	/**Phenomenon reported for polygon inside (WI token)*/
	private boolean inPolygon = false;
	
	/**Phenomenon reported for aisle(corridor) at both sides of certain line (e.g. WTN 45 NM OF LINE ...)*/
	private boolean withinCorridor = false;
	
	private boolean withinRadius = false;
	/**Wideness of the aisle(corridor) or radius from certain line or point*/
	private int wideness;
	
	private LENGTH_UNITS widenessUnits = LENGTH_UNITS.KM;
	
	/**Vertexes of polygon*/
	private LinkedList<CoordPoint> polygonPoints = new LinkedList<>();
	
	private LinkedList<DirectionFromLine> directionsFromLines = new LinkedList<>();
	
	/**Describes coordinate of central of a circle in the case of within radius, or single phenomena point*/
	private CoordPoint point;
	
	/**If phenomenon reported in a single point*/
	private boolean singlePoint=false;

	public boolean isEntireFIR() {
		return entireFIR;
	}

	public void setEntireFIR(boolean entireFIR) {
		this.entireFIR = entireFIR;
	}

	public boolean isInPolygon() {
		return inPolygon;
	}

	public void setInPolygon(boolean inPolygon) {
		this.inPolygon = inPolygon;
	}

	public boolean isWithinCorridor() {
		return withinCorridor;
	}

	public void setWithinCorridor(boolean withinCorridor) {
		this.withinCorridor = withinCorridor;
	}

	public int getWideness() {
		return wideness;
	}

	public void setWideness(int wideness) {
		this.wideness = wideness;
	}

	public LinkedList<CoordPoint> getPolygonPoints() {
		return polygonPoints;
	}

	public void setPolygonPoints(LinkedList<CoordPoint> polygonPoints) {
		this.polygonPoints = polygonPoints;
	}

	public LinkedList<DirectionFromLine> getDirectionsFromLines() {
		return directionsFromLines;
	}

	public void setDirectionsFromLines(LinkedList<DirectionFromLine> directionsFromLines) {
		this.directionsFromLines = directionsFromLines;
	}

	public boolean isWithinRadius() {
		return withinRadius;
	}

	public void setWithinRadius(boolean withinRadius) {
		this.withinRadius = withinRadius;
	}

	public LENGTH_UNITS getWidenessUnits() {
		return widenessUnits;
	}

	public void setWidenessUnits(LENGTH_UNITS widenessUnits) {
		this.widenessUnits = widenessUnits;
	}

	public CoordPoint getPoint() {
		return point;
	}

	public void setPoint(CoordPoint point) {
		this.point = point;
	}

	public boolean isSinglePoint() {
		return singlePoint;
	}

	public void setSinglePoint(boolean singlePoint) {
		this.singlePoint = singlePoint;
	}
	
	
}
