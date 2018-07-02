package com.moxieit.orderplatform.lambda.function;

import org.springframework.util.SystemPropertyUtils;

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
import com.moxieit.orderplatform.lambda.request.MenuCategoriesRequest;
import com.moxieit.orderplatform.lambda.response.MainMenuCategoryResponse;

public class MenuCategoriesImpl implements RequestHandler<MainMenuRequest, MainMenuCategoryResponse> {

	@Override
	public MainMenuCategoryResponse handleRequest(MainMenuRequest menuCategoriesRequest, Context context) {
		MainMenuCategoryResponse mainMenuCategoryResponses = new MainMenuCategoryResponse();
		try {
			DynamoDB dynamoDB = DBService.getDBConnection();
			Table menuCategoriesTable = dynamoDB.getTable("Menu_Categories");
			Table restaurantTable = dynamoDB.getTable("Restaurant");
			System.out.println(menuCategoriesRequest.getBotName());
			System.out.println(menuCategoriesRequest.getType());
			Item item = new Item();
			String type = menuCategoriesRequest.getType();
			String botName = menuCategoriesRequest.getBotName();
			Item restaurantItem = restaurantTable.getItem("botName", botName);
			System.out.println(restaurantItem.getString("id"));
			if (restaurantItem != null) {
				if (type != null) {
					switch (type) {
					case "Insert": {
						for (MenuCategoriesRequest temp : menuCategoriesRequest.getMenuCategoriesRequest()) {
							if (temp != null) {
								String catId = String.valueOf(IdService.getID());
								item.withString("categoryName", temp.getCategoryName())
										.withString("image", temp.getImage()).withString("categoryId",restaurantItem.getString("id")+"_"+catId)
										.withString("restaurantId", restaurantItem.getString("id"));
								menuCategoriesTable.putItem(item);
							}
          }
						mainMenuCategoryResponses.setMessage("Successfully Inserted the MenuCategory");
						mainMenuCategoryResponses.setStatus("success");
						return mainMenuCategoryResponses;
					}
					case "Update": {
						for (MenuCategoriesRequest temp : menuCategoriesRequest.getMenuCategoriesRequest()) {
							if (temp != null) {
								String categoryName = null;
								String image = null;
								String catId = temp.getCategoryId();
								Object restId = restaurantItem.getString("id");
								Object categoryId = catId;
								System.out.println(restId);
								System.out.println(categoryId);
								Item menuCategoryItem = menuCategoriesTable.getItem("restaurantId", restId,
										"categoryId", categoryId);
								System.out.println(menuCategoryItem);
								if (menuCategoryItem != null) {
									if (temp.getCategoryName() != null) {
										categoryName = temp.getCategoryName();
									} else {
										categoryName = menuCategoryItem.getString("categoryName");
									}
									if (temp.getImage() != null) {
										image = temp.getImage();
									} else {
										image = menuCategoryItem.getString("image");
									}
									UpdateItemSpec updateItemSpec = new UpdateItemSpec()
											.withPrimaryKey("restaurantId", menuCategoryItem.getString("restaurantId"),
													"categoryId", menuCategoryItem.getString("categoryId"))
											.withUpdateExpression("set categoryName = :val,image = :img")
											.withValueMap(new ValueMap().withString(":val", categoryName)
													.withString(":img", image));
									menuCategoriesTable.updateItem(updateItemSpec);
								}
							}

						}
						mainMenuCategoryResponses.setMessage("Successfully updated your MenuCategory");
						mainMenuCategoryResponses.setStatus("success");
						return mainMenuCategoryResponses;
					}
					case "Delete": {
						for (String temp:menuCategoriesRequest.getCategoryIds()) {
							System.out.println(temp);
							if (temp != null) {
								System.out.println(restaurantItem.getString("id"));
									DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(
											new PrimaryKey("restaurantId", restaurantItem.getString("id"), "categoryId",
													temp));
									menuCategoriesTable.deleteItem(deleteItemSpec);

							}
						}
						mainMenuCategoryResponses.setMessage("Successfully Deleted your MenuCategory");
						mainMenuCategoryResponses.setStatus("success");
						return mainMenuCategoryResponses;
					}
					}
				}
			}
		} catch (Exception e) {
			context.getLogger().log("Exception is :" + e);
		}

		mainMenuCategoryResponses.setMessage("Failed the operation");
		mainMenuCategoryResponses.setStatus("failed");
		return mainMenuCategoryResponses;

	}
}
