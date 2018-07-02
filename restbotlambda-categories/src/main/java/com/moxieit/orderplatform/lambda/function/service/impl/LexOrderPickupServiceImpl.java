package com.moxieit.orderplatform.lambda.function.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.ILexDTO;
import com.moxieit.orderplatform.function.service.api.LexOrderPayNowService;
import com.moxieit.orderplatform.lambda.response.Buttons;
import com.moxieit.orderplatform.lambda.response.DialogActionResponseForElicitIntent;
import com.moxieit.orderplatform.lambda.response.GenericAttachments;
import com.moxieit.orderplatform.lambda.response.LexResponse;
import com.moxieit.orderplatform.lambda.response.Message;
import com.moxieit.orderplatform.lambda.response.ResponseCard;
import com.moxieit.orderplatform.lambda.response.Slots;

public class LexOrderPickupServiceImpl extends AbstractLexOrderServiceImpl {

	@Override
	public LexResponse serveLex(ILexDTO lexDTO, Context context) {
		// TODO Auto-generated method stub
		LexOrderPayNowService lexOrderCategory = (LexOrderPayNowService) lexDTO;
		String orderuuid = lexOrderCategory.getOrderuuid();
		updateCreationDate(orderuuid);
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderTable = dynamoDB.getTable("Order");
		Table restaurantTable = dynamoDB.getTable("Restaurant");
		Item restaurantItem = restaurantTable.getItem("botName", lexOrderCategory.getBotName());
		/*String street_1 = restaurantItem.getString("street_1");
		String state = restaurantItem.getString("state");
		String address = street_1 + state;
		UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", orderuuid)
				.withUpdateExpression("set address = :val").withValueMap(new ValueMap().withString(":val", address));
		orderTable.updateItem(updateItemSpec);*/
		LexResponse lexResponse = new LexResponse();
		DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
		ResponseCard responseCard = new ResponseCard();
		responseCard.setVersion("1");
		responseCard.setContentType("application/vnd.amazonaws.card.generic");
		Message message1 = new Message();
		message1.setContentType("PlainText");
		message1.setContent("Please select pickup time");
		List<Buttons> buttons = new ArrayList<Buttons>();
		buttons.add(new Buttons("10 Mins", "10 Mins_" + orderuuid));
		buttons.add(new Buttons("20 Mins", "20 Mins_" + orderuuid));
		buttons.add(new Buttons("30 Mins", "30 Mins_" + orderuuid));
		List<GenericAttachments> genericAttachments = new ArrayList<GenericAttachments>();
		genericAttachments.add(new GenericAttachments("Please Select One", ".", null, null, buttons, null));
		GenericAttachments[] attachments1 = new GenericAttachments[genericAttachments.size()];
		attachments1 = genericAttachments.toArray(attachments1);
		responseCard.setGenericAttachments(attachments1);
		Slots slots = new Slots();
		slots.setSlotName("Hours");
		dialogAction.setType("ElicitSlot");
		dialogAction.setIntentName("GetStarted");
		dialogAction.setSlots(slots);
		dialogAction.setSlotToElicit("slotName");
		dialogAction.setMessage(message1);
		dialogAction.setResponseCard(responseCard);
		lexResponse.setDialogAction(dialogAction);
		context.getLogger().log("output: " + lexResponse.getDialogAction());
		return lexResponse;
	}

}
