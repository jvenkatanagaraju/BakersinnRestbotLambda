package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.ILexDTO;
import com.moxieit.orderplatform.function.service.api.LexOrderMenuItem;
import com.moxieit.orderplatform.lambda.response.Buttons;
import com.moxieit.orderplatform.lambda.response.DialogActionResponseForElicitIntent;
import com.moxieit.orderplatform.lambda.response.GenericAttachments;
import com.moxieit.orderplatform.lambda.response.LexResponse;
import com.moxieit.orderplatform.lambda.response.MenuItemResponse;
import com.moxieit.orderplatform.lambda.response.Message;
import com.moxieit.orderplatform.lambda.response.ResponseCard;
import com.moxieit.orderplatform.lambda.response.Slots;

public class LexOrderMenuItemServiceImpl extends AbstractLexOrderServiceImpl {

	@Override
	public LexResponse serveLex(ILexDTO lexDTO, Context context) {
		LexOrderMenuItem lexOrderMenuItem = (LexOrderMenuItem) lexDTO;
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderTable = dynamoDB.getTable("Order");
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		RestaurantMenuImpl restaurantMenuImpl = new RestaurantMenuImpl();
		MenuItemResponse menuItemResponse = restaurantMenuImpl.getMenuItem(lexOrderMenuItem.getMenuItemId());
		KeyAttribute primaryKey = new KeyAttribute("uuid", lexOrderMenuItem.getOrderUuid());
		Item item = orderTable.getItem(primaryKey);
		Item orderItem = null;
		if (item != null) {
			orderItem = getOrderItem(lexOrderMenuItem.getMenuItemId(), lexOrderMenuItem.getOrderUuid());
			if (orderItem == null) {
				orderItem = new Item();
				String newOrderItemUuid = UUID.randomUUID().toString();
				orderItem.withString("uuid", newOrderItemUuid)
				.withString("userId", (String) lexOrderMenuItem.getUserId())
				.withString("orderuuid", lexOrderMenuItem.getOrderUuid())
				.withString("categoryId", lexOrderMenuItem.getMenuCategoryId())
				.withString("menuItemId", lexOrderMenuItem.getMenuItemId()).withNumber("quantity", 0)
				.withString("specialInstructions", "Nothing").withBoolean("readItem", false)
				.withString("spiceyLevel", "NULL").withNumber("itemCost", menuItemResponse.getPrice());
				orderItemTable.putItem(orderItem);
			} else {

			}
		} else {
			// TODO need to create new order and order item
		}
		updateCreationDate(lexOrderMenuItem.getOrderUuid());
		LexResponse lexResponse = new LexResponse();
		DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
		ResponseCard responseCard = new ResponseCard();
		responseCard.setVersion("1");
		responseCard.setContentType("application/vnd.amazonaws.card.generic");
		Message message = new Message();
		message.setContentType("PlainText");
		message.setContent("Quantity :");
		List<Buttons> buttons = new ArrayList<Buttons>();
		buttons.add(new Buttons("1", "menuItemQuantity_" + orderItem.getString("uuid") + "_1"));
		buttons.add(new Buttons("2", "menuItemQuantity_" + orderItem.getString("uuid") + "_2"));
		buttons.add(new Buttons("3", "menuItemQuantity_" + orderItem.getString("uuid") + "_3"));
		List<Buttons> buttonsii = new ArrayList<Buttons>();
		buttonsii.add(new Buttons("4", "menuItemQuantity_" + orderItem.getString("uuid") + "_4"));
		buttonsii.add(new Buttons("5", "menuItemQuantity_" + orderItem.getString("uuid") + "_5"));
		buttonsii.add(new Buttons("6", "menuItemQuantity_" + orderItem.getString("uuid") + "_6"));
		List<Buttons> buttonsiii = new ArrayList<Buttons>();
		buttonsiii.add(new Buttons("7", "menuItemQuantity_" + orderItem.getString("uuid") + "_7"));
		buttonsiii.add(new Buttons("8", "menuItemQuantity_" + orderItem.getString("uuid") + "_8"));
		buttonsiii.add(new Buttons("9", "menuItemQuantity_" + orderItem.getString("uuid") + "_9"));
		List<GenericAttachments> genericAttachments = new ArrayList<GenericAttachments>();
		genericAttachments.add(new GenericAttachments("Please Select Quantity", ".", null, null, buttons, null));
		genericAttachments.add(new GenericAttachments("Please Select Quantity", ".", null, null, buttonsii, null));
		genericAttachments.add(new GenericAttachments("Please Select Quantity", ".", null, null, buttonsiii, null));
		GenericAttachments[] attachments = new GenericAttachments[genericAttachments.size()];
		attachments = genericAttachments.toArray(attachments);
		responseCard.setGenericAttachments(attachments);
		Slots slots = new Slots();
		slots.setSlotName("Hours");
		dialogAction.setType("ElicitSlot");
		dialogAction.setIntentName("GetStarted");
		dialogAction.setSlots(slots);
		dialogAction.setSlotToElicit("slotName");
		dialogAction.setMessage(message);
		dialogAction.setResponseCard(responseCard);
		lexResponse.setDialogAction(dialogAction);
		context.getLogger().log("output: " + lexResponse.getDialogAction());
		return lexResponse;
	}

	private Item getOrderItem(String menuItemId, String orderUuid) {
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		ScanExpressionSpec xspec = new ExpressionSpecBuilder()
				.withCondition(S("orderuuid").eq(orderUuid).and(S("menuItemId").eq(menuItemId))).buildForScan();
		ItemCollection<ScanOutcome> scan = orderItemTable.scan(xspec);
		Item orderItem = null;
		Page<Item, ScanOutcome> firstPage = scan.firstPage();
		if (firstPage.iterator().hasNext()) {
			orderItem = firstPage.iterator().next();
		}
		return orderItem;
	}

}
