package com.moxieit.orderplatform.Menu;

import java.util.List;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.lambda.function.service.impl.RestaurantMenuImpl;
import com.moxieit.orderplatform.lambda.response.MenuCategoryResponse;

public class GetMenuCategories implements RequestHandler<String, List<MenuCategoryResponse>> {

	public List<MenuCategoryResponse> handleRequest(String botName, Context context) {
		context.getLogger().log("Input: " + botName);

		List<MenuCategoryResponse> menuCategoryList = null;
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table restaurantTable = dynamoDB.getTable("Restaurant");
		Item restaurantItem = restaurantTable.getItem("botName", botName);
		String restaurantId = restaurantItem.getString("id");
		RestaurantMenuImpl restBotLambdaImpl = new RestaurantMenuImpl();
		try {
			menuCategoryList = restBotLambdaImpl.getMenuCategories(restaurantId);
		} catch (Exception e) {
			context.getLogger().log(e.getMessage());
		}
		return menuCategoryList;

	}

}
