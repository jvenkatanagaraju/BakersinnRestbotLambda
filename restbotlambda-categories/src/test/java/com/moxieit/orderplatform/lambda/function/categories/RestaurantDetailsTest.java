package com.moxieit.orderplatform.lambda.function.categories;

import java.io.IOException;

import org.junit.BeforeClass;

import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.lambda.function.RestaurantDetailsImpl;
import com.moxieit.orderplatform.lambda.request.RestaurantRequestImpl;

public class RestaurantDetailsTest {

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

		RestaurantDetailsImpl restaurantDetailsImpl = new RestaurantDetailsImpl();
		RestaurantRequestImpl restaurantRequestImpl = new RestaurantRequestImpl();
		// restaurantRequestImpl.setId("31");
		restaurantRequestImpl.setImage("kkk");
		restaurantRequestImpl.setCity("ggg");
		restaurantRequestImpl.setCountry("afsad");
		restaurantRequestImpl.setEmailId("kkommin122243@gmail.com");
		restaurantRequestImpl.setMonToFriHours("sfadsad");
		restaurantRequestImpl.setSatToSunHours("asfdsafd");
		restaurantRequestImpl.setZipCode("fsadfsda");
		restaurantRequestImpl.setPageName("fdsa");
		restaurantRequestImpl.setPhone_no("9098754475");
		restaurantRequestImpl.setState("asfakjjkjkf");
		restaurantRequestImpl.setStreet_1("asfda");
		restaurantRequestImpl.setRestaurantName("Paradise");
		restaurantRequestImpl.setType("Insert");
		restaurantRequestImpl.setBotName("SitaraBottt");
		Context context = null;
		restaurantDetailsImpl.handleRequest(restaurantRequestImpl, context);
	}
}
