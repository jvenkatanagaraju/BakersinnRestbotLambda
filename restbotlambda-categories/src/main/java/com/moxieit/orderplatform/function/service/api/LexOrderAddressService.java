package com.moxieit.orderplatform.function.service.api;

public class LexOrderAddressService extends ILexDTO {

	private String orderuuid;
	private String address;
	

	public String getOrderuuid() {
		return orderuuid;
	}

	public void setOrderuuid(String orderuuid) {
		this.orderuuid = orderuuid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	

}
