package com.RestBotLambda.lambda.function;

	public enum TableStatus {

		INITIATED(false), PAYMENT_PENDING(false), PAYMENT_SUCCESS(false), ACCEPTED(true), CONFIRMED(true), CONFORMED(true), AVALIBLE(
				true);
		
		
		private Boolean showAdmin;

		private TableStatus(Boolean showAdmin) {
			this.showAdmin = showAdmin;
		}
		public Boolean getShowAdmin() {
			return showAdmin;
		}

	}



