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


package general;

import exceptions.ParsingException;

/**Interface that should be implemented by all sections that provides weather information.
 *So TAF contains mandatory weather section with tokens of temperature, wind, visibility and so force. 
 *Additionally there can be several trend sections that also contains all or part of these tokens.
 *Implement this interface to simplify further parsing and data accessing  
 * */
public interface CommonWeatherSection {
	
	public StringBuffer parseSection(StringBuffer tac) throws ParsingException;

}
