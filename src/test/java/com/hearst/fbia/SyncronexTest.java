package com.hearst.fbia;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class SyncronexTest {

	public static void main(String[] args) {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
		requestBody.add("grant_type", "password");
		requestBody.add("username", "api@syncronex.com");
		requestBody.add("password", "robcom88");

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(requestBody,
				headers);

		ResponseEntity<String> accessTokenResponse = restTemplate.exchange(
				"https://syncaccess-hst-sfc.stage.syncronex.com/appservices/token", HttpMethod.POST, entity,
				String.class);

		JSONObject accessTokenJson = new JSONObject(accessTokenResponse.getBody());

		String accessToken = accessTokenJson.getString("access_token");

		System.out.println(accessToken);

		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity<MultiValueMap<String, String>>(null,
				headers);

		ResponseEntity<String> subscriptionResponsee = restTemplate.exchange(
				"https://syncaccess-hst-sfc.stage.syncronex.com/appservices/api/v1/subscriber?externalid=31903147A1BAB071E61ED069248FF0A1&provider=treg",
				HttpMethod.GET, entity1, String.class);

		JSONObject subscriptionJson = new JSONObject(subscriptionResponsee.getBody());
		System.out.println(subscriptionJson);
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

		System.out.println(accountId);
		System.out.println(subscriptionStatus);
		System.out.println(expirationDate);

	}
}