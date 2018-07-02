package com.moxieit.orderplatform.lambda.function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SetSMSAttributesRequest;
import com.google.gson.Gson;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.HttpCallHelper;
import com.moxieit.orderplatform.function.service.api.LexFbStatusDto;
import com.moxieit.orderplatform.lambda.function.service.impl.SESorderitems;
import com.moxieit.orderplatform.lambda.request.SesRequest;
import com.moxieit.orderplatform.lambda.request.WebsiteorderstatusRequest;
import com.moxieit.orderplatform.lambda.response.Message;
import com.moxieit.orderplatform.lambda.response.Recipient;
import com.stripe.Stripe;
import com.stripe.model.Charge;

public class LexStatusUpdatingToFacebook implements RequestHandler<Object, String> {

	@Override
	public String handleRequest(Object o, Context context) {
		DynamoDB dynamoDB = DBService.getDBConnection();
		LambdaLogger log = context.getLogger();
		log.log("START:Dynamodbtrigger");

		Gson gson = new Gson();
		String json = gson.toJson(o);
		System.out.println(json);
		log.log(json);

		// Adding handle Trigger to map
		Map<String, List<Map<String, Map<String, Map<String, Map>>>>> map = new HashMap<String, List<Map<String, Map<String, Map<String, Map>>>>>();
		map = (Map<String, List<Map<String, Map<String, Map<String, Map>>>>>) o;

		try {
			// Getting userId and the orderStatus from trigger object

			String uuid = (String) map.get("Records").get(0).get("dynamodb").get("NewImage").get("uuid").get("S");
			log.log("uuid:" + uuid);
	
			Table orderTable = dynamoDB.getTable("Order");
			Item orderItem = orderTable.getItem("uuid", uuid);
			Number totalBill = (Number) orderItem.get("totalBillWithTax");
			Double totalBillDouble = totalBill.doubleValue() * 100;
			Integer totalBillInteger = totalBillDouble.intValue();
			String totalBillString = totalBillInteger.toString();
			/*String emailId = "";
			try{
			 emailId = (String) map.get("Records").get(0).get("dynamodb").get("NewImage").get("emailId").get("S");
			log.log("emailId:" + emailId);
			} catch (Exception e){
				log.log("Error in emailId" + e.getMessage());
			}*/
			
			String userId = (String) map.get("Records").get(0).get("dynamodb").get("NewImage").get("userId").get("S");			
			String orderFrom = (String) map.get("Records").get(0).get("dynamodb").get("NewImage").get("orderFrom").get("S");
			log.log("orderFrom:" + orderFrom);
			
			String customerToken = (String) map.get("Records").get(0).get("dynamodb").get("NewImage").get("customerToken").get("S");					
			String botName = (String) map.get("Records").get(0).get("dynamodb").get("NewImage")
					.get("botName").get("S");
			
			String phoneNumber = (String) map.get("Records").get(0).get("dynamodb").get("NewImage")
					.get("phoneNumber").get("S");
		
			String orderStatusNew = (String) map.get("Records").get(0).get("dynamodb").get("NewImage")
					.get("orderTracking").get("S");
			String orderStatusOld = (String) map.get("Records").get(0).get("dynamodb").get("OldImage")
					.get("orderTracking").get("S");
			String pageAccessToken = "";
			try{
			 pageAccessToken = (String) map.get("Records").get(0).get("dynamodb").get("OldImage")
					.get("pageAccessToken").get("S");
			} catch (Exception e){
				log.log("Error in pageAccessToken" + e.getMessage());
			}
			AmazonSNSClient snsClient = new AmazonSNSClient();
			
			if (!orderStatusNew.equals(orderStatusOld)) {
					
				Table restaurantTable = dynamoDB.getTable("Restaurant");
				Item botnameitem = restaurantTable.getItem("botName", botName);
				String restaurantName = botnameitem.getString("restaurantName");
				String restaurantphn = botnameitem.getString("phone_no");
				
		      
				if(orderStatusNew.equalsIgnoreCase("CONFIRMED")){
					if(orderFrom.equals("Website") || orderFrom.equals("Facebook")){
								Stripe.apiKey = "sk_test_h8l3zo52N4fKamsxtuTBQ0lH";
					Map<String, Object> chargeParams = new HashMap<String, Object>();
					chargeParams.put("amount", totalBillString); 
					chargeParams.put("currency", "usd");
					chargeParams.put("customer", customerToken);
					Charge charge = Charge.create(chargeParams);
					System.out.println("Charge is Successful");
					UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", uuid)
							.withUpdateExpression("set paymentDone =:pd")
							.withValueMap(new ValueMap().withString(":pd", "true"));
					UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
					outcome2.getItem();
					 String snsmessage1 = "Congratulations. Your order was confirmed. You will get notifications of your order status. Thank you for choosing "+restaurantName+".";
					 sendSMSMessage(snsClient, snsmessage1, phoneNumber);		
					}
					
					 try{
						SESorderitems sesorderitems = new SESorderitems();
						SesRequest sesRequest = new SesRequest();
						sesRequest.setOrdertableuuid(uuid);					
						sesorderitems.getSES(sesRequest);
					 } catch (Exception e){
							System.out.println("Error in mail");
					 }
					 String snsmessage2 = restaurantName+" | Online Receipt\n"
				        		+ "https://vhnnifn9o0.execute-api.us-east-1.amazonaws.com/Lexpayment/receipt?order="+uuid;
					 sendSMSMessage(snsClient, snsmessage2, phoneNumber);
					 return "CONFIRMED";
				
				} else if(orderStatusNew.equalsIgnoreCase("DECLINED")){
					String snsmessage2 = "Thank you for your order. Unfortunately,"+restaurantName+" was unable to confirm your order."
							+ " You are suggested to call "+restaurantName+ "to see what happened with your order - "+restaurantphn;
					 sendSMSMessage(snsClient, snsmessage2, phoneNumber);
					 return "DECLINED";
				} else {				
				
				LexFbStatusDto lexFbStatusDto = new LexFbStatusDto();
				Recipient recipient = new Recipient();
				recipient.setId(userId);
				Message message = new Message();
				message.setText(orderStatusNew);
				lexFbStatusDto.setRecipient(recipient);
				lexFbStatusDto.setMessage(message);

				String json1 = gson.toJson(lexFbStatusDto);
				System.out.println(json1);
				HttpCallHelper httpCallHelper = new HttpCallHelper();
				Map<String, String> headers = new HashMap<>();
				headers.put("Content-Type", "application/json");
				Map<String, String> params = new HashMap<>();
				params.put("access_token",
						pageAccessToken);
				try{
				httpCallHelper.doPostCall(headers, "https://graph.facebook.com/v2.6/me/messages", params, json1);
				} catch(Exception e){
					System.out.println("pageAccessToken is Null");
				}
				
				
		        String snsmessage = restaurantName +" just updated your order status as \""+ orderStatusNew +"\".Call us at "+restaurantphn+" if you have any questions." ;
		    	        
		        sendSMSMessage(snsClient, snsmessage, phoneNumber);
				}
				
			} else {
				log.log("The Previous OrderStatus is same as Current OrderStatus");
			}
		} catch (Exception e) {
			log.log("Error in Trigger" + e.getMessage());
			return "Dynamodbtrigger failed";
		}
		System.out.println(""
				+ "");
		log.log("END:Dynamodbtrigger:Success");
		return "Dynamodbtrigger Success";
	}
	public static void sendSMSMessage(AmazonSNSClient snsClient, String message, 
			String phoneNumber) {
		setDefaultSmsAttributes(snsClient);
		// Map<String, MessageAttributeValue> smsAttributes = new HashMap<String, MessageAttributeValue>();
	        PublishResult result = snsClient.publish(new PublishRequest()
	                        .withMessage(message)
	                        .withPhoneNumber(phoneNumber));
	                       // .withMessageAttributes(smsAttributes));
	        System.out.println(result);
	}
	public static void setDefaultSmsAttributes(AmazonSNSClient snsClient) {
		SetSMSAttributesRequest setRequest = new SetSMSAttributesRequest()
				.addAttributesEntry("DefaultSenderID", "mySenderID")
				.addAttributesEntry("MonthlySpendLimit", "5")			
		
				.addAttributesEntry("DefaultSMSType", "Transactional");

		snsClient.setSMSAttributes(setRequest);
	
	}

}
