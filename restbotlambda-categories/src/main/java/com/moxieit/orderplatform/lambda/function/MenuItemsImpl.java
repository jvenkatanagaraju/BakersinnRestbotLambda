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
import com.moxieit.orderplatform.lambda.request.MenuItemsRequest;
import com.moxieit.orderplatform.lambda.response.CommonResponse;

public class MenuItemsImpl implements RequestHandler<MainMenuRequest, CommonResponse> {

	@Override
	public CommonResponse handleRequest(MainMenuRequest menuItemsRequest, Context context) {
		// TODO Auto-generated method stub
		CommonResponse commonResponse=new CommonResponse();
		try {
			
			DynamoDB dynamoDB = DBService.getDBConnection();
			Table menuItemsTable = dynamoDB.getTable("Menu_Items");
			Item item = new Item();
			String type = menuItemsRequest.getType();
			if (type != null) {
				switch (type) {
				case "Insert": {
					for (MenuItemsRequest temp : menuItemsRequest.getMenuItemsRequest()) {
						if (temp != null) {
							String itemId = String.valueOf(IdService.getID());
							String isSpicyString=temp.getIsSpicy();
							boolean boolean2 = Boolean.parseBoolean(isSpicyString);
							item.withString("itemName", temp.getItemName()).withString("image", temp.getImage())
									.withString("categoryId", temp.getCategoryId()).withString("itemId", temp.getCategoryId()+"_"+itemId)
									.withBoolean("isSpicy", boolean2).
									withNumber("price",(Number)Double.parseDouble(temp.getPrice()))
									.withString("itemType", temp.getItemType());
							System.out.println(item);
							menuItemsTable.putItem(item);
							
						}
						
					}
					commonResponse.setMessage("Successfully Inserted");
					commonResponse.setStatus("success");
					return  commonResponse;
				}
				case "Update": {
					for (MenuItemsRequest temp : menuItemsRequest.getMenuItemsRequest()) {
						if (temp != null) {
							String itemName = null;
							String isSpicy=null;
							String image = null;
							 boolean boolean2 = false;
							Number price = null;
							String itemType = null;
							String catId = temp.getCategoryId();
							String iteId = temp.getItemId();
							Object o = catId;
							Object itemId = iteId;
							Item menuItem = menuItemsTable.getItem("categoryId", o, "itemId", itemId);
							if (menuItem != null) {
								if (temp.getItemName() != null) {
									itemName = temp.getItemName();
								} else {
									itemName = menuItem.getString("itemName");
								}
								if (temp.getIsSpicy()!=null) {
									 isSpicy = temp.getIsSpicy();
									 boolean2=Boolean.parseBoolean(isSpicy);
								} else {
									boolean2= menuItem.getBoolean("isSpicy");
									
								}
								if (temp.getImage() != null) {
									image = temp.getImage();
								} else {
									image = menuItem.getString("image");
								}
								if (temp.getPrice() != null) {
									price = (Number)Double.parseDouble(temp.getPrice());
								} else {
									price = menuItem.getNumber("price");
								}
								if (temp.getItemType() != null) {
									itemType = temp.getItemType();
								} else {
									itemType = menuItem.getString("itemType");
								}
								UpdateItemSpec updateItemSpec = new UpdateItemSpec()
										.withPrimaryKey("categoryId", menuItem.get("categoryId"), "itemId",
												menuItem.get("itemId"))
										.withUpdateExpression(
												"set itemName = :val,price = :pr,itemType = :ty,isSpicy=:iS,image = :img")
										.withValueMap(
												new ValueMap().withString(":val", itemName).withBoolean(":iS", boolean2)
														.withNumber(":pr", price).withString(":ty", itemType).withString(":img", image));
								menuItemsTable.updateItem(updateItemSpec);
							}

						}
					}
					commonResponse.setMessage("Successfully Updated");
					commonResponse.setStatus("success");
					return  commonResponse;
				}
				case "Delete": {
					for (MenuItemsRequest temp : menuItemsRequest.getMenuItemsRequest()) {
						if (temp != null) {
							if (temp.getItemId() != null && temp.getCategoryId() != null) {
								String catId = temp.getCategoryId();
								String iteId = temp.getItemId();
								Object o = catId;
								Object itemId = iteId;
								Item menuItem = menuItemsTable.getItem("categoryId", o, "itemId", itemId);
								if (menuItem != null) {
								DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(
										new PrimaryKey("itemId", temp.getItemId(), "categoryId", temp.getCategoryId()));
								menuItemsTable.deleteItem(deleteItemSpec);
							}
								commonResponse.setMessage("Successfully deleted");
								commonResponse.setStatus("success");
								return  commonResponse;
								}

						}
					}
					
				}
				}
			}
		} catch (Exception e) {
			//context.getLogger().log("Exception is :" + e);
		}
		commonResponse.setMessage("Failed to update the MenuItem Details might your categoryId or ItemId is wrong");
		commonResponse.setStatus("failed");
		return  commonResponse;
	}
}