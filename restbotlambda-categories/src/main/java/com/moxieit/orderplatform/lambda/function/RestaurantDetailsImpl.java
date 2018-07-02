package com.moxieit.orderplatform.lambda.function;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.util.function.Consumer;

import org.apache.commons.lang3.mutable.MutableInt;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.lambda.function.service.impl.IdService;
import com.moxieit.orderplatform.lambda.request.RestaurantRequestImpl;
import com.moxieit.orderplatform.lambda.response.RestaurantResponse;

public class RestaurantDetailsImpl implements RequestHandler<RestaurantRequestImpl, RestaurantResponse> {
	

	@Override
	public RestaurantResponse handleRequest(RestaurantRequestImpl restaurantRequestImpl, Context context) {

		try {
			//final int condition = 0;
			final MutableInt condition = new MutableInt(0);
			DynamoDB dynamoDB = DBService.getDBConnection();
			String type = restaurantRequestImpl.getType();
			Table restaurantTable = dynamoDB.getTable("Restaurant");
			String restaurantId = String.valueOf(IdService.getID());			
			if (type != null) {
				switch (type) {
				case "Insert": {
					if (restaurantRequestImpl != null) {
						System.out.println(restaurantRequestImpl.getPhone_no());
						System.out.println(restaurantRequestImpl.getEmailId());
						if (restaurantRequestImpl.getPhone_no() != null & restaurantRequestImpl.getEmailId() != null) {
							ScanExpressionSpec xspec = new ExpressionSpecBuilder()
									.withCondition(S("phone_no").eq(restaurantRequestImpl.getPhone_no())
											.or(S("emailId").eq(restaurantRequestImpl.getEmailId())))
									.buildForScan();
							ItemCollection<ScanOutcome> scan = restaurantTable.scan(xspec);
							Consumer<Item> action = new Consumer<Item>() {
								@Override
								public void accept(Item t) {
									//condition = 1;
									condition.add(1);									
								
								}

							};
							scan.forEach(action);

						}
						if (condition.intValue() == 0) {						
							Item restaurantItem = restaurantTable.getItem("botName",
									restaurantRequestImpl.getBotName());
							if (restaurantItem == null) {
								Item item = new Item();
								item.withString("id", restaurantId)
										.withString("monToFri", restaurantRequestImpl.getMonToFriHours())
										.withString("zipCode", restaurantRequestImpl.getZipCode())
										.withString("satToSun", restaurantRequestImpl.getSatToSunHours())
										.withString("image", restaurantRequestImpl.getImage())
										.withString("street_1", restaurantRequestImpl.getStreet_1())
										.withString("city", restaurantRequestImpl.getCity())	
										.withString("state", restaurantRequestImpl.getState())
										.withString("country", restaurantRequestImpl.getCountry())
										.withString("phone_no", restaurantRequestImpl.getPhone_no())
										.withString("emailId", restaurantRequestImpl.getEmailId())
										.withString("restaurantName", restaurantRequestImpl.getRestaurantName())
										.withString("botName", restaurantRequestImpl.getBotName())
										.withString("securePin", restaurantRequestImpl.getSecurePin())
										.withString("deviceId", restaurantRequestImpl.getDeviceId())
										.withString("pageName", restaurantRequestImpl.getPageName());
								System.out.println("item:"+item);
								restaurantTable.putItem(item);
								return new RestaurantResponse(restaurantId,
										"Successfully Registered your Restaurant Details", "success");
							}
						}
						return new RestaurantResponse(null, "Details are already saved with these Phone_no,EmailId or BotName",
								"failed");
					}

				}

				case "Update": {
					String botName = restaurantRequestImpl.getBotName();
					Item restaurantItem = restaurantTable.getItem("botName", botName);
					String phone_no = null;
					String restaurantName = null;
					String emailId = null;
					String securePin = null;
					String monToFri = null;
					String satToSun = null;
					if (restaurantItem != null)
					{
						if (restaurantItem.getString("securePin") != null) {
							securePin = restaurantRequestImpl.getSecurePin();
						} else {
							securePin = restaurantItem.getString("securePin");
						}
						if (restaurantItem.getString("phone_no") != null) {
							phone_no = restaurantRequestImpl.getPhone_no();
						} else {
							phone_no = restaurantItem.getString("phone_no");
						}
						if (restaurantItem.getString("restaurantName") != null) {
							restaurantName = restaurantRequestImpl.getRestaurantName();
						} else {
							restaurantName = restaurantItem.getString("restaurantName");
						}
						if (restaurantItem.getString("emailId") != null) {
							emailId = restaurantRequestImpl.getEmailId();
						} else {
							emailId = restaurantItem.getString("emailId");
						}
						if (restaurantItem.getString("monToFri") != null) {
							monToFri = restaurantRequestImpl.getMonToFriHours();
						} else {
							monToFri = restaurantItem.getString("monToFri");
						}
						if (restaurantItem.getString("satToSun") != null) {
							satToSun = restaurantRequestImpl.getSatToSunHours();
						} else {
							satToSun = restaurantItem.getString("satToSun");
						}
						UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("botName", botName)
								.withUpdateExpression("set phone_no = :ph_no,restaurantName = :rest,emailId = :em,securePin = :pin,monToFri = :mf,satToSun = :ss")
								.withValueMap(new ValueMap().withString(":ph_no", phone_no)
										.withString(":rest", restaurantName).withString(":em", emailId).withString(":pin", securePin).withString(":mf", monToFri).withString(":ss", satToSun));
						restaurantTable.updateItem(updateItemSpec);
						return new RestaurantResponse(restaurantItem.getString("id"),
								"Successfully Updated your Restaurant details", "success");
					}
			 	}
				case "Delete": {
					String botName = restaurantRequestImpl.getBotName();
					Item restaurantItem = restaurantTable.getItem("botName", botName);
					if (restaurantItem != null) {
						DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
								.withPrimaryKey(new PrimaryKey("botName", botName));
						restaurantTable.deleteItem(deleteItemSpec);
						return new RestaurantResponse(restaurantItem.getString("id"),
								"Successfully Deleted your Restaurant details", "success");
					}
				}

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			context.getLogger().log("Exception is :" + e);
		}

		return new RestaurantResponse(null, "Unable to Save the Details", "failed");

	}

}
