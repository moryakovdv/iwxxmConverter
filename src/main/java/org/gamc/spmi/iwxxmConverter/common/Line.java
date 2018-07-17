package org.gamc.spmi.iwxxmConverter.common;

import java.io.Serializable;

public final class Line implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8275937527178470577L;
	private CoordPoint startPoint;
	private CoordPoint endPoint;
	
	public Line() {}
	
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
	
}
