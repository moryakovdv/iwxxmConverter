package org.gamc.gis.model;

import java.io.Serializable;

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
public class GTCoordinate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3234000357216667876L;
	private String azimuth;
	private int deg;
	private int min;
	
	public GTCoordinate() {
		
	}
	
	public GTCoordinate(String azimuth, int deg, int min) {
		this.azimuth=azimuth;
		this.deg=deg;
		this.min=min;
	}
	
	public String getAzimuth() {
		return azimuth;
	}
	public void setAzimuth(String azimuth) {
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
		GTCoordinate other = (GTCoordinate) obj;
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

}
