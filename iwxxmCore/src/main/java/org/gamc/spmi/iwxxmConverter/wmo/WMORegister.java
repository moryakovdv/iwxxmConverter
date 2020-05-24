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
package org.gamc.spmi.iwxxmConverter.wmo;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Base interface for parsing WMO XML Registers. Each class should implement
 * parsing method and method for query WMO url by code Project directory stores
 * XML-files downloaded from WMO
 * 
 * @author moryakov
 */
public interface WMORegister<T> {

	public static final String xpathToConcept = "/RDF/*/member/Concept";

	Logger registerLogger = LoggerFactory.getLogger(WMORegister.class);

	/** Parse XML file and populate TreeMap storage */
	default void parseWMOXml() {
		try (InputStream is = new FileInputStream(getRegisterFileName())) {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			// this.getClass().getResourceAsStream(registerFileName);
			Document doc = docBuilder.parse(is);

			// normalize text representation
			doc.getDocumentElement().normalize();

			// Create XPathFactory object
			XPathFactory xpathFactory = XPathFactory.newInstance();

			// Create XPath object
			XPath xpath = xpathFactory.newXPath();

			XPathExpression expr = xpath.compile(xpathToConcept); // "/RDF/RegisterItem/register/Register/member/Concept");

			NodeList listOfElements = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);// root.getElementsByTagName("member");
			int totalElements = listOfElements.getLength();
			registerLogger
					.debug(String.format("Total members in register %s: %d", this.getClass().getName(), totalElements));

			for (int i = 0; i < listOfElements.getLength(); i++) {

				Node currentNode = listOfElements.item(i);
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

					String wmoCode = "";
					String wmoUrl = "";
					String bufrUrl = "";
					String description = "";
					String label = "";

					Element concept = (Element) currentNode;
					wmoUrl = concept.getAttribute("rdf:about");

					NodeList notationList = concept.getElementsByTagName("skos:notation");
					if (notationList != null && notationList.getLength() > 0) {
						Element notation = (Element) notationList.item(0);
						wmoCode = notation.getTextContent();

					}

					// extract code from url if notation is absent
					if (wmoCode.isEmpty()) {
						int lastSlashIndex = wmoUrl.lastIndexOf("/");
						wmoCode = wmoUrl.substring(lastSlashIndex + 1);
					}

					NodeList bufrList = concept.getElementsByTagName("skos:related");
					if (bufrList != null && bufrList.getLength() > 0) {
						Element bufr = (Element) bufrList.item(0);
						bufrUrl = bufr.getAttribute("rdf:resource");

					}

					NodeList descriptionList = concept.getElementsByTagName("dct:description");
					if (descriptionList != null && descriptionList.getLength() > 0) {
						for (int index = 0; index < descriptionList.getLength(); index++) {
							Element descr = (Element) descriptionList.item(index);
							String xmlLang = descr.getAttribute("xml:lang");
							if (xmlLang.equalsIgnoreCase(getLocale().getLanguage())) {
								description = descr.getTextContent();
								break;
							}
						}
						if (description.isEmpty()) {
							Element descr = (Element) descriptionList.item(0);
							description =  descr.getTextContent();
						}

					}

					NodeList labelList = concept.getElementsByTagName("rdfs:label");
					if (labelList != null && labelList.getLength() > 0) {
						for (int index = 0; index < labelList.getLength(); index++) {
							Element lab = (Element) labelList.item(index);
							String xmlLang = lab.getAttribute("xml:lang");
							if (xmlLang.equalsIgnoreCase(getLocale().getLanguage())) {
								label = lab.getTextContent();
								break;
							}
						}
						if (label.isEmpty()) {
							Element lab = (Element) labelList.item(0);
							label =  lab.getTextContent();
						}

					}

					if (wmoCode.length() > 0)
						putToContent(wmoCode, new WMORegisterDescription(wmoUrl, description, bufrUrl, label));

				}
			}
		} catch (SAXParseException err) {
			registerLogger.error("Error in parsing ", err);
		} catch (SAXException e) {
			registerLogger.error("SAX Exception", e);
		} catch (Throwable t) {
			registerLogger.error("Unknown error", t);
		}

	};

	/** Get All content of a registry as a Map of elements */
	TreeMap<T, WMORegisterDescription> getContent();

	/** Get particular URL for given code */
	default public String getWMOUrlByCode(T code) {
		if (getContent().size() == 0)
			parseWMOXml();
		if (code instanceof String)
			code = (T) ((String) code).toUpperCase();
		return getContent().get(code).getWmoUrl();
	}

	/** Returns register file name for particular registry */
	String getRegisterFileName();

	default void putToContent(String wmoCode, WMORegisterDescription description) {
		getContent().put((T) wmoCode.toUpperCase(), description);
	};

	/** gets Locale for using for labels and descriptions */
	Locale getLocale();

}
