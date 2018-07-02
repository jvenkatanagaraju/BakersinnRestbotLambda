package com.moxieit.orderplatform.lambda.function.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
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

public class LexOrderPayNowServiceImpl extends AbstractLexOrderServiceImpl {

	@Override
	public LexResponse serveLex(ILexDTO lexDTO, Context context) {
		LexOrderPayNowService lexOrderCategory = (LexOrderPayNowService) lexDTO;
		String orderuuid = lexOrderCategory.getOrderuuid();
        updateCreationDate(orderuuid);
		LexResponse lexResponse = new LexResponse();
		DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
		ResponseCard responseCard = new ResponseCard();
		responseCard.setVersion("1");
		responseCard.setContentType("application/vnd.amazonaws.card.generic");
		Message message1 = new Message();
		message1.setContentType("PlainText");
		message1.setContent("Please Select one Option");
		List<Buttons> buttons = new ArrayList<Buttons>();
		buttons.add(new Buttons("Delivery", "Delivery_" + orderuuid));
		buttons.add(new Buttons("Pickup", "Pickup_" + orderuuid));
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
