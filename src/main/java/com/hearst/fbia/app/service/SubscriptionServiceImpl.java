package com.hearst.fbia.app.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.hearst.fbia.app.domain.SubscriptionAccess;
import com.hearst.fbia.app.market.Market;
import com.hearst.fbia.app.market.MarketRegistry;
import com.hearst.fbia.app.model.Response;
import com.hearst.fbia.frm.service.dao.AdminDao;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

	@Autowired
	Environment environment;

	@Autowired
	AdminDao adminDao;

	@Override
	public AdminDao getAdminDao() {
		return adminDao;
	}

	@Override
	public SubscriptionAccess getRequestInfo(String subscriptionTrackingToken) {
		return adminDao.uniqueResult("from SubscriptionAccess where subscriptionTrackingToken = ?",
				SubscriptionAccess.class, subscriptionTrackingToken);
	}

	@Transactional
	@Override
	public String saveRequestInfo(String account_linking_token, String redirect_uri, String accessType) {
		SubscriptionAccess subscriptionAccess = adminDao.uniqueResult(
				"from SubscriptionAccess where fbLinkingToken = ? and accessType = ?", SubscriptionAccess.class,
				account_linking_token, accessType);

		if (null == subscriptionAccess) {
			subscriptionAccess = new SubscriptionAccess();
			subscriptionAccess.setSubscriptionTrackingToken(UUID.randomUUID().toString());
			subscriptionAccess.setAccessType(accessType);
			subscriptionAccess.setSubscribeMarket(getCurrentMarket());
		}

		subscriptionAccess.setFbRedirectURI(redirect_uri);
		subscriptionAccess.setFbLinkingToken(account_linking_token);

		adminDao.save(subscriptionAccess);

		return subscriptionAccess.getSubscriptionTrackingToken();
	}

	@Transactional
	@Override
	public Response getSubscriptionPayload(String subscription) {
		JSONObject subscriptionJson = new JSONObject(subscription);
		Market market = MarketRegistry.getRegisteredMarket(getCurrentMarket());
		return market.getSubscriptionPayload(subscriptionJson);
	}

	@Override
	public String getCurrentMarket() {
		String market = null;
		String domain = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getHost();
		logger.info("Domain - {}", domain);
		if (domain.contains("houston")) {
			market = environment.getRequiredProperty("houston.subscriptionAccess.subscribeMarket");
		} else if (domain.contains("sfchronicle")) {
			market = environment.getRequiredProperty("sfchronicle.subscriptionAccess.subscribeMarket");
		} else {
			market = "Houston";
		}
		return market;
	}

	@Override
	public String getRedirectUrl() {
		String currentMarket = getCurrentMarket();
		String redirectUrl = null;
		if (currentMarket
				.equalsIgnoreCase(environment.getRequiredProperty("houston.subscriptionAccess.subscribeMarket"))) {
			redirectUrl = environment.getRequiredProperty("houston.subscribe.redirect.url");
		} else if (currentMarket
				.equalsIgnoreCase(environment.getRequiredProperty("sfchronicle.subscriptionAccess.subscribeMarket"))) {
			redirectUrl = environment.getRequiredProperty("sfchronicle.subscribe.redirect.url");
		}
		return redirectUrl;
	}

	@Override
	public String convertMsToTime(long miliSeconds) {
		int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
		int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
		int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
		return String.format("%02d:%02d:%02d", hrs, min, sec);
	}

}