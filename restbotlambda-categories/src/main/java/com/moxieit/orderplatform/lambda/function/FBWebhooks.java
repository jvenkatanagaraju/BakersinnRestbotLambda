package com.moxieit.orderplatform.lambda.function;

import java.util.HashMap;
import java.util.Map;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.HttpCallHelper;
import com.moxieit.orderplatform.lambda.request.FBWebhooksRequest;
import com.moxieit.orderplatform.lambda.response.FBWebhooksResponce;
import com.moxieit.orderplatform.lambda.response.HttpResponse;

public class FBWebhooks implements RequestHandler<FBWebhooksRequest, FBWebhooksResponce>{
	
	public FBWebhooksResponce handleRequest(FBWebhooksRequest loginInput, Context context) {
		
		FBWebhooksResponce fbwebhooks = new FBWebhooksResponce();
		try {
		String callBackUrl = loginInput.getCallBackUrl();
		String verifyToken = loginInput.getVerifyToken();		
		String botName = loginInput.getBotName();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table restauarantTable = dynamoDB.getTable("Restaurant");	
		Item restaurantItem = restauarantTable.getItem("botName",botName);
		String pageAccessToken = restaurantItem.getString("pageAccessToken");
		String appId = restaurantItem.getString("appId");
		String appSecretKey = restaurantItem.getString("appSecretKey");
		String message = "{\"get_started\":{\"payload\":\"Getstarted\"}}";
		
		HttpCallHelper httpCallHelper4 = new HttpCallHelper();
		Map<String, String> headers4 = new HashMap<>();
		headers4.put("Content-Type", "application/json");
		Map<String, String> params4 = new HashMap<>();
		Map<String, String> body4 = new HashMap<>();
		body4.put("body", message);
		params4.put("access_token", pageAccessToken);		
		HttpResponse request4 = httpCallHelper4.doPostCall(headers4, "https://graph.facebook.com/v2.10/me", params4);
		
		
		
		HttpCallHelper httpCallHelper3 = new HttpCallHelper();
		Map<String, String> headers3 = new HashMap<>();
		headers3.put("Content-Type", "application/json");
		Map<String, String> params3 = new HashMap<>();
		params3.put("access_token", pageAccessToken);		
		String request3 = httpCallHelper3.doGetCall(headers3, "https://graph.facebook.com/v2.10/me", params3);
		Gson gson3 = new Gson();
		Object json3 = gson3.fromJson(request3, Object.class);
		System.out.println(json3);
		Map<String,String> map3 = new HashMap<String, String>();
		map3 = (Map<String, String>) json3;
		String pageId = (String) map3.get("id");
		System.out.println(pageId);

		
		HttpCallHelper httpCallHelper1 = new HttpCallHelper();
		Map<String, String> headers1 = new HashMap<>();
		headers1.put("Content-Type", "application/json");
		Map<String, String> params1 = new HashMap<>();
		params1.put("client_id", appId);
		params1.put("client_secret", appSecretKey);
		String request1 = httpCallHelper1.doGetCall(headers1, "https://graph.facebook.com/oauth/access_token?grant_type=client_credentials", params1);
		System.out.println(request1.toString());
		Gson gson2 = new Gson();
		Object json2 = gson2.fromJson(request1, Object.class);
		System.out.println(json2);
		Map<String,String> map1 = new HashMap<String, String>();
		map1 = (Map<String, String>) json2;
		String appAccessToken = (String) map1.get("access_token");
		System.out.println(appAccessToken);
		
		HttpCallHelper httpCallHelper = new HttpCallHelper();
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		Map<String, String> params = new HashMap<>();
		params.put("object", "page");
		params.put("fields", "messages,messaging_postbacks,messaging_optins,message_deliveries,message_reads,messaging_payments,"
				+ "messaging_pre_checkouts,messaging_checkout_updates,messaging_account_linking,messaging_referrals,"
				+ "message_echoes,standby,messaging_handovers,messaging_policy_enforcement"
);
		params.put("callback_url", callBackUrl);
		params.put("verify_token", verifyToken);
		params.put("access_token", appAccessToken);
		HttpResponse request = httpCallHelper.doPostCall(headers, "https://graph.facebook.com/v2.8/"+ appId+"/subscriptions", params);
		System.out.println(request.toString());
		
		HttpCallHelper httpCallHelper2 = new HttpCallHelper();
		Map<String, String> headers2 = new HashMap<>();
		headers2.put("Content-Type", "application/json");
		Map<String, String> params2 = new HashMap<>();
		params2.put("access_token", pageAccessToken);		
		HttpResponse request2 = httpCallHelper2.doPostCall(headers2, "https://graph.facebook.com/v2.6/"+ pageId+"/subscribed_apps", params2);
		System.out.println(request2.toString());
		
		fbwebhooks.setMessage("Successfully subscribed");		
		fbwebhooks.setStatus("success");
		return fbwebhooks;
	}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		fbwebhooks.setMessage("failed");	
		fbwebhooks.setStatus("failed");
		return fbwebhooks;
	}
	
	/*public static void main(String[] args) {
		FBWebhooks fbWebhooks = new FBWebhooks();
		Context context = null;

		FBWebhooksRequest fbWebhooksRequest = new FBWebhooksRequest();

		fbWebhooksRequest.setCallBackUrl("https://channels.lex.us-east-1.amazonaws.com/facebook/webhook/c72326b6-6db3-4751-829c-7e7aca61af0c");
		fbWebhooksRequest.setVerifyToken("moxie");
		fbWebhooksRequest.setBotName("Test");		
		fbWebhooks.handleRequest(fbWebhooksRequest, context);
	}*/

}
