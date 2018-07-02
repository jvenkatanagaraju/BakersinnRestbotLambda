package com.moxieit.orderplatform.lambda.request;

public class DeviceTokenRequest {
	private String deviceToken;
	private String botName;

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getBotName() {
		return botName;
	}

	public void setBotName(String botName) {
		this.botName = botName;
	}

}
