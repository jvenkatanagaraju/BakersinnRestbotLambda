package com.moxieit.orderplatform.lambda.function.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.ILexDTO;
import com.moxieit.orderplatform.function.service.api.ILexService;
import com.moxieit.orderplatform.function.service.api.LexOrderPayNowService;
import com.moxieit.orderplatform.lambda.response.Buttons;
import com.moxieit.orderplatform.lambda.response.DialogActionResponseForElicitIntent;
import com.moxieit.orderplatform.lambda.response.GenericAttachments;
import com.moxieit.orderplatform.lambda.response.LexResponse;
import com.moxieit.orderplatform.lambda.response.Message;
import com.moxieit.orderplatform.lambda.response.ResponseCard;
import com.moxieit.orderplatform.lambda.response.Slots;

public class LexClickHereServiceImpl implements ILexService {

	@Override
	public LexResponse serveLex(ILexDTO lexDTO, Context context) {
		// TODO Auto-generated method stub
		LexOrderPayNowService lexOrderCategory = (LexOrderPayNowService) lexDTO;
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderTable = dynamoDB.getTable("Order");
		KeyAttribute primaryKey = new KeyAttribute("uuid", lexOrderCategory.getOrderuuid());
		Item order = orderTable.getItem(primaryKey);
		if (order != null) {
			LexResponse lexResponse = new LexResponse();
			DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
			ResponseCard responseCard = new ResponseCard();
			responseCard.setVersion("1");
			responseCard.setContentType("application/vnd.amazonaws.card.generic");
			Message message1 = new Message();
			message1.setContentType("PlainText");
			message1.setContent("Choose a Button");
			List<Buttons> buttons = new ArrayList<Buttons>();
			buttons.add(new Buttons("PayNow", "PayNow_" + order.getString("uuid")));
			buttons.add(new Buttons("Continue", "Continue_" + order.getString("uuid")));
			buttons.add(new Buttons("StartOver", "StartOver_" + order.getString("uuid")));
			List<GenericAttachments> genericAttachments = new ArrayList<GenericAttachments>();
			genericAttachments.add(new GenericAttachments("Please Select One ", ".", null, null, buttons, null));
			GenericAttachments[] attachments2 = new GenericAttachments[genericAttachments.size()];
			attachments2 = genericAttachments.toArray(attachments2);
			responseCard.setGenericAttachments(attachments2);
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
			Gson gson1 = new Gson();
			String json1 = gson1.toJson(lexResponse);
			System.out.println(json1);
			return lexResponse;
		}
		return null;
	}
}
