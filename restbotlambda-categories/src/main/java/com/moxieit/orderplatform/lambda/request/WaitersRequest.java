
			
			package com.moxieit.orderplatform.lambda.request;

			public class WaitersRequest {
				
				
				private String waiterName;
				private String waiterId;
				private String securePin;
				private String restaurantId;
				
				
				
			
				public String getSecurePin() {
					return securePin;
				}
				public void setSecurePin(String securePin) {
					this.securePin = securePin;
				}
				public String getRestaurantId() {
					return restaurantId;
				}
				public void setRestaurantId(String restaurantId) {
					this.restaurantId = restaurantId;
				}
				public String getWaiterName() {
					return waiterName;
				}
				public void setWaiterName(String waiterName) {
					this.waiterName = waiterName;
				}
				public String getWaiterId() {
					return waiterId;
				}
				public void setWaiterId(String waiterId) {
					this.waiterId = waiterId;
				}
				
				
			
				
			}
