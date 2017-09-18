package com.hearst.fbia.app.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SUBSCRIPTION_ACCESS")
public class SubscriptionAccess extends BaseEntity {

	@Id
	@Column(name = "SUBSCRIPTION_TRACKING_TOKEN", unique = true, nullable = false, length = 100)
	private String subscriptionTrackingToken;

	@Column(name = "FB_REDIRECT_URI", nullable = false, length = 300)
	private String fbRedirectURI;

	@Column(name = "FB_LINKING_TOKEN", length = 100)
	private String fbLinkingToken;

	@Column(name = "SUBSCRIBE_MARKET", length = 50)
	private String subscribeMarket;

	@Column(name = "SUBSCRIBER_ID", length = 100)
	private String subscriberId;

	@Column(name = "SUBSCRIPTION_STATUS", length = 200)
	private String subscriptionStatus;

	@Column(name = "ACCOUNT_ID", length = 100)
	private String accountId;

	@Column(name = "SUBSCRIPTION_EXPIRY_DATE")
	private Date subscriptionExpiryDate;

	@Column(name = "EXPIRE_DATE_BATCH_UPD")
	private Date expireDateBatchUpd;

	@Column(name = "BATCH_UPDATED_DATE")
	private Date batchUpdatedDate;

	@Column(name = "UPDATED_BY", length = 30)
	private String batchUpdatedBy;

	public String getSubscriptionTrackingToken() {
		return subscriptionTrackingToken;
	}

	public void setSubscriptionTrackingToken(String subscriptionTrackingToken) {
		this.subscriptionTrackingToken = subscriptionTrackingToken;
	}

	public String getFbRedirectURI() {
		return fbRedirectURI;
	}

	public void setFbRedirectURI(String fbRedirectURI) {
		this.fbRedirectURI = fbRedirectURI;
	}

	public String getFbLinkingToken() {
		return fbLinkingToken;
	}

	public void setFbLinkingToken(String fbLinkingToken) {
		this.fbLinkingToken = fbLinkingToken;
	}

	public String getSubscribeMarket() {
		return subscribeMarket;
	}

	public void setSubscribeMarket(String subscribeMarket) {
		this.subscribeMarket = subscribeMarket;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getSubscriptionStatus() {
		return subscriptionStatus;
	}

	public void setSubscriptionStatus(String subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public Date getSubscriptionExpiryDate() {
		return subscriptionExpiryDate;
	}

	public void setSubscriptionExpiryDate(Date subscriptionExpiryDate) {
		this.subscriptionExpiryDate = subscriptionExpiryDate;
	}

	public Date getExpireDateBatchUpd() {
		return expireDateBatchUpd;
	}

	public void setExpireDateBatchUpd(Date expireDateBatchUpd) {
		this.expireDateBatchUpd = expireDateBatchUpd;
	}

	public Date getBatchUpdatedDate() {
		return batchUpdatedDate;
	}

	public void setBatchUpdatedDate(Date batchUpdatedDate) {
		this.batchUpdatedDate = batchUpdatedDate;
	}

	public String getBatchUpdatedBy() {
		return batchUpdatedBy;
	}

	public void setBatchUpdatedBy(String batchUpdatedBy) {
		this.batchUpdatedBy = batchUpdatedBy;
	}

}