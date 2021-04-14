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
package org.gamc.gis.service;

public class GeomToken {
	private String direction = "P";
	private boolean line;
	
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		if (direction.equals("NNE") || direction.equals("ENE")) {
			direction = "NE";
		}
		if (direction.equals("SSE") || direction.equals("ESE")) {
			direction = "SE";
		}
		if (direction.equals("SSW") || direction.equals("WSW")) {
			direction = "SW";
		}
		if (direction.equals("NNW") || direction.equals("WNW")) {
			direction = "NW";
		}
		this.direction = direction;
	}
	public boolean isLine() {
		return line;
	}
	public void setLine(boolean line) {
		this.line = line;
	}
	
	@Override
	public String toString() {
		return String.format("%s %s", this.direction, this.line);
	}
	
}
