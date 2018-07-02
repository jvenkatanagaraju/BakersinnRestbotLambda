package com.moxieit.orderplatform.lambda.function.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.ILexDTO;
import com.moxieit.orderplatform.function.service.api.LexOrderCategory;
import com.moxieit.orderplatform.lambda.response.Buttons;
import com.moxieit.orderplatform.lambda.response.DialogActionResponseForElicitIntent;
import com.moxieit.orderplatform.lambda.response.GenericAttachments;
import com.moxieit.orderplatform.lambda.response.LexResponse;
import com.moxieit.orderplatform.lambda.response.MenuItemResponse;
import com.moxieit.orderplatform.lambda.response.Message;
import com.moxieit.orderplatform.lambda.response.ResponseCard;
import com.moxieit.orderplatform.lambda.response.Slots;

public class LexOrderMenuCategoryServiceImpl extends AbstractLexOrderServiceImpl {

	@Override
	public LexResponse serveLex(ILexDTO lexDTO, Context context) {
		LexOrderCategory lexOrderCategory = (LexOrderCategory) lexDTO;
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderTable = dynamoDB.getTable("Order");
		KeyAttribute primaryKey = new KeyAttribute("uuid", lexOrderCategory.getOrderuuid());
		Item order = orderTable.getItem(primaryKey);
		if (order != null) {
			RestaurantMenuImpl restaurantMenuImpl = new RestaurantMenuImpl();
			List<MenuItemResponse> menuItemResponses = restaurantMenuImpl
					.getMenuItems(lexOrderCategory.getCategoryId());
			LexResponse lexResponse = new LexResponse();
			List<GenericAttachments> genericAttachments = new ArrayList<GenericAttachments>();
			List<GenericAttachments> genericAttachments1 = new ArrayList<GenericAttachments>();
			List<GenericAttachments> genericAttachments2 = new ArrayList<GenericAttachments>();
			ResponseCard responseCard = new ResponseCard();
			responseCard.setVersion("1");
			responseCard.setContentType("application/vnd.amazonaws.card.generic");
			Message message = new Message();
			message.setContentType("PlainText");
			message.setContent("Items :");
			if(lexOrderCategory.getCondition().contains("first")){
				for (MenuItemResponse item : menuItemResponses) {
					String itemId = item.getItemId();
					String itemName = item.getItemName();
					String image = item.getImage();
					Number price = item.getPrice();
					List<Buttons> buttons = new ArrayList<Buttons>();
					buttons.add(new Buttons(itemName, "menuItem_" + order.getString("uuid") + "_" + itemId));
					buttons.add(new Buttons("ViewCart", "ViewCart_"+ order.getString("uuid")));
					GenericAttachments genericAttachment = new GenericAttachments();
					genericAttachment.setAttachmentLinkUrl(null);
					genericAttachment.setButtons(buttons);
					genericAttachment.setSubTitle(".");
					genericAttachment.setImageUrl(image);
					genericAttachment.setTitle("ItemCost" + " " + "$" + price);
					if(genericAttachments.size()<12){
					genericAttachments.add(genericAttachment);
					}
					context.getLogger().log("output: " + lexResponse.getDialogAction());
				}
                if(genericAttachments.size()==12
                		
               )
                {
				List<Buttons> buttons = new ArrayList<Buttons>();
				buttons.add(new Buttons("MoreItems", "MoreItemsMenuCategory_" + order.getString("uuid") +"_"+ lexOrderCategory.getCategoryId() + "_"+"second"));
				genericAttachments.add(new GenericAttachments("MoreItems", ".", null, null, buttons, null));
                }
				}
				else if(lexOrderCategory.getCondition().contains("second")){
					for (MenuItemResponse item : menuItemResponses) {
						String itemId = item.getItemId();
						String itemName = item.getItemName();
						String image = item.getImage();
						Number price = item.getPrice();
						List<Buttons> buttons = new ArrayList<Buttons>();
						buttons.add(new Buttons(itemName, "menuItem_" + order.getString("uuid") + "_" + itemId));
						buttons.add(new Buttons("ViewCart", "ViewCart_"+ order.getString("uuid")));
						GenericAttachments genericAttachment = new GenericAttachments();
						genericAttachment.setAttachmentLinkUrl(null);
						genericAttachment.setButtons(buttons);
						genericAttachment.setSubTitle(".");
						genericAttachment.setImageUrl(image);
						genericAttachment.setTitle("ItemCost" + " " + "$ " + price);
						if(genericAttachments.size()<12){
						genericAttachments.add(genericAttachment);
						genericAttachments2.add(genericAttachment);
						}
						else if (genericAttachments.size() >=12 && genericAttachments.size() <= 30) {
							genericAttachments.add(genericAttachment);
							genericAttachments1.add(genericAttachment);
						}
						context.getLogger().log("output: " + lexResponse.getDialogAction());
					}
					if (genericAttachments2.size() > 30) {
						List<Buttons> buttons=new ArrayList<Buttons>();
						buttons.add(new Buttons("MoreItems", "MoreItemsMenuCategory_" + order.getString("uuid") + "_"
								+ lexOrderCategory.getCategoryId() + "_" + "third"));
						genericAttachments1.add(new GenericAttachments("MoreItems", ".", null, null, buttons, null));

					}
					List<Buttons> buttons=new ArrayList<Buttons>();
					buttons.add(new Buttons("PreviousItems", "PreviousItemsMenuCategory_" + order.getString("uuid") + "_"
							+ lexOrderCategory.getCategoryId() + "_" + "first"));
					genericAttachments1.add(new GenericAttachments("PreviousItems", ".", null, null, buttons, null));

				}
			    updateCreationDate(lexOrderCategory.getOrderuuid());
				Slots slots = new Slots();
				slots.setSlotName("Hours");
				DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
				dialogAction.setType("ElicitSlot");
				dialogAction.setSlots(slots);
				dialogAction.setIntentName("GetStarted");
				dialogAction.setSlotToElicit("slotName");
				dialogAction.setMessage(message);
				if(lexOrderCategory.getCondition().contains("first")){
				GenericAttachments[] attachments = new GenericAttachments[genericAttachments.size()];
				attachments = genericAttachments.toArray(attachments);
				responseCard.setGenericAttachments(attachments);
				}
				else if(lexOrderCategory.getCondition().contains("second")){
					GenericAttachments[] attachments = new GenericAttachments[genericAttachments1.size()];
					attachments = genericAttachments1.toArray(attachments);
					responseCard.setGenericAttachments(attachments);
				}
				dialogAction.setResponseCard(responseCard);
				lexResponse.setDialogAction(dialogAction);
				return lexResponse;
			} else {
				// TODO Need to create new order
			}
			return null;
		}
		
}
