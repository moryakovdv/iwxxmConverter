package org.gamc.spmi.iwxxmconverter.rest.common;

import static org.junit.Assert.assertTrue;

import javax.annotation.PostConstruct;

import org.gamc.iwxxmconverterrest.application.RestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = {RestApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestStartupIntegrationTest {
    @LocalServerPort
    int localPort;
    
    @Autowired
    private TestRestTemplate template;

    
    @PostConstruct
    public void initialize() {
      
      
    }
    
    @Test
    public void testHello() {
    	String result = template.getForObject("http://localhost:"+localPort+"/test", String.class);
    	assertTrue(result.contains("It works"));
    	System.out.println(result);
    	
    };
}
