package com.moxieit.orderplatform.lambda.function.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.ILexDTO;
import com.moxieit.orderplatform.function.service.api.LexOrderMenuItemSpicyLevel;
import com.moxieit.orderplatform.lambda.function.LexOrchestration;
import com.moxieit.orderplatform.lambda.request.Bot;
import com.moxieit.orderplatform.lambda.request.LexOrchestrationRequest;
import com.moxieit.orderplatform.lambda.response.LexResponse;

public class LexMenuOrderItemSpicyLevelServiceImpl extends AbstractLexOrderServiceImpl {

	@Override
	public LexResponse serveLex(ILexDTO lexDTO, Context context) {
		LexOrderMenuItemSpicyLevel lexOrderMenuItemSpicyLevel = (LexOrderMenuItemSpicyLevel) lexDTO;
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Item orderItem = orderItemTable.getItem("uuid", lexOrderMenuItemSpicyLevel.getOrderItemId());
		String orderTableuuid = orderItem.getString("orderuuid");
		String userId = orderItem.getString("userId");
		UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", orderItem.get("uuid"))
				.withUpdateExpression("set spiceyLevel = :val")
				.withValueMap(new ValueMap().withString(":val", lexOrderMenuItemSpicyLevel.getSpicyLevel()));
		UpdateItemOutcome outcome = orderItemTable.updateItem(updateItemSpec);
		outcome.getItem();
		updateCreationDate(orderTableuuid);
		LexOrchestration lexOrchestration = new LexOrchestration();
		Bot bot = new Bot();
		bot.setName(lexDTO.getBotName());
		ObjectMapper oMapper = new ObjectMapper();
		Map<String, Object> map1 = oMapper.convertValue(bot, Map.class);
		Map<String, Object> lexRequest = new HashMap<>();
		lexRequest.put("inputTranscript", "ViewCart_" + orderTableuuid);
		lexRequest.put("userId", userId);
		lexRequest.put("bot", map1);
		System.out.println(lexRequest);

		LexResponse lexResponse = lexOrchestration.handleRequest(lexRequest, context);

		return lexResponse;
	}
}
