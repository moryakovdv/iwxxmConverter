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

/**Describes coordinate point with latitude and longitude, e.g E18000, W9000*/
public final class GTCoordPoint implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3093885536768642653L;
	private GTCoordinate latitude;
	private GTCoordinate longitude;
	
	public GTCoordPoint() {
		
	}
	
	public GTCoordPoint(GTCoordinate latitude, GTCoordinate longitude) {
		this.latitude=latitude;
		this.longitude=longitude;
	}
	
	public GTCoordPoint(String lat, int laDeg, int laMin, String lon, int loDeg, int loMin) {
		this.latitude=new GTCoordinate(lat, laDeg, laMin);
		this.longitude = new GTCoordinate(lon,loDeg,loMin);
		
	}
	
	@Override
	public String toString() {
	
		return String.format("%s - %s",latitude,longitude);
	}

	public GTCoordinate getLatitude() {
		return latitude;
	}

	public void setLatitude(GTCoordinate latitude) {
		this.latitude = latitude;
	}

	public GTCoordinate getLongitude() {
		return longitude;
	}

	public void setLongitude(GTCoordinate longitude) {
		this.longitude = longitude;
	}
	
		
}
