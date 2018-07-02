package com.moxieit.orderplatform.lambda.function;


import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.GetSMSAttributesRequest;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SetSMSAttributesRequest;



public class AmazonSNS 
{
	
	public static void setDefaultSmsAttributes(AmazonSNSClient snsClient) {
		SetSMSAttributesRequest setRequest = new SetSMSAttributesRequest()
				.addAttributesEntry("DefaultSenderID", "mySenderID")
				.addAttributesEntry("MonthlySpendLimit", "5")			
		
				.addAttributesEntry("DefaultSMSType", "Transactional");

		snsClient.setSMSAttributes(setRequest);
		/*Map<String, String> myAttributes = snsClient.getSMSAttributes(new GetSMSAttributesRequest())
			.getAttributes();
		System.out.println("My SMS attributes:");
		for (String key : myAttributes.keySet()) {
			System.out.println(key + " = " + myAttributes.get(key));
		}*/
	}
  
	
public static void sendSMSMessage(AmazonSNSClient snsClient, String message, 
		String phoneNumber) {
	setDefaultSmsAttributes(snsClient);

	  Map<String, MessageAttributeValue> smsAttributes = 
	            new HashMap<String, MessageAttributeValue>();
        PublishResult result = snsClient.publish(new PublishRequest()
                        .withMessage(message)
                        .withPhoneNumber(phoneNumber));
                       // .withMessageAttributes(smsAttributes));
        System.out.println(result);
}

/*public static void main(String[] args) {
		//create a new SNS client and set endpoint
		
		@SuppressWarnings("deprecation")
		AmazonSNSClient snsClient = new AmazonSNSClient();
		setDefaultSmsAttributes(snsClient);
        String message = "Dear #custName,Thank you for your order and orderid was #getorderId at #restaurantName.we will contact you as soon as possible";
        String phoneNumber = "+917396336460";
        Map<String, MessageAttributeValue> smsAttributes = 
                new HashMap<String, MessageAttributeValue>();
        //<set SMS attributes>
        sendSMSMessage(snsClient, message, phoneNumber);
}*/
	

}
