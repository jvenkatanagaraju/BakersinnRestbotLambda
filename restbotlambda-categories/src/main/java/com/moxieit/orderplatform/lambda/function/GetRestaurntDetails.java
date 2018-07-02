package com.moxieit.orderplatform.lambda.function;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.lambda.response.LoginResponse;

public class GetRestaurntDetails implements RequestHandler<String, LoginResponse> {

	public LoginResponse handleRequest(String botName, Context context) {
		// context.getLogger().log(lexRequest);
		LoginResponse loginResponse = new LoginResponse();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table restaurantTable = dynamoDB.getTable("Restaurant");

		Item restaurantItem = restaurantTable.getItem("botName", botName);
		if (restaurantItem != null) {
			loginResponse.setBotName(restaurantItem.getString("botName"));
			loginResponse.setMonToFriHours(restaurantItem.getString("monToFri"));
			loginResponse.setSatToSunHours(restaurantItem.getString("satToSun"));
			loginResponse.setMessage("BotName and UserDetails");
			loginResponse.setRestaurantId(restaurantItem.getString("id"));
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
			loginResponse.setZipCode(restaurantItem.getString("zipCode"));
			loginResponse.setPhone_no(restaurantItem.getString("phone_no"));
			loginResponse.setEmailId(restaurantItem.getString("emailId"));
			loginResponse.setPageName(restaurantItem.getString("pageName"));
			System.out.println(loginResponse);
			return loginResponse;
		}
		loginResponse.setStatus("failed");
		loginResponse.setMessage("There is no Details with this BotName");
		return loginResponse;

	}

}
