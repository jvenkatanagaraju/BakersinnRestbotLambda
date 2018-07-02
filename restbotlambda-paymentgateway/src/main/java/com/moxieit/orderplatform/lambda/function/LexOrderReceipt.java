package com.moxieit.orderplatform.lambda.function;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.util.StringUtils;
import com.moxieit.orderplatform.lambda.DB.DBService;

public class LexOrderReceipt implements RequestHandler<String, String>{
	
	public String handleRequest(String orderuuid, Context context) {
		StringBuilder BODY = new StringBuilder();	
		// TODO Auto-generated method stub.
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderTable = dynamoDB.getTable("Order");
		KeyAttribute primaryKey = new KeyAttribute("uuid", orderuuid);
		System.out.println(orderuuid);
		Item order = orderTable.getItem(primaryKey);
		String botName = order.getString("botName");
		String orderDate = order.getString("orderDate");
		Table restaurantTable = dynamoDB.getTable("Restaurant");
		String restaurantName = null;
		String RestaurantPhone = null;
		String restaurantaddress = null;
		Item restaurantItem = null;
		if (botName != null) {
			restaurantItem = restaurantTable.getItem("botName", botName);
			restaurantName = restaurantItem.getString("restaurantName");
			RestaurantPhone = restaurantItem.getString("phone_no");
			String street_1 = restaurantItem.getString("street_1");
			String city = restaurantItem.getString("city");
			String state = restaurantItem.getString("state");
			String zipCode = restaurantItem.getString("zipCode");
			restaurantaddress = restaurantName+","+street_1+","+city+","+state+","+zipCode;
		}
		Number totalBill1 = order.getNumber("totalBill");
		if (order != null && totalBill1.intValue() != 0) {
			Number totalBill = order.getNumber("totalBill");
			Number tax = order.getNumber("tax");
			Number totalBillWithTax = order.getNumber("totalBillWithTax");
			String totalBillValue = String.valueOf(totalBill);
			String totalBillWithTaxValue = String.valueOf(totalBillWithTax);
			String taxValue = String.valueOf(tax);

			Table orderItemsTable = dynamoDB.getTable("OrderItems");			
			
			ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("orderuuid").eq(orderuuid))
									.buildForScan();
			ItemCollection<ScanOutcome> scan1 = orderItemsTable.scan(xspec1);

			Consumer<Item> action1 = new Consumer<Item>() {
				@Override
				public void accept(Item t1) {
					Table menuItemTable = dynamoDB.getTable("Menu_Items");
					Object categoryObject = t1.getString("categoryId");
					Object itemObject = t1.getString("menuItemId");
					Item menuItem = menuItemTable.getItem("categoryId", categoryObject, "itemId", itemObject);						

					
					/*String Items = "</td><td class=\"thick-line text-center\">" + menuItem.getString("itemName")							
							+ "</td><td class=\"thick-line text-center\">" + t1.getString("quantity")
							+ "</strong></td><td class=\"thick-line text-center\">$" + t1.getString("itemCost")
							+ "</td></tr>";*/
					if (!t1.getString("quantity").equals("0")){
					BODY.append("<tr style='margin:0;vertical-align:baseline;height:20px;background:#f5f3f0; border-top:1px solid #fff'><td style='padding:15px;'>")
					.append(t1.getString("quantity")).append(" x ").append(menuItem.getString("itemName"))
					/*.append("</td><td class=\"thick-line text-center\">").append(t1.getString("spiceyLevel"))*/
					.append("</td><td style='text-align:right;padding:15px;'>").append(t1.getString("itemCost")).append(".00 USD")
					.append("</td></tr>");		
					}

				}

			};
			scan1.forEach(action1);
			String itemsbody = BODY.toString();
			System.out.println(BODY);		
			

	ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("file/Receipt.html").getFile());		
			try {

				String readFileToString = FileUtils.readFileToString(file);			
				String orderItemReplace = StringUtils.replace(readFileToString, "CustomItem", itemsbody);
				String orderIdReplace = StringUtils.replace(orderItemReplace, "8Q6W5KFC6YMJM", orderuuid);
				String dateoforder = StringUtils.replace(orderIdReplace, "DateOfOrder", orderDate);
				String totalbillamount1 = StringUtils.replace(dateoforder, "TotalBillValue", totalBillValue);
				String totalbillamount2 = StringUtils.replace(totalbillamount1, "TotalBillWithTaxValue",
						totalBillWithTaxValue);
				String taxValue1 = StringUtils.replace(totalbillamount2, "TaxValue", taxValue);				
				String restaurantNameValue = StringUtils.replace(taxValue1, "RestaurantName", restaurantName);
				String RestaurantAddress = StringUtils.replace(restaurantNameValue, "RestaurantAddress", restaurantaddress);
				String restphoneadd = StringUtils.replace(RestaurantAddress, "RestaurantPhone", RestaurantPhone);
				return restphoneadd;
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			System.out.println("Your Cart is Empty.");
			return "Your Cart is Empty.";
		}
	
		return null;
		
	}
	/*public static void main(String[] args) {
		//create a new SNS client and set endpoint
		LexOrderReceipt Sesorderitems = new LexOrderReceipt();	
		Context context = null;
		String orderItem = "f6e82a26-9784-462f-b600-24e8768f924e";

		Sesorderitems.handleRequest(orderItem, context);
		
}*/

}
