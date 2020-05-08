package org.gamc.spmi.iwxxmConverter.common;

import java.io.Serializable;
import java.math.BigDecimal;

import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;

/**Describes coordinate point with latitude and longitude*/
public final class CoordPoint implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3093885536768642653L;
	private Coordinate latitude;
	private Coordinate longitude;
	
	public CoordPoint() {
		
	}
	
	public CoordPoint(Coordinate latitude, Coordinate longitude) {
		this.latitude=latitude;
		this.longitude=longitude;
	}
	
	public CoordPoint(RUMB_UNITS lat, int laDeg, int laMin, RUMB_UNITS lon, int loDeg, int loMin) {
		this.latitude=new Coordinate(lat, laDeg, laMin);
		this.longitude = new Coordinate(lon,loDeg,loMin);
		
	}
	
	@Override
	public String toString() {
	
		return String.format("%s - %s",latitude,longitude);
	}

	public Coordinate getLatitude() {
		return latitude;
	}

	public void setLatitude(Coordinate latitude) {
		this.latitude = latitude;
	}

	public Coordinate getLongitude() {
		return longitude;
	}

	public void setLongitude(Coordinate longitude) {
		this.longitude = longitude;
	}
	
		
}
