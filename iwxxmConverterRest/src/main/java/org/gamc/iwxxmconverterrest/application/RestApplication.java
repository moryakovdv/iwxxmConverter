package org.gamc.iwxxmconverterrest.application;

import org.gamc.iwxxmconverterrest.common.Converters;
import org.gamc.iwxxmconverterrest.common.ConvertersV3;
import org.gamc.iwxxmconverterrest.common.Validator;
import org.gamc.iwxxmconverterrest.common.ValidatorImpl;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAutoConfiguration
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"org.gamc.spmi.iwxxmconverterrest.controllers"})
@EnableJpaRepositories
public class RestApplication extends RepositoryRestMvcConfiguration {

	
	public RestApplication(ApplicationContext context, ObjectFactory<ConversionService> conversionService) {
		super(context, conversionService);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(RestApplication.class, args);
	}
	
	@Bean
	public Validator getValidatorImpl() {
		return new ValidatorImpl();
	}
	
	@Bean
	public Converters getConvertersImpl() {
		return new ConvertersV3();
	}

}
