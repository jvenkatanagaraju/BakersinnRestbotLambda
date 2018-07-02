package com.moxieit.orderplatform.lambda.function.categories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;

import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.lambda.function.LexGetStarted;
import com.moxieit.orderplatform.lambda.function.MenuCategoriesImpl;
import com.moxieit.orderplatform.lambda.function.service.impl.LexOrderGetStartedServiceImpl;
import com.moxieit.orderplatform.lambda.request.MainMenuRequest;
import com.moxieit.orderplatform.lambda.request.MenuCategoriesRequest;

public class LexGetStartedTest {

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
	LexGetStarted lexGetStarted=new LexGetStarted();
	Context context=null;
	lexGetStarted.handleRequest("aa", context);

	}

}
