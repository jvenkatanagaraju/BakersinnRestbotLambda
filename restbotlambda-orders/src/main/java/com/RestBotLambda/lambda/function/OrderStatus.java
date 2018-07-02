package com.RestBotLambda.lambda.function;

public enum OrderStatus {

	INITIATED(false), PAYMENT_PENDING(false), PAYMENT_SUCCESS(false), ACCEPTED(true), CONFIRMED(true), CONFORMED(true), COOKING_INPROGRESS(
			true), COOKED_PACKED(true), DISPATCHED(false), DELIVERED(false);

	private Boolean showAdmin;

	private OrderStatus(Boolean showAdmin) {
		this.showAdmin = showAdmin;
	}

	public Boolean getShowAdmin() {
		return showAdmin;
	}

}
	