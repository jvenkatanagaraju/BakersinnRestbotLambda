package com.moxieit.orderplatform.lambda.function;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.CreateStackRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.StackStatus;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3Client;
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
import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;
import com.moxieit.orderplatform.lambda.DB.DBService;
import com.moxieit.orderplatform.helper.IdService;

public class CloudFormationSample implements RequestHandler<Object, String> {

	public static String notificationARNs = "CAPABILITY_IAM";
	AmazonS3Client s3client;
	private static final String[] TO = {"alukka@mavenstaffing.in", "vjoneboina@mavenstaffing.in", "vvallabhaneni@mavenstaffing.in"};
	private static final String FROM = "no-reply@moxieit.com";
	private static final String SUBJECT = "INFO: Amazon Lex Bot FaceBook Channel Integration";


	public static String convertStreamToString(InputStream in) throws Exception {

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder stringbuilder = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			stringbuilder.append(line + "\n");
		}
		in.close();
		return stringbuilder.toString();
	}

	public static String waitForCompletion(AmazonCloudFormation stackbuilder, String stackName) throws Exception {

		DescribeStacksRequest wait = new DescribeStacksRequest();
		wait.setStackName(stackName);
		Boolean completed = false;
		String stackStatus = "Unknown";
		String stackReason = "";

		System.out.print("Waiting");

		while (!completed) {
			List<Stack> stacks = stackbuilder.describeStacks(wait).getStacks();
			if (stacks.isEmpty()) {
				completed = true;
				stackStatus = "NO_SUCH_STACK";
				stackReason = "Stack has been deleted";
			} else {
				for (Stack stack : stacks) {
					if (stack.getStackStatus().equals(StackStatus.CREATE_COMPLETE.toString())
							|| stack.getStackStatus().equals(StackStatus.CREATE_FAILED.toString())
							|| stack.getStackStatus().equals(StackStatus.ROLLBACK_FAILED.toString())
							|| stack.getStackStatus().equals(StackStatus.DELETE_FAILED.toString())) {
						completed = true;
						stackStatus = stack.getStackStatus();
						stackReason = stack.getStackStatusReason();
					}
				}
			}
			System.out.print(".");
			if (!completed)
				Thread.sleep(10000);
		}
		System.out.print("done\n");

		return stackStatus + " (" + stackReason + ")";
	}

	@Override
	public String handleRequest(Object o, Context context) {
		// LambdaLogger log = context.getLogger();
		Gson gson = new Gson();
		String json = gson.toJson(o);
		System.out.println(json);
		// log.log(json);

		Map<String, List<Map<String, Map<String, Map<String, Map>>>>> map = new HashMap<String, List<Map<String, Map<String, Map<String, Map>>>>>();
		map = (Map<String, List<Map<String, Map<String, Map<String, Map>>>>>) o;
		Map<String, List<Map<String, String>>> eventMap = new HashMap<>();
		eventMap = (Map<String, List<Map<String, String>>>) o;
		if ((eventMap.get("Records").get(0).get("eventName")).equals("INSERT")) {
		AmazonCloudFormation stackbuilder = new AmazonCloudFormationClient();
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		stackbuilder.setRegion(usEast1);

		System.out.println("===========================================");
		System.out.println("Getting Started with AWS CloudFormation");
		System.out.println("===========================================\n");
		IdService idService = new IdService();
		try {
			// Create a stack
			String stackName1 = "CloudFormationBotTemplate" + idService.getID();
			String botName = (String) map.get("Records").get(0).get("dynamodb").get("Keys").get("botName").get("S");
			System.out.println(botName);
			CreateStackRequest createRequest1 = new CreateStackRequest();
			createRequest1.setStackName(stackName1);
			createRequest1.withCapabilities(notificationARNs);
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("file/CloudformationBot.template").getFile());
			try {
				String readFileToString = FileUtils.readFileToString(file);
				String botNameReplace = StringUtils.replace(readFileToString, "botnamee", botName);
				String aliasReplace = StringUtils.replace(botNameReplace, "aliasnamee", botName + "alias");
				createRequest1.setTemplateBody(aliasReplace);
			} catch (Exception e) {
				// TODO: handle exception
			}
			System.out.println("Creating a stack called " + createRequest1.getStackName() + ".");
			stackbuilder.createStack(createRequest1);
			try {
				DynamoDB dynamoDB = DBService.getDBConnection();
				Table restauarantTable = dynamoDB.getTable("Restaurant");			
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
								 +"}\n\n" 
								 +"Step5: Submit App for Review.\n\n"									
									    +"Go to path PRODUCTS -> Messenger -> App Review for Messenger -> pages_messaging -> Add to Submission" 
									    + "\n\n"
									+"Select a page as:" + pageName + "\n"
									+"Commands: Getstarted\n"
									+"Automated Reply: Menu Categories :\n\n"+"Thanks&Regards,\nMavenStaffing Pvt Ltd.";
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
			
			return "Success";
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with AWS CloudFormation, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		}
		return "Already you have created the bot.";
	}
	private static void verifyEmailAddress(AmazonSimpleEmailService ses, String address) {
		ListVerifiedEmailAddressesResult verifiedEmails = ses.listVerifiedEmailAddresses();
		if (verifiedEmails.getVerifiedEmailAddresses().contains(address))
			return;

		ses.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress(address));
		System.out.println("Please check the email address " + address + " to verify it");
		System.exit(0);
	}

}
