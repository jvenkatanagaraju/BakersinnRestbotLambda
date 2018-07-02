
	
	package com.moxieit.orderplatform.lambda.function;

	import java.io.IOException;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
	import com.amazonaws.services.dynamodbv2.document.Item;
	import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
	import com.amazonaws.services.dynamodbv2.document.Table;
	import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
	import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
	import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
	import com.amazonaws.services.lambda.runtime.Context;
	import com.amazonaws.services.lambda.runtime.RequestHandler;
	import com.moxieit.orderplatform.DB.DBService;
	import com.moxieit.orderplatform.lambda.function.service.impl.IdService;
	import com.moxieit.orderplatform.lambda.request.MainMenuRequest;
import com.moxieit.orderplatform.lambda.request.WaitersRequest;
import com.moxieit.orderplatform.lambda.response.CommonResponse;

	public class WaitersImpl implements RequestHandler<MainMenuRequest, CommonResponse> {

		@Override
		public CommonResponse handleRequest(MainMenuRequest waitersRequest, Context context) {
			// TODO Auto-generated method stub
			CommonResponse commonResponse=new CommonResponse();
			try {
				
				DynamoDB dynamoDB = DBService.getDBConnection();
				Table waitersTable = dynamoDB.getTable("Waiters");
				//Table restaurantTable = dynamoDB.getTable("Restaurant");
				Item item = new Item();
				String type = waitersRequest.getType();
				String restaurantId = waitersRequest.getRestaurantId();
				Item restaurantItem = waitersTable.getItem("restaurantId", restaurantId);
				if (type != null) {
					switch (type) {
					case "Insert": {
						for (WaitersRequest temp : waitersRequest.getWaitersRequest()) {
							if (temp != null) {
								String waiterId = String.valueOf(IdService.getID());							
								item.withString("restaurantId", restaurantItem.getString("id"))
									.withString("waiterId", restaurantItem.getString("id")+"_"+waiterId)
									.withString("waiterName", temp.getWaiterName())
									.withString("SecurePin", temp.getSecurePin());
								System.out.println(item);
								waitersTable.putItem(item);
							}
							}
						commonResponse.setMessage("Successfully Inserted");
						commonResponse.setStatus("success");
						return  commonResponse;
					}
					case "Update": {
						for (WaitersRequest temp : waitersRequest.getWaitersRequest()) {
							if (temp != null) {
								String waiterName = null;
								String SecurePin = null;
								
								 restaurantId =  restaurantItem.getString("id");
								String waiterId = temp.getWaiterName();
								Object o = restaurantId;
								Object waitersId = waiterId;
								Item tableItem = waitersTable.getItem("restaurantId", o, "waitersId", waitersId);
								if (tableItem != null) {
									if (temp.getWaiterName() != null) {
										waiterName = temp.getWaiterName();
									} else {
										waiterName = tableItem.getString("waiterName");
				
									if (temp.getSecurePin() != null) {
										SecurePin = temp.getSecurePin();
									} else {
										SecurePin = tableItem.getString("SecurePin");
									}
									
									UpdateItemSpec updateItemSpec = new UpdateItemSpec()
											.withPrimaryKey("restaurantId", tableItem.get("restaurantId"), "waiterId",
													tableItem.get("waiterId"))
											.withUpdateExpression(
													"set waiterName = :val,SecurePin = :sp")
											.withValueMap(
													new ValueMap().withString(":val", waiterId).withString(":ty", SecurePin));
									waitersTable.updateItem(updateItemSpec);
								}

							}
						}
						commonResponse.setMessage("Successfully Updated");
						commonResponse.setStatus("success");
						return  commonResponse;
					}}
					case "Delete": {
						for (String temp: waitersRequest.getWaiterIds()) {
							System.out.println(temp);
							if (temp != null) {
								System.out.println(restaurantItem.getString("id"));
									DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(
											new PrimaryKey("restaurantId", restaurantItem.getString("id"), "waiterId",
													temp));
									waitersTable.deleteItem(deleteItemSpec);
							}
						}
						commonResponse.setMessage("Successfully Deleted");
						commonResponse.setStatus("success");
						return  commonResponse;
						
					}
					}
				}
			} catch (Exception e) {
				//context.getLogger().log("Exception is :" + e);
			}
			commonResponse.setMessage("Failed the operation");
			commonResponse.setStatus("failed");
			return  commonResponse;
		}

		 public static void main(String[] args) throws IOException {
			 WaitersImpl waitersImpl = new WaitersImpl();		
			 MainMenuRequest mainMenuRequest = new MainMenuRequest();
			 Context context = null;
			 mainMenuRequest.setSecurePin("425555");
			 mainMenuRequest.setRestaurantId("23");	
			 mainMenuRequest.setWaiterName("gjhgjhbhbnxb");
			 //mainMenuRequest.setWaiterId("23_55");	
			 waitersImpl.handleRequest(mainMenuRequest, context);
				}

			

		
			
		
	}
