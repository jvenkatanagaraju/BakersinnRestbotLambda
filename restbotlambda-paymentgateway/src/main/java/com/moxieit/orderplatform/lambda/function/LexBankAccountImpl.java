package com.moxieit.orderplatform.lambda.function;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxieit.orderplatform.lambda.DB.DBService;
import com.moxieit.orderplatform.lambda.request.LexBankAccountRequest;
import com.moxieit.orderplatform.lambda.response.LexBankAccountResponse;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Customer;
import com.stripe.model.Token;

public class LexBankAccountImpl implements RequestHandler<LexBankAccountRequest, LexBankAccountResponse> {


	private HashMap<String, Object> custmorParams;

	@Override
	public LexBankAccountResponse handleRequest(LexBankAccountRequest lexBankAccountRequest, Context context) {

		try {
			DynamoDB dynamoDB = DBService.getDBConnection();

			String type = lexBankAccountRequest.getType();
			Table bankAccountDetailsTable = dynamoDB.getTable("BankAccountDetails");

			if (type != null) {
				switch (type) {
				case "Insert": {
					Item bankDetailsItem=bankAccountDetailsTable.getItem("botName",lexBankAccountRequest.getBotName());
					if(bankDetailsItem==null){
					Stripe.apiKey = "sk_live_p5EAJtOI50fF6LQSFftY5Hqc";
					//Stripe.apiKey = "sk_test_h8l3zo52N4fKamsxtuTBQ0lH";

			
				 
					Map<String, Object> tokenParams = new HashMap<String, Object>();
					Map<String, Object> bank_accountParams = new HashMap<String, Object>();
					bank_accountParams.put("country", lexBankAccountRequest.getCountry());
					bank_accountParams.put("currency", lexBankAccountRequest.getCurrency());
					bank_accountParams.put("account_holder_name", lexBankAccountRequest.getAccount_holder_name());
					bank_accountParams.put("routing_number", lexBankAccountRequest.getRouting_number());
					bank_accountParams.put("account_holder_type", lexBankAccountRequest.getAccount_holder_type());
					bank_accountParams.put("account_number", lexBankAccountRequest.getAccount_number());
					tokenParams.put("bank_account", bank_accountParams);
					String tokenId = null;
					ObjectMapper oMapper = new ObjectMapper();
					try {
						Object tokenDetails = Token.create(tokenParams);
						Map<String, String> map3 = oMapper.convertValue(tokenDetails, Map.class);
						tokenId = map3.get("id");

					} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException
							| APIException e1) {
						// TODO Auto-generated catch block
						((Throwable) e1).printStackTrace();
					}

					Map<String, Object> customerParams = new HashMap<String, Object>();
					customerParams.put("description", "Customer for " + lexBankAccountRequest.getEmailId());
					customerParams.put("source", tokenId);
					// ^ obtained with Stripe.js
					String customerId=null;
					try {

						Object costumerDetails = Customer.create(customerParams);
						Map<String, String> map3 = oMapper.convertValue(costumerDetails, Map.class);
						customerId = map3.get("id");


					} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException
							| APIException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					if (lexBankAccountRequest != null) {
						Item item = new Item();
						item.withString("country", lexBankAccountRequest.getCountry())
								.withString("currency", lexBankAccountRequest.getCurrency())
								.withString("account_holder_name", lexBankAccountRequest.getAccount_holder_name())
								.withString("routing_number", lexBankAccountRequest.getRouting_number())
								.withString("account_number", lexBankAccountRequest.getAccount_number())
								.withString("account_holder_type", lexBankAccountRequest.getAccount_holder_type())
								.withString("emailId", lexBankAccountRequest.getEmailId())
								.withString("customerId", customerId).withString("botName", lexBankAccountRequest.getBotName());
						      
						bankAccountDetailsTable.putItem(item);
						return new LexBankAccountResponse(lexBankAccountRequest.getAccount_number(),
								"Successfully Registered your LexBankAccount Details", "success");
					}}
					
				return new LexBankAccountResponse( "Details  already saved ");
			
				}
					
				

				case "Update": {
					Stripe.apiKey = "sk_live_p5EAJtOI50fF6LQSFftY5Hqc";
					//Stripe.apiKey = "sk_test_h8l3zo52N4fKamsxtuTBQ0lH";
					Item bankAccountDetailsItem=bankAccountDetailsTable.getItem("botName",lexBankAccountRequest.getBotName());
					String emailId=null;
					if(bankAccountDetailsItem!=null){
					Customer Customer = com.stripe.model.Customer.retrieve(bankAccountDetailsItem.getString("customerId"));
					Map<String, Object> updateParams = new HashMap<String, Object>();
					updateParams.put("description", "Customer for"+ lexBankAccountRequest.getEmailId());
				
					// ^ obtained with Stripe.js
					try {

						Customer = Customer.update(updateParams);

					} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException
							| APIException e2) {
						e2.printStackTrace();
					}
						if (bankAccountDetailsItem.getString("emailId") != null) {
								emailId = lexBankAccountRequest.getEmailId();
						} else {
								emailId = bankAccountDetailsItem.getString("emailId");
						}
						
						UpdateItemSpec updateItemSpec = new UpdateItemSpec()
								.withPrimaryKey("botName", lexBankAccountRequest.getBotName())
								.withUpdateExpression("set emailId = :em")
								.withValueMap(new ValueMap().withString(":em", emailId));
								
						bankAccountDetailsTable.updateItem(updateItemSpec);
						return new LexBankAccountResponse(null, "Successfully Updated your LexBankAccount details",
								"success");
					}
				}
				case "Delete": {
			

					Item bankAccountDetailsItem = bankAccountDetailsTable.getItem("botName", lexBankAccountRequest.getBotName());
					if (bankAccountDetailsItem != null) {
						//Stripe.apiKey = "sk_test_h8l3zo52N4fKamsxtuTBQ0lH";
						Stripe.apiKey = "sk_live_p5EAJtOI50fF6LQSFftY5Hqc";
						Customer Customer = com.stripe.model.Customer.retrieve(bankAccountDetailsItem.getString("customerId"));
						Customer.delete();
						DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
								.withPrimaryKey(new PrimaryKey("botName", bankAccountDetailsItem.getString("botName")));
						bankAccountDetailsTable.deleteItem(deleteItemSpec);
						return new LexBankAccountResponse(bankAccountDetailsItem.getString("account_number"), "Successfully Deleted your LexBankAccount details",
								"success");
					}
				}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			context.getLogger().log("Exception is :" + e);
		}

		return new LexBankAccountResponse(null, "failed the operation", "failed");

	}

	private Item currency() {
		// TODO Auto-generated method stub
		return null;
	}

	private Item account_holder_name() {
		// TODO Auto-generated method stub
		return null;
	}



}
