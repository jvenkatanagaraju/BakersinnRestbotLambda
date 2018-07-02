
package com.moxieit.orderplatform.lambda.function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.moxieit.orderplatform.lambda.function.service.impl.RestaurantMenuImpl;
import com.moxieit.orderplatform.lambda.response.WebsiteMenuCategoryWithItemsResponse;

public class WebSiteMenuItemsImpl implements RequestHandler<String, WebsiteMenuCategoryWithItemsResponse> {

	public WebsiteMenuCategoryWithItemsResponse handleRequest(String restaurantId, Context context) {
		context.getLogger().log("Input: " + restaurantId);

		WebsiteMenuCategoryWithItemsResponse websiteMenuCategoryWithItemsResponse = new WebsiteMenuCategoryWithItemsResponse();
		RestaurantMenuImpl restBotLambdaImpl = new RestaurantMenuImpl();
		try {
			websiteMenuCategoryWithItemsResponse = restBotLambdaImpl.getMenuItemsWithRestaurantId(restaurantId);
		} catch (Exception e) {
			context.getLogger().log(e.getMessage());
		}
		return websiteMenuCategoryWithItemsResponse;

	}
	
}
