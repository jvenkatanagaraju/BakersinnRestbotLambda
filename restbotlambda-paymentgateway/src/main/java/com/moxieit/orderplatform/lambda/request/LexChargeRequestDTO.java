package com.moxieit.orderplatform.lambda.request;

public class LexChargeRequestDTO {

	private String stripeTokenType;

	private String stripeToken;

	private String stripeEmail;

	private String order;

	public String getStripeTokenType() {
		return stripeTokenType;
	}

	public void setStripeTokenType(String stripeTokenType) {
		this.stripeTokenType = stripeTokenType;
	}

	public String getStripeToken() {
		return stripeToken;
	}

	public void setStripeToken(String stripeToken) {
		this.stripeToken = stripeToken;
	}

	public String getStripeEmail() {
		return stripeEmail;
	}

	public void setStripeEmail(String stripeEmail) {
		this.stripeEmail = stripeEmail;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "LexChargeRequestDTO [stripeTokenType=" + stripeTokenType + ", stripeToken=" + stripeToken
				+ ", stripeEmail=" + stripeEmail + ", order=" + order + "]";
	}

}
