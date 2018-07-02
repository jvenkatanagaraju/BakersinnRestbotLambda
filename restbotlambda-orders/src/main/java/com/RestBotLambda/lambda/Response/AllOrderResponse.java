package com.RestBotLambda.lambda.Response;

import java.util.List;

import javax.swing.Spring;

public class AllOrderResponse {

	private String RestaurantId;

	private String RestaurantName;

	private String Address;

	private String botName;
	
	private String waiterId;

	List<OrderResponse> orders;

	public String getBotName() {
		return botName;
	}

	public void setBotName(String botName) {
		this.botName = botName;
	}

	public String getRestaurantId() {
		return RestaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		RestaurantId = restaurantId;
	}

	public String getRestaurantName() {
		return RestaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		RestaurantName = restaurantName;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public List<OrderResponse> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderResponse> orders) {
		this.orders = orders;
	}

	@Override
	public String toString() {
		return "AllOrderResponse [RestaurantId=" + RestaurantId + ", RestaurantName=" + RestaurantName + ", Address="
				+ Address + ", orders=" + orders + "]";
	}

	public String getWaiterId() {
		return waiterId;
	}

	public void setWaiterId(String waiterId) {
		this.waiterId = waiterId;
	}

	
}
