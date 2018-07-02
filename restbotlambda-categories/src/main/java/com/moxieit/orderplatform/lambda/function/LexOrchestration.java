package com.moxieit.orderplatform.lambda.function;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.time.*;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.HttpCallHelper;
import com.moxieit.orderplatform.function.service.api.ILexDTO;
import com.moxieit.orderplatform.function.service.api.ILexService;
import com.moxieit.orderplatform.function.service.api.LexOrderAddressService;
import com.moxieit.orderplatform.function.service.api.LexOrderCategory;
import com.moxieit.orderplatform.function.service.api.LexOrderMenuItem;
import com.moxieit.orderplatform.function.service.api.LexOrderMenuItemQuantity;
import com.moxieit.orderplatform.function.service.api.LexOrderMenuItemSpicyLevel;
import com.moxieit.orderplatform.function.service.api.LexOrderPayNowService;
import com.moxieit.orderplatform.function.service.api.LexOrderStartOverService;
import com.moxieit.orderplatform.function.service.api.LexOrderViewCart;
import com.moxieit.orderplatform.lambda.function.service.impl.LexClickHereServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexFacebookPageTimings;
import com.moxieit.orderplatform.lambda.function.service.impl.LexMenuOrderItemSpicyLevelServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexMenuOrderViewCartServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexOrderAddressSavingImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexOrderDeliveryServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexOrderGetStartedServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexOrderMenuCategoryServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexOrderMenuItemQuantityServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexOrderMenuItemServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexOrderPayNowServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexOrderPhoneNumberImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexOrderPickupServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexOrderPutAddressServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexOrderStartOverServiceImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexOrderTimingsServiceImpl;
import com.moxieit.orderplatform.lambda.response.LexResponse;

