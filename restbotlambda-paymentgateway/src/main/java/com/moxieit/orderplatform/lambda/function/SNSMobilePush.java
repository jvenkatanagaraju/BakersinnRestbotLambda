package com.moxieit.orderplatform.lambda.function;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformApplicationResult;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.DeletePlatformApplicationRequest;
import com.amazonaws.services.sns.model.EndpointDisabledException;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxieit.orderplatform.lambda.request.PushSNSRequest;


public class SNSMobilePush  {


	 public static  String defaultMessage = ""; 
    private final static ObjectMapper objectMapper = new ObjectMapper(); 
 
    public static enum Platform { 
        // Apple Push Notification Service 
        APNS, 
        // Sandbox version of Apple Push Notification Service 
        APNS_SANDBOX, 
        // Amazon Device Messaging 
        ADM, 
        // Google Cloud Messaging 
        GCM 
    } 
    String certificate = "";
    String privateKey = "";
   static String deviceToken = "";
   static String phoneNumber = "";
   String platformApplicationArn = "";
   
   private AmazonSNS snsClient; 
   AmazonSNS sns;
   public String pushSNS(PushSNSRequest pushSNSRequest, Context context) {
  
	   AmazonSNS sns = new AmazonSNSClient();
         snsClient = sns;  
  
        //PushSNSRequest pushSNSRequest = new PushSNSRequest();98
       deviceToken = pushSNSRequest.getDeviceToken();      
        phoneNumber = pushSNSRequest.getPhoneNumber();
        defaultMessage ="You have got New Order from Phone Number: "+phoneNumber;  
    
        try {
        	
        	if(deviceToken.length()<= 70){
        		platformApplicationArn = "arn:aws:sns:us-east-1:241173457298:app/APNS/Restaurant";
        		  demoAppleAppNotification(Platform.APNS); 
        	}
        	else{
        		platformApplicationArn =  "arn:aws:sns:us-east-1:241173457298:app/GCM/android_push_notification";
              	demoAndroidAppNotification(Platform.GCM); 
        	}
        	
        	
            
        
   } catch (AmazonServiceException ase) { 
       System.out.println("Error Message:    " + ase.getMessage()); 
    
   } catch (AmazonClientException ace) { 
            System.out.println("Error Message: " + ace.getMessage()); 
        } 
 
		return certificate;
    }
    public void demoAppleAppNotification(Platform platform){ 
        
        String applicationName = ""; 
        demoNotification(platform, certificate, privateKey, deviceToken, 
                applicationName); 
    }  
 
    public void demoAndroidAppNotification(Platform platform){ 
        // TODO: Please fill in following values for your application 
        String registrationId = ""; 
        String principal = ""; // principal is not applicable for GCM 
        String serverAPIKey = ""; 
        String applicationName = ""; 
        demoNotification(platform, principal, serverAPIKey, registrationId, 
                applicationName); 
    } 
    private void demoNotification(Platform platform, String principal, 
            String credential, String platformToken, String applicationName){ 
    	platformToken = deviceToken;
   
      /*  CreatePlatformApplicationResult platformApplicationResult = createPlatformApplication( 
                applicationName, platform, principal, credential); 
        System.out.println(platformApplicationResult);  
        // The Platform Application Arn can be used to uniquely identify the Platform Application. 
        String platformApplicationArn = platformApplicationResult.getPlatformApplicationArn(); */
    	
    	
    	 
        // Create an Endpoint. This corresponds to an app on a device. 
        CreatePlatformEndpointResult platformEndpointResult = createPlatformEndpoint( 
                "CustomData - Useful to store endpoint specific data", platformToken, platformApplicationArn); 
        System.out.println("token:"+platformToken); 
        System.out.println("endpointArn:"+platformEndpointResult); 
 
        // Publish a push notification to an Endpoint.      
        PublishResult publishResult = publish(platformEndpointResult.getEndpointArn(), platform); 
        
        System.out.println("Published.  MessageId="+ publishResult.getMessageId()); 
        
      //  deletePlatformApplication(platformApplicationArn); 
    } 
 
  
 
    private CreatePlatformEndpointResult createPlatformEndpoint( 
            String customData, String platformToken, String applicationArn) { 
        CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest(); 
        platformEndpointRequest.setCustomUserData(customData); 
        platformEndpointRequest.setToken(platformToken); 
        platformEndpointRequest.setPlatformApplicationArn(applicationArn); 
        return snsClient.createPlatformEndpoint(platformEndpointRequest); 
    } 
 
    private String getPlatformSampleMessage(Platform platform) { 
        switch (platform) { 
        case APNS: 
            return getSampleAppleMessage(); 
        case APNS_SANDBOX: 
            return getSampleAppleMessage(); 
        case GCM: 
            return getSampleAndroidMessage(); 
       /* case ADM: 
            return getSampleKindleMessage(); */
        default: 
            throw new IllegalArgumentException("Platform Not supported : " + platform.name()); 
        } 
    } 
 
