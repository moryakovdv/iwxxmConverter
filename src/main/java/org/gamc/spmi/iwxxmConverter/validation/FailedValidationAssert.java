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
package org.gamc.spmi.iwxxmConverter.validation;

import org.oclc.purl.dsdl.svrl.FailedAssert;

/**
 * Describes a rule that failed to pass during validation process. This top level class is
 * used to cover PH-Schematrons FailedAssert class to allow end-user to miss the
 * PH-Schematron libraries in class-path
 */
public final class FailedValidationAssert {

	private FailedAssert failedAssert;
	
	public FailedValidationAssert(FailedAssert failedAssert) {
		this.failedAssert = failedAssert;
	
	}
	
	/**Returs rule text*/
	public String getTest() {
		return this.failedAssert.getTest();
	}
	
	/**Returs error text*/
	public String getText() {
		return this.failedAssert.getText();
	}
	
	@Override
	public String toString() {
		return String.format("Rule: %s Text: %s", getTest(), getText());
	} 
}
