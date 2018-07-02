package com.moxieit.orderplatform.function.service.api;

public class LexOrderMenuItemQuantity extends ILexDTO {

	private String orderItemId;

	private Number quantity;

	public String getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(String orderItemId) {
		this.orderItemId = orderItemId;
	}

	public Number getQuantity() {
		return quantity;
	}

	public void setQuantity(Number quantity) {
		this.quantity = quantity;
	}

	

}
