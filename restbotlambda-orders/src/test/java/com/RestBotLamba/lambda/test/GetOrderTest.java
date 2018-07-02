package com.RestBotLamba.lambda.test;

import org.junit.Test;

import com.RestBotLambda.lambda.Response.AllOrderResponse;
import com.RestBotLambda.lambda.function.GetOrders;
import com.amazonaws.services.lambda.runtime.Context;

public class GetOrderTest {

	private Context createContext() {
		TestContext ctx = new TestContext();
		ctx.setFunctionName("Your Function Name");
		return ctx;
	}

	@Test
	public void testsampleinput() {
		GetOrders handler = new GetOrders();
		Context ctx = createContext();
		try {
			AllOrderResponse output = handler.handleRequest("SitaraBot", ctx);
			System.out.println(output);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) {
		GetOrderTest getOrderTest = new GetOrderTest();
		getOrderTest.testsampleinput();
	}
}
