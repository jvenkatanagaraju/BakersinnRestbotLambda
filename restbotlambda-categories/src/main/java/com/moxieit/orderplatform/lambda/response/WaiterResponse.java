package com.moxieit.orderplatform.lambda.response;

public class WaiterResponse {
private String restaurantId;
	
	private String waiterId;
	
	private String waiterName;
	
	private String SecurePin;
	
	
	public WaiterResponse() {
		// TODO Auto-generated constructor stub
	}

	public WaiterResponse(String restaurantId, String waiterId, String Name,String SecurePin) {
		this.restaurantId = restaurantId;
		this.setWaiterId(waiterId);
		this.setWaiterName(waiterName);
		this.setSecurePin(SecurePin);
		
	}

	

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getWaiterId() {
		return waiterId;
	}

	public void setWaiterId(String waiterId) {
		this.waiterId = waiterId;
	}

	

	public String getSecurePin() {
		return SecurePin;
	}

	public void setSecurePin(String securePin) {
		SecurePin = securePin;
	}

	public String getWaiterName() {
		return waiterName;
	}

	public void setWaiterName(String waiterName) {
		this.waiterName = waiterName;
	}

	


}
