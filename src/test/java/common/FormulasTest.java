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

import static org.junit.Assert.*;

import java.util.TreeMap;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.junit.Test;
import org.mariuszgromada.math.mxparser.Expression;

public class FormulasTest {

	String sPv = "1 1/4";
	String sPv1 = "1/4";
	String sPv2 = "4.5";
	
	@Test
	public void testSM() {
		String sms = sPv.replaceFirst("\\s", "+");
		Expression fExp = new Expression(sms);  
		double result = fExp.calculate();
		assertEquals(Double.valueOf("1.25"), Double.valueOf(result));
	}
	
	@Test
	public void testSM1() {
		String sms = sPv1.replaceFirst("\\s", "+");
		Expression fExp = new Expression(sms);  
		double result = fExp.calculate();
		assertEquals(Double.valueOf("0.25"), Double.valueOf(result));
	}
	@Test
	public void testSM2() {
		String sms = sPv2.replaceFirst("\\s", "+");
		Expression fExp = new Expression(sms);  
		double result = fExp.calculate();
		assertEquals(Double.valueOf("4.5"), Double.valueOf(result));
	}
	
	
	
	

}
