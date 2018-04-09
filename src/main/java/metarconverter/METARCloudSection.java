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
package metarconverter;

import tac.TacSectionImpl;

/**Describes cloud entry in METAR*/
public class METARCloudSection extends TacSectionImpl {

	private String amount;
	private Integer height;
	private String type;
	
	private boolean isVerticalVisibility;
	
	public METARCloudSection(String initialTac) {
		super(initialTac);
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		if (amount != null)
			this.amount = amount;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		if (height != null)
			this.height = height;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type != null)
			this.type = type;
	}

	public boolean isVerticalVisibility() {
		return isVerticalVisibility;
	}

	public void setVerticalVisibility(boolean isVerticalVisibility) {
		this.isVerticalVisibility = isVerticalVisibility;
	}
	

}
