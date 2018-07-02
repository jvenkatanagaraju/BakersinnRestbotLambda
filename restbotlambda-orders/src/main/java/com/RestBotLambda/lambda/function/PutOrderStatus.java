package com.RestBotLambda.lambda.function;

import java.util.Map;
import java.util.function.Consumer;

import com.RestBotLambda.DB.DBService;
import com.RestBotLambda.lambda.Response.PutResponse;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class PutOrderStatus  implements RequestHandler<Map<String, Object>, PutResponse> {

	@Override
	public PutResponse handleRequest(Map<String, Object> object, Context context) {
		String orderId = (String) object.get("uuid");
		System.out.println("OrderId:" + orderId);
		String status = (String) object.get("tableStatus");
		System.out.println("Status:" + status);
		// log.log("START:PostDealDao:handleRequest():For Request:" + );
		context.getLogger().log("status: " + status);
		System.out.println("status: " + status);
		try {
			DynamoDB dynamoDB = DBService.getDBConnection();
			Table order = dynamoDB.getTable("Order");
			if ((orderId != null) && (status != null)) {
				ScanFilter scanExpressions = new ScanFilter("uuid");
				scanExpressions.eq(orderId);
				ItemCollection<ScanOutcome> scan = order.scan(scanExpressions);
				Consumer<Item> action = new Consumer<Item>() {

					@Override
					public void accept(Item t) {
						// TODO Auto-generated method stub

						UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", orderId)
								.withUpdateExpression("set orderTracking = :val")
								.withValueMap(new ValueMap().withString(":val", status));
						order.updateItem(updateItemSpec);
						//if (status.equalsIgnoreCase("Dispatched")) {
							//UpdateItemSpec updateItemSpec1 = new UpdateItemSpec().withPrimaryKey("uuid", orderId)
									//.withUpdateExpression("set tableStatus = :val")
									//.withValueMap(new ValueMap().withString(":val", status));
							//order.updateItem(updateItemSpec1);
						//}

					}

				};
				scan.forEach(action);
				return new PutResponse("Successfully Updated", "success");
			} else {
				return new PutResponse("Failed to upload the Status", "Failed");
			}

		} catch (Exception e) {
			// TODO: handle exception
			context.getLogger().log("Exception:" + e.getMessage());
		}

		return new PutResponse("Failed to upload the Status", "Failed");

	}

}


