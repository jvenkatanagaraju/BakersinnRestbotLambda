package com.moxieit.orderplatform.lambda.function;



import java.util.List;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.moxieit.orderplatform.lambda.function.service.impl.RestaurantMenuImpl;
import com.moxieit.orderplatform.lambda.response.TablesResponce;


public class WebSiteTableStatus implements RequestHandler<String, List<TablesResponce>>{

	@Override
	public List<TablesResponce> handleRequest(String restaurantId, Context context) {
		context.getLogger().log("Input: " + restaurantId);
		// TODO Auto-generated method stub
		List<TablesResponce> tablesResponce  = null;
	
		RestaurantMenuImpl restBotLambdaImpl = new RestaurantMenuImpl();
	
		try {
			tablesResponce = restBotLambdaImpl.getTables(restaurantId);
		} catch (Exception e) {
			context.getLogger().log(e.getMessage());
		}
		return tablesResponce;
	}

}
