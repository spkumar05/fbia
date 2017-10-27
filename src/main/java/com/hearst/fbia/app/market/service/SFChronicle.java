package com.hearst.fbia.app.market.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.hearst.fbia.app.domain.SubscriptionAccess;
import com.hearst.fbia.app.market.Market;
import com.hearst.fbia.app.market.MarketUtil;
import com.hearst.fbia.app.model.Meta;
import com.hearst.fbia.app.model.Response;
import com.hearst.fbia.frm.service.dao.AdminDao;

@Component
public class SFChronicle extends Market {

	private static final Logger logger = LoggerFactory.getLogger(SFChronicle.class);

	public SFChronicle() {
		super("SFChronicle");
	}

	@Autowired
	Environment environment;

	@Autowired
	MarketUtil marketUtil;

	@Autowired
	AdminDao adminDao;

	@Autowired
	RestTemplate restTemplate;

	@Override
	public Response getSubscriptionPayload(JSONObject params) {
		Meta meta = null;
		String subscriptionPayload = null;
		try {

			String publisherUserId = params.getString("externalId");
			String accountLinkingToken = params.getString("accountLinkingToken");
			String subscriptionTrackingToken = params.getString("subscriptionTrackingToken");
			String redirectUri = params.getString("redirectUri");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
			requestBody.add("grant_type", environment.getRequiredProperty("sfchronicle.token.grantType"));
			requestBody.add("username", environment.getRequiredProperty("sfchronicle.token.username"));
			requestBody.add("password", environment.getRequiredProperty("sfchronicle.token.password"));

			HttpEntity<MultiValueMap<String, String>> tokenRequestEntity = new HttpEntity<MultiValueMap<String, String>>(
					requestBody, headers);

			ResponseEntity<String> accessTokenResponse = restTemplate.exchange(
					environment.getRequiredProperty("sfchronicle.token.url"), HttpMethod.POST, tokenRequestEntity,
					String.class);

			String accessToken = new JSONObject(accessTokenResponse.getBody()).getString("access_token");

			logger.info(accessToken);

			headers.set("Authorization", "Bearer " + accessToken);

			HttpEntity<MultiValueMap<String, String>> subscriptionRequestEntity = new HttpEntity<MultiValueMap<String, String>>(
					null, headers);

			ResponseEntity<String> subscriptionResponse = restTemplate.exchange(
					environment.getRequiredProperty("sfchronicle.subscription.url"), HttpMethod.GET,
					subscriptionRequestEntity, String.class, publisherUserId);

			JSONObject subscriptionJson = new JSONObject(subscriptionResponse.getBody());

			JSONArray subscriptions = subscriptionJson.getJSONArray("Subscriptions");

			JSONObject subscription = null;
			for (int i = 0; i < subscriptions.length(); i++) {
				subscription = subscriptions.getJSONObject(i);
				if (subscription.getBoolean("Active")) {
					break;
				}
			}
			
			Long accountId = subscriptionJson.getLong("UserId");
			Long subscriptionStatus = subscription.getBoolean("Active") ? 1L : 0L;
			String expirationDate = subscription.getString("ExpirationDateTime");
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date date = df.parse(expirationDate);

			SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
			df1.setTimeZone(TimeZone.getTimeZone("UTC"));

			String expiryTime = df1.format(date);
			
			String facebookAppSecret = environment.getRequiredProperty("sfchronicle.facebook.app.secret");

			subscriptionPayload = marketUtil.constructPayload(expiryTime, accountLinkingToken, subscriptionStatus,
					publisherUserId, facebookAppSecret);

			SubscriptionAccess subscriptionAccess = adminDao.uniqueResult(
					"from SubscriptionAccess where subscriptionTrackingToken = ?", SubscriptionAccess.class,
					subscriptionTrackingToken);

			if (null == subscriptionAccess) {
				subscriptionAccess = new SubscriptionAccess();
				subscriptionAccess.setSubscriptionTrackingToken(UUID.randomUUID().toString());
				subscriptionAccess.setSubscribeMarket(
						environment.getRequiredProperty("sfchronicle.subscriptionAccess.subscribeMarket"));
			}
			subscriptionAccess.setFbRedirectURI(redirectUri);
			subscriptionAccess.setFbLinkingToken(accountLinkingToken);

			subscriptionAccess.setSubscriberId(publisherUserId);
			subscriptionAccess.setSubscriptionStatus(subscriptionStatus.toString());
			subscriptionAccess.setAccountId(accountId.toString());
			subscriptionAccess.setSubscriptionExpiryDate(date);

			adminDao.save(subscriptionAccess);

			meta = new Meta();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getMessage(e));
			meta = new Meta(500, ExceptionUtils.getMessage(e).split(":")[0], ExceptionUtils.getMessage(e));
		}
		Response response = new Response(subscriptionPayload, meta);
		return response;
	}

}