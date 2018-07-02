package com.moxieit.orderplatform.function.service.api;

public class ILexDTO {

	private String userId;

	private String inputTranscript;

	private String botName;

	private String restaurantId;

	private String pageAccessToken;

	public String getPageAccessToken() {
		return pageAccessToken;
	}

	public void setPageAccessToken(String pageAccessToken) {
		this.pageAccessToken = pageAccessToken;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getBotName() {
		return botName;
	}

	public void setBotName(String botName) {
		this.botName = botName;
	}

	public String getInputTranscript() {
		return inputTranscript;
	}

	public void setInputTranscript(String inputTranscript) {
		this.inputTranscript = inputTranscript;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
