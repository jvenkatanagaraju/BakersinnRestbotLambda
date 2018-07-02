package com.RestBotLambda.lambda.Response;

public class OrderHistoryResponse {

	private String orderuuid;

	private String orderTracking;

	private String userId;

	private String totalBillWithTax;

	private String orderDate;

	public String getOrderuuid() {
		return orderuuid;
	}

	public void setOrderuuid(String orderuuid) {
		this.orderuuid = orderuuid;
	}

	public String getOrderTracking() {
		return orderTracking;
	}

	public void setOrderTracking(String orderTracking) {
		this.orderTracking = orderTracking;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTotalBillWithTax() {
		return totalBillWithTax;
	}

	public void setTotalBillWithTax(String totalBillWithTax) {
		this.totalBillWithTax = totalBillWithTax;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;

	}

}
