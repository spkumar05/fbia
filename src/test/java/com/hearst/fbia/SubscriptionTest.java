package com.hearst.fbia;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class SubscriptionTest {
	public static void main(String[] args) {

		try {
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
			masterIdElement.addTextNode("405204");

			Name authenticationToken = soapFactory.createName("AuthenticationToken", "tem", "http://tempuri.org/");
			SOAPElement authenticationTokenElement = getSubscriptionsByMasterIdElement
					.addChildElement(authenticationToken);
			authenticationTokenElement.addTextNode("BB54B608-C0FE-4443-96BE-C34D2DDB45BF");

			Name sourceSystem = soapFactory.createName("SourceSystem", "tem", "http://tempuri.org/");
			SOAPElement sourceSystemElement = getSubscriptionsByMasterIdElement.addChildElement(sourceSystem);
			sourceSystemElement.addTextNode("SITE");

			String soapRequest = getStringFromSoapMessage(soapMessage);

			System.out.println(soapRequest);

			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			SOAPMessage response = soapConnection.call(soapMessage,
					"https://hearstadmin.solicitor-concierge.com/houston/Offers/SubscriberService.asmx");

			String soapResponseString = getStringFromSoapMessage(response);
			System.out.println(soapResponseString);

			JSONObject xmlJSONObj = XML.toJSONObject(soapResponseString);

			System.out.println(xmlJSONObj);

			JSONObject getSubscriptionsByMasterIdResult = xmlJSONObj.getJSONObject("soap:Envelope")
					.getJSONObject("soap:Body").getJSONObject("GetSubscriptionsByMasterIdResponse")
					.getJSONObject("GetSubscriptionsByMasterIdResult");

			System.out.println(getSubscriptionsByMasterIdResult);

			boolean success = getSubscriptionsByMasterIdResult.getBoolean("Success");
			System.out.println(success);
			if (success) {
				Object subscriptionCheck = getSubscriptionsByMasterIdResult.getJSONObject("Subscriptions")
						.get("Subscription");
				JSONObject subscription = null;
				if (subscriptionCheck instanceof JSONArray) {
					JSONArray subscriptionArray = (JSONArray) subscriptionCheck;
					System.out.println(subscriptionArray);
					for (int i = 0; i < subscriptionArray.length(); i++) {
						subscription = subscriptionArray.getJSONObject(i);
						if (subscription.getLong("SubscriptionLevel") == 2) {
							break;
						}
					}
				} else if (subscriptionCheck instanceof JSONObject) {
					subscription = (JSONObject) subscriptionCheck;
				}
				System.out.println(subscription);
			}

			/*
			 * ObjectMapper objectMapper = new ObjectMapper();
			 * 
			 * GetSubscriptionsByMasterIdResult getSubscriptionsByMasterIdResult
			 * = objectMapper
			 * .readValue(getSubscriptionsByMasterIdResponse.toString(),
			 * GetSubscriptionsByMasterIdResult.class);
			 * 
			 * System.out.println(getSubscriptionsByMasterIdResult);
			 */
			soapConnection.close();

		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String getStringFromSoapMessage(SOAPMessage soapMessage) throws SOAPException, IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		soapMessage.writeTo(byteArrayOutputStream);
		String soapResponseString = byteArrayOutputStream.toString();
		return soapResponseString;
	}
}