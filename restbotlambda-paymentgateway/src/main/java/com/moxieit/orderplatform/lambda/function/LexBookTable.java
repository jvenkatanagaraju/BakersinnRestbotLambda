package com.moxieit.orderplatform.lambda.function;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.moxieit.orderplatform.lambda.DB.DBService;
import com.moxieit.orderplatform.lambda.request.BookTableRequest;

public class LexBookTable implements RequestHandler<BookTableRequest, String> {
	
		@Override
		public String handleRequest(BookTableRequest bookTableRequest, Context context) {
			// TODO Auto-generated method stub
		
				
				DynamoDB dynamoDB = DBService.getDBConnection();
				Table tables = dynamoDB.getTable("Tables");
				UpdateItemSpec updateItemSpec = new UpdateItemSpec()
						.withPrimaryKey("restaurantId", bookTableRequest.getRestaurantId(),
								"tableId", bookTableRequest.getTableId())
						.withUpdateExpression("set emailId = :emi,phoneNumber = :phn, userName = :un, tableStatus = :sts,"
								+ " bookedTime = :tim, bookedDate = :dat, comments = :com, bookedSeats = :boo")
						.withValueMap(new ValueMap().withString(":emi", bookTableRequest.getEmailId())
													.withString(":phn", bookTableRequest.getPhoneNumber())
													.withString(":un", bookTableRequest.getUserName())
													.withString(":sts", "BOOKED")
													.withString(":tim", bookTableRequest.getBookedTime())
													.withString(":com", bookTableRequest.getComments())
													.withString(":boo", bookTableRequest.getBookedSeats())
													.withString(":dat", bookTableRequest.getBookedDate()));
				tables.updateItem(updateItemSpec);
					
							return  "successfully Booked";
					
				
}
		


			}