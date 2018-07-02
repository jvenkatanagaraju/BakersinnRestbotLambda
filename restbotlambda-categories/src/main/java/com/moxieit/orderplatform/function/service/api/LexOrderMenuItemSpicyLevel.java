package com.moxieit.orderplatform.function.service.api;

public class LexOrderMenuItemSpicyLevel extends ILexDTO {

	private String orderItemId;

	private String spicyLevel;

	public String getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(String orderItemId) {
		this.orderItemId = orderItemId;
	}

	public String getSpicyLevel() {
		return spicyLevel;
	}

	public void setSpicyLevel(String spicyLevel) {
		this.spicyLevel = spicyLevel;
	}

}
