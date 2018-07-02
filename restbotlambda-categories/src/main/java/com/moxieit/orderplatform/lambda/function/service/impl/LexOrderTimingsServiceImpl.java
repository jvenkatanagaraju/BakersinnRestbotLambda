package com.moxieit.orderplatform.lambda.function.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.moxieit.orderplatform.function.service.api.HttpCallHelper;
import com.moxieit.orderplatform.function.service.api.ILexDTO;
import com.moxieit.orderplatform.function.service.api.ILexService;
import com.moxieit.orderplatform.function.service.api.LexOrderPayNowService;
import com.moxieit.orderplatform.lambda.response.Attachment;
import com.moxieit.orderplatform.lambda.response.Buttons;
import com.moxieit.orderplatform.lambda.response.DialogActionResponseForElicitIntent;
import com.moxieit.orderplatform.lambda.response.GenericAttachments;
import com.moxieit.orderplatform.lambda.response.LexResponse;
import com.moxieit.orderplatform.lambda.response.Message;
import com.moxieit.orderplatform.lambda.response.Payload;
import com.moxieit.orderplatform.lambda.response.Recipient;
import com.moxieit.orderplatform.lambda.response.ResponseCard;
import com.moxieit.orderplatform.lambda.response.Slots;
import com.moxieit.orderplatform.lambda.response.ViewCartResponse;

public class LexOrderTimingsServiceImpl extends AbstractLexOrderServiceImpl {

	@Override
	public LexResponse serveLex(ILexDTO lexDTO, Context context) {
		LexOrderPayNowService lexOrderCategory = (LexOrderPayNowService) lexDTO;
		String userId = lexOrderCategory.getUserId();
		String pickUp = lexOrderCategory.getPickUp();
		String orderuuid = lexOrderCategory.getOrderuuid();
		Table orderTable = dynamoDB.getTable("Order");
		UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", orderuuid)
				.withUpdateExpression("set pickUp = :val,address = :add")
				.withValueMap(new ValueMap().withString(":val", pickUp).withString(":add", "NULL"));
		orderTable.updateItem(updateItemSpec);
		updateCreationDate(orderuuid);
		
		
		LexResponse lexResponse = new LexResponse();
		DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
		ResponseCard responseCard = new ResponseCard();
		responseCard.setVersion("1");
		responseCard.setContentType("application/vnd.amazonaws.card.generic");
		Message message1 = new Message();
		message1.setContentType("PlainText");
		message1.setContent("Please Enter your 10 Digit Phone Number");
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
		/*ClassLoader classLoader=getClass().getClassLoader();
		File file = new File(classLoader.getResource("file/aws.properties").getFile());
			String readFileToString=null;
			try {
				readFileToString = FileUtils.readFileToString(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] url=readFileToString.split("=");
			String urlforApi=url[1];
		if (userId.contains("us-east-1")) {
			LexResponse lexResponse = new LexResponse();
			DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
			ResponseCard responseCard = new ResponseCard();
			responseCard.setVersion("1");
			responseCard.setContentType("application/vnd.amazonaws.card.generic");
			Message message1 = new Message();
			message1.setContentType("PlainText");
			message1.setContent("Pay here");
			List<Buttons> buttons = new ArrayList<Buttons>();
			buttons.add(new Buttons("Payment",
					urlforApi +"?order="+ orderuuid));
			List<GenericAttachments> genericAttachments = new ArrayList<GenericAttachments>();
			genericAttachments.add(new GenericAttachments("Payment", ".", null, urlforApi +"?order=" + orderuuid, buttons, null));
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
		} else {
			updateCreationDate(orderuuid);
			ViewCartResponse viewCartResponse = new ViewCartResponse();
			Recipient recipient = new Recipient();
			recipient.setId(userId);
			Message message = new Message();
			Attachment attachment = new Attachment();
			attachment.setType("template");
			Payload payload = new Payload();
			payload.setTemplate_type("button");
			payload.setText("Pay your Bill");
			List<Buttons> buttons = new ArrayList<Buttons>();
			buttons.add(new Buttons(
					urlforApi +"?order="+ orderuuid,
					"web_url", "Payment"));
			
			payload.setButtons(buttons);
			attachment.setPayload(payload);
			message.setAttachment(attachment);
			viewCartResponse.setMessage(message);
			viewCartResponse.setRecipient(recipient);

			Gson gson = new Gson();
			String json = gson.toJson(viewCartResponse);
			System.out.println(json);
			HttpCallHelper httpCallHelper = new HttpCallHelper();
			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "application/json");
			Map<String, String> params = new HashMap<>();
			params.put("access_token",
					lexDTO.getPageAccessToken());
			params.put("order", orderuuid);
			httpCallHelper.doPostCall(headers, "https://graph.facebook.com/v2.6/me/messages", params, json);
			return null;
		}*/
	}

}
