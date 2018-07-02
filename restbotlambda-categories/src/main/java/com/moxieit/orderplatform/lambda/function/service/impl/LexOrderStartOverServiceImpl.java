package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.api.UpdateItemApi;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.ILexDTO;
import com.moxieit.orderplatform.function.service.api.ILexService;
import com.moxieit.orderplatform.function.service.api.LexOrderStartOverService;
import com.moxieit.orderplatform.lambda.function.LexOrchestration;
import com.moxieit.orderplatform.lambda.request.Bot;
import com.moxieit.orderplatform.lambda.response.LexResponse;

public class LexOrderStartOverServiceImpl extends AbstractLexOrderServiceImpl {

	@Override
	public LexResponse serveLex(ILexDTO lexDTO, Context context) {
		LexOrderStartOverService lexOrderStartOverService = (LexOrderStartOverService) lexDTO;
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderTable = dynamoDB.getTable("Order");
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		String orderuuid = lexOrderStartOverService.getOrderuuid();
		String userId = lexOrderStartOverService.getUserId();
		ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("orderuuid").eq(orderuuid))
				.buildForScan();

		ItemCollection<ScanOutcome> scan = orderItemTable.scan(xspec);
		Consumer<Item> action = new Consumer<Item>() {
			@Override
			public void accept(Item t) {
				try {
					String orderItemuuid = t.getString("uuid");
					DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
							.withPrimaryKey(new PrimaryKey("uuid", orderItemuuid));
					orderItemTable.deleteItem(deleteItemSpec);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		};
		scan.forEach(action);
		UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", orderuuid)
				.withUpdateExpression("set 	totalBill = :val, tax = :tax, totalBillWithTax = :tbtax")
				.withValueMap(new ValueMap().withNumber(":val", 0).withNumber(":tax", 0).withNumber(":tbtax", 0));
		orderTable.updateItem(updateItemSpec);
		updateCreationDate(orderuuid);
		LexOrchestration lexOrchestration = new LexOrchestration();
		Bot bot = new Bot();
		bot.setName(lexDTO.getBotName());
		ObjectMapper oMapper = new ObjectMapper();
		Map<String, Object> map1 = oMapper.convertValue(bot, Map.class);
		Map<String, Object> lexRequest = new HashMap<>();
		lexRequest.put("inputTranscript", "Getstarted");
		lexRequest.put("userId", userId);
		lexRequest.put("bot", map1);
		System.out.println(lexRequest);
		LexResponse lexResponse = lexOrchestration.handleRequest(lexRequest, context);
		return lexResponse;
	}

}
