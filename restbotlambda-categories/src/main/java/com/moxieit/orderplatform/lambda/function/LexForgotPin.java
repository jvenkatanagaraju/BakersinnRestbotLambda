package com.moxieit.orderplatform.lambda.function;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.io.IOException;
import java.util.function.Consumer;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
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
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.lambda.request.ForgotPinRequest;
import com.moxieit.orderplatform.lambda.response.ForgotPinResponse;
import com.moxieit.orderplatform.lambda.response.LoginResponse;



public class LexForgotPin implements RequestHandler<ForgotPinRequest, ForgotPinResponse>{
	
	String securePin;
    static final String FROM = "no-reply@moxieit.com";    
  
    private static final String SUBJECT = "Forgotten pin confirmation";
    
    public ForgotPinResponse handleRequest(ForgotPinRequest forgotPinRequest, Context context) {
    	ForgotPinResponse forgotPinResponse = new ForgotPinResponse();
    	  final String TO = forgotPinRequest.getEmailAddress();
    	  DynamoDB dynamoDB = DBService.getDBConnection();		
			Table restaurantTable = dynamoDB.getTable("Restaurant");
			ScanExpressionSpec xspec3 = new ExpressionSpecBuilder().withCondition(S("emailId").eq(TO))
					.buildForScan();
			ItemCollection<ScanOutcome> scan3 = restaurantTable.scan(xspec3);

			Consumer<Item> action3 = new Consumer<Item>() {
				@Override
				public void accept(Item t2) {
					Object botName1 = t2.getString("botName");
					Item itemuuid = restaurantTable.getItem("botName", botName1);
					securePin = itemuuid.getString("securePin");			
					System.out.println("securePin:" +securePin);

				}

			};
			scan3.forEach(action3);
			if (securePin != null) {		
			String BODY = "Hi, \n\n We've received a request for Forget Pin of your App. If you didn't make the request, just ignore this email."
					+ " Otherwise, you can login to your App using this Secure Pin.\n\n Secure Pin: " + securePin+ "\n\n Thanks,\n Restaurant App.";
	AmazonSimpleEmailService ses = new AmazonSimpleEmailServiceClient();
	verifyEmailAddress(ses, FROM);
	Destination destination = new Destination().withToAddresses((String) TO);
	Content subject = new Content().withData(SUBJECT);
	Content textBody = new Content().withData(BODY);
    Body body = new Body().withText(textBody);
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
	forgotPinResponse.setMessage("Email sent Successfully");
	forgotPinResponse.setStatus("success");
	return forgotPinResponse;
    } else {
    	forgotPinResponse.setMessage("There is no restaurant with given email address");
    	forgotPinResponse.setStatus("failed");
		System.out.println("failed");
    	return forgotPinResponse;
    }
    }
    
	private static void verifyEmailAddress(AmazonSimpleEmailService ses, String address) {
		ListVerifiedEmailAddressesResult verifiedEmails = ses.listVerifiedEmailAddresses();
		if (verifiedEmails.getVerifiedEmailAddresses().contains(address))
			return;

		ses.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress(address));
		System.out.println("Please check the email address " + address + " to verify it");
		System.exit(0);
	}
	
	/*public static void main(String[] args) throws IOException {
		LexForgotPin Sesorderitems = new LexForgotPin();	
		Context context = null;
		ForgotPinRequest forgotPinRequest= new ForgotPinRequest();
		forgotPinRequest.setEmailAddress("alukka@mavenstaffing.in");
		Sesorderitems.handleRequest(forgotPinRequest, context);
		
	}*/

}
