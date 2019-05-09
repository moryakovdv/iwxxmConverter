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

import java.util.Optional;

import org.gamc.spmi.iwxxmConverter.tac.TacSectionImpl;

/**Describes Runway state entry in METAR*/
public class METARRunwayStateSection extends TacSectionImpl {

	private String rvrDesignator;
	boolean applicableForAllRunways;
	
	private Optional<Integer> type =Optional.empty();
	private Optional<Integer> contamination=Optional.empty();
	private Optional<Integer> depositDepth=Optional.empty();
	private Optional<Integer> friction=Optional.empty();
	
	private boolean cleared;
	
	public METARRunwayStateSection(String initialTac) {
		super(initialTac);
	}

	public String getRvrDesignator() {
		return rvrDesignator;
	}

	public void setRvrDesignator(String rvrDesignator) {
		this.rvrDesignator = rvrDesignator;
	}

	public boolean isApplicableForAllRunways() {
		return applicableForAllRunways;
	}

	public void setApplicableForAllRunways(boolean applicableForAllRunways) {
		this.applicableForAllRunways = applicableForAllRunways;
	}

	public Optional<Integer> getType() {
		return type;
	}

	public void setType(Optional<Integer> type) {
		this.type = type;
	}

	public Optional<Integer> getContamination() {
		return contamination;
	}

	public void setContamination(Optional<Integer> contamination) {
		this.contamination = contamination;
	}

	public Optional<Integer> getDepositDepth() {
		return depositDepth;
	}

	public void setDepositDepth(Optional<Integer> depositDepth) {
		this.depositDepth = depositDepth;
	}

	public Optional<Integer> getFriction() {
		return friction;
	}

	public void setFriction(Optional<Integer> friction) {
		this.friction = friction;
	}

	public boolean isCleared() {
		return cleared;
	}

	public void setCleared(boolean cleared) {
		this.cleared = cleared;
	}

	

	
	
	
}
