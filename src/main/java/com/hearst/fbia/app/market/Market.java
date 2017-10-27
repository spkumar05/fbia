package com.hearst.fbia.app.market;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.hearst.fbia.app.model.Response;

@Component
public abstract class Market {

	String marketerName;

	public Market(String marketerName) {
		this.marketerName = marketerName;
	}

	public abstract Response getSubscriptionPayload(JSONObject params);

	@PostConstruct
	protected void postContruction() {
		MarketRegistry.registerMarket(marketerName, this);
	}

}