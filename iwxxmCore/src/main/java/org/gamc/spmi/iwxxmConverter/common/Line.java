package org.gamc.spmi.iwxxmConverter.common;

import java.io.Serializable;
import java.util.LinkedList;

import org.gamc.gis.model.GTCoordinate;
import org.gamc.gis.model.GTLine;

/**The sigmet area may be indicated as number of lines.
 *Line itself can have start and end point or just one single point 
 *in the case of vertical or horizontal location indication */
public class Line implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8275937527178470577L;
	private LinkedList<CoordPoint> coordinatesList = new LinkedList<CoordPoint>();

	private Coordinate singlePointCoordinate;

	public Line() {
		
	}
	
	/**Construct line (meridian or latitude) through single point*/
	public Line(Coordinate singlePointCoordinate) {
		this.singlePointCoordinate=singlePointCoordinate;
	}


	public CoordPoint getStartPoint() {
		
			return coordinatesList.getFirst();
	
	}

	public void setStartPoint(CoordPoint startPoint) {
		coordinatesList.add(0, startPoint);
	}

	public CoordPoint getEndPoint() {
		return coordinatesList.getLast();
	}

	public void setEndPoint(CoordPoint endPoint) {
		if (this.coordinatesList.size()>0)
			this.coordinatesList.add(this.coordinatesList.size()-1, endPoint);
		else
			this.coordinatesList.add(endPoint);
	}

	public void addPoint(CoordPoint point) {
		this.coordinatesList.add(point);
	}

	@Override
	public String toString() {

		return this.coordinatesList.toString();
	}

	/** Convert to GTLine for GIS calculations */
	public GTLine toGTLine() {
		GTLine line = new GTLine();
		if (this.singlePointCoordinate != null)
			line.setSinglePointCoordinate(this.singlePointCoordinate.toGTCoordinate());
		this.coordinatesList.forEach(p -> line.addPoint(p.toGTCoordPoint()));

		return line;

	}

	public boolean isSingleLine() {
		return (this.coordinatesList.isEmpty() && singlePointCoordinate != null);
	}

	public Coordinate getSinglePointCoordinate() {
		return singlePointCoordinate;
	}

	public void setSinglePointCoordinate(Coordinate coordinate) {
		this.singlePointCoordinate = coordinate;
	}

	public LinkedList<CoordPoint> getCoordinatesList() {
		return coordinatesList;
	}

	public void setCoordinatesList(LinkedList<CoordPoint> coordinates) {
		this.coordinatesList = coordinates;
	}
}
