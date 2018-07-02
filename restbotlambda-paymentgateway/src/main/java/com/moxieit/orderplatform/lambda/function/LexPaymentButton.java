package com.moxieit.orderplatform.lambda.function;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.StringUtils;
import com.moxieit.orderplatform.lambda.DB.DBService;

public class LexPaymentButton implements RequestHandler<String, String> {

	public String handleRequest(String lexRequest, Context context) {
		// context.getLogger().log(lexRequest);
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderTable = dynamoDB.getTable("Order");
		Item orderTableItem = orderTable.getItem("uuid", lexRequest);
		if(orderTableItem.getString("paymentDone").equals("false")){
		UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", lexRequest)
				.withUpdateExpression("set creationDate = :val")
				.withValueMap(new ValueMap().withNumber(":val", System.currentTimeMillis()));
		orderTable.updateItem(updateItemSpec);
		Number orderAmount = orderTableItem.getNumber("totalBillWithTax");
		Double orderAmount1 = orderAmount.doubleValue() * 100;
		String orderAmount2 = orderAmount1.toString();
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("file/PaymentTest.html").getFile());
		try {
			String readFileToString = FileUtils.readFileToString(file);
			String itemuuidReplace = StringUtils.replace(readFileToString, "Itemuuid", lexRequest);
			String orderAmountReplace = StringUtils.replace(itemuuidReplace, "orderAmount", orderAmount2);
			return orderAmountReplace;
		} catch (Exception e) {
			// TODO: handle exception
		}
		}
		else{
			return "YOUR PAYMENT IS ALREADY DONE";
		}
		return null;
	}
}
