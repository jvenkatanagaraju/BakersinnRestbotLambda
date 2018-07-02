package com.moxieit.orderplatform.lambda.function.categories;

	import com.amazonaws.services.dynamodbv2.document.DynamoDB;
	import com.amazonaws.services.dynamodbv2.document.Item;
	import com.amazonaws.services.dynamodbv2.document.Table;
	import com.amazonaws.services.lambda.runtime.Context;
	import com.amazonaws.services.lambda.runtime.RequestHandler;
	import com.moxieit.orderplatform.DB.DBService;
	import com.moxieit.orderplatform.lambda.response.GetBankAccountDetailsResponse;

	public class GetBankAccountDetails implements RequestHandler<String, GetBankAccountDetailsResponse> {

		public GetBankAccountDetailsResponse handleRequest(String botName, Context context) {
			// context.getLogger().log(lexRequest);
			GetBankAccountDetailsResponse getBankAccountDetailsResponse = new GetBankAccountDetailsResponse();
			DynamoDB dynamoDB = DBService.getDBConnection();
			Table bankAccountDetailsTable = dynamoDB.getTable("BankAccountDetails");

			Item bankAccountDetailsItem = bankAccountDetailsTable.getItem("botName", botName);
			if (bankAccountDetailsItem != null) {
				getBankAccountDetailsResponse.setBotName(bankAccountDetailsItem.getString("botName"));
				getBankAccountDetailsResponse.setAccount_number(bankAccountDetailsItem.getString("account_number"));
				getBankAccountDetailsResponse.setAccount_holder_name(bankAccountDetailsItem.getString("account_holder_name"));
				getBankAccountDetailsResponse.setCustomerId(bankAccountDetailsItem.getString("customerId"));
				getBankAccountDetailsResponse.setEmailId(bankAccountDetailsItem.getString("emailId"));
				getBankAccountDetailsResponse.setCountry(bankAccountDetailsItem.getString("country"));
				getBankAccountDetailsResponse.setRouting_number(bankAccountDetailsItem.getString("routing_number"));
				getBankAccountDetailsResponse.setCurrency(bankAccountDetailsItem.getString("currency"));
				getBankAccountDetailsResponse.setStatus("success");
				getBankAccountDetailsResponse.setMessage("BotName and UserDetails");
				System.out.println(bankAccountDetailsItem);
				return getBankAccountDetailsResponse;
			}
			getBankAccountDetailsResponse.setStatus("failed");
			getBankAccountDetailsResponse.setMessage("There is no Details with this BotName");
			return getBankAccountDetailsResponse;

		}

	}



