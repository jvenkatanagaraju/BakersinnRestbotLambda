package com.moxieit.orderplatform.lambda.function.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.function.service.api.ILexDTO;
import com.moxieit.orderplatform.function.service.api.IRestaurantMenu;
import com.moxieit.orderplatform.lambda.response.Buttons;
import com.moxieit.orderplatform.lambda.response.DialogActionResponseForElicitIntent;
import com.moxieit.orderplatform.lambda.response.GenericAttachments;
import com.moxieit.orderplatform.lambda.response.LexResponse;
import com.moxieit.orderplatform.lambda.response.MenuCategoryResponse;
import com.moxieit.orderplatform.lambda.response.Message;
import com.moxieit.orderplatform.lambda.response.ResponseCard;
import com.moxieit.orderplatform.lambda.response.Slots;

public class LexOrderGetStartedServiceImpl extends AbstractLexOrderServiceImpl {

	@Override
	public LexResponse serveLex(ILexDTO lexDTO, Context context) {
		IRestaurantMenu restaurantMenuImpl = new RestaurantMenuImpl();
		// TODO get resturantID from bot name
		List<MenuCategoryResponse> menuCategoryResponses = restaurantMenuImpl.getMenuCategories(lexDTO.getRestaurantId());
		LexResponse lexResponse = new LexResponse();
		Item order = getOrder(lexDTO.getUserId(),lexDTO.getRestaurantId(),lexDTO.getBotName(),lexDTO.getPageAccessToken());
		List<GenericAttachments> genericAttachments = new ArrayList<GenericAttachments>();
		DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
		ResponseCard responseCard = new ResponseCard();
		responseCard.setVersion("1");
		responseCard.setContentType("application/vnd.amazonaws.card.generic");
		Message message = new Message();
		message.setContentType("PlainText");
		message.setContent("Menu Categories :");
		for (MenuCategoryResponse category : menuCategoryResponses) {
			String categoryId = category.getCategoryId();
			String categoryName = category.getCategoryName();
			String image = category.getImage();
			List<Buttons> buttons = new ArrayList<Buttons>();
			buttons.add(new Buttons(categoryName, "menuCategory_" + order.getString("uuid") + "_" + categoryId+"_"+"first"));
			buttons.add(new Buttons("ViewCart", "ViewCart_" + order.getString("uuid")));
			GenericAttachments genericAttachment = new GenericAttachments();
			genericAttachment.setAttachmentLinkUrl(null);
			genericAttachment.setButtons(buttons);
			genericAttachment.setSubTitle(".");
			genericAttachment.setImageUrl(image);
			genericAttachment.setTitle(categoryName);
			genericAttachments.add(genericAttachment);
			context.getLogger().log("output: " + lexResponse.getDialogAction());
		}
		Slots slots = new Slots();
		slots.setSlotName("Hours");
		dialogAction.setType("ElicitSlot");
		dialogAction.setSlots(slots);
		dialogAction.setIntentName("GetStarted");
		dialogAction.setSlotToElicit("slotName");
		GenericAttachments[] attachments = new GenericAttachments[genericAttachments.size()];
		attachments = genericAttachments.toArray(attachments);
		responseCard.setGenericAttachments(attachments);
		dialogAction.setMessage(message);
		dialogAction.setResponseCard(responseCard);
		lexResponse.setDialogAction(dialogAction);
		return lexResponse;
	}

}
