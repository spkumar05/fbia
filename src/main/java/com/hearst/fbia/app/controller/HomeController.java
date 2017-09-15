package com.hearst.fbia.app.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate);
		return "home";
	}

	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String login(@RequestParam String redirect_uri, @RequestParam String account_linking_token, Model model) {
		logger.info("redirect_uri {}", redirect_uri);
		logger.info("account_linking_token {}", account_linking_token);
		model.addAttribute("redirect_uri", redirect_uri);
		model.addAttribute("account_linking_token", account_linking_token);
		return "login";
	}

	@RequestMapping(value = "subscribe", method = RequestMethod.GET)
	public String subscribe(@RequestParam String redirect_uri, @RequestParam String account_linking_token,
			Model model) {
		logger.info("redirect_uri {}", redirect_uri);
		logger.info("account_linking_token {}", account_linking_token);
		model.addAttribute("redirect_uri", redirect_uri);
		model.addAttribute("account_linking_token", account_linking_token);
		return "home";
	}

	@ResponseBody
	@RequestMapping(value = "getSubscriptionPayload/{edbid}", method = RequestMethod.GET)
	public String getSubscriptionPayload(@PathVariable String edbid) {
		logger.info("edbid {}", edbid);
		// Call MG2 Soap service here
		return "Success";
	}

}