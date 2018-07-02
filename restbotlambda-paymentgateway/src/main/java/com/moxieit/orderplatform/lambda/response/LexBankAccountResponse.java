package com.moxieit.orderplatform.lambda.response;

public class LexBankAccountResponse {
	
	

		private String account_number;

		private String message;

		private String status;

		public LexBankAccountResponse(String account_number) {
			super();
			this.account_number = account_number;
		}

		public String getMessage() {
			return message;
		}

		public LexBankAccountResponse(String account_number, String message, String status) {
			super();
			this.account_number = account_number;
			this.message = message;
			this.status = status;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getAccount_number() {
			return account_number;
		}

		public void setRestaurantId( String account_number) {
			this.account_number = account_number;
		}

	}



