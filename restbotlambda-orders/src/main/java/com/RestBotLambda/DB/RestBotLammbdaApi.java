package com.RestBotLambda.DB;

import java.util.List;

import com.RestBotLambda.lambda.Response.MenuCategoryResponse;
import com.RestBotLambda.lambda.Response.MenuItemResponse;

public interface RestBotLammbdaApi {

	public List<MenuCategoryResponse> getMenuCategories(String input);

	public List<MenuItemResponse> getMenuItems(String input);

	public MenuItemResponse getMenuItem(String input);

}
