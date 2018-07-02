package com.RestBotLambda.lambda.Response;

import java.util.List;
import java.util.Map;

public class OrderResponse {

	private String orderId;

	private String status;

	private Number totalPrice;

	private String orderTracking;
	private String phoneNumber;
	private String orderDate;
	private String address;
	private String pickUp;
	private String orderTime;
	private String orderFrom;
	private String paymentMethod;

	public String getOrderFrom() {
		return orderFrom;
	}

	public void setOrderFrom(String orderFrom) {
		this.orderFrom = orderFrom;
	}

	Map<String, List<CommonResponseforOrderItems>> foodItems;

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderTracking() {
		return orderTracking;
	}

	public void setOrderTracking(String orderTracking) {
		this.orderTracking = orderTracking;
	}

	public Number getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Number totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String OrderId) {
		this.orderId = OrderId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Map<String, List<CommonResponseforOrderItems>> getFoodItems() {
		return foodItems;
	}

	public void setFoodItems(Map<String, List<CommonResponseforOrderItems>> foodItems) {
		this.foodItems = foodItems;
	}

	@Override
	public String toString() {
		return "OrderResponse [orderId=" + orderId + ", status=" + status + ", totalPrice=" + totalPrice
				+ ", foodItems=" + foodItems + "]";
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPickUp() {
		return pickUp;
	}

	public void setPickUp(String pickUp) {
		this.pickUp = pickUp;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

}
