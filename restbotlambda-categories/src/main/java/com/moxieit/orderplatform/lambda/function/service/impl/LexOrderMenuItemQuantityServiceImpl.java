package com.moxieit.orderplatform.lambda.function.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.moxieit.orderplatform.function.service.api.LexOrderMenuItemQuantity;
import com.moxieit.orderplatform.lambda.function.LexOrchestration;
import com.moxieit.orderplatform.lambda.request.Bot;
import com.moxieit.orderplatform.lambda.response.Buttons;
import com.moxieit.orderplatform.lambda.response.DialogActionResponseForElicitIntent;
import com.moxieit.orderplatform.lambda.response.GenericAttachments;
import com.moxieit.orderplatform.lambda.response.LexResponse;
import com.moxieit.orderplatform.lambda.response.Message;
import com.moxieit.orderplatform.lambda.response.ResponseCard;
import com.moxieit.orderplatform.lambda.response.Slots;

public class LexOrderMenuItemQuantityServiceImpl extends AbstractLexOrderServiceImpl {

	// THis class gets latest order and order item then updates it
	@Override
	public LexResponse serveLex(ILexDTO lexDTO, Context context) {
		LexOrderMenuItemQuantity lexOrderMenuItemQuantity = (LexOrderMenuItemQuantity) lexDTO;
		String userId = lexOrderMenuItemQuantity.getUserId();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderTable = dynamoDB.getTable("Order");
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table menuItemsTable = dynamoDB.getTable("Menu_Items");
		String orderItemuuid = lexOrderMenuItemQuantity.getOrderItemId();
		Item orderItem = orderItemTable.getItem("uuid", orderItemuuid);
		if (orderItem != null) {
			Number existingQuantity = orderItem.getNumber("quantity");
			String orderuuid = orderItem.getString("orderuuid");		
			Number orderItemCost = orderItem.getNumber("itemCost");
			Item orderTableItem = orderTable.getItem("uuid", orderuuid);
			Number existBilled = (Number) orderTableItem.get("totalBill");
			String menuItemId = orderItem.getString("menuItemId");
			String[] menuItemIdSplit = menuItemId.split("_");
			String categoryId = menuItemIdSplit[0] + "_" + menuItemIdSplit[1];
			int exisQuantity = 0;
			Double existBill = 0.0;
			try {
				exisQuantity = existingQuantity.intValue();
				existBill = existBilled.doubleValue();
			} catch (NumberFormatException e) {

			}
			int totalQuantity = exisQuantity + (int) lexOrderMenuItemQuantity.getQuantity();
			UpdateItemSpec updateItemSpec1 = new UpdateItemSpec().withPrimaryKey("uuid", orderItem.get("uuid"))
					.withUpdateExpression("set quantity = :val")
					.withValueMap(new ValueMap().withNumber(":val", (Number) totalQuantity));
			UpdateItemOutcome outcome1 = orderItemTable.updateItem(updateItemSpec1);
			orderItem = outcome1.getItem();
			Double totalValue = (existBill
					+ (lexOrderMenuItemQuantity.getQuantity().doubleValue() * (orderItemCost.doubleValue())));
			Double taxper = .06;
			Double tax = (double) totalValue * taxper.doubleValue();
			Double totalValueWithTax = totalValue + tax;
			UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", orderuuid)
					.withUpdateExpression("set totalBill = :val,tax = :tax ,totalBillWithTax = :tbtax")
					.withValueMap(new ValueMap().withNumber(":val", (Number) totalValue)
							.withNumber(":tax", (Number) tax).withNumber(":tbtax", (Number) totalValueWithTax));
			UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
			orderItem = outcome2.getItem();
             updateCreationDate(orderuuid);
			Object menuCategoryId = categoryId;
			Object menuItemIdd = menuItemId;
			Item menuItem = menuItemsTable.getItem("categoryId", menuCategoryId, "itemId", menuItemIdd);
			if (menuItem != null) {
				boolean isSpicy = (boolean) menuItem.get("isSpicy");
				if (isSpicy) {
					LexResponse lexResponse = new LexResponse();
					DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
					ResponseCard responseCard = new ResponseCard();
					Message message = new Message();
					message.setContentType("PlainText");
					message.setContent("Spice Level");
					responseCard.setVersion("1");
					responseCard.setContentType("application/vnd.amazonaws.card.generic");
					List<Buttons> buttons = new ArrayList<Buttons>();
					buttons.add(new Buttons("Mild", "menuItemSpicy_" + orderItemuuid + "_Mild"));
					buttons.add(new Buttons("Medium", "menuItemSpicy_" + orderItemuuid + "_Medium"));
					buttons.add(new Buttons("Spicy", "menuItemSpicy_" + orderItemuuid + "_Spicy"));
					Slots slots = new Slots();
					slots.setSlotName("Hours");
					List<GenericAttachments> genericAttachments = new ArrayList<GenericAttachments>();
					genericAttachments
							.add(new GenericAttachments("Choose the Spice level", ".", null, null, buttons, null));
					GenericAttachments[] attachments = new GenericAttachments[genericAttachments.size()];
					attachments = genericAttachments.toArray(attachments);
					responseCard.setGenericAttachments(attachments);
					dialogAction.setType("ElicitSlot");
					dialogAction.setIntentName("GetStarted");
					dialogAction.setSlots(slots);
					dialogAction.setSlotToElicit("slotName");
					dialogAction.setMessage(message);
					dialogAction.setResponseCard(responseCard);
					lexResponse.setDialogAction(dialogAction);
					context.getLogger().log("output: " + lexResponse.getDialogAction());
					return lexResponse;
				} else {
					LexOrchestration lexOrchestration = new LexOrchestration();
					Bot bot = new Bot();
					bot.setName(lexDTO.getBotName());
					ObjectMapper oMapper = new ObjectMapper();
					Map<String, Object> map1 = oMapper.convertValue(bot, Map.class);
					Map<String, Object> lexRequest = new HashMap<>();
					lexRequest.put("inputTranscript", "ViewCart_" + orderuuid);
					lexRequest.put("userId", userId);
					lexRequest.put("bot", map1);
					System.out.println(lexRequest);
					LexResponse lexResponse = lexOrchestration.handleRequest(lexRequest, context);
					return lexResponse;

				}
			}
		}
		return null;
	}

}
