package org.gamc.spmi.iwxxmConverter.common;

import java.io.Serializable;

import org.gamc.spmi.gis.model.GTCoordinate;
import org.gamc.spmi.gis.model.GTLine;

/**The sigmet area may be indicated as number of lines.
 *Line itself can have start and end point or just one single point 
 *in the case of vertical or horizontal location indication */
public final class Line implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8275937527178470577L;
	private CoordPoint startPoint;
	private CoordPoint endPoint;
	
	private Coordinate coordinate;
	
	public Line() {}
	
	/**Single point line. 
	 * If Line is described by only one coordinate it is a meridian(E,W) 
	 * or parallel(N,S)*/
	public Line(Coordinate c) {
		this.setCoordinate(c);
		
	}
	
	/**A Line described by start and end points*/
	public Line(CoordPoint start, CoordPoint end) {
		this.startPoint=start;
		this.endPoint=end;
	}
	
	public CoordPoint getStartPoint() {
		return startPoint;
	}
	public void setStartPoint(CoordPoint startPoint) {
		this.startPoint = startPoint;
	}
	public CoordPoint getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(CoordPoint endPoint) {
		this.endPoint = endPoint;
	}
	
	public boolean isSingleLine() {
		return (this.startPoint==null && this.endPoint==null && coordinate!=null);
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	@Override
	public String toString() {
		
		if (isSingleLine()) {
			return this.coordinate.toString();
		}
		else
			return this.startPoint.toString()+"-"+this.endPoint.toString();
	}
	
	/**Convert to GTLine for GIS calculations*/
	public GTLine toGTLine() {
		GTLine line = new GTLine();
		if (this.coordinate!=null)
			line.setCoordinate(this.coordinate.toGTCoordinate());
		if (this.startPoint!=null)
			line.setStartPoint(startPoint.toGTCoordPoint());
		
		if (this.endPoint!=null)
			line.setEndPoint(endPoint.toGTCoordPoint());
		
		return line;
		
	}
}
