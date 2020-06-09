package org.gamc.spmi.iwxxmconverterrest.controllers;

import java.util.List;

import org.gamc.spmi.iwxxmConverter.validation.FailedValidationAssert;

public class ValidationResult {
	
	private List<FailedValidationAssert> asserts;
	private String xml;

	
	public ValidationResult(List<FailedValidationAssert> asserts, String xml) {
		this.asserts=asserts;
		this.xml = xml;
	}
	
	public boolean isValid() {
		return asserts!=null && asserts.isEmpty();
	}

	public List<FailedValidationAssert> getAsserts() {
		return asserts;
	}

	public String getXml() {
		return xml;
	}
	

}
