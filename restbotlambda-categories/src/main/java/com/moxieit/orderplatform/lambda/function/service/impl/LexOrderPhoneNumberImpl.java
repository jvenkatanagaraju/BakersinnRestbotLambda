package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;

import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.HttpCallHelper;
import com.moxieit.orderplatform.function.service.api.ILexDTO;
import com.moxieit.orderplatform.function.service.api.ILexService;
import com.moxieit.orderplatform.function.service.api.LexFbStatusDto;
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

public class LexOrderPhoneNumberImpl implements ILexService {
	String orderuuid;
	static DynamoDB dynamoDB = DBService.getDBConnection();
	static Table orderTable = dynamoDB.getTable("Order");
	Table restaurantTable = dynamoDB.getTable("Restaurant");
	@Override
	public LexResponse serveLex(ILexDTO lexDTO, Context context) {
		String userId = lexDTO.getUserId();	
		//try {
		//LexOrderAddressService lexOrderCategory = (LexOrderAddressService) lexDTO;
		
		String botName = lexDTO.getBotName();
		Item restaurantItem = restaurantTable.getItem("botName", botName);
		String countryName = restaurantItem.getString("country");		
		String orderedPhoneNumber = lexDTO.getInputTranscript();
		System.out.println("inputtransscript;"+orderedPhoneNumber);
		if((countryName.contains("US") ||countryName.equalsIgnoreCase("US")||countryName.equalsIgnoreCase("USA")) && !(orderedPhoneNumber.startsWith("+"))) {
			orderedPhoneNumber = "+1"+orderedPhoneNumber;
		       System.out.println("Us phonenumber:" + orderedPhoneNumber);
		}else if ((countryName.contains("India")||countryName.equalsIgnoreCase("India")) && !(orderedPhoneNumber.startsWith("+"))){
			orderedPhoneNumber = "+91"+orderedPhoneNumber;
		       System.out.println("India phonenumber:" + orderedPhoneNumber);
		} 
		
			List<String> orderStatuses = new ArrayList<>();
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

			long milliSeconds = System.currentTimeMillis();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(milliSeconds);
			String date = formatter.format(calendar.getTime());
			orderStatuses.add("Initiated");
			orderStatuses.add("Payment Pending");
			ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("userId").eq(userId)
					.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("orderStatus").in(orderStatuses)))
					.buildForScan();

			ItemCollection<ScanOutcome> scan = orderTable.scan(xspec);
			Item order = null;

			Page<Item, ScanOutcome> firstPage = scan.firstPage();
			if (firstPage.iterator().hasNext()) {
				order = firstPage.iterator().next();
				orderuuid = (String) order.get("uuid");
				System.out.println("orderuuid is ;"+orderuuid);
				UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", order.get("uuid"))
						.withUpdateExpression("set phoneNumber = :val")
						.withValueMap(new ValueMap().withString(":val", orderedPhoneNumber));
				orderTable.updateItem(updateItemSpec);
				
			}		
	
		
		//updateCreationDate(orderuuid);
		ClassLoader classLoader=getClass().getClassLoader();
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
			message1.setContent("Pay your Bill");
			List<Buttons> buttons = new ArrayList<Buttons>();		
			buttons.add(new Buttons("Confirm Order",
					urlforApi +"?order="+ orderuuid));
			/*Buttons buttons2=new Buttons();
			List<Buttons> buttons1 = new ArrayList<Buttons>();
			buttons2.setText("Check Your Cart");
			buttons2.setValue("https://vhnnifn9o0.execute-api.us-east-1.amazonaws.com/Lexpayment/viewcart?order="+orderuuid);
			buttons1.add(buttons2);	*/
			
			List<GenericAttachments> genericAttachments = new ArrayList<GenericAttachments>();
			//genericAttachments.add(new GenericAttachments("Check your cart",".",null,"https://vhnnifn9o0.execute-api.us-east-1.amazonaws.com/Lexpayment/viewcart?order="+orderuuid,buttons1,null));
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
			//updateCreationDate(orderuuid);
			ViewCartResponse viewCartResponse = new ViewCartResponse();
			Recipient recipient = new Recipient();
			recipient.setId(userId);
			Message message = new Message();
			Attachment attachment = new Attachment();
			attachment.setType("template");
			Payload payload = new Payload();
			payload.setTemplate_type("button");
			payload.setText("Do you want to confirm this order?");
			System.out.println("Hi this is loop in pay bill");
			List<Buttons> buttons = new ArrayList<Buttons>();
			buttons.add(new Buttons(
					urlforApi +"?order="+ orderuuid,
					"web_url", "Confirm Order"));
			
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
		}
		/*} catch (Exception e){
			
			LexFbStatusDto lexFbStatusDto = new LexFbStatusDto();
			Recipient recipient = new Recipient();
			recipient.setId(userId);
			Message message = new Message();
			message.setText("Please ");
			lexFbStatusDto.setRecipient(recipient);
			lexFbStatusDto.setMessage(message);
		}*/
	}
	public static void updateCreationDate(String uuid) {

		UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", uuid)
				.withUpdateExpression("set creationDate = :val")
				.withValueMap(new ValueMap().withNumber(":val", System.currentTimeMillis()));
		orderTable.updateItem(updateItemSpec);

	}

}
