package org.gamc.iwxxmconverterrest.application;

import org.gamc.iwxxmconverterrest.common.Converters;
import org.gamc.iwxxmconverterrest.common.ConvertersV3;
import org.gamc.iwxxmconverterrest.common.Validator;
import org.gamc.iwxxmconverterrest.common.ValidatorImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties.Application;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAutoConfiguration

@Configuration
@ComponentScan(basePackages = {"org.gamc.spmi.iwxxmconverterrest.controllers"})
@EnableJpaRepositories
public class RestApplication extends Application {

	
	

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
