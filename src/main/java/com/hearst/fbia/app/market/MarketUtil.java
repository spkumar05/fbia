package com.hearst.fbia.app.market;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hearst.fbia.frm.service.dao.AdminDao;

@Component
public class MarketUtil {

	private static final Logger logger = LoggerFactory.getLogger(MarketUtil.class);

	@Autowired
	AdminDao adminDao;

	public String getStringFromSoapMessage(SOAPMessage soapMessage) throws SOAPException, IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		soapMessage.writeTo(byteArrayOutputStream);
		return byteArrayOutputStream.toString();
	}

	public String constructPayload(String expiryTime, String accountLinkingToken, Long subscriptionStatus,
			String publisherUserId, String facebookAppSecret)
			throws ParseException, NoSuchAlgorithmException, InvalidKeyException {

		String subscriptionPayload = null;

		String facebookUserId = null;

		JSONArray accountLinkingJson = new JSONArray();
		accountLinkingJson.put(accountLinkingToken); // Account Linking Token
		accountLinkingJson.put(subscriptionStatus == 1 ? 1 : 0);
		// Subscription Status - 1/0
		accountLinkingJson.put(expiryTime); // Expiry Time
		accountLinkingJson.put(publisherUserId); // Publisher User ID
		accountLinkingJson.put(facebookUserId); // Facebook User ID

		String accountLinking = accountLinkingJson.toString();
		logger.info("accountLinking : {}", accountLinking);

		String payload = Base64.encodeBase64String(accountLinking.getBytes());
		logger.info("payload : {}", payload);

		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(facebookAppSecret.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key);

		String payloadSignature = Base64.encodeBase64String(sha256_HMAC.doFinal(payload.getBytes()));
		logger.info("payloadSignature : {}", payloadSignature);

		subscriptionPayload = payload + "." + payloadSignature;
		logger.info("subscriptionPayload : {}", subscriptionPayload);

		return subscriptionPayload;
	}

}