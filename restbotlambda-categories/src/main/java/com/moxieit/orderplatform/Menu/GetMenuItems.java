package com.moxieit.orderplatform.Menu;

import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.moxieit.orderplatform.lambda.function.service.impl.RestaurantMenuImpl;
import com.moxieit.orderplatform.lambda.response.MenuItemResponse;

public class GetMenuItems implements RequestHandler<String, List<MenuItemResponse>> {

	public List<MenuItemResponse> handleRequest(String categoryId, Context context) {
		System.out.println(categoryId);

		List<MenuItemResponse> menuItemList = null;
		RestaurantMenuImpl restBotLambdaImpl = new RestaurantMenuImpl();
		try {
			menuItemList = restBotLambdaImpl.getMenuItems(categoryId);
		} catch (Exception e) {
			context.getLogger().log(e.getMessage());
		}
		return menuItemList;
	}

}
