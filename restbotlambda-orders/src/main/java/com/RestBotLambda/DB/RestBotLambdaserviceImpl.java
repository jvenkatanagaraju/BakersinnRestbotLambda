package com.RestBotLambda.DB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.StringUtils;

import com.RestBotLambda.lambda.Response.MenuCategoryResponse;
import com.RestBotLambda.lambda.Response.MenuItemResponse;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

public class RestBotLambdaserviceImpl implements RestBotLammbdaApi {

	@Override
	public List<MenuCategoryResponse> getMenuCategories(String input) {
		// TODO Auto-generated method stub
		List<MenuCategoryResponse> menuCategoryResponses = new ArrayList<MenuCategoryResponse>();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table table = dynamoDB.getTable("Menu_Categories");
		HashMap<String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put(":v_id", input);
		valueMap.put(":letter1", input + "_");
		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("restaurantId = :v_id and begins_with(categoryId,:letter1)")
				.withValueMap(valueMap);

		ItemCollection<QueryOutcome> items = table.query(spec);

		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			// System.out.println(iterator.next().toJSONPretty());
			Item item = iterator.next();
			MenuCategoryResponse menuCategoryResponse = new MenuCategoryResponse();
			menuCategoryResponse.setCategoryId(item.getString("categoryId"));
			menuCategoryResponse.setName(item.getString("categoryName"));
			menuCategoryResponse.setImage(item.getString("image"));
			menuCategoryResponses.add(menuCategoryResponse);
			System.out.println(menuCategoryResponse.getImage());
		}
		System.out.println(menuCategoryResponses.toString());
		return menuCategoryResponses;

	}

	@Override
	public List<MenuItemResponse> getMenuItems(String input) {
		// TODO Auto-generated method stub
		List<MenuItemResponse> menuItemResponses = new ArrayList<MenuItemResponse>();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table table = dynamoDB.getTable("Menu_Items");
		HashMap<String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put(":v_id", input);
		valueMap.put(":letter1", input + "_");
		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("categoryId = :v_id and begins_with(itemId,:letter1)")
				.withValueMap(valueMap);

		ItemCollection<QueryOutcome> items = table.query(spec);

		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			Item item = iterator.next();
			MenuItemResponse menuItemResponse = new MenuItemResponse();
			menuItemResponse.setItemId(item.getString("itemId"));
			menuItemResponse.setItemName(item.getString("itemName"));
			menuItemResponse.setImage(item.getString("image"));
			menuItemResponse.setPrice(item.getNumber("price"));
			menuItemResponse.setItemType(item.getString("itemType"));
			menuItemResponses.add(menuItemResponse);
			System.out.println(menuItemResponse.getImage());
		}
		System.out.println(menuItemResponses.toString());
		return menuItemResponses;

	}

	@Override
	public MenuItemResponse getMenuItem(String input) {
		// TODO Auto-generated method stub
		List<MenuItemResponse> menuItemResponses = new ArrayList<MenuItemResponse>();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table table = dynamoDB.getTable("Menu_Items");
		HashMap<String, Object> valueMap = new HashMap<String, Object>();
		String split[] = StringUtils.split(input, "_");
		String id = split[0] + "_";
		String id1 = split[1];
		String split1[] = StringUtils.split(id1, "_");
		String id2 = id + split1[0];
		valueMap.put(":v_id", id2);
		valueMap.put(":letter1", input);
		QuerySpec spec = new QuerySpec().withKeyConditionExpression("categoryId = :v_id and itemId = :letter1")
				.withValueMap(valueMap);

		ItemCollection<QueryOutcome> items = table.query(spec);

		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			Item item = iterator.next();
			MenuItemResponse menuItemResponse = new MenuItemResponse();
			menuItemResponse.setItemId(item.getString("itemId"));
			menuItemResponse.setItemName(item.getString("itemName"));
			menuItemResponse.setImage(item.getString("image"));
			menuItemResponse.setPrice(item.getNumber("price"));
			menuItemResponse.setItemType(item.getString("itemType"));
			// menuItemResponses.add(menuItemResponse);
			System.out.println(menuItemResponse.getImage());
			return menuItemResponse;
		}
		System.out.println(menuItemResponses.toString());
		return null;

	}
}
