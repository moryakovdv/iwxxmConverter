package org.gamc.iwxxmconverterrest.common;

import javax.annotation.PostConstruct;

import org.gamc.spmi.iwxxmConverter.validation.IwxxmValidator;
import org.slf4j.Logger;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
public class ValidatorImpl implements Validator {

	private Logger logger = org.slf4j.LoggerFactory.getLogger("Rest-validator");

	private IwxxmValidator iwxxmValidator;

	@PostConstruct
	public void onInit() {
		iwxxmValidator = new IwxxmValidator();
		iwxxmValidator.init();
		logger.info("Validator created");

	}

	public IwxxmValidator getIwxxmValidator() {
		return iwxxmValidator;
	}

}
