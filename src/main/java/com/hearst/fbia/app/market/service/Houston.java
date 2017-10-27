package com.hearst.fbia.app.market.service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.hearst.fbia.app.domain.SubscriptionAccess;
import com.hearst.fbia.app.market.Market;
import com.hearst.fbia.app.market.MarketUtil;
import com.hearst.fbia.app.model.Meta;
import com.hearst.fbia.app.model.Response;
import com.hearst.fbia.frm.service.dao.AdminDao;

@Component
public class Houston extends Market {

	private static final Logger logger = LoggerFactory.getLogger(Houston.class);

	public Houston() {
		super("Houston");
	}

	@Autowired
	Environment environment;

	@Autowired
	MarketUtil marketUtil;

	@Autowired
	AdminDao adminDao;

	@Override
	public Response getSubscriptionPayload(JSONObject params) {
		Meta meta = null;
		String subscriptionPayload = null;
		try {

			SOAPMessage soapRequest = constuctSoapRequest(params.getString("edbid"));
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			SOAPMessage soapResponse = soapConnection.call(soapRequest,
					environment.getRequiredProperty("houston.soap.request.url"));

			String soapResponseString = marketUtil.getStringFromSoapMessage(soapResponse);
			logger.info("Soap Response : {}", soapResponseString);

			subscriptionPayload = processSoapResponse(soapResponseString, params.getString("redirectUri"),
					params.getString("accountLinkingToken"), params.getString("edbid"),
					params.getString("subscriptionTrackingToken"));

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
			String publisherUserId, String subscriptionTrackingToken)
			throws ParseException, InvalidKeyException, NoSuchAlgorithmException {

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
			Long subscriptionStatus = subscription.getLong("SubscriptionLevelHousehold");
			String expirationDate = subscription.getString("ExpirationDate");

			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
			Date date = df.parse(expirationDate);

			SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
			df1.setTimeZone(TimeZone.getTimeZone("UTC"));

			String expiryTime = df1.format(date);

			String facebookAppSecret = environment.getRequiredProperty("houston.facebook.app.secret");

			subscriptionPayload = marketUtil.constructPayload(expiryTime, accountLinkingToken, subscriptionStatus,
					publisherUserId, facebookAppSecret);

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

			subscriptionAccess.setSubscriberId(publisherUserId);
			subscriptionAccess.setSubscriptionStatus(subscriptionStatus.toString());
			subscriptionAccess.setAccountId(accountId.toString());
			subscriptionAccess.setSubscriptionExpiryDate(date);

			adminDao.save(subscriptionAccess);
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

		String soapRequestString = marketUtil.getStringFromSoapMessage(soapMessage);
		logger.info("Soap Request : {}", soapRequestString);

		return soapMessage;
	}

}