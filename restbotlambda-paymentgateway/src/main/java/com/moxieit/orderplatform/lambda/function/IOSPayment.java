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
import com.moxieit.orderplatform.lambda.DB.DBService;
import com.moxieit.orderplatform.lambda.request.PushSNSRequest;
import com.moxieit.orderplatform.lambda.request.SesRequest;
import com.moxieit.orderplatform.lambda.response.CommonResponse;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import com.stripe.model.InvoiceItem;

public class IOSPayment implements RequestHandler<Object, CommonResponse>{

	String botName = null;
	String restaurantName = "";

	public CommonResponse handleRequest(Object stripeInput,Context context) {
		DynamoDB dynamoDB = DBService.getDBConnection();
		 CommonResponse commonResponse = new  CommonResponse();
		Map<String, Object> inputMap = new HashMap<String, Object>();
		inputMap = (Map<String, Object>) stripeInput;
		Map<String, List<Map<String, String>>> map = new HashMap<String, List<Map<String, String>>>();
		map = (Map<String, List<Map<String, String>>>) stripeInput;

		Table orderTable = dynamoDB.getTable("Order");
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		String orderuuid = UUID.randomUUID().toString();
		String userId = UUID.randomUUID().toString();
		//String orderItemuuid = UUID.randomUUID().toString();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		String date = formatter.format(calendar.getTime());
		if (stripeInput != null) {			
			Item item = new Item();
			
			Table restaurantTable = dynamoDB.getTable("Restaurant");
			String restaurantId = (String) inputMap.get("restaurantId");
			
			ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("id").eq(restaurantId))
					.buildForScan();

			ItemCollection<ScanOutcome> scan = restaurantTable.scan(xspec);
			Consumer<Item> action = new Consumer<Item>() {
				@Override
				public void accept(Item t) {
					Object botName1 = t.getString("botName");				
					botName = (String) botName1;
					restaurantName = t.getString("restaurantName");	
				}

			};
			scan.forEach(action);			
			//System.out.println("botname:" +botName);
			
			item.withString("uuid", orderuuid)
					.withNumber("totalBill",Double.parseDouble( String.valueOf( inputMap.get("totalBill"))))
					.withNumber("totalBillWithTax",Double.parseDouble( String.valueOf( inputMap.get("totalBillWithTax"))))
					.withString("phoneNumber", ((String) inputMap.get("phoneNumber")))
					.withNumber("tax",Double.parseDouble( String.valueOf( inputMap.get("Tax"))))
					.withString("emailId", (String) inputMap.get("emailId"))
					.withString("botName", botName)
					.withString("restaurantId", restaurantId)
					.withString("userId", userId)				
					.withString("userName", (String) inputMap.get("userName"))
					.withString("orderDate", date)
					.withNumber("creationDate", System.currentTimeMillis()).withString("orderTracking", "ACCEPTED")
					.withString("orderStatus", "Initiated").withString("orderFrom", "Dine-In")
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
			Stripe.apiKey = "sk_test_h8l3zo52N4fKamsxtuTBQ0lH";

			Map<String, Object> chargeParams = new HashMap<String, Object>();
			Map<String, Object> tokenParams = new HashMap<String, Object>();	
			
			Item orderItem = orderTable.getItem("uuid", orderuuid);
			Number totalBill = (Number) orderItem.get("totalBillWithTax");
			Double totalBillDouble = totalBill.doubleValue() * 100;
			Integer totalBillInteger = totalBillDouble.intValue();
			String totalBillString = totalBillInteger.toString();
			
			chargeParams.put("amount", totalBillString);
			chargeParams.put("description", "Charge for " + (String) inputMap.get("emailId"));
			chargeParams.put("currency", "usd");
			chargeParams.put("source", (String) inputMap.get("token"));

			// ^ obtained with Stripe.js
			try {
				Charge.create(chargeParams);
				/*String 	botName = orderItem.getString("botName");
				Table BankAccountDetailsTable = dynamoDB.getTable("BankAccountDetails");
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
				
				String EmailId = (String) inputMap.get("emailId");
				String phoneNumber = (String) inputMap.get("phoneNumber");
				
				if (phoneNumber != null) {
					AmazonSNSClient snsClient = new AmazonSNSClient();
					String snsmessage = restaurantName+" | Online Receipt\n"+ "https://vhnnifn9o0.execute-api.us-east-1.amazonaws.com/Lexpayment/receipt?order="+orderuuid;
					   AmazonSNS.sendSMSMessage(snsClient, snsmessage, phoneNumber);
				}
				
				/*SESorderitems sesorderitems = new SESorderitems();
				SesRequest sesRequest = new SesRequest();
				sesRequest.setOrdertableuuid(orderuuid);					
				try {
					sesorderitems.getSES(sesRequest);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

				UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", orderuuid)
						.withUpdateExpression("set orderStatus = :val,paymentDone =:pd,orderTracking =:tra")
						.withValueMap(new ValueMap().withString(":val", "ACCEPTED").withString(":pd", "true").withString(":tra", "CONFIRMED"));
				UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
				outcome2.getItem();
				commonResponse.setMessage("Payment is successful");
				commonResponse.setStatus("Success");
				return commonResponse;
			} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException
					| APIException e1){
				e1.printStackTrace();
				commonResponse.setMessage(e1.getMessage());
				commonResponse.setStatus("Error");
				return commonResponse;
			}

			} 
		commonResponse.setMessage("Input is NULL");
		commonResponse.setStatus("Failed");
		return commonResponse;
	}


	
}
