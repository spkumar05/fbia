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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hearst.fbia.app.domain.SubscriptionAccess;
import com.hearst.fbia.app.model.Meta;
import com.hearst.fbia.app.model.Response;
import com.hearst.fbia.frm.service.dao.AdminDao;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

	@Value("${soap.request.sourceSystem}")
	private String sourceSystem;

	@Value("${soap.request.authenticationToken}")
	private String authenticationToken;

	@Value("${soap.request.url}")
	private String endpoint;

	@Value("${facebook.app.secret}")
	private String appSecret;

	@Value("${subscriptionAccess.subscribeMarket}")
	private String subscribeMarket;

	@Autowired
	AdminDao adminDao;

	@Override
	public AdminDao getAdminDao() {
		return adminDao;
	}

	@Transactional
	@Override
	public Response getSubscriptionPayload(String edbid, String accountLinkingToken) {

		Meta meta = null;
		String subscriptionPayload = null;
		try {
			SOAPMessage soapRequest = constuctSoapRequest(edbid);
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			SOAPMessage soapResponse = soapConnection.call(soapRequest, endpoint);

			String soapResponseString = getStringFromSoapMessage(soapResponse);
			logger.info("Soap Response : {}", soapResponseString);

			subscriptionPayload = processSoapResponse(soapResponseString, accountLinkingToken, accountLinkingToken,
					edbid);
			soapConnection.close();
			meta = new Meta();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getMessage(e));
			meta = new Meta(500, ExceptionUtils.getMessage(e).split(":")[0], ExceptionUtils.getMessage(e));
		}
		Response response = new Response(subscriptionPayload, meta);

		return response;

	}

	private String processSoapResponse(String soapResponse, String redirectUri, String accountLinkingToken,
			String edbid) throws ParseException, NoSuchAlgorithmException, InvalidKeyException {

		String subscriptionPayload = null;

		JSONObject xmlJSONObject = XML.toJSONObject(soapResponse);
		JSONObject getSubscriptionsByMasterIdResult = xmlJSONObject.getJSONObject("soap:Envelope")
				.getJSONObject("soap:Body").getJSONObject("GetSubscriptionsByMasterIdResponse")
				.getJSONObject("GetSubscriptionsByMasterIdResult");

		boolean success = getSubscriptionsByMasterIdResult.getBoolean("Success");
		if (success) {

			Long accountId = getSubscriptionsByMasterIdResult.getJSONObject("Subscriptions")
					.getJSONObject("Subscription").getLong("AccountId");

			Long subscriptionLevelHousehold = getSubscriptionsByMasterIdResult.getJSONObject("Subscriptions")
					.getJSONObject("Subscription").getLong("SubscriptionLevelHousehold");

			String expirationDate = getSubscriptionsByMasterIdResult.getJSONObject("Subscriptions")
					.getJSONObject("Subscription").getString("ExpirationDate");

			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
			Date date = df.parse(expirationDate);

			SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
			df1.setTimeZone(TimeZone.getTimeZone("UTC"));

			String isoDate = df1.format(date);

			String facebookUserId = null;

			JSONArray accountLinkingJson = new JSONArray();
			accountLinkingJson.put(accountLinkingToken); // Account Linking Token
			accountLinkingJson.put(subscriptionLevelHousehold); // Subscription Status - 1/0
			accountLinkingJson.put(isoDate); // Expiry Time
			accountLinkingJson.put(edbid); // Publisher User ID
			accountLinkingJson.put(facebookUserId); // Facebook User ID

			String accountLinking = accountLinkingJson.toString();
			logger.info("accountLinking : {}", accountLinking);

			String payload = Base64.encodeBase64String(accountLinking.getBytes());
			logger.info("payload : {}", payload);

			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(appSecret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);

			String payloadSignature = Base64.encodeBase64String(sha256_HMAC.doFinal(payload.getBytes()));
			logger.info("payloadSignature : {}", payloadSignature);

			subscriptionPayload = payload + "." + payloadSignature;

			SubscriptionAccess subscriptionAccess = new SubscriptionAccess();
			subscriptionAccess.setSubscriptionTrackingToken(UUID.randomUUID().toString());
			subscriptionAccess.setFbRedirectURI(redirectUri);
			subscriptionAccess.setFbLinkingToken(accountLinkingToken);
			subscriptionAccess.setSubscribeMarket(subscribeMarket);
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
		authenticationTokenElement.addTextNode(authenticationToken);

		Name sourceSystemName = soapFactory.createName("SourceSystem", "tem", "http://tempuri.org/");
		SOAPElement sourceSystemElement = getSubscriptionsByMasterIdElement.addChildElement(sourceSystemName);
		sourceSystemElement.addTextNode(sourceSystem);

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