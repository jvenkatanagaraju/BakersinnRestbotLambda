package com.moxieit.orderplatform.lambda.function;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.moxieit.orderplatform.lambda.DB.DBService;
import com.moxieit.orderplatform.lambda.request.DeviceTokenRequest;
import com.moxieit.orderplatform.lambda.response.DeviceTokenResponse;


public class GetDeviceToken implements RequestHandler<DeviceTokenRequest, DeviceTokenResponse> {
	
	
	  public DeviceTokenResponse handleRequest(DeviceTokenRequest deviceTokenRequest, Context context) {
		  DeviceTokenResponse deviceTokenResponse = new DeviceTokenResponse();
		  String deviceToken = deviceTokenRequest.getDeviceToken();
		  String botName = deviceTokenRequest.getBotName();
		  DynamoDB dynamoDB = DBService.getDBConnection();		
			Table restaurantTable = dynamoDB.getTable("Restaurant");
			try{
						
			UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("botName", botName)
					.withUpdateExpression("set deviceToken = :val")
					.withValueMap(new ValueMap().withString(":val", deviceToken));
			UpdateItemOutcome outcome = restaurantTable.updateItem(updateItemSpec);
			outcome.getItem();
		  
			deviceTokenResponse.setMessage("Successfully saved DeviceToken.");
			deviceTokenResponse.setStatus("success");
			return deviceTokenResponse;
			} catch (Exception e) {				
				
				e.printStackTrace();
			}
		  
			deviceTokenResponse.setMessage("There is no Bot with this Botname.");
			deviceTokenResponse.setStatus("failed");
			return deviceTokenResponse;
		
	  }




}
