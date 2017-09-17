package com.hearst.fbia;

import java.util.ArrayList;
import java.util.List;

public class GetSubscriptionsByMasterIdResult {

	private boolean success;
	List<Subscription> subscriptions = new ArrayList<Subscription>();

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

}