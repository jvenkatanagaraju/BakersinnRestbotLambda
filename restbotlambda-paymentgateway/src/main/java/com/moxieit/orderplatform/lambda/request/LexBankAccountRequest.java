package com.moxieit.orderplatform.lambda.request;

public class LexBankAccountRequest {
	

		private String country;

		private String currency;

		private String account_holder_name;

		private String routing_number;

		private String account_number;

		private String account_holder_type;
		
		private String type;
		
		private String emailId;
		
		private String customerId;
		
		private String botName;
		
		

		public String getBotName() {
			return botName;
		}

		public void setBotName(String botName) {
			this.botName = botName;
		}

		public String getEmailId() {
			return emailId;
		}
		
		public String getCustomerId() {
			return customerId;
		}

		public void setEmailId(String emailId) {
			this.emailId = emailId;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}

		public String getAccount_holder_name() {
			return account_holder_name;
		}

		public void setAccount_holder_name(String account_holder_name) {
			this.account_holder_name = account_holder_name;
		}

		public String getRouting_number() {
			return routing_number;
		}

		public void setRouting_number(String routing_number) {
			this.routing_number = routing_number;
		}

		public String getAccount_number() {
			return account_number;
		}

		public void setAccount_number(String account_number) {
			this.account_number = account_number;
		}

		public String getAccount_holder_type() {
			return account_holder_type;
		}

		public void setAccount_holder_type(String account_holder_type) {
			this.account_holder_type = account_holder_type;
		}

		
	


}


