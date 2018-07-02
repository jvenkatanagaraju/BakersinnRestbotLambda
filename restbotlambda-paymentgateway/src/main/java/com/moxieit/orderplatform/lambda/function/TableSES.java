package com.moxieit.orderplatform.lambda.function;

	import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

	import java.io.File;
	import java.io.IOException;
	

	import org.apache.commons.io.FileUtils;

	
	import com.amazonaws.regions.Regions;
	import com.amazonaws.services.dynamodbv2.document.DynamoDB;
	import com.amazonaws.services.dynamodbv2.document.Item;
	import com.amazonaws.services.dynamodbv2.document.Table;
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
	import com.amazonaws.util.StringUtils;
	import com.moxieit.orderplatform.lambda.DB.DBService;
	import com.moxieit.orderplatform.lambda.request.SesRequest;
	import com.moxieit.orderplatform.lambda.response.SesResponce;
	
	public class TableSES implements RequestHandler<String, String> {

		private static final String FROM = "no-reply@moxieit.com";
		private static final String SUBJECT = "Customer Order Confirmation";
		private static final String Seats = null;
		static int counter = 0;
		static int instanceCounter = 0;
		private String bookedDate;
		private String bookedOn;
		private String tableName;
		private String emailId;
		private String taxamount;

		public SesResponce getSES(SesRequest sesRequest) throws IOException {
			StringBuilder BODY = new StringBuilder();
			SesResponce sesResponse = new SesResponce();
			//String stripeEmail = sesRequest.getStripeEmail();
			//System.out.println(stripeEmail);
			String ordertableuuid = sesRequest.getOrdertableuuid();
			DynamoDB dynamoDB = DBService.getDBConnection();
			Table Table = dynamoDB.getTable("Tables");
			String tableId = null;
			Item tableItem = Table.getItem("tableId", tableId);
			String botemailId = tableItem.getString("bookedDate");
			String phone_no = tableItem.getString("bookedOn");
			String restaurantName1 = tableItem.getString("bookedTime");
			String street_1 = tableItem.getString("emailId");
			String city = tableItem.getString("phoneNumber");
			String state = tableItem.getString("state");
			String zipCode = tableItem.getString("tableName");
			String restaurantaddress = restaurantName1+","+street_1+","+city+","+state;
			String restaurantName = null;
			
			/*if (tableId != null) {
					restaurantName = tableItem.getString("restaurantName");
			} else
			{
				restaurantName = "OnlineOrder";
			}
			String address1 = userid.getString("address");
			String address2 = "NULL";
			if (address1.equals(address2)) {
				String pickUp = userid.getString("pickUp");
				address1 = "PickUp in "+pickUp;	
			}
			String userName = userid.getString("userName");
			if (userName == null){
				userName = "Customer";
			}
			String emailId = userid.getString("emailId");	
			String orderDate = userid.getString("orderDate");
			String orderFrom = userid.getString("orderFrom");
			String phoneNumber = userid.getString("phoneNumber");
			String totalBill = userid.getString("totalBill");
			Number totalBill1 = (Number) userid.get("totalBillWithTax");
			String totalBillWithTax = String.valueOf(totalBill1);
			Number tax = userid.getNumber("tax");
			String taxValue = String.valueOf(tax);	
			
			//String TO = botemailId;
			//String[] TO = {botemailId, stripeEmail};		
			
			ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("orderuuid").eq(ordertableuuid))
					.buildForScan();
			ItemCollection<ScanOutcome> scan1 = orderItemTable.scan(xspec1);
			Consumer<Item> action1 = new Consumer<Item>() {
				@Override
				public void accept(Item t) {					
							
					Table menuItemTable = dynamoDB.getTable("Menu_Items");
					Object categoryObject = t.getString("categoryId");
					Object itemObject = t.getString("menuItemId");
					Item menuItem = menuItemTable.getItem("categoryId", categoryObject, "itemId", itemObject);
					//String Image = menuItem.getString("image");				
					
					/*BODY.append("<tr><td class=\"thick-line text-center\">").append(menuItem.getString("itemName"))
					.append("</td><td class=\"thick-line text-center\">").append(t.getString("spiceyLevel"))
					.append("</td><td class=\"thick-line text-center\">").append(t.getString("quantity"))
					.append("</strong></td><td class=\"thick-line text-center\">$")
					.append(t.getString("itemCost")).append("</td></tr>");	
					if (!t.getString("quantity").equals("0")){
					BODY.append("<tr style='margin:0;vertical-align:baseline;height:20px;background:#f5f3f0; border-top:1px solid #fff'><td style='padding:15px;'>")
					.append(t.getString("quantity")).append(" x ").append(menuItem.getString("itemName"))
					/*.append("</td><td class=\"thick-line\">").append(t.getString("spiceyLevel"))
					.append("</td><td style='text-align:right;padding:15px;'>").append(t.getString("itemCost")).append(".00 USD")
							
					.append("</td></tr>");
					}
					
					}
												
			};*/
			//scan1.forEach(action1);		
			String itemsbody = BODY.toString();
		System.out.println(itemsbody);
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("file/ses.html").getFile());
			try {
				
				String readFileToString = FileUtils.readFileToString(file);
				String orderItemReplace = StringUtils.replace(readFileToString, "tableId", tableId);
				String phoneNumberReplace = StringUtils.replace(orderItemReplace, "Seats", Seats);
				String restaddressadd = StringUtils.replace(phoneNumberReplace, "RestaurantAddress", restaurantaddress);
				String dateReplace = StringUtils.replace(restaddressadd, "BookedDate", bookedDate);
				String bookadd = StringUtils.replace(dateReplace, "bookedOn", bookedOn);
				String check = "false";
				String restaurantNameValue = null;
				if (tableId != null) {
					restaurantNameValue = StringUtils.replace( "RestaurantName", restaurantName, restaurantNameValue);
					check = "true";
				}
				AmazonSimpleEmailService ses = new AmazonSimpleEmailServiceClient();
				verifyEmailAddress(ses, FROM);
				Destination destination = new Destination();
				if (emailId != null){
				
					String[] TO = {botemailId, emailId};
					 destination = new Destination().withToAddresses(TO);
					
				} else {
					
					String TO = botemailId;
					 destination = new Destination().withToAddresses(TO);
				}
				//Destination destination = new Destination().withToAddresses(TO);
				Content subject = new Content().withData(SUBJECT);
				// Content textBody = new Content().withData(BODY);
				Content htmlContent = null;
				if (check.equals("true")) {
					htmlContent = new Content().withData(restaurantNameValue);
				} else {
					htmlContent = new Content().withData(taxamount);
				}			
				Body body = new Body().withHtml(htmlContent);
				Message message1 = new Message().withSubject(subject).withBody(body);
				SendEmailRequest request2 = new SendEmailRequest().withSource(FROM).withDestination(destination)
						.withMessage(message1);

				message1.setBody(body);

				try {
					System.out.println("Attempting to send an email through Amazon SES using the AWS SDK for Java.");

					AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
							.withRegion(Regions.US_EAST_1).build();

					// Send the email.
					client.sendEmail(request2);
					System.out.println("Email sent!");

				} catch (Exception ex) {
					System.out.println("The email was not sent.");
					System.out.println("Error message: " + ex.getMessage());
				}

				sesResponse.setStatus("success");
				sesResponse.setMessage("Successfully sent the Messages");

				// return sesResponse;

			} catch (Exception e) {
				// TODO: handle exception
			}
			return sesResponse;

		}

		private static void verifyEmailAddress(AmazonSimpleEmailService ses, String address) {
			ListVerifiedEmailAddressesResult verifiedEmails = ses.listVerifiedEmailAddresses();
			if (verifiedEmails.getVerifiedEmailAddresses().contains(address))
				return;

			ses.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress(address));
			System.out.println("Please check the email address " + address + " to verify it");
			System.exit(0);
		}

	public static void main(String[] args) throws IOException {
			SESorderitems Sesorderitems = new SESorderitems();		
			SesRequest sesRequest = new SesRequest();
			 sesRequest.setOrdertableuuid("51dd1f8e-825a-4c80-bc67-faac8a718ef4");	
			Sesorderitems.getSES(sesRequest);
			
		}

		@Override
		public String handleRequest(String input, Context context) {
			// TODO Auto-generated method stub
			return null;
		}
	}



