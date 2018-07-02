package com.RestBotLambda.lambda.Request;

public class OrdersHistoryRequest {

	private String restaurantId;

	private String botName;
	
	private String waiterId;

	private String date;

	public String getBotName() {
		return botName;
	}

	public void setBotName(String botName) {
		this.botName = botName;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getwaiterId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getWaiterId() {
		return waiterId;
	}

	public void setWaiterId(String waiterId) {
		this.waiterId = waiterId;
	}

}
