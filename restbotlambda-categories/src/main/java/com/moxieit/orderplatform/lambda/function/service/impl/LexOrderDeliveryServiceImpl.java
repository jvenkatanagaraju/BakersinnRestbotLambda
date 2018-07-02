package com.moxieit.orderplatform.lambda.function.service.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.function.service.api.ILexDTO;
import com.moxieit.orderplatform.function.service.api.LexOrderPayNowService;
import com.moxieit.orderplatform.lambda.response.DialogActionResponseForElicitIntent;
import com.moxieit.orderplatform.lambda.response.LexResponse;
import com.moxieit.orderplatform.lambda.response.Message;
import com.moxieit.orderplatform.lambda.response.ResponseCard;
import com.moxieit.orderplatform.lambda.response.Slots;

public class LexOrderDeliveryServiceImpl extends AbstractLexOrderServiceImpl {

	@Override
	public LexResponse serveLex(ILexDTO lexDTO, Context context) {
		// TODO Auto-generated method stub
		LexOrderPayNowService lexOrderCategory = (LexOrderPayNowService) lexDTO;
		updateCreationDate(lexOrderCategory.getOrderuuid());
		LexResponse lexResponse = new LexResponse();
		DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
		ResponseCard responseCard = new ResponseCard();
		responseCard.setVersion("1");
		responseCard.setContentType("application/vnd.amazonaws.card.generic");
		Message message1 = new Message();
		message1.setContentType("PlainText");
		message1.setContent("Please Enter Delivery Address");
		Slots slots = new Slots();
		slots.setSlotName("Hours");
		dialogAction.setType("ElicitSlot");
		dialogAction.setIntentName("GetStarted");
		dialogAction.setSlots(slots);
		dialogAction.setSlotToElicit("slotName");
		dialogAction.setMessage(message1);
		lexResponse.setDialogAction(dialogAction);
		context.getLogger().log("output: " + lexResponse.getDialogAction());
		return lexResponse;
	}

}
