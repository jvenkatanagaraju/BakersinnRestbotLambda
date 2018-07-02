package com.moxieit.orderplatform.lambda.function;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;

public class PaymentGatewayTest {

	public void testPaymentGateway()
	{
	LexPaymentButton lexPaymentButton=new LexPaymentButton();

	Context context = null;
	String response=lexPaymentButton.handleRequest("8b933bc7-af80-46f5-9c47-fae27420ff10", context);
	System.out.println(response);
	}
	public static void main(String[] args) {
		PaymentGatewayTest paymentGatewayTest=new PaymentGatewayTest();
		paymentGatewayTest.testPaymentGateway();
	}
}
