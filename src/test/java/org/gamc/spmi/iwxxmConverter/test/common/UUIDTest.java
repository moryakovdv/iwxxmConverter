package org.gamc.spmi.iwxxmConverter.test.common;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.WeakHashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.uuid.Jug;
import com.fasterxml.uuid.UUIDType;
import com.fasterxml.uuid.impl.UUIDUtil;

public class UUIDTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/*
    <gml:TimeInstant gml:id="uuid.b6cc5140-1e51-4642-b2a1-ce4a5e76b674">
    <gml:timePosition>2018-10-21T16:30:00Z</gml:timePosition>
    </gml:TimeInstant>
   */
	/*
	 <gml:TimeInstant gml:id="ti-ULLI-201810261230Z">
     <gml:timePosition>2018-10-26T12:30:00Z</gml:timePosition>
     </gml:TimeInstant>
     */
	
	WeakHashMap<String, UUID> hash = new WeakHashMap<>();
	
	@Test
	public void test() throws UnsupportedEncodingException {
		long s = System.nanoTime();
		UUID id1 = UUID.nameUUIDFromBytes("ti-UUWW-201810211630Z".getBytes("UTF-8"));
		long e = System.nanoTime();
		
		System.out.println(e-s);
			
		s = System.nanoTime();
		UUID id2 = UUID.randomUUID();
		e = System.nanoTime();
		
		System.out.println(e-s);
		
		
		hash.put("ti-UUEE-201810211630Z", id2);
		hash.put("ti-UUDD-201810211630Z", id2);
		hash.put("ti-UUFF-201810211630Z", id2);
		hash.put("ti-UUAA-201810211630Z", id2);
		hash.put("ti-UUSS-201810211630Z", id2);
		hash.put("ti-UUKK-201810211630Z", id2);
		hash.put("ti-UUWW-201810211630Z", id2);
		hash.put("ti-UUII-201810211630Z", id2);
		hash.put("ti-UUMM-201810211630Z", id2);
		hash.put("ti-UUVV-201810211630Z", id2);
		
		
		s = System.nanoTime();
		UUID hashU = hash.get("ti-UUWW-201810211630Z");
		e = System.nanoTime();
		System.out.println(e-s);
		assertEquals(id2, hashU);
		
	}

}
