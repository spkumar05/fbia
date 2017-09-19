package com.hearst.fbia.app.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hearst.fbia.app.domain.SubscriptionAccess;
import com.hearst.fbia.app.model.Response;
import com.hearst.fbia.app.service.SubscriptionService;

@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	SubscriptionService subscriptionService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		return "redirect:swagger-ui.html";
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
	@RequestMapping(value = "getSubscriptionPayload", method = RequestMethod.POST)
	public Response getSubscriptionPayload(@RequestParam String subscription) {
		return subscriptionService.getSubscriptionPayload(subscription);
	}

	@ResponseBody
	@RequestMapping(value = "test", method = RequestMethod.GET)
	public List<SubscriptionAccess> test() {
		return subscriptionService.getAdminDao().getAllEntities(SubscriptionAccess.class);
	}

}