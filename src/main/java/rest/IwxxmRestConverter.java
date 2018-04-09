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
package rest;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.slf4j.Logger;

import common.LoggingClass;
import exceptions.ParsingException;
import general.ConverterFactory;
import tac.TacConverter;
import validation.IwxxmValidator;

@Path("/api")
public class IwxxmRestConverter {

	Logger logger = LoggingClass.INSTANCE.getLoggerInstanceForClass(this.getClass());
	
	/**Test rest method. Returns welcome string*/
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_HTML)
	public Response getTestMessage(@Context HttpHeaders head) {

		return Response.status(Status.OK).entity("IwxxmConverter REST works.<br> "
				+ "To convert TAC message send TAC as parameter use <code>api/convert?message=...</code><br>"
				+ "To convert TAC message and get validation results use <code>api/convertandvalidate?message=...</code><br>"
				+ "To validate input xml use <code>api/validate?message=...</code>").build();

	}
	
	
	/**Rest for converting TAC to IWXXM*/
	@GET
	@Path("/convert")
	@Produces(MediaType.TEXT_XML)
	public Response getConvertedMessage(@Context HttpHeaders head, @QueryParam("message") String tacMessage) {
		logger.info("Request to translation received");
		logger.info(head.getRequestHeaders().toString());
		logger.info(tacMessage);
		
		try {
			
			TacConverter<?, ?> converter = ConverterFactory.createForTac(tacMessage);
			String convertedIwxxm = converter.convertTacToXML(tacMessage);
			
			return Response.status(Status.OK).entity(convertedIwxxm).build();	
			
		} catch (ParsingException|UnsupportedEncodingException|DatatypeConfigurationException|JAXBException e) {
			logger.error("Conversion error",e);
			return Response.status(Status.OK).entity(e.getMessage()).build();
		}
		
		

	}
	
	/**Rest for validate input xml*/
	@GET
	@Path("/validate")
	@Produces(MediaType.TEXT_HTML)
	public Response getValidationResults(@Context HttpHeaders head, @QueryParam("message") String xmlMessage) {
		logger.info("Request to validation received");
		logger.info(head.getRequestHeaders().toString());
		logger.info(xmlMessage);
		
		try {
			
			
			IwxxmValidator validator = new IwxxmValidator();
			List<FailedAssert> failedList = validator.validateString(xmlMessage);
			
			if (failedList.size()==0) {
				String res = prepareHtml("Validation succeeded",xmlMessage);
				return Response.status(Status.OK).entity(res).build();
			}
			else {
				String res = prepareHtml("Validation failures:"+failedList.toString(),xmlMessage);
				return Response.status(Status.OK).entity(res).build();
			}
			
			
		} catch (Exception e) {
			logger.error("Validation error",e);
			return Response.status(Status.OK).entity(e.getMessage()).build();
		}
		
		

	}
	
	
	/**Rest for converting TAC to IWXXM and validation of result*/
	@GET
	@Path("/convertandvalidate")
	@Produces(MediaType.TEXT_HTML)
	public Response getConvertsionResults(@Context HttpHeaders head, @QueryParam("message") String tacMessage) {
		logger.info("Request to validation received");
		logger.info(head.getRequestHeaders().toString());
		logger.info(tacMessage);
		
		try {
			
			TacConverter<?, ?> converter = ConverterFactory.createForTac(tacMessage);
			String convertedIwxxm = converter.convertTacToXML(tacMessage);
			IwxxmValidator validator = new IwxxmValidator();
			List<FailedAssert> failedList = validator.validateString(convertedIwxxm);
			
			if (failedList.size()==0) {
				String res = prepareHtml("Validation succeeded",convertedIwxxm);
				return Response.status(Status.OK).entity(res).build();
			}
			else {
				String res = prepareHtml("Validation failures:"+failedList.toString(),convertedIwxxm);
				return Response.status(Status.OK).entity(res).build();
			}
			
			
		} catch (Exception e) {
			logger.error("Validation error",e);
			return Response.status(Status.OK).entity(e.getMessage()).build();
		}
		
		

	}
	
	private String prepareHtml(String validationResult, String xmlResult) {
		
		String result = String.format("<div style='width:100%%;clear:both'>"
				+ "<div id='valResult' style='width:20%%;float:left'>%s"
				+ "</div>"
				+ "<div id='xmlResult' style='width:80%%;float:right'>"
				+ "<textarea rows='100' cols='80' style='border:1px solid black;'>"
				+ "%s"
				+ "</textarea>"
				+ "</div>"
				+ "</div>", validationResult,xmlResult);
		return result;
	}

	
	
}