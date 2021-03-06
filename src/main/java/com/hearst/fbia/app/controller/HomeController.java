package com.hearst.fbia.app.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hearst.fbia.app.domain.SubscriptionAccess;
import com.hearst.fbia.app.model.Response;
import com.hearst.fbia.app.service.SubscriptionConstants;
import com.hearst.fbia.app.service.SubscriptionService;

@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	SubscriptionService subscriptionService;

	@Autowired
	Environment environment;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		return "redirect:swagger-ui.html";
	}

	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String login(@RequestParam(required = false) String redirect_uri,
			@RequestParam(required = false) String account_linking_token,
			@RequestParam(required = false) String subscription_tracking_token, Model model) {
		Long startTime = System.currentTimeMillis();
		logger.info("redirect_uri {}", redirect_uri);
		logger.info("account_linking_token {}", account_linking_token);
		if (null != redirect_uri && null != account_linking_token) {
			subscription_tracking_token = subscriptionService.saveRequestInfo(account_linking_token, redirect_uri,
					SubscriptionConstants.LOGIN.toString());
		} else if (null != subscription_tracking_token) {
			SubscriptionAccess subscriptionAccess = subscriptionService.getRequestInfo(subscription_tracking_token);
			if (null != subscriptionAccess) {
				redirect_uri = subscriptionAccess.getFbRedirectURI();
				account_linking_token = subscriptionAccess.getFbLinkingToken();
			}
		}
		model.addAttribute("subscriptionTrackingToken", subscription_tracking_token);
		model.addAttribute("redirect_uri", redirect_uri);
		model.addAttribute("account_linking_token", account_linking_token);
		logger.info("Time Taken - {}", subscriptionService.convertMsToTime(System.currentTimeMillis() - startTime));
		return "login";
	}

	@RequestMapping(value = "subscribe", method = RequestMethod.GET)
	public String subscribe(@RequestParam String redirect_uri, @RequestParam String account_linking_token,
			Model model) {
		Long startTime = System.currentTimeMillis();
		logger.info("redirect_uri {}", redirect_uri);
		logger.info("account_linking_token {}", account_linking_token);
		String subscriptionTrackingToken = subscriptionService.saveRequestInfo(account_linking_token, redirect_uri,
				SubscriptionConstants.SUBSCRIBE.toString());
		model.addAttribute("subscriptionTrackingToken", subscriptionTrackingToken);
		model.addAttribute("redirect_uri", redirect_uri);
		model.addAttribute("account_linking_token", account_linking_token);
		model.addAttribute("subscribeRedirectUrl", subscriptionService.getRedirectUrl());
		logger.info("Time Taken - {}", subscriptionService.convertMsToTime(System.currentTimeMillis() - startTime));
		return "subscribe";
	}

	@ResponseBody
	@RequestMapping(value = "getSubscriptionPayload", method = RequestMethod.POST)
	public Response getSubscriptionPayload(@RequestParam String subscription) {
		Long startTime = System.currentTimeMillis();
		Response response = subscriptionService.getSubscriptionPayload(subscription);
		logger.info("Time Taken - {}", subscriptionService.convertMsToTime(System.currentTimeMillis() - startTime));
		return response;
	}

	@ResponseBody
	@RequestMapping(value = "test", method = RequestMethod.GET)
	public List<SubscriptionAccess> test() {
		return subscriptionService.getAdminDao().getAllEntities(SubscriptionAccess.class);
	}

	@ResponseBody
	@RequestMapping(value = "currentMarket", method = RequestMethod.GET)
	public String currentMarket() {
		return subscriptionService.getCurrentMarket();
	}

}