    private String getSampleAppleMessage() { 
        Map<String, Object> appleMessageMap = new HashMap<String, Object>(); 
        Map<String, Object> appMessageMap = new HashMap<String, Object>(); 
        appMessageMap.put("alert", "You have got New Order from Phone Number: "+phoneNumber); 
        appMessageMap.put("badge", 1); 
        appMessageMap.put("sound", "default"); 
        appleMessageMap.put("aps", appMessageMap); 
        return jsonify(appleMessageMap); 
    } 
 

 
  /*  public void demoKindleAppNotification(Platform platform){ 
        // TODO: Please fill in following values for your application 
        String registrationId = ""; 
        String clientId = ""; 
        String clientSecret = ""; 
        String applicationName = ""; 
        demoNotification(platform, clientId, clientSecret, registrationId, 
                applicationName); 
    }
     private CreatePlatformApplicationResult createPlatformApplication( 
            String applicationName, Platform platform, String principal, String credential) { 
        CreatePlatformApplicationRequest platformApplicationRequest = new CreatePlatformApplicationRequest(); 
        Map<String, String> attributes = new HashMap<String, String>(); 
        attributes.put("PlatformPrincipal", certificate); 
        
        attributes.put("PlatformCredential", privateKey); 
        platformApplicationRequest.setAttributes(attributes); 
        platformApplicationRequest.setName("Restaurent"); 
        platformApplicationRequest.setPlatform("APNS_SANDBOX"); 
        return snsClient.createPlatformApplication(platformApplicationRequest); 
    } 
   private String getSampleKindleMessage() { 
        Map<String, Object> kindleMessageMap = new HashMap<String, Object>(); 
        kindleMessageMap.put("data", getData()); 
        kindleMessageMap.put("consolidationKey", "Welcome"); 
        kindleMessageMap.put("expiresAfter", 1000); 
        return jsonify(kindleMessageMap); 
    }  */
 
    private String getSampleAndroidMessage() { 
        Map<String, Object> androidMessageMap = new HashMap<String, Object>(); 
        androidMessageMap.put("collapse_key", "Welcome"); 
        androidMessageMap.put("notification", getData()); 
        androidMessageMap.put("delay_while_idle", true); 
        androidMessageMap.put("time_to_live", 125); 
        androidMessageMap.put("dry_run", false); 
        return jsonify(androidMessageMap); 
    } 
 
    private Map<String, String> getData() { 
        Map<String, String> payload = new HashMap<String, String>(); 
        payload.put("text", "You have got New Order from Phone Number: "+phoneNumber  ); 
        return payload; 
    }


    private PublishResult publish(String endpointArn, Platform platform) { 
    	System.out.println("arn:"+endpointArn);
        System.out.println("this is defaultMessage:"+defaultMessage);
        PublishRequest publishRequest = new PublishRequest(); 
        Map<String, String> messageMap = new HashMap<String, String>(); 
        String message; 
       messageMap.put("default", defaultMessage);    
      
        messageMap.put(platform.name(), getPlatformSampleMessage(platform)); 
        // For direct publish to mobile end points, topicArn is not relevant. 
        publishRequest.setTargetArn(endpointArn); 
        publishRequest.setMessageStructure("json"); 
        message = jsonify(messageMap); 
 
        // Display the message that will be sent to the endpoint/ 
        System.out.println(message); 
        publishRequest.setMessage(message); 
        return snsClient.publish(publishRequest); 
    } 
 
   /* private void deletePlatformApplication(String applicationArn) { 
        DeletePlatformApplicationRequest request = new DeletePlatformApplicationRequest(); 
        request.setPlatformApplicationArn(applicationArn); 
        snsClient.deletePlatformApplication(request);   
    } */
    private static String jsonify(Object message) { 
        try { 
            return objectMapper.writeValueAsString(message); 
        } catch (Exception e) { 
            e.printStackTrace(); 
            throw (RuntimeException) e; 
        } 
    }


	public static void main(String[] args) throws IOException {
		SNSMobilePush Sesorderitems = new SNSMobilePush();	
		Context context = null;
		PushSNSRequest forgotPinRequest= new PushSNSRequest();
		forgotPinRequest.setDeviceToken("egRHgN6pJEE:APA91bFnkdp5L2mugcOW4q4iwJPetBFZHLeujBVir04bA4kvIoexrzJFZZlVhoWgmzvBCmHT7COpi3kQw9Ro15iFS3o1prM31665LET7Rk4GZTqpnkSI8BNh4OcFNs01AfiF_S2vCVGC56aja2iO_8WQpHSaUrgW3g");
		forgotPinRequest.setPhoneNumber("+918465865203");
		Sesorderitems.pushSNS(forgotPinRequest, context);
		
	}


 
    }
