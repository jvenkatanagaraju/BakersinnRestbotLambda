package com.moxieit.orderplatform.lambda.function.categories;

import java.io.IOException;

import org.junit.BeforeClass;

import com.RestBotLambda.lambda.Request.OrdersHistoryRequest;
import com.RestBotLambda.lambda.Response.AllOrderResponse;
import com.RestBotLambda.lambda.function.OrdersHistoryImpl;
import com.amazonaws.services.lambda.runtime.Context;

public class OrderHistoryTest {

	@BeforeClass
	public static void createInput() throws IOException {
		// TODO: set up your sample input object here.

	}

	private Context createContext() {
		TestContext ctx = new TestContext();

		// TODO: customize your context here if needed.
		ctx.setFunctionName("Your Function Name");

		return ctx;
	}

	public void testGetStarted() {

	}

	public static void main(String[] args) {
		OrdersHistoryImpl ordersHistoryImpl = new OrdersHistoryImpl();
		OrdersHistoryRequest ordersHistoryRequest = new OrdersHistoryRequest();
		ordersHistoryRequest.setRestaurantId("1");
		ordersHistoryRequest.setBotName("Test");
		ordersHistoryRequest.setDate("17/07/2017");
		Context context = null;
		AllOrderResponse orderHistoryResponses = ordersHistoryImpl.handleRequest(ordersHistoryRequest,
				context);
		System.out.println(orderHistoryResponses);
	}
}
