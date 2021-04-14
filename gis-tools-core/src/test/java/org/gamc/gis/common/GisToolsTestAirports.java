package org.gamc.gis.common;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.gamc.gis.service.GeoService;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.fasterxml.jackson.core.JsonProcessingException;

public class GisToolsTestAirports {

	static GeoService gs = new GeoService();

	@BeforeClass
	public static void warmUp() throws URISyntaxException {
		gs.init(false, "", true);
	}

	@Test
	public void test() throws URISyntaxException {

		
			Object a = gs.getAirportCoordinates("UUWW");
			assertNotNull(a);
			System.out.println(a);
		

	}

	@Test
	public void testJsonForSingleAirport() throws JsonProcessingException {
		String json = gs.jsonForAirport("UUWW");
		assertFalse(json.isEmpty());
		System.out.println(json);
	}

	@Test
	public void testJsonForAllLargeAirports() throws IOException {
		String json = gs.jsonForAllAirports();
		assertFalse(json.isEmpty());
		System.out.println(json.length());
		byte[] b = json.getBytes("UTF-8");
		File f = new File("/home/moryakov/airports-small.json");
		f.createNewFile();
		try (FileOutputStream os = new FileOutputStream(f);
				BufferedOutputStream bos = new BufferedOutputStream(os);) {

			bos.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
