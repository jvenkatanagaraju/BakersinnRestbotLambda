package com.RestBotLambda.lambda.function;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.RestBotLambda.DB.DBService;
import com.RestBotLambda.DB.RestBotLambdaserviceImpl;
import com.RestBotLambda.lambda.Request.OrdersHistoryRequest;
import com.RestBotLambda.lambda.Response.AllOrderResponse;
import com.RestBotLambda.lambda.Response.CommonResponseforOrderItems;
import com.RestBotLambda.lambda.Response.MenuCategoryResponse;
import com.RestBotLambda.lambda.Response.MenuItemResponse;
import com.RestBotLambda.lambda.Response.OrderResponse;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class OrdersHistoryImpl implements RequestHandler<OrdersHistoryRequest, AllOrderResponse> {

	@Override
	public AllOrderResponse handleRequest(OrdersHistoryRequest orderHistoryRequest, Context context) {
		try {
			AllOrderResponse allOrderResponse = new AllOrderResponse();
			DynamoDB dynamoDB = DBService.getDBConnection();
			Table orderTable = dynamoDB.getTable("Order");
			Table orderItems = dynamoDB.getTable("OrderItems");
			Table restaurantTable=dynamoDB.getTable("Restaurant");
			String botName = orderHistoryRequest.getBotName();
			Item restaurantItem=restaurantTable.getItem("botName",botName);
			String restId=restaurantItem.getString("id");
			//String orderStatues = "ACCEPTED";
			String[] orderStatues1 = { "DISPATCHED","ACCEPTED"} ;
			ScanExpressionSpec xspec = new ExpressionSpecBuilder()
					.withCondition(S("restaurantId").eq(restId).and(S("orderStatus").in(orderStatues1)))
					.buildForScan();

			ItemCollection<ScanOutcome> scan = orderTable.scan(xspec);
			RestBotLambdaserviceImpl restBotLambdaserviceImpl = new RestBotLambdaserviceImpl();
			List<OrderResponse> orderResponse = new ArrayList<>();
			List<MenuCategoryResponse> menuItemResponse = restBotLambdaserviceImpl.getMenuCategories(restaurantItem.getString("id"));
			Map<String, MenuCategoryResponse> mapCategory = new HashMap<>();
			for (MenuCategoryResponse menuCategoryResponse : menuItemResponse) {
				mapCategory.put(menuCategoryResponse.getCategoryId(), menuCategoryResponse);
			}
			Consumer<Item> action = new Consumer<Item>() {
				@Override
				public void accept(Item t) {
					String orderDate = orderHistoryRequest.getDate();
					Date date = null;
					try {
						date = new SimpleDateFormat("dd/MM/yyyy").parse(orderDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String orderItemDate = t.getString("orderDate");
					Date date1 = null;
					try {
						date1 = new SimpleDateFormat("dd/MM/yyyy").parse(orderItemDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int value = date1.compareTo(date);
					if (value == 0) {
						String currentOrderStatus = t.getString("orderStatus");
						String orderuuid = t.getString("uuid");
						String userId=t.getString("userId");
						Number creationDate = t.getNumber("creationDate");
						String x =  creationDate.toString();
						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

						long milliSeconds = Long.parseLong(x);
						System.out.println(milliSeconds);

						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(milliSeconds);
						System.out.println(formatter.format(calendar.getTime()));
						OrderResponse order = new OrderResponse();
						order.setOrderId(orderuuid);
						order.setStatus(currentOrderStatus);
						order.setTotalPrice(t.getNumber("totalBillWithTax"));
						order.setOrderTracking(t.getString("orderTracking"));
						order.setOrderDate(t.getString("orderDate"));
						order.setAddress(t.getString("address"));
						order.setPickUp(t.getString("pickUp"));
						order.setPhoneNumber(t.getString("phoneNumber"));
						order.setOrderFrom(t.getString("orderFrom"));
						order.setPaymentMethod(t.getString("paymentMethod"));
						order.setOrderTime(formatter.format(calendar.getTime()));
						
						ScanExpressionSpec xspec = new ExpressionSpecBuilder()
								.withCondition(S("orderuuid").in(orderuuid).and(S("userId").eq(userId))).buildForScan();
						ItemCollection<ScanOutcome> scan1 = orderItems.scan(xspec);
						Map<String, List<CommonResponseforOrderItems>> foodItems = new HashMap<>();
						order.setFoodItems(foodItems);
						Consumer<Item> action1 = new Consumer<Item>() {
							@Override
							public void accept(Item t) {
								String menuItemId = t.getString("menuItemId");
								try {
									MenuItemResponse menuItemResponse = restBotLambdaserviceImpl.getMenuItem(menuItemId);
									String[] menuItemSplit = menuItemId.split("_");
									MenuCategoryResponse menuCategoryResponse = mapCategory
											.get(menuItemSplit[0] + "_" + menuItemSplit[1]);
									CommonResponseforOrderItems commonResponseforOrderItems = new CommonResponseforOrderItems();
									commonResponseforOrderItems.setMenuItemId(menuItemId);
									commonResponseforOrderItems.setName(menuItemResponse.getItemName());
									commonResponseforOrderItems.setQuantity(t.getNumber("quantity"));
									commonResponseforOrderItems.setSpiceyLevel(t.getString("spiceyLevel"));
									List<CommonResponseforOrderItems> list = foodItems.get(menuCategoryResponse.getName());
									if (list == null) {
										list = new ArrayList<>();
									}
									list.add(commonResponseforOrderItems);
									foodItems.put(menuCategoryResponse.getName(), list);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

						};
						scan1.forEach(action1);
						orderResponse.add(order);
					
					}
				}
			};
			scan.forEach(action);
			// context.getLogger().log(orderHistoryResponses.get(0).getOrderuuid());
			allOrderResponse.setOrders(orderResponse);
			return allOrderResponse;
			
		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
	}
	public static void main(String[] args) {
		OrdersHistoryImpl FbLoginServiveImpl = new OrdersHistoryImpl();
		Context context = null;
		
		
		OrdersHistoryRequest orderHistoryRequest1 = new OrdersHistoryRequest();
		orderHistoryRequest1.setBotName("RBot");
		orderHistoryRequest1.setDate("16/12/2017");
		FbLoginServiveImpl.handleRequest(orderHistoryRequest1, context);
	}

}
