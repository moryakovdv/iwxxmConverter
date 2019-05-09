package org.gamc.spmi.iwxxmConverter.common;

import java.math.BigDecimal;

import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;

/**Describes coordinate point with latitude and longitude*/
public final class CoordPoint {
	
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
	
		
}
