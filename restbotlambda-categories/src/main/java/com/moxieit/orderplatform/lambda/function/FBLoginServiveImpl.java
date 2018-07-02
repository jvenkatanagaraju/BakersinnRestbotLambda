package com.moxieit.orderplatform.lambda.function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.ListVerifiedEmailAddressesResult;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;
import com.google.gson.Gson;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.HttpCallHelper;
import com.moxieit.orderplatform.lambda.request.FBLoginRequest;
import com.moxieit.orderplatform.lambda.response.FBLoginResponse;

public class FBLoginServiveImpl implements RequestHandler<FBLoginRequest, FBLoginResponse> {

	
	private static final String[] TO = {"alukka@mavenstaffing.in", "vjoneboina@mavenstaffing.in", "vvallabhaneni@mavenstaffing.in"};
	private static final String FROM = "mavenstaffing@gmail.com";
	private static final String SUBJECT = "INFO: Amazon Lex Bot FaceBook Channel Integration";

	@Override
	public FBLoginResponse handleRequest(FBLoginRequest loginInput, Context context) {
		// TODO Auto-generated method stub
		FBLoginResponse fbLoginResponse = new FBLoginResponse();
		try {
			DynamoDB dynamoDB = DBService.getDBConnection();
			Table restauarantTable = dynamoDB.getTable("Restaurant");
			String botName = loginInput.getBotName();
			Item restaurantItem = restauarantTable.getItem("botName",botName);
			String pageName = restaurantItem.getString("pageName");
			String restaurantName = restaurantItem.getString("restaurantName");
			String phone_no = restaurantItem.getString("phone_no");
			String emailId = restaurantItem.getString("emailId");
			System.out.println(pageName);
			//String userAccessToken = loginInput.getUserAccessToken();

			/*HttpCallHelper httpCallHelper = new HttpCallHelper();
			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "application/json");
			Map<String, String> params = new HashMap<>();
			params.put("fb_exchange_token", userAccessToken);
			params.put("client_id", "281917592269279");
			params.put("client_secret", "b540574996307785f6bdd6f8ec61d30b");
			String request = httpCallHelper.doGetCall(headers, "https://graph.facebook.com/v2.8/oauth/access_token?grant_type=fb_exchange_token", params);
			System.out.println(request.toString());
			Gson gson1 = new Gson();
			Object json1 = gson1.fromJson(request, Object.class);
			System.out.println(json1);
			Map<String,String> map1 = new HashMap<String, String>();
			map1 = (Map<String, String>) json1;
			String permanentUserAccessToken = (String) map1.get("access_token");
			System.out.println(permanentUserAccessToken);*/
			
			/*HttpCallHelper httpCallHelper1 = new HttpCallHelper();
			Map<String, String> headers1 = new HashMap<>();
			headers1.put("Content-Type", "application/json");
			Map<String, String> params1 = new HashMap<>();
			params1.put("access_token", userAccessToken);
			String request1 = httpCallHelper1.doGetCall(headers1, "https://graph.facebook.com/me/accounts", params1);
			System.out.println(request1.toString());
			Gson gson2 = new Gson();
			Object json2 = gson2.fromJson(request1, Object.class);
			System.out.println(json2);

			Map<String, List<Map<String, Map>>> map2 = new HashMap<String, List<Map<String, Map>>>();
			map2 = (Map<String, List<Map<String, Map>>>) json2;

			int size = (int) map2.get("data").size();
			System.out.println(size);
			int i = 0;

			for (i = 0; i <= size; i++) {

				Map<String, List<Map<String, String>>> map3 = new HashMap<String, List<Map<String, String>>>();
				map3 = (Map<String, List<Map<String, String>>>) json2;
				String pagename = (String) map3.get("data").get(i).get("name");
				String pageid = (String) map3.get("data").get(i).get("id");
				System.out.println(pagename);
				if (pagename.equals(pageName)) {
					String pageAccessToken = (String) map3.get("data").get(i).get("access_token");
					System.out.println(pageAccessToken);

					UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("botName", botName)
							.withUpdateExpression("set pageAccessToken = :pageAccessToken")
							.withValueMap(new ValueMap().withString(":pageAccessToken", pageAccessToken));
					restauarantTable.updateItem(updateItemSpec);*/
					
					String BODY = "Hi,\n\n"+ 
					"Step1: Need to take Admin role permissions from Client of "+restaurantName+" Restaurant for "+pageName + " page.\n\nphone_no : "+phone_no +"\nemailId     : "+emailId +"\n\nStep2: Create a FaceBook Developer App and update details as below for BotName "
							+botName +" in Restaurant Table.\n\n"
							+"appId: @App_Id,\nappSecretKey: @App_Secretkey,\npageAccessToken: @Page_AccessToken of Page "+pageName+ ".\n\nFacebook Id    : vvamsi96@gmail.com.\nPassword	: Test@12345.\n\n"
									+ "Step3: Create Facebook Channel integration for " +pageName +" page in "+botName +" bot by using following details.\n\n"
							 +" \"BotName\" : " +botName + "\n"
							+ " \"Page AccessToken\" : @Page_AccessToken of page " + pageName + "\n"
							+ " \"App SecretKey\" : @App_SecretKey" +"\n"
							+ " \"Verify Token\" : moxie \n\n"
						+"Step4: Post Generated CallBack URL & Verify Token as input to the FBWebhooks lambda as shown below.\n\n"
							+ "{\n"
							    +"\"callBackUrl\"" + ": \"Generated Bot CallBack URL\",\n"
                                + "\"verifyToken\"" +": \"moxie\",\n"
                                + "\"botName\"" +": \""+botName + "\"\n"                               
							 +"}\n\n" + "Thanks&Regards,\nMavenStaffing Pvt Ltd.";
					AmazonSimpleEmailService ses = new AmazonSimpleEmailServiceClient();
					verifyEmailAddress(ses, FROM);
					  Destination destination = new Destination().withToAddresses(TO);
					  Content subject = new Content().withData(SUBJECT);
				        Content textBody = new Content().withData(BODY);
				        Body body = new Body().withText(textBody);
				        Message message = new Message().withSubject(subject).withBody(body);
				        SendEmailRequest request2 = new SendEmailRequest().withSource(FROM)
				                .withDestination(destination).withMessage(message);

				        try {
				            System.out.println(
				                    "Attempting to send an email through Amazon SES using the AWS SDK for Java.");

				            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
				                    .withRegion(Regions.US_EAST_1).build();

				            // Send the email.
				            client.sendEmail(request2);
				            System.out.println("Email sent!");
				            fbLoginResponse.setMessage("Successfully sent the Messages");
							fbLoginResponse.setPageAccessToken(null);
							fbLoginResponse.setStatus("success");
							return fbLoginResponse;
				        } catch (Exception ex) {
				            System.out.println("The email was not sent.");
				            System.out.println("Error message: " + ex.getMessage());
				        }
				   //	}
			//}
		}

		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		fbLoginResponse.setMessage("failed");
		fbLoginResponse.setPageAccessToken(null);
		fbLoginResponse.setStatus("failed");
		return fbLoginResponse;
	}

	private static void verifyEmailAddress(AmazonSimpleEmailService ses, String address) {
		ListVerifiedEmailAddressesResult verifiedEmails = ses.listVerifiedEmailAddresses();
		if (verifiedEmails.getVerifiedEmailAddresses().contains(address))
			return;

		ses.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress(address));
		System.out.println("Please check the email address " + address + " to verify it");
		System.exit(0);
	}
	/*public static void main(String[] args) {
		FBLoginServiveImpl FbLoginServiveImpl = new FBLoginServiveImpl();
		Context context = null;

		FBLoginRequest FbLoginRequest = new FBLoginRequest();

		FbLoginRequest.setBotName("Testfoody");
		
		FbLoginServiveImpl.handleRequest(FbLoginRequest, context);
	}*/
	
}
