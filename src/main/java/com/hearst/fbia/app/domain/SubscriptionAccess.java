package com.hearst.fbia.app.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "subscription_access")
public class SubscriptionAccess extends BaseEntity {

	@Id
	@Column(name = "subscription_tracking_token", unique = true, nullable = false, length = 100)
	private String subscriptionTrackingToken;

	@Column(name = "fb_redirect_uri", nullable = false, length = 1024)
	private String fbRedirectURI;

	@Column(name = "fb_linking_token", length = 1024)
	private String fbLinkingToken;

	@Column(name = "subscribe_market", length = 50)
	private String subscribeMarket;

	@Column(name = "subscriber_id", length = 100)
	private String subscriberId;

	@Column(name = "subscription_status", length = 200)
	private String subscriptionStatus;

	@Column(name = "account_id", length = 100)
	private String accountId;

	@Column(name = "subscription_expiry_date")
	private Date subscriptionExpiryDate;

	@Column(name = "expire_date_batch_upd")
	private Date expireDateBatchUpd;

	@Column(name = "batch_updated_date")
	private Date batchUpdatedDate;

	@Column(name = "batch_updated_by", length = 30)
	private String batchUpdatedBy;

	@Column(name = "access_type", length = 30)
	private String accessType;

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

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

}