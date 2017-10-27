package com.hearst.fbia.app.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
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
import com.hearst.fbia.app.model.Meta;
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

	@Transactional
	// @Override
	public Response getSubscriptionPayloadOld(String subscription) {
		JSONObject jsonObject = new JSONObject(subscription);
		Meta meta = null;
		String subscriptionPayload = null;
		try {
			SOAPMessage soapRequest = constuctSoapRequest(jsonObject.getString("edbid"));
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			SOAPMessage soapResponse = soapConnection.call(soapRequest,
					environment.getRequiredProperty("houston.soap.request.url"));

			String soapResponseString = getStringFromSoapMessage(soapResponse);
			logger.info("Soap Response : {}", soapResponseString);

			subscriptionPayload = processSoapResponse(soapResponseString, jsonObject.getString("redirectUri"),
					jsonObject.getString("accountLinkingToken"), jsonObject.getString("edbid"),
					jsonObject.getString("subscriptionTrackingToken"));
			soapConnection.close();
			meta = new Meta();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getMessage(e));
			meta = new Meta(500, ExceptionUtils.getMessage(e).split(":")[0], ExceptionUtils.getMessage(e));
		}
		Response response = new Response(subscriptionPayload, meta);
		return response;
	}

	@Override
	public String getCurrentMarket() {
		String market = null;
		String domain = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getHost();

		if (domain.contains("houston")) {
			market = environment.getRequiredProperty("houston.subscriptionAccess.subscribeMarket");
		} else if (domain.contains("sfchronicle")) {
			market = environment.getRequiredProperty("sfchronicle.subscriptionAccess.subscribeMarket");
		} else {
			market = "localhost";
		}
		return market;
	}

	private String processSoapResponse(String soapResponse, String redirectUri, String accountLinkingToken,
			String edbid, String subscriptionTrackingToken)
			throws ParseException, NoSuchAlgorithmException, InvalidKeyException {

		String subscriptionPayload = null;

		JSONObject xmlJSONObject = XML.toJSONObject(soapResponse);
		JSONObject getSubscriptionsByMasterIdResult = xmlJSONObject.getJSONObject("soap:Envelope")
				.getJSONObject("soap:Body").getJSONObject("GetSubscriptionsByMasterIdResponse")
				.getJSONObject("GetSubscriptionsByMasterIdResult");

		boolean success = getSubscriptionsByMasterIdResult.getBoolean("Success");
		if (success) {
			Object subscriptionCheck = getSubscriptionsByMasterIdResult.getJSONObject("Subscriptions")
					.get("Subscription");
			JSONObject subscription = null;
			if (subscriptionCheck instanceof JSONArray) {
				JSONArray subscriptionArray = (JSONArray) subscriptionCheck;
				for (int i = 0; i < subscriptionArray.length(); i++) {
					subscription = subscriptionArray.getJSONObject(i);
					if (subscription.getLong("SubscriptionLevel") == 1) {
						break;
					}
				}
			} else if (subscriptionCheck instanceof JSONObject) {
				subscription = (JSONObject) subscriptionCheck;
			}

			Long accountId = subscription.getLong("AccountId");
			Long subscriptionLevelHousehold = subscription.getLong("SubscriptionLevelHousehold");
			String expirationDate = subscription.getString("ExpirationDate");

			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
			Date date = df.parse(expirationDate);
			
			SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
			df1.setTimeZone(TimeZone.getTimeZone("UTC"));

			String isoDate = df1.format(date);

			String facebookUserId = null;

			JSONArray accountLinkingJson = new JSONArray();
			accountLinkingJson.put(accountLinkingToken); // Account Linking Token
			accountLinkingJson.put(subscriptionLevelHousehold == 1 ? 1 : 0); // Subscription Status - 1/0
			accountLinkingJson.put(isoDate); // Expiry Time
			accountLinkingJson.put(edbid); // Publisher User ID
			accountLinkingJson.put(facebookUserId); // Facebook User ID

			String accountLinking = accountLinkingJson.toString();
			logger.info("accountLinking : {}", accountLinking);

			String payload = Base64.encodeBase64String(accountLinking.getBytes());
			logger.info("payload : {}", payload);

			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(
					environment.getRequiredProperty("houston.facebook.app.secret").getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);

			String payloadSignature = Base64.encodeBase64String(sha256_HMAC.doFinal(payload.getBytes()));
			logger.info("payloadSignature : {}", payloadSignature);

			subscriptionPayload = payload + "." + payloadSignature;

			SubscriptionAccess subscriptionAccess = adminDao.uniqueResult(
					"from SubscriptionAccess where subscriptionTrackingToken = ?", SubscriptionAccess.class,
					subscriptionTrackingToken);

			if (null == subscriptionAccess) {
				subscriptionAccess = new SubscriptionAccess();
				subscriptionAccess.setSubscriptionTrackingToken(UUID.randomUUID().toString());
				subscriptionAccess.setSubscribeMarket(
						environment.getRequiredProperty("houston.subscriptionAccess.subscribeMarket"));
			}
			subscriptionAccess.setFbRedirectURI(redirectUri);
			subscriptionAccess.setFbLinkingToken(accountLinkingToken);

			subscriptionAccess.setSubscriberId(edbid);
			subscriptionAccess.setSubscriptionStatus(subscriptionLevelHousehold.toString());
			subscriptionAccess.setAccountId(accountId.toString());
			subscriptionAccess.setSubscriptionExpiryDate(date);

			adminDao.save(subscriptionAccess);

			logger.info("subscriptionPayload : {}", subscriptionPayload);
		}

		return subscriptionPayload;
	}

	private SOAPMessage constuctSoapRequest(String edbid) throws SOAPException, IOException {
		MessageFactory factory = MessageFactory.newInstance();
		SOAPMessage soapMessage = factory.createMessage();
		SOAPPart part = soapMessage.getSOAPPart();
		SOAPEnvelope envelope = part.getEnvelope();
		envelope.setPrefix("soap");
		envelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
		envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");
		envelope.removeNamespaceDeclaration("SOAP-ENV");

		SOAPHeader header = envelope.getHeader();
		header.setPrefix("soap");
		envelope.removeNamespaceDeclaration("SOAP-ENV");

		SOAPBody body = envelope.getBody();
		body.setPrefix("soap");
		envelope.removeNamespaceDeclaration("SOAP-ENV");

		SOAPFactory soapFactory = SOAPFactory.newInstance();

		Name getSubscriptionsByMasterId = soapFactory.createName("GetSubscriptionsByMasterId", "tem",
				"http://tempuri.org/");
		SOAPBodyElement getSubscriptionsByMasterIdElement = body.addBodyElement(getSubscriptionsByMasterId);

		Name masterId = soapFactory.createName("MasterId", "tem", "http://tempuri.org/");
		SOAPElement masterIdElement = getSubscriptionsByMasterIdElement.addChildElement(masterId);
		masterIdElement.addTextNode(edbid);

		Name authenticationTokenName = soapFactory.createName("AuthenticationToken", "tem", "http://tempuri.org/");
		SOAPElement authenticationTokenElement = getSubscriptionsByMasterIdElement
				.addChildElement(authenticationTokenName);
		authenticationTokenElement
				.addTextNode(environment.getRequiredProperty("houston.soap.request.authenticationToken"));

		Name sourceSystemName = soapFactory.createName("SourceSystem", "tem", "http://tempuri.org/");
		SOAPElement sourceSystemElement = getSubscriptionsByMasterIdElement.addChildElement(sourceSystemName);
		sourceSystemElement.addTextNode(environment.getRequiredProperty("houston.soap.request.sourceSystem"));

		String soapRequestString = getStringFromSoapMessage(soapMessage);
		logger.info("Soap Request : {}", soapRequestString);

		return soapMessage;
	}

	private String getStringFromSoapMessage(SOAPMessage soapMessage) throws SOAPException, IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		soapMessage.writeTo(byteArrayOutputStream);
		String soapResponseString = byteArrayOutputStream.toString();
		return soapResponseString;
	}

}