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
package iwxxmenums;

/**Enum for RUMBS angles units used in iwxxm
 * 
 * @author moryakov
 * */
public enum RUMB_UNITS implements IwxxmEnum {
	
	N("360"), NE("45"), E("90"),SE("135"), S("180"), SW("225"), W("270"), NW("315");

	private String name;
	
	private RUMB_UNITS(String strName) {
		this.name=strName;
	}
	
	@Override
	public String getStringValue() {
		return name;
	}
	
	
	
}
