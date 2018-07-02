package com.moxieit.orderplatform.lambda.request;

public class LoginRequest {

	private String phoneNo;

	private String botName;

	private String securePin;
	
	private String deviceId;
	
	private String deviceToken;
	
	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getBotName() {
		return botName;
	}

	public void setBotName(String botName) {
		this.botName = botName;
	}

	public String getSecurePin() {
		return securePin;
	}

	public void setSecurePin(String securePin) {
		this.securePin = securePin;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

}
