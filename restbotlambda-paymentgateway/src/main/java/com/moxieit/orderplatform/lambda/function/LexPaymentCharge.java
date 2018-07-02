package com.moxieit.orderplatform.lambda.function;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.SetSMSAttributesRequest;
import com.google.gson.Gson;
import com.moxieit.orderplatform.helper.HttpCallHelper;
import com.moxieit.orderplatform.lambda.DB.DBService;
import com.moxieit.orderplatform.lambda.function.SNSMobilePush.Platform;
import com.moxieit.orderplatform.lambda.request.PushSNSRequest;
import com.moxieit.orderplatform.lambda.request.SesRequest;
import com.moxieit.orderplatform.lambda.response.Attachment;
import com.moxieit.orderplatform.lambda.response.DialogActionResponseForElicitIntent;
import com.moxieit.orderplatform.lambda.response.Elements;
import com.moxieit.orderplatform.lambda.response.LexResponse;
import com.moxieit.orderplatform.lambda.response.Message;
import com.moxieit.orderplatform.lambda.response.Payload;
import com.moxieit.orderplatform.lambda.response.Recipient;
import com.moxieit.orderplatform.lambda.response.Slots;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.InvoiceItem;

public class LexPaymentCharge implements RequestHandler<Object, String> {

	@Override
        public String handleRequest(Object input, Context context) {
		// TODO Auto-generated method stub
		System.out.println("Input" + input);
		context.getLogger().log("Input:" + input);
		// String readFileToString=null;
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderTable = dynamoDB.getTable("Order");
		Map<String, Map<String, Map>> map = new HashMap<String, Map<String, Map>>();
		map = (Map<String, Map<String, Map>>) input;
		String orderuuid = (String) map.get("params").get("header").get("Referer");
		System.out.println("orderuuid :" + orderuuid);
		URL url = null;
		String ordertableuuid = null;
		try {
			url = new URL(orderuuid);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Map<String, String> map1 = splitQuery(url);
			ordertableuuid = map1.get("order");
			System.out.println("ordertableuuid :" + ordertableuuid);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String stripeToken = (String) map.get("params").get("querystring").get("stripeToken");
		System.out.println("stripeToken :" + stripeToken);
		String stripeEmail = (String) map.get("params").get("querystring").get("stripeEmail");
		System.out.println("stripeEmail :" + stripeEmail);
		UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", ordertableuuid)
				.withUpdateExpression("set creationDate = :val,emailId = :id")
				.withValueMap(new ValueMap().withNumber(":val", System.currentTimeMillis()).withString(":id", stripeEmail));
		orderTable.updateItem(updateItemSpec);

		Item orderItem = orderTable.getItem("uuid", ordertableuuid);
		String userId = (String) orderItem.get("userId");
		String 	botName = orderItem.getString("botName");
		String pageAccessToken = orderItem.getString("pageAccessToken");
		Number totalBill = (Number) orderItem.get("totalBillWithTax");
		Double totalBillDouble = totalBill.doubleValue() * 100;
		Integer totalBillInteger = totalBillDouble.intValue();
		String totalBillString = totalBillInteger.toString();
		Stripe.apiKey = "sk_test_h8l3zo52N4fKamsxtuTBQ0lH";
		 String customerToken = "";
		// Create a Customer:
		Map<String, Object> customerParams = new HashMap<String, Object>();
		customerParams.put("email", stripeEmail);
		customerParams.put("source", stripeToken);
		try {
			Customer customer = Customer.create(customerParams);
			customerToken = customer.getId();
			System.out.println("customerId" +customerToken);
			
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException
				| APIException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("amount", totalBillString);
		chargeParams.put("currency", "usd");
		chargeParams.put("description", "Charge for " + stripeEmail);
		chargeParams.put("source", stripeToken);
		// ^ obtained with Stripe.js
		try {
			//Charge.create(chargeParams);
			/*Table BankAccountDetailsTable = dynamoDB.getTable("BankAccountDetails");
			Item customerItem = BankAccountDetailsTable.getItem("botName", botName);			
			String customerId = customerItem.getString("customerId");
			Stripe.apiKey = "sk_test_h8l3zo52N4fKamsxtuTBQ0lH"; 
			Map<String, Object> params1 = new HashMap<String, Object>(); 
			params1.put("customer", customerId); 
			params1.put("amount", totalBillString); 
			params1.put("currency", "usd");
			params1.put("description", "One-time setup fee"); 
			try {
				InvoiceItem.create(params1);
			} catch (Exception e)
			{
				System.out.println(e);
			}*/

			/*SESorderitems sesorderitems = new SESorderitems();
			SesRequest sesRequest = new SesRequest();
			sesRequest.setOrdertableuuid(ordertableuuid);
			sesRequest.setStripeEmail(stripeEmail);
			sesorderitems.getSES(sesRequest);*/
			
			String phoneNumber = (orderItem.get("phoneNumber")).toString();
		
			Table restaurantTable = dynamoDB.getTable("Restaurant");
			Item botnameitem = restaurantTable.getItem("botName", botName);
			String deviceToken = botnameitem.getString("deviceToken");
			if (deviceToken != null) {
				PushSNSRequest pushSNSRequest = new PushSNSRequest();						
				pushSNSRequest.setDeviceToken(deviceToken);	
				pushSNSRequest.setPhoneNumber(phoneNumber);
				SNSMobilePush sns = new SNSMobilePush(); 			      
				  sns.pushSNS(pushSNSRequest, context); 
			}
			  
			AmazonSNSClient snsClient = new AmazonSNSClient();			
			String restaurantName = botnameitem.getString("restaurantName");
			String restaurantphn = botnameitem.getString("phone_no");
	        String snsmessage = "Your Order was placed successfully. You will get a confirmation message/email once your order is confirmed by "+restaurantName
	        		+ ".If you want to make any changes to this order contact "+restaurantName+" – "
	        		+ restaurantphn;
	       //String snsmessage1 = restaurantName+" | Online Receipt\n"+ "https://vhnnifn9o0.execute-api.us-east-1.amazonaws.com/Lexpayment/receipt?order="+ordertableuuid;
	      // String phoneNumber = "+918465865203";
	     /*   Map<String, MessageAttributeValue> smsAttributes = 
	                new HashMap<String, MessageAttributeValue>();
	     
	     SetSMSAttributesRequest setRequest = new SetSMSAttributesRequest()
	    			.addAttributesEntry("DefaultSenderID", restaurantName)
	    			.addAttributesEntry("MonthlySpendLimit", "5")	
	    			.addAttributesEntry("DefaultSMSType", "Transactional");
	    		.addAttributesEntry("DeliveryStatusSuccessSamplingRate", "10")	    			
	    			.addAttributesEntry("UsageReportS3Bucket", "sns-sms-daily-usage")
	       snsClient.setSMSAttributes(setRequest);	     */   
	        AmazonSNS.sendSMSMessage(snsClient, snsmessage, phoneNumber);
	       
			
			UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", ordertableuuid)
					.withUpdateExpression("set orderStatus = :val,emailId= :emi, customerToken =:cus")
					.withValueMap(new ValueMap().withString(":val", "ACCEPTED").withString(":emi", stripeEmail)
							.withString(":cus", customerToken));
			UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
			outcome2.getItem();
			
			// AmazonSNS.sendSMSMessage(snsClient, snsmessage1, phoneNumber, smsAttributes);
			LexResponse lexResponse = new LexResponse();		
			Recipient recipient = new Recipient();
			recipient.setId(userId);
			Message message = new Message();
			Attachment attachment = new Attachment();
			attachment.setType("template");
			Payload payload = new Payload();
			payload.setTemplate_type("generic");
			List<Elements> elements = new ArrayList<Elements>();
			Elements elements1 = new Elements();
			elements1.setTitle("Your order has Been Placed");
			elements1.setSubtitle(".");
			elements.add(elements1);
			Elements[] attachments = new Elements[elements.size()];
			attachments = elements.toArray(attachments);
			payload.setElements(attachments);
			attachment.setPayload(payload);
			message.setAttachment(attachment);
			lexResponse.setMessage(message);
			lexResponse.setRecipient(recipient);
			Gson gson = new Gson();
			String json = gson.toJson(lexResponse);
			System.out.println(json);
			Object json1 = gson.fromJson(json, Object.class);			
			Map<String, Map<String, String>> map1 = new HashMap<String, Map<String, String>>();			
			map1 = (Map<String, Map<String, String>>) json1;			
			String recipientId = (String) map1.get("recipient").get("id");
			System.out.println(recipientId);				
			if (recipientId.contains("us-east-1")) {
				System.out.println("Payment is Successfull. Your Order was placed.");	
				/*Message message1 = new Message();
				message1.setContentType("PlainText");
				message1.setContent("Your Order was placed. If you want to make any changes to this order contact Sitara Indian Cuisine –"+restaurantphn);
				Slots slots = new Slots();
				slots.setSlotName("Hours");			
				dialogAction.setType("ElicitSlot");
				dialogAction.setIntentName("GetStarted");
				dialogAction.setSlots(slots);
				dialogAction.setSlotToElicit("slotName");
				dialogAction.setMessage(message1);			
				lexResponse.setDialogAction(dialogAction);*/
				
			
		}
			else {
				HttpCallHelper httpCallHelper = new HttpCallHelper();
				Map<String, String> headers = new HashMap<>();
				headers.put("Content-Type", "application/json");
				Map<String, String> params = new HashMap<>();
				params.put("access_token", pageAccessToken);
				httpCallHelper.doPostCall(headers, "https://graph.facebook.com/v2.6/me/messages", params, json);
				ClassLoader classLoader = getClass().getClassLoader();
				File file = new File(classLoader.getResource("file/RedirectFB.html").getFile());
				try {
					String readFileToString = FileUtils.readFileToString(file);
					return readFileToString;
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			String html = "<!doctype html><html><body style=\"background-color:#ebe8e4;\"><div style = \"margin: auto;width: 30%; border:1px solid #4caf50;padding:10px;background-color:#fff;\"><div style=\"text-align:center;\">"
					+ "	<img src=\"http://www.anthonytours.com/imgs/ok.png\" width=\"120\" height=\"120\" style=\"float:left;\"><h1 style=\"padding-top:15px;color:#4caf50;\">Successfull</h1>"
						+ "<h2>Your order was Placed</h2></div>	</div></body></html>";
			return html;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String errorhtml = "<!doctype html><html><body style=\"background-color:#ebe8e4;\">	<div style = \"margin: auto;width: 30%; border:2px solid #ee2b46;padding:10px;background-color:#fff;\">"
				+ "		<div style=\"text-align:center;\">	<img src=\"http://www.deluxevectors.com/images/vector_images/thumb/red-green-ok-not-ok-icons-1\" width=\"150\" height=\"150\" style=\"float:left;\">"
				+ "			<h1 style=\"padding-top:15px;color:#ee2b46;\">Payment is not Successfull</h1><h2>Your order was Cancelled</h2>	</div>	</div></body></html></html>";
		return errorhtml;
	}

	public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		String query = url.getQuery();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
					URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}
		return query_pairs;
	
}
}