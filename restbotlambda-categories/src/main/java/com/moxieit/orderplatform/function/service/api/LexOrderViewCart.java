package com.moxieit.orderplatform.function.service.api;

public class LexOrderViewCart extends ILexDTO {

	private String orderuuId;

	public String getOrderuuId() {
		return orderuuId;
	}

	public void setOrderuuId(String orderuuId) {
		this.orderuuId = orderuuId;
	}

	@Override
	public String toString() {
		return "LexOrderViewCart [orderuuId=" + orderuuId + "]";
	}

}
