package com.moxieit.orderplatform.lambda.response;

public class LoginResponse {

	private String restaurantId;
	private String restaurantName;
	private String botName;
	private String monToFriHours;
	private String satToSunHours;
	private String status;
	private String message;
	private String image;
	private String state;
	private String street_1;
	private String city;
	private String country;
	private String zipCode;
	private String phone_no;
	private String emailId;
	private String pageName;
	private String securePin;
	private String deviceId;
	private String deviceToken;

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPhone_no() {
		return phone_no;
	}

	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStreet_1() {
		return street_1;
	}

	public void setStreet_1(String street_1) {
		this.street_1 = street_1;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
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

	public String getMonToFriHours() {
		return monToFriHours;
	}

	public void setMonToFriHours(String monToFriHours) {
		this.monToFriHours = monToFriHours;
	}

	public String getSatToSunHours() {
		return satToSunHours;
	}

	public void setSatToSunHours(String satToSunHours) {
		this.satToSunHours = satToSunHours;
	}

}
