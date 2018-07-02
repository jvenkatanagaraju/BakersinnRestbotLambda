package com.moxieit.orderplatform.lambda.function.categories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;

import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.lambda.function.MenuItemsImpl;
import com.moxieit.orderplatform.lambda.request.MainMenuRequest;
import com.moxieit.orderplatform.lambda.request.MenuItemsRequest;

public class MenuItemsTest {

	@BeforeClass
	public static void createInput() throws IOException {
		// TODO: set up your sample input object here.

	}

	private Context createContext() {
		TestContext ctx = new TestContext();

		// TODO: customize your context here if needed.
		ctx.setFunctionName("Your Function Name");

		return ctx;
	}

	public void testGetStarted() {

	}

	public static void main(String[] args) {
		MainMenuRequest mainMenuRequest = new MainMenuRequest();
		List<MenuItemsRequest> menuItemsRequests = new ArrayList<MenuItemsRequest>();
		MenuItemsImpl menuItemsImpl = new MenuItemsImpl();
		MenuItemsRequest menuItemsRequest = new MenuItemsRequest();
		menuItemsRequest.setCategoryId("61_94");
		menuItemsRequest.setItemId("61_94_90");
		menuItemsRequest.setImage("kkkkkkkkkk");
		menuItemsRequest.setItemName("choc");
		menuItemsRequest.setItemType("NonVeg");
		menuItemsRequest.setPrice("10.00");
		menuItemsRequest.setIsSpicy("true");

		menuItemsRequests.add(menuItemsRequest);
		Context context = null;
		mainMenuRequest.setMenuItemsRequest(menuItemsRequests);
		mainMenuRequest.setType("Update");
		menuItemsImpl.handleRequest(mainMenuRequest, context);

	}
}
