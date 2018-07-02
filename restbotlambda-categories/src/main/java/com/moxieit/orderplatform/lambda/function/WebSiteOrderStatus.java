package com.moxieit.orderplatform.lambda.function;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.lambda.request.WebsiteorderstatusRequest;
import org.json.simple.JSONObject;;

public class WebSiteOrderStatus implements RequestHandler<WebsiteorderstatusRequest, Object>{
	
	@Override
	public Object handleRequest(WebsiteorderstatusRequest websiteorderstatusRequest, Context context) {
		// TODO Auto-generated method stub
		
		
			JSONObject data = new JSONObject();	
			String orderuuid = websiteorderstatusRequest.getOrderuuid();
			DynamoDB dynamoDB = DBService.getDBConnection();
			Table orderTable = dynamoDB.getTable("Order");
  			Item orderItem = orderTable.getItem("uuid", orderuuid);
      	  	String orderTracking = orderItem.getString("orderTracking");
			 data.put("orderStatus", orderTracking);
	  			data.put("orderuuid", orderuuid);
		/* boolean isConditionMet = false;
    	   int secondsToWait = 240;
   		long endWaitTime = System.currentTimeMillis() + secondsToWait*1000;
    
        while (System.currentTimeMillis() < endWaitTime && !isConditionMet) {
        	 for(int i = 0;i<=7;i++){
        		boolean status;
          	  	Table orderTable = dynamoDB.getTable("Order");
      			Item orderItem = orderTable.getItem("uuid", orderuuid);
          	  	String orderTracking = orderItem.getString("orderTracking");
          	
          	  data.put("orderStatus", orderTracking);
  			data.put("orderuuid", orderuuid);
          	if (orderTracking.equalsIgnoreCase("CONFIRMED") || orderTracking.equalsIgnoreCase("CONFORMED") 
          				|| orderTracking.equalsIgnoreCase("DECLINED")){
          			 status = true;
          		         			
          		} else {
          			 status = false;
          		}
            //isConditionMet = condition();
        	isConditionMet = status;
        
            if (isConditionMet) {
            	 break;
            } else {
                
					try {
						Thread.sleep(30000);
						//System.out.println("sleeping.... ");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
            }
        }
    }*/
        System.out.println("data: "+data);
		return data;
	}

	
	public static void main(String[] args) {
		//create a new SNS client and set endpoint
		WebSiteOrderStatus Sesorderitems = new WebSiteOrderStatus();	
		Context context = null;
		String orderuuid = "b5923eb7-c6b1-4119-bacb-346598864f94";
		WebsiteorderstatusRequest websiteorderstatusRequest = new WebsiteorderstatusRequest();
		websiteorderstatusRequest.setOrderuuid(orderuuid);

		Sesorderitems.handleRequest(websiteorderstatusRequest, context);
		
}

}
