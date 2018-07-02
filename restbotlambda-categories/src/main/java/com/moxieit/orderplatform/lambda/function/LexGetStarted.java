package com.moxieit.orderplatform.lambda.function;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.HttpCallHelper;
import com.moxieit.orderplatform.function.service.api.LexFbStatusDto;
import com.moxieit.orderplatform.lambda.response.Recipient;

public class LexGetStarted implements RequestHandler<Object, String> {

	@Override
	public String handleRequest(Object o, Context context) {
		try {
			DynamoDB dynamoDB = DBService.getDBConnection();
			Table orderTable = dynamoDB.getTable("Order");
			ScanExpressionSpec xspec = new ExpressionSpecBuilder()
					.withCondition(N("creationDate").le(System.currentTimeMillis() - 900000)
							.and(S("orderStatus").eq("Initiated").and(S("returnMessage").eq("false"))))
					.buildForScan();
			ItemCollection<ScanOutcome> scan = orderTable.scan(xspec);
			Consumer<Item> action = new Consumer<Item>() {
				@Override
				public void accept(Item t) {
					String userId = t.getString("userId");
					String pageAccessToken = t.getString("pageAccessToken");
					LexFbStatusDto lexFbStatusDto = new LexFbStatusDto();
					Recipient recipient = new Recipient();
					recipient.setId(userId);
					com.moxieit.orderplatform.lambda.response.Message message = new com.moxieit.orderplatform.lambda.response.Message();
					message.setText("Your session has Expired please type GETSTARTED to order Food");
					System.out.println(message.getText());
					lexFbStatusDto.setRecipient(recipient);
					lexFbStatusDto.setMessage(message);
					Gson gson = new Gson();
					String json1 = gson.toJson(lexFbStatusDto);
					System.out.println(json1);
					HttpCallHelper httpCallHelper = new HttpCallHelper();
					Map<String, String> headers = new HashMap<>();
					headers.put("Content-Type", "application/json");
					Map<String, String> params = new HashMap<>();
					params.put("access_token", pageAccessToken);
					httpCallHelper.doPostCall(headers, "https://graph.facebook.com/v2.6/me/messages", params, json1);
					UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", t.getString("uuid"))
							.withUpdateExpression("set returnMessage = :val")
							.withValueMap(new ValueMap().withString(":val", "true"));
					orderTable.updateItem(updateItemSpec);

				}
			};
			scan.forEach(action);

		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

}
