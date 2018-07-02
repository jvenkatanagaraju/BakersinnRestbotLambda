package com.moxieit.orderplatform.lambda.request;

public class SesRequest {
	
	private String stripeEmail;

	private String 	ordertableuuid;
	

	public String getStripeEmail() {
		return stripeEmail;
	}

	public String setStripeEmail(String stripeEmail) {
		return this.stripeEmail = stripeEmail;
	}

	public String getOrdertableuuid() {
		return ordertableuuid;
	}

	public void setOrdertableuuid(String ordertableuuid) {
		this.ordertableuuid = ordertableuuid;
	}

	

}
