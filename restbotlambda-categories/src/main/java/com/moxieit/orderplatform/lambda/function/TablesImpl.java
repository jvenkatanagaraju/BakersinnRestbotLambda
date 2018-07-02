package com.moxieit.orderplatform.lambda.function;

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
import com.moxieit.orderplatform.lambda.request.TablesRequest;
import com.moxieit.orderplatform.lambda.response.CommonResponse;

public class TablesImpl implements RequestHandler<MainMenuRequest, CommonResponse>{
	@Override
	public CommonResponse handleRequest(MainMenuRequest tablesRequest, Context context) {
		// TODO Auto-generated method stub
		CommonResponse commonResponse=new CommonResponse();
		try {
			DynamoDB dynamoDB = DBService.getDBConnection();
			Table tablesTable = dynamoDB.getTable("Tables");
			Table restaurantTable = dynamoDB.getTable("Restaurant");
			Item item = new Item();
			String type = tablesRequest.getType();
			String botName = tablesRequest.getBotName();
			Item restaurantItem = restaurantTable.getItem("botName", botName);
			if (type != null) {
				switch (type) {
				case "Insert": {
					for (TablesRequest temp : tablesRequest.getTablesRequest()) {
						if (temp != null) {                                       
							String tableId = String.valueOf(IdService.getID());						
							item.withString("restaurantId", restaurantItem.getString("id"))
								.withString("tableId", restaurantItem.getString("id")+"_"+tableId)
								.withString("tableName", temp.getTableName())
								.withString("seats", temp.getSeats())
								.withString("tableStatus", "AVAILABLE")
								.withString("minSeats", temp.getMinSeats());
							System.out.println(item);
							tablesTable.putItem(item);
								}
						}
					commonResponse.setMessage("Successfully Inserted");
					commonResponse.setStatus("success");
					return  commonResponse;
				}
				case "Update": {
					for (TablesRequest temp : tablesRequest.getTablesRequest()) {
						if (temp != null) {
							String tableName = null;
							String itemSeats = null;
							String itemMinSeats = null;
							String restaurantId =  restaurantItem.getString("id");
							String tableId = temp.getTableId();
							Object o = restaurantId;
							Object itemId = tableId;
							Item tableItem = tablesTable.getItem("restaurantId", o, "tableId", itemId);
							if (tableItem != null) {
								if (temp.getTableName() != null) {
									tableName = temp.getTableName();
								} else {
									tableName = tableItem.getString("tableName");
								}
								if (temp.getSeats() != null) {
									itemSeats = temp.getSeats();
								} else {
									itemSeats = tableItem.getString("seats");
								}
								if (temp.getMinSeats() != null) {
									itemMinSeats = temp.getMinSeats();
								} else {
									itemMinSeats = tableItem.getString("minSeats");
								}
								UpdateItemSpec updateItemSpec = new UpdateItemSpec()
										.withPrimaryKey("restaurantId", tableItem.get("restaurantId"), "tableId",
												tableItem.get("tableId"))
										.withUpdateExpression(
												"set tableName = :val,seats = :ty, minSeats = :min")
										.withValueMap(
												new ValueMap().withString(":val", tableName).withString(":ty", itemSeats).withString(":min", itemMinSeats));
								tablesTable.updateItem(updateItemSpec);
								
							}

						}
					}
					commonResponse.setMessage("Successfully Updated");
					commonResponse.setStatus("success");
					return  commonResponse;
				}
				case "Delete": {
					for (String temp: tablesRequest.getTableIds()) {
						System.out.println(temp);
						if (temp != null) {
							System.out.println(restaurantItem.getString("id"));
								DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(
										new PrimaryKey("restaurantId", restaurantItem.getString("id"), "tableId",
												temp));
								tablesTable.deleteItem(deleteItemSpec);

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

}
