package com.moxieit.orderplatform.lambda.function.categories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;

import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.lambda.function.MenuCategoriesImpl;
import com.moxieit.orderplatform.lambda.request.MainMenuRequest;
import com.moxieit.orderplatform.lambda.request.MenuCategoriesRequest;

public class MenuCategoriesTest {

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
       MainMenuRequest mainMenuCategoryRequest=new MainMenuRequest();
		MenuCategoriesImpl menuCategoriesImpl=new MenuCategoriesImpl();
		List<MenuCategoriesRequest> menuCategoriesRequest1=new ArrayList<MenuCategoriesRequest>();
		MenuCategoriesRequest menuCategoriesRequest=new MenuCategoriesRequest();
	     menuCategoriesRequest.setCategoryName("bhindiiiii");
		//menuCategoriesRequest.setImage("1234");
		menuCategoriesRequest.setCategoryId("24");
		//menuCategoriesRequest.setRestaurantId("2");
		MenuCategoriesRequest menuCategoriesRequest2=new MenuCategoriesRequest();
		menuCategoriesRequest2.setCategoryName("malai");
		//menuCategoriesRequest2.setImage("1234");
		menuCategoriesRequest2.setCategoryId("39");
		//menuCategoriesRequest2.setRestaurantId("2");
		menuCategoriesRequest1.add(menuCategoriesRequest);
		menuCategoriesRequest1.add(menuCategoriesRequest2);
		
		mainMenuCategoryRequest.setBotName("Test");
		mainMenuCategoryRequest.setType("Delete");
	mainMenuCategoryRequest.setMenuCategoriesRequest(menuCategoriesRequest1);
		Context context=null;
		menuCategoriesImpl.handleRequest(mainMenuCategoryRequest, context);
	
	}
}
