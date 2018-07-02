package com.moxieit.orderplatform.lambda.function;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.util.function.Consumer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.lambda.request.LoginRequest;
import com.moxieit.orderplatform.lambda.response.LoginResponse;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;

public class LoginServiceImpl implements RequestHandler<LoginRequest, LoginResponse> {

	
	@Override
	public LoginResponse handleRequest(LoginRequest loginInput, Context context) {
		// TODO Auto-generated method stub
		LoginResponse loginResponse = new LoginResponse();
	
		try {
			StringBuilder botName = new StringBuilder();
		
			DynamoDB dynamoDB = DBService.getDBConnection();
			Table restauarantTable = dynamoDB.getTable("Restaurant");			
			
			ScanExpressionSpec xspec3 = new ExpressionSpecBuilder().withCondition(S("deviceId").eq(loginInput.getDeviceId())
					.and(S("securePin").eq(loginInput.getSecurePin())))
					.buildForScan();
			ItemCollection<ScanOutcome> scan3 = restauarantTable.scan(xspec3);

			Consumer<Item> action3 = new Consumer<Item>() {
				@Override
				public void accept(Item t2) {
					Object botName1 = t2.getString("botName");
					Item itemuuid = restauarantTable.getItem("botName", botName1);
					//botName = itemuuid.getString("botName");	
					botName.append(itemuuid.getString("botName"));
					System.out.println("botname:" +botName);					
					UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("botName", botName.toString())
							.withUpdateExpression("set deviceToken = :val")
							.withValueMap(new ValueMap().withString(":val", loginInput.getDeviceToken()));
					UpdateItemOutcome outcome = restauarantTable.updateItem(updateItemSpec);
					outcome.getItem();
					

				}

			};
			scan3.forEach(action3);		
			
			Item restaurantItem = restauarantTable.getItem("botName", botName.toString());
			
			if (restaurantItem != null && restaurantItem.getString("botName").equals(botName.toString())) {

				loginResponse.setBotName(restaurantItem.getString("botName"));
				loginResponse.setSatToSunHours(restaurantItem.getString("satToSun"));
				loginResponse.setMonToFriHours(restaurantItem.getString("monToFri"));
				loginResponse.setMessage("Login Successfully");
				loginResponse.setRestaurantId(restaurantItem.getString("id"));
				loginResponse.setPhone_no(restaurantItem.getString("phone_no"));
				loginResponse.setEmailId(restaurantItem.getString("emailId"));
				loginResponse.setPageName(restaurantItem.getString("pageName"));
				loginResponse.setZipCode(restaurantItem.getString("zipCode"));
				loginResponse.setRestaurantName(restaurantItem.getString("restaurantName"));
				loginResponse.setStatus("success");
				loginResponse.setImage(restaurantItem.getString("image"));
				loginResponse.setCity(restaurantItem.getString("city"));
				loginResponse.setCountry(restaurantItem.getString("country"));
				loginResponse.setState(restaurantItem.getString("state"));
				loginResponse.setStreet_1(restaurantItem.getString("street_1"));
				loginResponse.setSecurePin(restaurantItem.getString("securePin"));
				loginResponse.setDeviceId(restaurantItem.getString("deviceId"));
				loginResponse.setDeviceToken(restaurantItem.getString("deviceToken"));
				System.out.println("success");
				return loginResponse;
			} else {
				loginResponse.setMessage("Failed to login");
				loginResponse.setStatus("failed");
				System.out.println("fail");
				return loginResponse;
			}

		} catch (Exception e) {
			// TODO: handle exception
		
		}
		loginResponse.setMessage("Failed to login, if you are updated your device then please signup to the App again.");
		loginResponse.setStatus("failed");
		System.out.println("failed to login");
		return loginResponse;
	} 
	
	
	/*public static void main(String[] args) {
		LoginServiceImpl fbWebhooks = new LoginServiceImpl();
		Context context = null;

		LoginRequest fbWebhooksRequest = new LoginRequest();
		fbWebhooksRequest.setDeviceToken("1234");
		fbWebhooksRequest.setDeviceId("123");
		fbWebhooksRequest.setSecurePin("1234");		
		fbWebhooks.handleRequest(fbWebhooksRequest, context);
	}*/

}
