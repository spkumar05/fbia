package com.hearst.fbia.app.service;

import com.hearst.fbia.app.model.Response;

public interface SubscriptionService {

	Response getSubscriptionPayload(String edbid, String accountLinkingToken);

}