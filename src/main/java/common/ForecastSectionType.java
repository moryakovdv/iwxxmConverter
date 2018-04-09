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

package common;

/** Enum to describe common Forecast Section types */
public enum ForecastSectionType {
	BECMG("BECOMING"), TEMPO("TEMPORARY_FLUCTUATIONS"), PROB30("PROBABILITY_30"), PROB40("PROBABILITY_40"), PROB30TEMPO(
			"PROBABILITY_30_TEMPORARY_FLUCTUATIONS"), PROB40TEMPO("PROBABILITY_40_TEMPORARY_FLUCTUATIONS");

	private ForecastSectionType(String name) {
	}

}
