package org.gamc.spmi.iwxxmConverter.common;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileResourcesHelper {

	
	public static Path findResourcePathByFileName(Class<?> cls, String pathElements) throws  FileNotFoundException, URISyntaxException {
		
		pathElements = pathElements.replaceFirst("^~", System.getProperty("user.home"));
		
		Path pToFile = Paths.get(pathElements);
		
		if (!Files.exists(pToFile)) {
			URL url = cls.getClassLoader().getResource(pToFile.toString());
			if (url==null)
				throw new FileNotFoundException("Cannot find file "+pathElements);
			
			return Paths.get(url.toURI());
			
		} else {
			return pToFile;
		}

		
	}
}
