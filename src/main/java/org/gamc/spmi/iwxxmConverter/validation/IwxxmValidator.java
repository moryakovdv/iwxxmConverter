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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.FiredRule;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;

import com.helger.schematron.ISchematronResource;
import com.helger.schematron.svrl.SVRLMarshaller;
import com.helger.schematron.xslt.SchematronResourceSCH;

/**Class for validation IWXXM XML-file against schemas.
 * Uses ph-schematron
 **/
public class IwxxmValidator {

	private ISchematronResource aResSCH;
	final static String tempFilePath = "tempIwxxmFile.xml";
	
	/**Prepare and compile schema*/
	public void init() {
		aResSCH = SchematronResourceSCH.fromClassPath("iwxxm/schematron/iwxxm.sch");
		if (!aResSCH.isValidSchematron())
			throw new IllegalArgumentException("Invalid Schematron!");
	}
	
	/**
	 * Validates input file
	 * @param inputXmlFile - input file with ready IWXXM XML message
	 * @param outReportFile - generate report file with given name. Null if not nesessary
	 * @return list of the Failed assertions
	 * */
	public List<FailedValidationAssert> validate(File inputXmlFile, File outReportFile) throws Exception {

		if (aResSCH==null)
			init();
		
		LinkedList<FailedValidationAssert> failedAssertions = new LinkedList<>();
		SchematronOutputType results = aResSCH.applySchematronValidationToSVRL(new StreamSource(inputXmlFile));

		List<Object> allAsserts = results.getActivePatternAndFiredRuleAndFailedAssert();
		for (Object object : allAsserts) {

			if (object instanceof FiredRule) {
				FiredRule rule = (FiredRule) object;
				
			}

			if (object instanceof FailedAssert) {
				FailedAssert failedAssert = (FailedAssert) object;
				failedAssertions.add(new FailedValidationAssert(failedAssert));
			}

		}

		if (outReportFile != null) {
			SVRLMarshaller m = new SVRLMarshaller();
			m.write(results, outReportFile);
		}
		
		return failedAssertions;

	}
	
	/**Validates input iwxxm string
	 * @param inputXml - String with iwxxm text
	 * @return list of validation errors
	 * @throws IOException when unable to create temp file
	 * */
	public List<FailedValidationAssert> validateString(String inputXml) throws Exception {
		
		File f = File.createTempFile(tempFilePath,"rw");
		
		try(FileOutputStream fs = new FileOutputStream(f);) {
			fs.write(inputXml.getBytes("UTF-8"));	
		}
		catch(Exception e) {
			throw new IOException("Can not create temporary file");
		}
		
		List<FailedValidationAssert> result = validate(f, null);
		return result;
		
	} 

}
