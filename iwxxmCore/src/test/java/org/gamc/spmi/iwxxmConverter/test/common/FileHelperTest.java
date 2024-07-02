package org.gamc.spmi.iwxxmConverter.test.common;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gamc.spmi.iwxxmConverter.common.FileResourcesHelper;
import org.junit.Test;

public class FileHelperTest {

	@Test
	public void testResource() throws URISyntaxException, IOException {
		Path u = FileResourcesHelper.findResourcePathByFileName(this.getClass(),"shapes/airports/airports.cpg");
		System.out.println(u);
		
		boolean e = Files.exists(u); 
		byte[] r = Files.readAllBytes(u);
		System.out.println(new String(r,"UTF-8"));
	}
	
	@Test
	public void testFS() throws URISyntaxException, IOException {
		Path u = FileResourcesHelper.findResourcePathByFileName(this.getClass(),"~/fir/firs_shape.cpg");
		System.out.println(u);

		boolean e = Files.exists(u); 
		byte[] r = Files.readAllBytes(u);
		System.out.println(new String(r,"UTF-8"));
		
	}

}
