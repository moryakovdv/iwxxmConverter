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
package org.gamc.spmi.iwxxmConverter.metarconverter;

import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;
import org.gamc.spmi.iwxxmConverter.tac.TacMessageImpl;
import org.gamc.spmi.iwxxmConverter.tac.TacSectionImpl;

/**Describes RVR entry in METAR*/
public class METARRVRSection extends TacSectionImpl {

	private String rvrDesignator;
	private Integer rvrValue;
	private RVRVisibilityTendency tendency;
	
	private RVROperator operator;
	private LENGTH_UNITS units = LENGTH_UNITS.M;
	
	public METARRVRSection(String initialTac,TacMessageImpl parent) {
		super(initialTac, parent);
		
	}

	public String getRvrDesignator() {
		return rvrDesignator;
	}

	public void setRvrDesignator(String rvrDesignator) {
		if (rvrDesignator != null)
			this.rvrDesignator = rvrDesignator;
	}

	public Integer getRvrValue() {
		return rvrValue;
	}

	public void setRvrValue(Integer rvrValue) {
		if (rvrValue != null)
			this.rvrValue = rvrValue;
	}

	public RVRVisibilityTendency getTendency() {
		return tendency;
	}

	public void setTendency(RVRVisibilityTendency tendency) {
		if (tendency != null)
			this.tendency = tendency;
	}

	public RVROperator getOperator() {
		return operator;
	}

	public void setOperator(RVROperator operator) {
		this.operator = operator;
	}

	public LENGTH_UNITS getUnits() {
		return units;
	}

	public void setUnits(LENGTH_UNITS units) {
		this.units = units;
	}

	
}
