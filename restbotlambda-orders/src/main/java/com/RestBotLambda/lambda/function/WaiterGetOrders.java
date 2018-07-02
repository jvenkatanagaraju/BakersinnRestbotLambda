package com.RestBotLambda.lambda.function;

	import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

	import java.text.DateFormat;
	import java.text.SimpleDateFormat;
	import java.util.ArrayList;
	import java.util.Calendar;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.function.Consumer;

	import com.RestBotLambda.DB.DBService;
	import com.RestBotLambda.DB.RestBotLambdaserviceImpl;
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


	public class WaiterGetOrders implements RequestHandler<String, AllOrderResponse> {

		public AllOrderResponse handleRequest(String waiterId, Context context) {
			List<OrderResponse> orderResponse = new ArrayList<>();

			AllOrderResponse allOrderResponse = new AllOrderResponse();
			DynamoDB dynamoDB = DBService.getDBConnection();
			Table order = dynamoDB.getTable("Order");
			Table orderItems = dynamoDB.getTable("OrderItems");
			//Table restaurantTable = dynamoDB.getTable("Restaurant");
			Item restaurantItem = order.getItem("waiterId", waiterId);
			//Item restaurantItem = restaurantTable.getItem("botName", botName);
			//allOrderResponse.setRestaurantId(restaurantItem.getString("id"));
			//String botName = orderHistoryRequest.getBotName();
			//Item restaurantItem = order.getItem("waiterId", waiterId);
			//Item restaurantItem1 = order.getItem("waiterId", waiterId);
			allOrderResponse.setRestaurantId(restaurantItem.getString("id"));
			allOrderResponse.setRestaurantName(restaurantItem.getString("restaurantName"));
			allOrderResponse.setWaiterId("waiterId");
			
			allOrderResponse.setAddress(restaurantItem.getString("city")+","+restaurantItem.getString("state"));
			List<String> orderStatues = new ArrayList<>();
			for (OrderStatus orderStatus : OrderStatus.values()) {
				if (orderStatus.getShowAdmin()) {
					orderStatues.add(orderStatus.toString());
					System.out.println("ordersttus is"+orderStatues);
				}
			}
			ScanExpressionSpec xspec1 = new ExpressionSpecBuilder()
					.withCondition(
							S("restaurantId").in(restaurantItem.getString("id")).and(S("orderTracking").in(orderStatues)).and(S("orderStatus").in("ACCEPTED")))
					.buildForScan();
			ItemCollection<ScanOutcome> scan1 = order.scan(xspec1);
			Consumer<Item> action2 = new Consumer<Item>() {
				@Override
				public void accept(Item t) {
					
					RestBotLambdaserviceImpl restBotLambdaserviceImpl = new RestBotLambdaserviceImpl();
					List<MenuCategoryResponse> menuItemResponse = restBotLambdaserviceImpl
							.getMenuCategories(restaurantItem.getString("id"));
					Map<String, MenuCategoryResponse> mapCategory = new HashMap<>();
					for (MenuCategoryResponse menuCategoryResponse : menuItemResponse) {
						mapCategory.put(menuCategoryResponse.getCategoryId(), menuCategoryResponse);
					}

					String currentOrderStatus = t.getString("orderStatus");
					String orderuuid = t.getString("uuid");
					String userId = t.getString("userId");
					Number creationDate = t.getNumber("creationDate");
					String x = creationDate.toString();
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

					long milliSeconds = Long.parseLong(x);
					

					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(milliSeconds);
					System.out.println(formatter.format(calendar.getTime()));
					OrderResponse order = new OrderResponse();
					order.setOrderId(orderuuid);
					order.setStatus(currentOrderStatus);
					order.setOrderTime(formatter.format(calendar.getTime()));
					order.setTotalPrice(t.getNumber("totalBillWithTax"));
					order.setOrderTracking(t.getString("orderTracking"));
					order.setOrderDate(t.getString("orderDate"));
					//order.setAddress(t.getString("address"));
					order.setPaymentMethod(t.getString("paymentMethod"));
					order.setPhoneNumber(t.getString("phoneNumber"));
					//order.setPickUp(t.getString("pickUp"));
					order.setOrderFrom(t.getString("orderFrom"));
					ScanExpressionSpec xspec = new ExpressionSpecBuilder()
							.withCondition(S("orderuuid").in(orderuuid).and(S("userId").eq(userId))).buildForScan();
					ItemCollection<ScanOutcome> scan1 = orderItems.scan(xspec);
					Map<String, List<CommonResponseforOrderItems>> foodItems = new HashMap<>();
					Consumer<Item> action1 = new Consumer<Item>() {
						@Override
						public void accept(Item t) {
							String menuItemId = t.getString("menuItemId");
							try {
								MenuItemResponse menuItemResponse = restBotLambdaserviceImpl
										.getMenuItem(menuItemId);
								System.out.println("menu item"+menuItemResponse);
								String[] menuItemSplit = menuItemId.split("_");
								MenuCategoryResponse menuCategoryResponse = mapCategory
										.get(menuItemSplit[0] + "_" + menuItemSplit[1]);
								CommonResponseforOrderItems commonResponseforOrderItems = new CommonResponseforOrderItems();
								commonResponseforOrderItems.setMenuItemId(menuItemId);
								commonResponseforOrderItems.setName(menuItemResponse.getItemName());
								commonResponseforOrderItems.setQuantity(t.getNumber("quantity"));
								//commonResponseforOrderItems.setSpiceyLevel(t.getString("spiceyLevel"));
								List<CommonResponseforOrderItems> list = foodItems
										.get(menuCategoryResponse.getName());
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
					order.setFoodItems(foodItems);
					orderResponse.add(order);
				}
			};
			scan1.forEach(action2);
			allOrderResponse.setOrders(orderResponse);
			return allOrderResponse;
		}
		public static void main(String[] args) {
			WaiterGetOrders waiterGetOrders = new WaiterGetOrders();
			Context context = null;
			String orderItem = "23_1";
			
			waiterGetOrders.handleRequest(orderItem, context);
		
	}}


