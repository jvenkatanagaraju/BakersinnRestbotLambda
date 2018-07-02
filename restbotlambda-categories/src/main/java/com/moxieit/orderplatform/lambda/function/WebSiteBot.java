package com.moxieit.orderplatform.lambda.function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class WebSiteBot implements RequestHandler<String, String>{
	
	public String handleRequest(String botName, Context context) {
		context.getLogger().log("Input: " + botName);
		System.out.println("botName: "+botName);
		System.out.println("botAlias: "+botName+"alias");
		return botName;
	}
		
}
