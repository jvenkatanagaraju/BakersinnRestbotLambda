package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.CollectionUtils;
import com.google.gson.Gson;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.HttpCallHelper;
import com.moxieit.orderplatform.function.service.api.ILexDTO;
import com.moxieit.orderplatform.function.service.api.LexOrderViewCart;
import com.moxieit.orderplatform.lambda.request.Elements;
import com.moxieit.orderplatform.lambda.request.Summary;
import com.moxieit.orderplatform.lambda.response.Address;
import com.moxieit.orderplatform.lambda.response.Adjustments;
import com.moxieit.orderplatform.lambda.response.Buttons;
import com.moxieit.orderplatform.lambda.response.DialogActionResponseForElicitIntent;
import com.moxieit.orderplatform.lambda.response.GenericAttachments;
import com.moxieit.orderplatform.lambda.response.LexResponse;
import com.moxieit.orderplatform.lambda.response.MenuItemResponse;
import com.moxieit.orderplatform.lambda.response.Message;
import com.moxieit.orderplatform.lambda.response.Payload;
import com.moxieit.orderplatform.lambda.response.Recipient;
import com.moxieit.orderplatform.lambda.response.ResponseCard;
import com.moxieit.orderplatform.lambda.response.Slots;
import com.moxieit.orderplatform.lambda.response.ViewCartResponse;

public class LexMenuOrderViewCartServiceImpl extends AbstractLexOrderServiceImpl {

