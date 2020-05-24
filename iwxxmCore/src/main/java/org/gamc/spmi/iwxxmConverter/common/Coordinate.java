package org.gamc.spmi.iwxxmConverter.common;

import java.io.Serializable;

import org.gamc.spmi.gis.model.GTCoordinate;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;

public class Coordinate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3234000357216667876L;
	private RUMB_UNITS azimuth;
	private int deg;
	private int min;
	
	public Coordinate() {
		
	}
	
	public Coordinate(RUMB_UNITS azimuth, int deg, int min) {
		this.azimuth=azimuth;
		this.deg=deg;
		this.min=min;
	}
	
	public RUMB_UNITS getAzimuth() {
		return azimuth;
	}
	public void setAzimuth(RUMB_UNITS azimuth) {
		this.azimuth = azimuth;
	}
	public int getDeg() {
		return deg;
	}
	public void setDeg(int deg) {
		this.deg = deg;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("%s%02d%02d", azimuth,deg,min);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((azimuth == null) ? 0 : azimuth.hashCode());
		result = prime * result + deg;
		result = prime * result + min;
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
		Coordinate other = (Coordinate) obj;
		if (azimuth == null) {
			if (other.azimuth != null)
				return false;
		} else if (!azimuth.equals(other.azimuth))
			return false;
		if (deg != other.deg)
			return false;
		if (min != other.min)
			return false;
		return true;
	}
	
	/**Convert to GTCoordinate for GIS calculations*/
	public GTCoordinate toGTCoordinate() {
		return new GTCoordinate(this.azimuth.name(), this.deg, this.min);
	}

}
