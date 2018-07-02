package com.moxieit.orderplatform.lambda.function;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.moxieit.orderplatform.lambda.DB.DBService;
import com.moxieit.orderplatform.lambda.request.PushSNSRequest;
import com.moxieit.orderplatform.lambda.request.SesRequest;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;

public class WebsiteChargeImpl implements RequestHandler<Object, String> {
	static int counter = 0;
	static int instanceCounter = 0;
	String botName = null;

	@Override
	public String handleRequest(Object websiteInput, Context context) {
		DynamoDB dynamoDB = DBService.getDBConnection();

		Map<String, Object> websiteMap = new HashMap<String, Object>();
		websiteMap = (Map<String, Object>) websiteInput;
		Map<String, List<Map<String, String>>> map = new HashMap<String, List<Map<String, String>>>();
		map = (Map<String, List<Map<String, String>>>) websiteInput;

		Table orderTable = dynamoDB.getTable("Order");
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		String orderuuid = UUID.randomUUID().toString();
		String userId = UUID.randomUUID().toString();
		//String orderItemuuid = UUID.randomUUID().toString();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		String date = formatter.format(calendar.getTime());
		if (websiteInput != null) {			
			Item item = new Item();
			
			Table restaurantTable = dynamoDB.getTable("Restaurant");
			String restaurantId = (String) websiteMap.get("restaurantId");
			
			ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("id").eq(restaurantId))
					.buildForScan();

			ItemCollection<ScanOutcome> scan = restaurantTable.scan(xspec);
			Consumer<Item> action = new Consumer<Item>() {
				@Override
				public void accept(Item t) {
					instanceCounter++;
					counter = counter + 1;
					Object botName1 = t.getString("botName");				
					botName = (String) botName1;
				}

			};
			scan.forEach(action);			
			//System.out.println("botname:" +botName);
			
			item.withString("uuid", orderuuid)
					.withNumber("totalBill",Double.parseDouble( String.valueOf( websiteMap.get("totalCost"))))
					.withNumber("totalBillWithTax",Double.parseDouble( String.valueOf( websiteMap.get("totalBillWithTax"))))
					.withString("phoneNumber", ((String) websiteMap.get("phoneNumber")))
					.withNumber("tax",Double.parseDouble( String.valueOf( websiteMap.get("Tax"))))
					.withString("emailId", (String) websiteMap.get("emailId"))
					.withString("botName", botName)
					.withString("restaurantId", restaurantId)
					.withString("userId", userId)
					.withString("pickUp", (String) websiteMap.get("pickUp"))
					.withString("paymentMethod", (String) websiteMap.get("paymentMethod"))
					.withString("userName", (String) websiteMap.get("fullName"))
					.withString("address", (String) websiteMap.get("address")).withString("orderDate", date)
					.withNumber("creationDate", System.currentTimeMillis()).withString("orderTracking", "ACCEPTED")
					.withString("orderStatus", "Initiated").withString("orderFrom", "Website")
					.withString("returnMessage", "true").withString("paymentDone", "false");
			orderTable.putItem(item);
			int itemsSize = map.get("items").size();			
			for (int i = 0; i < itemsSize; i++) {
				Item item1 = new Item();
				String orderItemuuid = UUID.randomUUID().toString();
				String itemId = (String) map.get("items").get(i).get("itemId");
				String[] itemIdSplit = itemId.split("_");
				String categoryId = itemIdSplit[0] + "_" + itemIdSplit[1];
				item1.withNumber("quantity", Integer.parseInt((String) map.get("items").get(i).get("quantity")))
						.withString("orderuuid", orderuuid).withString("readItem", "true")
						.withString("userId", userId)
						.withString("uuid", orderItemuuid).withString("categoryId", categoryId)
						.withString("itemName", (String) map.get("items").get(i).get("itemName"))
						.withString("spiceyLevel", (String) map.get("items").get(i).get("SpicyLevel"))
						.withString("specialInstructions", (String) map.get("items").get(i).get("specialInstructions"))
						.withNumber("itemCost", Double.parseDouble((String) map.get("items").get(i).get("cost")))
						.withString("menuItemId", (String) map.get("items").get(i).get("itemId"));				
				orderItemTable.putItem(item1);
			}
			//Stripe.apiKey = "sk_live_p5EAJtOI50fF6LQSFftY5Hqc";
			Stripe.apiKey = "sk_live_p5EAJtOI50fF6LQSFftY5Hqc";
			
			 String customerToken = "NULL";
		
				
			
			Item orderItem = orderTable.getItem("uuid", orderuuid);
			Number totalBill = (Number) orderItem.get("totalBillWithTax");
			Double totalBillDouble = totalBill.doubleValue() * 100;
			Integer totalBillInteger = totalBillDouble.intValue();
			String totalBillString = totalBillInteger.toString();
			
			/*Map<String, Object> chargeParams = new HashMap<String, Object>();
			chargeParams.put("amount", totalBillString);
			chargeParams.put("description", "Charge for " + (String) websiteMap.get("emailId"));
			chargeParams.put("currency", "usd");
			chargeParams.put("source", (String) websiteMap.get("token"));*/
			
			String paymentMethod = (String) websiteMap.get("paymentMethod");
			// ^ obtained with Stripe.js
			try {
				if (paymentMethod.equals("Credit-card/Debit-card")){
					//Charge.create(chargeParams);
					
						// Create a Customer:
						Map<String, Object> customerParams = new HashMap<String, Object>();
						customerParams.put("email", (String) websiteMap.get("emailId"));
						customerParams.put("source", (String) websiteMap.get("token"));
						try {
							Customer customer = Customer.create(customerParams);
							customerToken = customer.getId();
							
							System.out.println("customerId" +customerToken);
							
						} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException
								| APIException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					
				}
				
				String EmailId = (String) websiteMap.get("emailId");
				/*SESorderitems sesorderitems = new SESorderitems();
				SesRequest sesRequest = new SesRequest();
				sesRequest.setOrdertableuuid(orderuuid);
				sesRequest.setStripeEmail(EmailId);				
				sesorderitems.getSES(sesRequest);*/
				
				AmazonSNSClient snsClient = new AmazonSNSClient();
				//String phoneNumber = (orderItem.get("phoneNumber")).toString();
				String phoneNumber = orderItem.getString("phoneNumber");
				String 	botName = orderItem.getString("botName");				
				Item botnameitem = restaurantTable.getItem("botName", botName);
				String deviceToken = botnameitem.getString("deviceToken");
				if (deviceToken != null) {
				PushSNSRequest pushSNSRequest = new PushSNSRequest();								
				pushSNSRequest.setDeviceToken(deviceToken);	
				pushSNSRequest.setPhoneNumber(phoneNumber);
				SNSMobilePush sns = new SNSMobilePush(); 			      
				  sns.pushSNS(pushSNSRequest, context);
				}
							
				String restaurantName = botnameitem.getString("restaurantName");
				String restaurantphn = botnameitem.getString("phone_no");
		        String snsmessage = "Your Order was placed successfully. You will get a confirmation message/email once your order is confirmed by "+restaurantName
		        		+ "If you want to make any changes to this order contact "+restaurantName+" – "
		        		+ restaurantphn;
		     //  String snsmessage1 = restaurantName+" | Online Receipt\n"+ "https://vhnnifn9o0.execute-api.us-east-1.amazonaws.com/Lexpayment/receipt?order="+orderuuid;
		     
		       
		        AmazonSNS.sendSMSMessage(snsClient, snsmessage, phoneNumber);
		        
				UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", orderuuid)
						.withUpdateExpression("set orderStatus = :val,customerToken =:cus")
						.withValueMap(new ValueMap().withString(":val", "ACCEPTED").withString(":cus", customerToken));
				UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
				outcome2.getItem();
				
				// AmazonSNS.sendSMSMessage(snsClient, snsmessage1, phoneNumber);
				return orderuuid;

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return "Failed";
	}

	
}