	@Override
	public LexResponse serveLex(ILexDTO lexDTO, Context context) {
		// TODO Auto-generated method stub
		LexOrderViewCart lexOrderCategory = (LexOrderViewCart) lexDTO;
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderTable = dynamoDB.getTable("Order");
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		String userId=lexOrderCategory.getUserId();
		System.out.println("my userId:"+userId);
		String getOrderuuId=lexOrderCategory.getOrderuuId();
		System.out.println("my getOrderuuId:"+getOrderuuId);
		updateCreationDate(lexOrderCategory.getOrderuuId());
		if(userId.contains("us-east-1")){
			KeyAttribute primaryKey = new KeyAttribute("uuid", lexOrderCategory.getOrderuuId());
			System.out.println("input:"+lexOrderCategory.getOrderuuId());
			Item order = orderTable.getItem(primaryKey);
			System.out.println("primarykey order:"+order);
			if (order != null) {
				LexResponse lexResponse = new LexResponse();
				DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
				ResponseCard responseCard = new ResponseCard();
				responseCard.setVersion("1");
				responseCard.setContentType("application/vnd.amazonaws.card.generic");
				Message message1 = new Message();
				message1.setContentType("PlainText");
				message1.setContent("Do you want to confirm this order?");
				List<Buttons> buttons = new ArrayList<Buttons>();
				buttons.add(new Buttons("PayNow", "PayNow_" + order.getString("uuid")));
				System.out.println(order.getString("uuid"));
				buttons.add(new Buttons("Continue", "Continue_" + order.getString("uuid")));
				System.out.println(order.getString("uuid"));
				buttons.add(new Buttons("StartOver", "StartOver_" + order.getString("uuid")));
				System.out.println(order.getString("uuid"));
				List<GenericAttachments> genericAttachments = new ArrayList<GenericAttachments>();
				
				List<Buttons> buttons3 = new ArrayList<Buttons>();
				Buttons buttons2=new Buttons();
				buttons2.setText("ClickHere");
				buttons2.setValue("https://vhnnifn9o0.execute-api.us-east-1.amazonaws.com/Lexpayment/viewcart?order="+lexOrderCategory.getOrderuuId());
				buttons3.add(buttons2);
				genericAttachments.add(new GenericAttachments("Check your cart",".",null,"https://vhnnifn9o0.execute-api.us-east-1.amazonaws.com/Lexpayment/viewcart?order="+lexOrderCategory.getOrderuuId(),buttons3,null));
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
				System.out.println("json:"+json1);
				return lexResponse;
			}
		}
		else{
		KeyAttribute primaryKey = new KeyAttribute("uuid", lexOrderCategory.getOrderuuId());
		Item order = orderTable.getItem(primaryKey);
		if (order != null) {
			ViewCartResponse viewCartResponse = new ViewCartResponse();
			Recipient recipient = new Recipient();
			recipient.setId(lexOrderCategory.getUserId());
			Message message = new Message();
			com.moxieit.orderplatform.lambda.response.Attachment attachment = new com.moxieit.orderplatform.lambda.response.Attachment();
			attachment.setType("template");
			Payload payload = new Payload();
			payload.setTemplate_type("receipt");
			payload.setRecipient_name(".");
			payload.setOrder_number(order.getString("uuid"));
			payload.setCurrency("USD");
			payload.setPayment_method(".");
			payload.setOrder_url("");
			payload.setTimestamp(order.getNumber("creationDate".substring(0, 8)));
			Address address = new Address();
			if (order.getString("Address") != null) {
				address.setCity(order.getString("Address"));
				address.setStreet_1("'");
				address.setCountry(".");
				address.setPostal_code(".");
				address.setState("'");
			}
			List<Adjustments> adjustments = new ArrayList<Adjustments>();
			Adjustments adjustments2 = new Adjustments();
			adjustments2.setName(".");
			adjustments2.setAmount("0");
			adjustments.add(adjustments2);
			Summary summary = new Summary();
			Number totalBill = order.getNumber("totalBill");
			Number tax = order.getNumber("tax");
			Number totalBillWithTax = order.getNumber("totalBillWithTax");
			summary.setShipping_cost("0");
			summary.setSubtotal(totalBill);
			summary.setTotal_tax(tax);
			summary.setTotal_cost(totalBillWithTax);
			List<Elements> elements = new ArrayList<Elements>();
			ScanExpressionSpec xspec = new ExpressionSpecBuilder()
					.withCondition(S("orderuuid").eq(order.getString("uuid"))).buildForScan();

			ItemCollection<ScanOutcome> scan = orderItemTable.scan(xspec);
			Consumer<Item> action = new Consumer<Item>() {
				@Override
				public void accept(Item t) {
					try {
						RestaurantMenuImpl restaurantMenuImpl = new RestaurantMenuImpl();
						MenuItemResponse menuItemResponse = restaurantMenuImpl.getMenuItem(t.getString("menuItemId"));
						Elements elements2 = new Elements();
						elements2.setCurrency("USD");
						elements2.setImage_url(menuItemResponse.getImage());
						elements2.setPrice(menuItemResponse.getPrice());
						elements2.setQuantity(t.getNumber("quantity"));
						elements2.setSubtitle(t.getString("spiceyLevel"));
						elements2.setTitle(menuItemResponse.getItemName());
						elements.add(elements2);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			};
			scan.forEach(action);
			if (!CollectionUtils.isNullOrEmpty(elements)) {
				Elements[] attachments = new Elements[elements.size()];
				attachments = elements.toArray(attachments);
				payload.setElements(attachments);
				if (address.getCity() != null) {
					payload.setAddress(address);
				}
				Adjustments[] attachments2 = new Adjustments[adjustments.size()];
				attachments2 = adjustments.toArray(attachments2);
				payload.setAdjustments(attachments2);
				payload.setSummary(summary);
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
				httpCallHelper.doPostCall(headers, "https://graph.facebook.com/v2.6/me/messages", params, json);
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
				Gson gson1 = new Gson();
				String json1 = gson1.toJson(lexResponse);
				System.out.println(json1);
				return lexResponse;
			} else {
				LexResponse lexResponse = new LexResponse();
				DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
				ResponseCard responseCard = new ResponseCard();
				responseCard.setVersion("1");
				responseCard.setContentType("application/vnd.amazonaws.card.generic");
				Message message1 = new Message();
				message1.setContentType("PlainText");
				message1.setContent("Cart:");
				List<Buttons> buttons = new ArrayList<Buttons>();
				buttons.add(new Buttons("Continue", "Continue_" + order.getString("uuid")));
				buttons.add(new Buttons("StartOver", "StartOver_" + order.getString("uuid")));
				List<GenericAttachments> genericAttachments = new ArrayList<GenericAttachments>();
				genericAttachments.add(new GenericAttachments("Your cart is empty", ".", null, null, buttons, null));
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
		}
		return null;
	}

	public static void main(String[] args) {
		WebsiteViewCartServiceImpl FbLoginServiveImpl = new WebsiteViewCartServiceImpl();
		Context context = null;
		String orderItem = "e552a02d-9fc4-4acd-91e9-d47cb397a82f";
		
		FbLoginServiveImpl.handleRequest(orderItem, context);
	}
}
