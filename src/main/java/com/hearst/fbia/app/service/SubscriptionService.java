package com.hearst.fbia.app.service;

import com.hearst.fbia.app.domain.SubscriptionAccess;
import com.hearst.fbia.app.model.Response;
import com.hearst.fbia.frm.service.dao.AdminDao;

public interface SubscriptionService {

	Response getSubscriptionPayload(String subscription);

	AdminDao getAdminDao();

	String saveRequestInfo(String account_linking_token, String redirect_uri, String accessType);

	SubscriptionAccess getRequestInfo(String subscriptionTrackingToken);

	String getCurrentMarket();

	String getRedirectUrl();

	String convertMsToTime(long miliSeconds);

}