package org.gamc.spmi.iwxxmconverter.rest.common;

import static org.junit.Assert.assertTrue;

import javax.annotation.PostConstruct;

import org.gamc.iwxxmconverterrest.application.RestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableAutoConfiguration
@SpringBootTest(classes = {RestApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestCommonIntegrationTest {
    @LocalServerPort
    int localPort;

   
    
    @Autowired
    private TestRestTemplate template;

    
    String metarToTest = "METAR ULLI 261100Z 32004MPS 290V350 9999 FEW036 M02/M11 Q1008\n" + 
			"R88/190055 NOSIG=";
	

	String tafToTest = "TAF BIAR 210810Z 2109/2209 16020G30KT 9999 BKN020 OVC065\n" + 
			"      TX07/2115Z\n" + 
			"      TN00/2206Z\n" + 
			"      BECMG 2109/2111 13035G50KT 9999 SCT030 BKN045\n" + 
			"      BECMG 2113/2115 16048G62KT\n" + 
			"      FM211700 21015G25KT\n" + 
			"      BECMG 2120/2122 VRB02KT CAVOK=";
    
	String speciToTest = "SPECI UUEE 270845Z 02006MPS 2100 -SN OVC009 M04/M06 Q1008 R06L/590230\n" + 
			"     R06R/590230 TEMPO 1000 SHSN BKN012CB=";
	
	String sigmetToTest= "WSCH31SCFA 011035\n" + 
			"SCFZ SIGMET A1 VALID 011035/011435 SCFA-\n" + 
			"SCFZ ANTOFAGASTA FIR SEV ICE FCST E OF LINE S2127 W06840 - S2320 \n" + 
			"W06803 - S2442 W06846 FL180/280 STNR NC=";
	
	String airmetToTest="WSRS32 RUAA 010200\n" + 
			"UUYY AIRMET 1 VALID 010200/010600 UUYY-\n" + 
			"UUYY SYKTYVKAR FIR MOD ICE FCST W OF E06000\n" + 
			"FL240/370 MOV NE 30KMH NC=";
	
	String swxToTest="SWX ADVISORY\n" + 
			"DTG:                20161108/0100Z \n" + 
			"SWXC:               DONLON\n" + 
			"ADVISORY NR:        2016/2\n" + 
			"SWX EFFECT:         HF COM MOD AND GNSS MOD \n" + 
			"NR RPLC :           2016/1\n" + 
			"OBS SWX:            08/0100Z HNH HSH E18000 - W18000 \n" + 
			"FCST SWX +6 HR:     08/0700Z HNH HSH E18000 - W18000\n" + 
			"FCST SWX +12 HR:    08/1300Z HNH HSH E18000 - W18000\n" + 
			"FCST SWX +18 HR:    08/1900Z HNH HSH E18000 - W18000\n" + 
			"FCST SWX +24 HR:    09/0100Z NO SWX EXP\n" + 
			"RMK:                LOW LVL GEOMAGNETIC STORMING CAUSING INCREASED AURORAL ACT AND SUBSEQUENT MOD DEGRADATION OF GNSS AND HF COM AVBL IN THE AURORAL ZONE. THIS STORMING EXP TO SUBSIDE IN THE FCST PERIOD. SEE WWW.SPACEWEATHERPROVIDER.WEB \n" + 
			"NXT ADVISORY:       NO FURTHER ADVISORIES";;
	
	
	String sigmetToTest1 = "WSRS31RUMA 111143 XXX UEEE SIGMET 2 VALID 100800/101200 UEEE-\n" + 
			"UEEE YAKUTSK FIR EMBD TS FCST S OF N6530 TOP FL400 STNR NC=";
	
    @PostConstruct
    public void initialize() {
       /*
    	RestTemplate customTemplate = restTemplateBuilder
            .rootUri("http://localhost:"+localPort)
            .build();
            */
      
    }
    
    private void checkResult(String result) throws JsonMappingException, JsonProcessingException, IllegalArgumentException {
    	JsonNode parent= new ObjectMapper().readTree(result);
		boolean isValid = parent.path("valid").asBoolean();
		String xml = parent.path("xml").asText();
		if (!isValid) {
			throw new IllegalArgumentException();
			
		}
			
		assertTrue(isValid);
		System.out.println(xml);
    	
    }
    
    @Test
    public void testMetar() throws JsonMappingException, JsonProcessingException {
    	ResponseEntity<String> result = template.postForEntity("http://localhost:"+localPort+"/convert/v3/metar", metarToTest, String.class);
    	checkResult(result.getBody());
    };
    
    @Test
    public void testTaf() throws JsonMappingException, JsonProcessingException {
    	ResponseEntity<String> result = template.postForEntity("http://localhost:"+localPort+"/convert/v3/taf", tafToTest, String.class);
    	checkResult(result.getBody());
    	
    }
    
    @Test
    public void testSpeci() throws JsonMappingException, JsonProcessingException {
    	ResponseEntity<String> result = template.postForEntity("http://localhost:"+localPort+"/convert/v3/speci", speciToTest, String.class);
    	checkResult(result.getBody());
    	
    }
    
    @Test
    public void testSigmet() throws JsonMappingException, JsonProcessingException {
    	ResponseEntity<String> result = template.postForEntity("http://localhost:"+localPort+"/convert/v3/sigmet", sigmetToTest, String.class);
    	checkResult(result.getBody());
    	
    }
    
    @Test
    public void testAirmet() throws JsonMappingException, JsonProcessingException {
    	ResponseEntity<String> result = template.postForEntity("http://localhost:"+localPort+"/convert/v3/airmet", airmetToTest, String.class);
    	checkResult(result.getBody());
    	
    }
    
    @Test
    public void testSpaceWeather() throws JsonMappingException, JsonProcessingException {
    	ResponseEntity<String> result = template.postForEntity("http://localhost:"+localPort+"/convert/v3/swx", swxToTest, String.class);
    	checkResult(result.getBody());
    	
    }
    
    @Test
    
    public void testConverter() throws JsonMappingException, JsonProcessingException {
    	ResponseEntity<String> result = template.postForEntity("http://localhost:"+localPort+"/convert/v3", sigmetToTest1, String.class);
    	Assertions.assertThrows(IllegalArgumentException.class, ()->checkResult(result.getBody()));
    	
    }
}
