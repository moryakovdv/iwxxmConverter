/**
 * Copyright (C) 2018 Dmitry Moryakov, Main aeronautical meteorological center, Moscow, Russia
 * moryakovdv[at]gmail[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gamc.gis.model;

import java.io.Serializable;
import java.util.LinkedList;

/**The area may be indicated as number of lines.
 *Line itself has start, and end point or just one single point 
 *in the case of vertical or horizontal location indication */
public final class GTLine implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8275937527178470577L;
	
	private LinkedList<GTCoordPoint> coordinatesList = new LinkedList<GTCoordPoint>();
	
	private GTCoordinate singlePointCoordinate;
	
	public GTLine() {}
	
	/**Single point line. 
	 * If Line is described by only one coordinate it is a meridian(E,W) 
	 * or parallel(N,S)*/
	public GTLine(GTCoordinate c) {
		this.setSinglePointCoordinate(c);
		
	}
	
	/**A Line described by start and end points*/
	public GTLine(GTCoordPoint start, GTCoordPoint end) {
		this.coordinatesList.add(start);
		this.coordinatesList.add(end);
	}
	
	/**@return start point*/
	public GTCoordPoint getStartPoint() {
		return coordinatesList.getFirst();
	}
	/**set start point**/
	public void setStartPoint(GTCoordPoint startPoint) {
		this.coordinatesList.add(0, startPoint);
	}
	
	/**@return end point*/
	public GTCoordPoint getEndPoint() {
		return coordinatesList.getLast();
	}
	
	/**set end point**/
	public void setEndPoint(GTCoordPoint endPoint) {
		if (this.coordinatesList.size()>0)
			this.coordinatesList.add(this.coordinatesList.size()-1, endPoint);
		else
			this.coordinatesList.add(endPoint);
	}
	
	/**put point into the list*/
	public void addPoint(GTCoordPoint point) {
		this.coordinatesList.add(point);
	}
	
	/**Determines if this is a single meridian or latitude line */
	public boolean isSingleLine() {
		return (this.coordinatesList.isEmpty() && singlePointCoordinate!=null);
	}

	/**@return single point coordinate if line is meridian or latitude */
	public GTCoordinate getSinglePointCoordinate() {
		return singlePointCoordinate;
	}

	/**set single point coordinate if line is meridian or latitude */
	public void setSinglePointCoordinate(GTCoordinate coordinate) {
		this.singlePointCoordinate = coordinate;
	}
	
	@Override
	public String toString() {
		
		if (isSingleLine()) {
			return this.singlePointCoordinate.toString();
		}
		else
			return this.coordinatesList.toString();
	}

	public LinkedList<GTCoordPoint> getCoordinatesList() {
		return coordinatesList;
	}
	
	
}
