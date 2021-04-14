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

/**Describes direction from certain line, e.g 'NE OF LINE ...'*/	
public class GTDirectionFromLine implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1937563107841308842L;
	private String direction;
	private GTLine line;
	
	public GTDirectionFromLine() {}
	
	
	public GTDirectionFromLine(String azimuth, GTLine line) {
		this.direction=azimuth;
		this.line = line;
	}
	
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public GTLine getLine() {
		return line;
	}
	public void setLine(GTLine line) {
		this.line = line;
	}
	
	@Override
	public String toString() {
	
		return direction +" "+this.line.toString();
				
	}
	
	
}
