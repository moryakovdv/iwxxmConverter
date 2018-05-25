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
package org.gamc.spmi.iwxxmConverter.rest;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebAppStarter {

	 
	    public static final String ROOT_PATH = "api";
	    static final Logger logger = LoggerFactory.getLogger(WebAppStarter.class);
	    public static void main(String[] args) {
	        try {
	        	
	        	String host="localhost";
	        	String port="8082";
	        	
	        	if (args.length==2) {
	        		host=args[0];
	        		port=args[1];
	        	}
	        	
	        	URI BASE_URI = URI.create(String.format("http://%s:%s/iwxxmConverter/",host,port));
	        	logger.info("IWXXM Grizzly REST is starting...");

	            final ResourceConfig resourceConfig = new ResourceConfig(IwxxmRestConverter.class);
	            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false);
	            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	                @Override
	                public void run() {
	                    server.shutdownNow();
	                }
	            }));
	            server.start();

	            logger.info(String.format("IWXXM REST started",
	                    BASE_URI, ROOT_PATH));
	            Thread.currentThread().join();
	        } catch (IOException | InterruptedException ex) {
	        	logger.error("REST service error",ex);
	        }

	}
}