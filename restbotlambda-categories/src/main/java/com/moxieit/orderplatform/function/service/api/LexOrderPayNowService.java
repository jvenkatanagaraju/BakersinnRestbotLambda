package com.moxieit.orderplatform.function.service.api;

public class LexOrderPayNowService extends ILexDTO {

	private String orderuuid;
	private String pickUp;

	public String getOrderuuid() {
		return orderuuid;
	}

	public void setOrderuuid(String orderuuid) {
		this.orderuuid = orderuuid;
	}

	public String getPickUp() {
		return pickUp;
	}

	public void setPickUp(String pickUp) {
		this.pickUp = pickUp;
	}

}
