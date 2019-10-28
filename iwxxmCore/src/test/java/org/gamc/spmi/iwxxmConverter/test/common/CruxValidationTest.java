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
 * 
 * CRUX is provided under CRUX Licence
 * Copyright © 2016 University Corporation for Atmospheric Research (UCAR). All rights reserved.
 */
package org.gamc.spmi.iwxxmConverter.test.common;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import edu.ucar.ral.crux.Crux;
import edu.ucar.ral.crux.ValidationException;

public class CruxValidationTest {

	String schPath = "/iwxxm/schematron/iwxxm.sch";
	String fPath = "/output";
	  
	
	@Test
	public void cruxTest() throws IOException, SAXException, ParserConfigurationException, URISyntaxException {
		
		Crux crux = new Crux();
		
		URL resource = getClass().getResource(schPath);
		String schFile  = Paths.get(resource.toURI()).toFile().getAbsolutePath();
		
		URL resourceOut = getClass().getResource(fPath);
		String outFile  = Paths.get(resourceOut.toURI()).toFile().getAbsolutePath();
		
		File fDir = new File(outFile);
		if (fDir.isDirectory()) {
			
			File[] tafs = fDir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File arg0) {
					
					return arg0.getName().endsWith(".xml");
				}
			});
			
			
			
			for(File taf:tafs) {
				try {
				crux.validate(null, schFile,1, 
						taf.getPath());
				}
				catch(ValidationException e) {
					System.out.println(e);
				}
			}
			
		}
		
		
		
	}

	
		
			

}
