package org.gamc.spmi.iwxxmConverter.common;

import java.math.BigDecimal;

public final class CoordPoint {
	
	private String latitude;
	private int laDeg;
	private int laMin;
	
	private String longitude;
	private int loDeg;
	private int loMin;
	
	public CoordPoint() {
		
	}
	
	public CoordPoint(String lat, int laDeg, int laMin, String lon, int loDeg, int loMin) {
		this.latitude=lat;
		this.laDeg = laDeg;
		this.laMin = laMin;
		
		this.longitude = lon;
		this.loDeg = loDeg;
		this.loMin = loMin;
		
	}
	
	@Override
	public String toString() {
	
		return String.format("%s%d%d %s%d%d", latitude,laDeg,laMin,longitude,loDeg,loMin);
	}
	
	/**Implement if need to get decimal coordinates*/
	public BigDecimal getDecimalLatitude() {
		throw new RuntimeException("Not implemented yet");
	}
	
	public BigDecimal getDecimalLongitude() {
		throw new RuntimeException("Not implemented yet");
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public int getLaDeg() {
		return laDeg;
	}

	public void setLaDeg(int laDeg) {
		this.laDeg = laDeg;
	}

	public int getLaMin() {
		return laMin;
	}

	public void setLaMin(int laMin) {
		this.laMin = laMin;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public int getLoDeg() {
		return loDeg;
	}

	public void setLoDeg(int loDeg) {
		this.loDeg = loDeg;
	}

	public int getLoMin() {
		return loMin;
	}

	public void setLoMin(int loMin) {
		this.loMin = loMin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + laDeg;
		result = prime * result + laMin;
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + loDeg;
		result = prime * result + loMin;
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CoordPoint other = (CoordPoint) obj;
		if (laDeg != other.laDeg)
			return false;
		if (laMin != other.laMin)
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (loDeg != other.loDeg)
			return false;
		if (loMin != other.loMin)
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		return true;
	}
	
}
