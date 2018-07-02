package com.moxieit.orderplatform.lambda.function.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.HttpCallHelper;
import com.moxieit.orderplatform.function.service.api.ILexDTO;
import com.moxieit.orderplatform.lambda.response.Buttons;
import com.moxieit.orderplatform.lambda.response.DialogActionResponseForElicitIntent;
import com.moxieit.orderplatform.lambda.response.GenericAttachments;
import com.moxieit.orderplatform.lambda.response.LexResponse;
import com.moxieit.orderplatform.lambda.response.Message;
import com.moxieit.orderplatform.lambda.response.ResponseCard;
import com.moxieit.orderplatform.lambda.response.Slots;

public class LexOrderPutAddressServiceImpl extends AbstractLexOrderServiceImpl {

	private final String emo_regex = "([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])";

	@Override
	public LexResponse serveLex(ILexDTO lexDTO, Context context) {
		// TODO Auto-generated method stub
		LexResponse lexResponse = new LexResponse();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderTable = dynamoDB.getTable("Order");
		Item orderItem = getOrder(lexDTO.getUserId(), lexDTO.getRestaurantId(), lexDTO.getBotName(),lexDTO.getPageAccessToken());
		String lexMessage = lexDTO.getInputTranscript();
		String userId = lexDTO.getUserId();
		updateCreationDate(orderItem.getString("uuid"));
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
		/*if (userId.contains("us-east-1")) {
			DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
			ResponseCard responseCard = new ResponseCard();
			responseCard.setVersion("1");
			responseCard.setContentType("application/vnd.amazonaws.card.generic");
			Message message1 = new Message();
			message1.setContentType("PlainText");
			message1.setContent("Pay here");
			List<Buttons> buttons = new ArrayList<Buttons>();
			buttons.add(new Buttons("Payment",
					urlforApi +"?order="
							+ orderItem.getString("uuid")));
			List<GenericAttachments> genericAttachments = new ArrayList<GenericAttachments>();
			genericAttachments.add(new GenericAttachments("Payment", ".", null,
					urlforApi +"?order="
							+ orderItem.getString("uuid"),
					buttons, null));
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
		} else */if (!lexMessage.isEmpty()) {
			for (String word : lexMessage.split(" ")) {
				if (word.matches(emo_regex)) {
					System.out.println(word);
					DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
					ResponseCard responseCard = new ResponseCard();
					responseCard.setVersion("1");
					responseCard.setContentType("application/vnd.amazonaws.card.generic");
					Message message1 = new Message();
					message1.setContentType("PlainText");
					message1.setContent("Please Enter Valid Address");
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
				HttpCallHelper httpCallHelper = new HttpCallHelper();
				Map<String, String> headers = new HashMap<>();
				headers.put("Content-Type", "application/json");
				Map<String, String> params = new HashMap<>();
				params.put("address", lexMessage);
				params.put("key", "AIzaSyByeoAYjjiGUknxdLbkHxR9GNXenlyEJI8");
				String getLatLong = httpCallHelper.doGetCall(headers,
						"https://maps.googleapis.com/maps/api/geocode/json", params);
				Gson gson1 = new Gson();
				Object json1 = gson1.fromJson(getLatLong, Object.class);
				System.out.println(json1);
				Map<String, List<Map<String, Map<String, Map>>>> map = new HashMap<String, List<Map<String, Map<String, Map>>>>();
				map = (Map<String, List<Map<String, Map<String, Map>>>>) json1;
				DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
				
				Message message1 = new Message();
				ResponseCard responseCard = new ResponseCard();
				List<GenericAttachments> genericAttachments = new ArrayList<GenericAttachments>();
				
				try{
				double lat = (Double) map.get("results").get(0).get("geometry").get("location").get("lat");
				double lon = (Double) map.get("results").get(0).get("geometry").get("location").get("lng");
				Map<String, String> params1 = new HashMap<>();
				params1.put("latlng", lat + "," + lon);
				params1.put("key", "AIzaSyByeoAYjjiGUknxdLbkHxR9GNXenlyEJI8");
				String ExactAddress = httpCallHelper.doGetCall(headers,
						"https://maps.googleapis.com/maps/api/geocode/json", params1);
				Gson gson2 = new Gson();
				Object json2 = gson2.fromJson(ExactAddress, Object.class);
				System.out.println(json2);
				Map<String, List<Map<String, List<Map<String, String>>>>> addressMap = new HashMap<String, List<Map<String, List<Map<String, String>>>>>();
				addressMap = (Map<String, List<Map<String, List<Map<String, String>>>>>) json2;
				
				//DialogActionResponseForElicitIntent dialogAction = new DialogActionResponseForElicitIntent();
				
				responseCard.setVersion("1");
				responseCard.setContentType("application/vnd.amazonaws.card.generic");
				
				message1.setContentType("PlainText");
				message1.setContent("Please select your nearest Address:");
				Map<String, List<Map<String, String>>> addressMap1 = new HashMap<String, List<Map<String, String>>>();
				addressMap1 = (Map<String, List<Map<String, String>>>) json2;
				int i = 0;

				for (i = 0; i <= 4; i++) {
					String areaWithCity = null;
					String stateAndPincode = null;
					String formattedAdddres = (String) addressMap1.get("results").get(i).get("formatted_address");
					System.out.println("formattedAdddres " +formattedAdddres);
					String[] formattedAddSplit = formattedAdddres.split(",");
					int formatSize = formattedAddSplit.length;
				
					if (i == 0){
						areaWithCity = lexMessage;
						formattedAdddres = areaWithCity;
					}else {
					if (formattedAddSplit[0] != null && formattedAddSplit[1] != null) {
						areaWithCity = formattedAddSplit[0] + formattedAddSplit[1];						
						if (formatSize > 2) {
							if (formattedAddSplit[2] != null) {
								areaWithCity = formattedAddSplit[0] + formattedAddSplit[1] + formattedAddSplit[2];
							}
						}
						if (formatSize > 3) {
							if (formattedAddSplit[3] != null) {
								areaWithCity = formattedAddSplit[0] + formattedAddSplit[1] + formattedAddSplit[2]
										+ formattedAddSplit[3];
							}
						}
						if (formatSize > 4) {
							if (formattedAddSplit[4] != null) {
								stateAndPincode = formattedAddSplit[4];
							} else {
								stateAndPincode = ".";
							}
						} else {
							stateAndPincode = ".";
						}
						if (formatSize > 5) {
							if (formattedAddSplit[5] != null) {
								stateAndPincode = formattedAddSplit[4] + formattedAddSplit[5];
							} else {
								stateAndPincode = ".";
							}
						} else {
							stateAndPincode = ".";
						}
						if (formatSize > 6) {
							if (formattedAddSplit[6] != null) {
								stateAndPincode = formattedAddSplit[4] + formattedAddSplit[5] + formattedAddSplit[6];
							} else {
								stateAndPincode = ".";
							}
						} else {
							stateAndPincode = ".";
						}
					}
					}
					
					List<Buttons> buttons = new ArrayList<Buttons>();
					buttons.add(new Buttons(Integer.toString(i),
							i + "_" + orderItem.getString("uuid") + "_" + formattedAdddres));
					
					GenericAttachments genericAttachment = new GenericAttachments();
					genericAttachment.setAttachmentLinkUrl(null);
					genericAttachment.setButtons(buttons);
					genericAttachment.setSubTitle(stateAndPincode);
					genericAttachment.setImageUrl(null);
					genericAttachment.setTitle(areaWithCity);
					genericAttachments.add(genericAttachment);
				}
				} catch (Exception e){
					
					String areaWithCity = null;
					String stateAndPincode = null;
					String[] formattedAddSplit1 = lexMessage.split(",");
					int formatSize1 = formattedAddSplit1.length;
					if (formattedAddSplit1[0] != null && formattedAddSplit1[1] != null) {
						areaWithCity = formattedAddSplit1[0] + formattedAddSplit1[1];						
						if (formatSize1 > 2) {
							if (formattedAddSplit1[2] != null) {
								areaWithCity = formattedAddSplit1[0] + formattedAddSplit1[1] + formattedAddSplit1[2];
							}
						} if (formatSize1 > 3) {
							if (formattedAddSplit1[3] != null) {
								stateAndPincode = formattedAddSplit1[3];
								
							}
						} if (formatSize1 > 4) {
							if (formattedAddSplit1[4] != null) {
								stateAndPincode = formattedAddSplit1[3]+formattedAddSplit1[4];
								
							} else {
								stateAndPincode = formattedAddSplit1[3];
								
							}
						}/* else {
							stateAndPincode =formattedAddSplit1[3];
							System.out.println("iam at 4 else else");
						}*/
						if (formatSize1 > 5) {
							if (formattedAddSplit1[5] != null) {
								stateAndPincode = formattedAddSplit1[3]+formattedAddSplit1[4] + formattedAddSplit1[5];
								
							} else {
								stateAndPincode = formattedAddSplit1[3]+formattedAddSplit1[4];
							
							}
						} /*else {
							stateAndPincode = formattedAddSplit1[3];
							System.out.println("iam at 5 else else");
						}*/
						if (formatSize1 > 6) {
							if (formattedAddSplit1[6] != null) {
								stateAndPincode = formattedAddSplit1[3]+formattedAddSplit1[4] + formattedAddSplit1[5] +formattedAddSplit1[6];
								
							} else {
								stateAndPincode = formattedAddSplit1[3]+formattedAddSplit1[4] + formattedAddSplit1[5];
								
							}
						}/* else {
							stateAndPincode = formattedAddSplit1[3];
							System.out.println("iam at 6 else else");
						}*/
					}
					//areaWithCity = lexMessage;
					responseCard.setVersion("1");
					responseCard.setContentType("application/vnd.amazonaws.card.generic");
					
					message1.setContentType("PlainText");
					message1.setContent("Please confirm your Address:");
					String formattedAdddres = areaWithCity;
					List<Buttons> buttons = new ArrayList<Buttons>();
					buttons.add(new Buttons(Integer.toString(0),
							0 + "_" + orderItem.getString("uuid") + "_" + formattedAdddres));					
					GenericAttachments genericAttachment = new GenericAttachments();
					genericAttachment.setAttachmentLinkUrl(null);
					genericAttachment.setButtons(buttons);
					genericAttachment.setSubTitle(stateAndPincode);
					genericAttachment.setImageUrl(null);
					genericAttachment.setTitle(areaWithCity);
					genericAttachments.add(genericAttachment);
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
				dialogAction.setMessage(message1);
				dialogAction.setResponseCard(responseCard);
				lexResponse.setDialogAction(dialogAction);
				System.out.println(dialogAction);
				return lexResponse;
			}
		}
		return null;
	}

}
