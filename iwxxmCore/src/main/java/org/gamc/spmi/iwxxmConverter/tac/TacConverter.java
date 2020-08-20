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
package org.gamc.spmi.iwxxmConverter.tac;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.general.IWXXMHelpers;
import org.gamc.spmi.iwxxmConverter.wmo.WMORegister.WMORegisterException;

/** General interface for converter implementation
 * Generic parameters:
 * T - type of converted message, for example MetarTacMessage
 * R - type of IWXXM report to create via JAXB
 * H - type of IWXXMHelpers to use
 *  */
public interface TacConverter<T, R, H> {

	String convertTacToXML(String tac)
			throws UnsupportedEncodingException, DatatypeConfigurationException, JAXBException, WMORegisterException;
		
	R convertMessage(T translatedMessage)
			throws DatatypeConfigurationException, UnsupportedEncodingException, JAXBException, ParsingException, WMORegisterException;

	R addTranslationCentreHeader(R report) throws DatatypeConfigurationException;
	
	String marshallMessageToXML(R reportType) throws JAXBException, UnsupportedEncodingException;
	
	H getHelper();
	
	/**Simplifies converter creation with specified helper, for example, if the IwxxmHeper was prepared beforehand*/
	TacConverter<T, R, H> withHelper(H helper);
	
	
}
