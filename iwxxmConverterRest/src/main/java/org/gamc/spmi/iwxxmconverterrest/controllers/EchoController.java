package org.gamc.spmi.iwxxmconverterrest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class EchoController {
	
	private final static String githubWikiUrl = "https://github.com/moryakovdv/iwxxmConverter/wiki";
	
	
	@RequestMapping(value="/test", method = RequestMethod.GET)
	public @ResponseBody String echo() {
		return "It works. Please request /rest/help to get usage info.";
	}
	
	@RequestMapping(value="/help", method = RequestMethod.GET)
	public RedirectView redirectToHelp(RedirectAttributes attributes) {
		return new RedirectView(githubWikiUrl);
	}
	
	

}
