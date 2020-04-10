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
 * WITHOU T WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gamc.spmi.iwxxmConverter.test.v3.spaceweather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gamc.spmi.iwxxmConverter.exceptions.ParsingException;
import org.gamc.spmi.iwxxmConverter.general.ForecastSection;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.SIGMETTacMessage;
import org.gamc.spmi.iwxxmConverter.marshallers.v3.TAFTacMessage;
import org.gamc.spmi.iwxxmConverter.sigmetconverter.SIGMETParsingException;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFBecomingSection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFParsingException;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFProbabilitySection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFTempoSection;
import org.gamc.spmi.iwxxmConverter.tafconverter.TAFTimedFMSection;
import org.junit.Test;

public class SpaceWeatherTranslationTest {

	

	
}
