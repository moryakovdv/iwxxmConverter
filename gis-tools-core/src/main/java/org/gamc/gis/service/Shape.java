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

import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;


public class Shape {
	private ArrayList<Coordinate> coordinates = new ArrayList<>();
	private GeomToken geometry = new GeomToken();

	public GeomToken getGeometry() {
		return geometry;
	}

	public void setGeometry(GeomToken geometry) {
		this.geometry = geometry;
	}

	public ArrayList<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(ArrayList<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	public String toString() {
		return geometry + " " + coordinates;
	}

	public Shape(Shape s) {
		super();
		this.geometry = s.getGeometry();
		for (Coordinate c : s.getCoordinates()) {
			this.coordinates.add(new Coordinate(c.x, c.y));
		}
		if (coordinates.size() == 2 && coordinates.get(0).x > coordinates.get(1).x) {
			double x = coordinates.get(1).x;
			double y = coordinates.get(1).y;
			coordinates.get(1).x = coordinates.get(0).x;
			coordinates.get(1).y = coordinates.get(0).y;
			coordinates.get(0).x = x;
			coordinates.get(0).y = y;
		}
	}

	public Shape() {
		super();
	}
	
	
}