public class LexOrchestration implements RequestHandler<Object, LexResponse> {
	String countryName;
	@Override
	public LexResponse handleRequest(Object lexRequest, Context context) {
		// LambdaLogger logger = context.getLogger();
		System.out.println("Input :" + lexRequest);
		// logger.log("Input : " +lexRequest);
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table restaurantTable = dynamoDB.getTable("Restaurant");
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		map = (Map<String, Map<String, String>>) lexRequest;
		Map<String, String> map1 = new HashMap<String, String>();
		map1 = (Map<String, String>) lexRequest;
		String lexChatMessage = (String) map1.get("inputTranscript");
		String userId = (String) map1.get("userId");
		String botName = (String) map.get("bot").get("name");
		System.out.println(botName);
		Item restaurantItem = restaurantTable.getItem("botName", botName);
		String restaurantId = restaurantItem.getString("id");
		countryName = restaurantItem.getString("country");
		String pageAccessToken = restaurantItem.getString("pageAccessToken");
		String hours = getHours(pageAccessToken);
		System.out.println("my hours:"+hours);
		Gson gson2 = new Gson();
		Object json2 = gson2.fromJson(hours, Object.class);
		System.out.println(json2);
		Map<String, String> hoursMap = new HashMap<String,String>();
		hoursMap=(Map<String,String>)json2;
		String jsonString = new Gson().toJson(hoursMap, Map.class);
		boolean outputHours=jsonString.contains("hours");
		System.out.println("my outputHours:"+outputHours);
        boolean output;
        
            if(outputHours){
             output = timings(hours);
             System.out.println("my output:"+output);
            }else
            {
                     output=true;
            }
		if (output == true) {
			if (lexChatMessage.equalsIgnoreCase("Getstarted") || lexChatMessage.contains("Continue") || lexChatMessage.contains("GetStarted")) {
				ILexService lexService = new LexOrderGetStartedServiceImpl();
				ILexDTO lexDTO = new ILexDTO();
				lexDTO.setUserId(userId);
				lexDTO.setRestaurantId(restaurantId);
				lexDTO.setPageAccessToken(pageAccessToken);
				lexDTO.setBotName(botName);
				return lexService.serveLex(lexDTO, context);
			} else if (lexChatMessage.contains("menuCategory_") || lexChatMessage.contains("MoreItemsMenuCategory_")
					|| lexChatMessage.contains("PreviousItemsMenuCategory_")) {
				// menuCategory_orderuuid_restaurant_categoryId9
				ILexService lexService = new LexOrderMenuCategoryServiceImpl();
				String[] menuCategorySplit = lexChatMessage.split("_");
				LexOrderCategory lexDTO = new LexOrderCategory();
				lexDTO.setCategoryId(menuCategorySplit[2] + "_" + menuCategorySplit[3]);
				lexDTO.setOrderuuid(menuCategorySplit[1]);
				lexDTO.setCondition(menuCategorySplit[4]);
				lexDTO.setUserId(userId);
				lexDTO.setBotName(botName);
				return lexService.serveLex(lexDTO, context);
			} else if (lexChatMessage.contains("menuItem_")) {
				// Format for menuItem is
				// menuItem_orderuuid_resturantId_categoryId_menuItemId
				ILexService lexService = new LexOrderMenuItemServiceImpl();
				String[] menuItemSplit = lexChatMessage.split("_");
				LexOrderMenuItem lexDTO = new LexOrderMenuItem();
				lexDTO.setMenuCategoryId(menuItemSplit[2] + "_" + menuItemSplit[3]);
				lexDTO.setOrderUuid(menuItemSplit[1]);
				lexDTO.setMenuItemId(menuItemSplit[2] + "_" + menuItemSplit[3] + "_" + menuItemSplit[4]);
				lexDTO.setRestaurantId(menuItemSplit[2]);
				lexDTO.setBotName(botName);
				lexDTO.setUserId(userId);
				return lexService.serveLex(lexDTO, context);
			} else if (lexChatMessage.contains("menuItemQuantity_")) {
				ILexService lexService = new LexOrderMenuItemQuantityServiceImpl();
				LexOrderMenuItemQuantity lexDTO = new LexOrderMenuItemQuantity();
				String[] menuItemSplit = lexChatMessage.split("_");
				lexDTO.setOrderItemId(menuItemSplit[1]);
				lexDTO.setQuantity(Integer.valueOf(menuItemSplit[2]));
				lexDTO.setUserId(userId);
				lexDTO.setBotName(botName);
				return lexService.serveLex(lexDTO, context);
			} else if (lexChatMessage.contains("menuItemSpicy_")) {
				ILexService lexService = new LexMenuOrderItemSpicyLevelServiceImpl();
				LexOrderMenuItemSpicyLevel lexDTO = new LexOrderMenuItemSpicyLevel();
				String[] menuItemSplit = lexChatMessage.split("_");
				lexDTO.setOrderItemId(menuItemSplit[1]);
				lexDTO.setSpicyLevel(menuItemSplit[2]);
				lexDTO.setBotName(botName);
				return lexService.serveLex(lexDTO, context);
			} else if (lexChatMessage.contains("StartOver_")) {
				ILexService lexService = new LexOrderStartOverServiceImpl();
				LexOrderStartOverService lexDTO = new LexOrderStartOverService();
				String[] menuItemSplit = lexChatMessage.split("_");
				lexDTO.setOrderuuid(menuItemSplit[1]);
				lexDTO.setUserId(userId);
				lexDTO.setBotName(botName);
				return lexService.serveLex(lexDTO, context);
			} else if (lexChatMessage.contains("PayNow_")) {
				ILexService lexService = new LexOrderPayNowServiceImpl();
				LexOrderPayNowService lexDTO = new LexOrderPayNowService();
				String[] menuItemSplit = lexChatMessage.split("_");
				lexDTO.setOrderuuid(menuItemSplit[1]);
				lexDTO.setBotName(botName);
				return lexService.serveLex(lexDTO, context);
			} else if (lexChatMessage.contains("ViewCart_")) {
				ILexService lexService = new LexMenuOrderViewCartServiceImpl();
				LexOrderViewCart lexDTO = new LexOrderViewCart();
				String[] menuItemSplit = lexChatMessage.split("_");
				lexDTO.setOrderuuId(menuItemSplit[1]);
				lexDTO.setBotName(botName);
				lexDTO.setUserId(userId);
				lexDTO.setPageAccessToken(pageAccessToken);
				return lexService.serveLex(lexDTO, context);
			} else if (lexChatMessage.contains("Delivery_")) {
				ILexService lexService = new LexOrderDeliveryServiceImpl();
				LexOrderPayNowService lexDTO = new LexOrderPayNowService();
				String[] menuItemSplit = lexChatMessage.split("_");
				lexDTO.setOrderuuid(menuItemSplit[1]); 
				lexDTO.setBotName(botName);
				lexDTO.setUserId(userId);
				return lexService.serveLex(lexDTO, context);
			} else if (lexChatMessage.contains("Pickup_")) {
				ILexService lexService = new LexOrderPickupServiceImpl();
				LexOrderPayNowService lexDTO = new LexOrderPayNowService();
				String[] menuItemSplit = lexChatMessage.split("_");
				lexDTO.setBotName(botName);
				lexDTO.setOrderuuid(menuItemSplit[1]);
				return lexService.serveLex(lexDTO, context);
			} else if (lexChatMessage.contains("20 Mins_") || lexChatMessage.contains("10 Mins_")
					|| lexChatMessage.contains("30 Mins_")) {
				ILexService lexService = new LexOrderTimingsServiceImpl();
				LexOrderPayNowService lexDTO = new LexOrderPayNowService();
				String[] menuItemSplit = lexChatMessage.split("_");
				lexDTO.setPickUp(menuItemSplit[0]);
				lexDTO.setOrderuuid(menuItemSplit[1]);
				lexDTO.setUserId(userId);
				lexDTO.setBotName(botName);
				lexDTO.setPageAccessToken(pageAccessToken);
				return lexService.serveLex(lexDTO, context);
			} else if (lexChatMessage.contains("viewcart?")) {
				ILexService lexService = new LexClickHereServiceImpl();
				LexOrderPayNowService lexDTO = new LexOrderPayNowService();
				String[] menuItemSplit = lexChatMessage.split("=");
				lexDTO.setOrderuuid(menuItemSplit[1]);
				lexDTO.setUserId(userId);
				lexDTO.setBotName(botName);
				lexDTO.setPageAccessToken(pageAccessToken);
				return lexService.serveLex(lexDTO, context);
			} else if (lexChatMessage.contains("0_") || lexChatMessage.contains("1_") || lexChatMessage.contains("2_")
					|| lexChatMessage.contains("3_") || lexChatMessage.contains("4_")) {
				ILexService lexService = new LexOrderAddressSavingImpl();
				LexOrderAddressService lexDTO = new LexOrderAddressService();
				String[] menuAddressSplit = lexChatMessage.split("_");
				lexDTO.setOrderuuid(menuAddressSplit[1]);
				lexDTO.setAddress(menuAddressSplit[2]);
				lexDTO.setUserId(userId);
				lexDTO.setBotName(botName);
				lexDTO.setPageAccessToken(pageAccessToken);
				return lexService.serveLex(lexDTO, context);

			} else if (lexChatMessage.matches("^\\d{10}$") || lexChatMessage.matches("^\\+(?:[0-9] ?){6,14}[0-9]$")) {
				ILexService lexService = new LexOrderPhoneNumberImpl();
				ILexDTO lexDTO = new ILexDTO();
				lexDTO.setInputTranscript(lexChatMessage);
				lexDTO.setUserId(userId);
				lexDTO.setRestaurantId(restaurantId);
				lexDTO.setBotName(botName);
				lexDTO.setPageAccessToken(pageAccessToken);
				return lexService.serveLex(lexDTO, context);

			}else {
				ILexService lexService = new LexOrderPutAddressServiceImpl();
				ILexDTO lexDTO = new ILexDTO();
				lexDTO.setUserId(userId);
				lexDTO.setRestaurantId(restaurantId);
				lexDTO.setBotName(botName);
				lexDTO.setInputTranscript(lexChatMessage);
				lexDTO.setPageAccessToken(pageAccessToken);
				return lexService.serveLex(lexDTO, context);
				// TODO what to do in default behaviour
			}
		} else {
			ILexService lexService = new LexFacebookPageTimings();
			ILexDTO lexDTO = new ILexDTO();
			return lexService.serveLex(lexDTO, context);
		}
	}

	private boolean timings(String hours) {
		hours = hours.substring(1, hours.length() - 1); // remove curly brackets
		hours = hours.substring(9, hours.length() - 25);
		String[] keyValuePairs = hours.split(","); // split the string to creat
													// key-value pairs
		Map<String, String> map2 = new HashMap<String, String>();

		for (String pair : keyValuePairs) // iterate over the pairs
		{
			String[] entry = pair.split(":"); // split the pairs to get key and
												// value
			map2.put(entry[0].trim(), entry[1].trim() + "\""); // add them to
																// the hashmap
																// and trim
																// whitespaces
			System.out.println(map2);
		}
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		TimeZone timeZone = calendar.getTimeZone();
		System.out.println("time Zone :" + timeZone);
		SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
		String strDate = formatter.format(date);
		System.out.println("StartDate:" + strDate);
		
		/*formatter.setTimeZone(timeZone);
	       String Gmt = formatter.format(date);
	       System.out.println("TimeZone is:" + Gmt);	
	
			
			 ZonedDateTime mstTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC+05.50")); 
		        ZonedDateTime localTime = convert(mstTime, Clock.systemDefaultZone().getZone());
		        System.out.println("MST(" + mstTime + ") = " + localTime);*/
		        String usertimeZone = strDate ;		   
		if(countryName.contains("US")) {
			formatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));
			usertimeZone = formatter.format(date);
		       System.out.println("my gmt time:" + usertimeZone);
		}else if (countryName.contains("India")){
			
			formatter.setTimeZone(TimeZone.getTimeZone("IST"));
		        usertimeZone = formatter.format(date);
		       System.out.println("my india time:" + usertimeZone);
		} else	{
			
			 System.out.println("Restaurant Country:" + countryName);
		}
		
		  
       
		int time = Integer.parseInt(usertimeZone.substring(17, 19));
		// String day=(strDate.substring(0, 3)).toLowerCase();
		String dayop = (String) map2.get("\"" + (usertimeZone.substring(0, 3)).toLowerCase() + "_1_open" + "\"");
		int dayOpen;
		if (dayop != null){		
		String dayop1 = dayop.substring(1, dayop.length() - 1);
		dayOpen = Integer.parseInt(dayop1);
		System.out.println("dayOpen:" + dayOpen);
		} else {
			return false;
		}
		
		int dayOpen2 = 0;
		try{
		String dayop2 = (String) map2.get("\"" + (usertimeZone.substring(0, 3)).toLowerCase() + "_2_open" + "\"");
		String dayop3 = dayop2.substring(1, dayop2.length() - 1);
		dayOpen2 = Integer.parseInt(dayop3);
		System.out.println("dayOpen2:" + dayOpen2);
		} catch (Exception e){
			System.out.println("restaurant closed");
		}
		
		String dayclo = (String) map2.get("\"" + (usertimeZone.substring(0, 3)).toLowerCase() + "_1_close" + "\"");
		String dayclo1 = dayclo.substring(1, dayclo.length() - 1);
		int dayClose = Integer.parseInt(dayclo1);
		System.out.println("dayClose:" + dayClose);
		
		int dayClose2 = 0;
		try {
		String dayclo2 = (String) map2.get("\"" + (usertimeZone.substring(0, 3)).toLowerCase() + "_2_close" + "\"");
		String dayclo3 = dayclo2.substring(1, dayclo2.length() - 1);
		dayClose2 = Integer.parseInt(dayclo3);
		System.out.println("dayClose2:" + dayClose2);
		} catch (Exception e){
			System.out.println("restaurant closed");
		}
		
		System.out.println("My time:"+time);
		if (time >= dayOpen && time < dayClose) {
			System.out.println("Time is true");
			return true;
		} else if (dayOpen2 != 0 && dayClose2 != 0){
			if (time >= dayOpen2 && time < dayClose2){
			System.out.println("Time is true");
			return true;
			}
		}
		return false;
	}


	private String getHours(String pageAccessToken) {
		HttpCallHelper httpCallHelper = new HttpCallHelper();
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		Map<String, String> params = new HashMap<>();
		params.put("access_token", pageAccessToken);
		params.put("fields", "hours");
		String hours = httpCallHelper.doGetCall(headers, "https://graph.facebook.com/v2.10/me", params);
		return hours;
	}

}
