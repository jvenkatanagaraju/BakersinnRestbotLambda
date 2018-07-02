package com.moxieit.orderplatform.lambda.function.categories;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxieit.orderplatform.lambda.function.LexOrchestration;
import com.moxieit.orderplatform.lambda.request.Bot;
import com.moxieit.orderplatform.lambda.response.LexResponse;

public class TestLexOrchestration {

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

		/*
		 * LexOrchestration lexOrchestration = new LexOrchestration(); Context
		 * ctx = createContext(); Map<String, Object> lexRequest = new
		 * HashMap<>(); lexRequest.put("uuid",
		 * "257acb3ceb9444959195558ee2f85ba5");
		 * lexRequest.put("inputTranscript", "StartOver"); Context context=new
		 * TestContext(); LexResponse
		 * handleRequest=lexOrchestration.handleRequest(lexRequest, context);
		 * System.out.println(handleRequest);
		 */
		/*
		 * LexOrchestration lexOrchestration = new LexOrchestration(); Context
		 * ctx = createContext(); Map<String, Object> lexRequest = new
		 * HashMap<>(); lexRequest.put("userId", "1273627579421107");
		 * lexRequest.put("inputTranscript", "ViewCart"); Context context=new
		 * TestContext(); LexResponse
		 * handleRequest=lexOrchestration.handleRequest(lexRequest, context);
		 * System.out.println(handleRequest);
		 */
		/*
		 * Context context = new TestContext(); LexResponse handleRequest =
		 * lexOrchestration.handleRequest(lexRequest, context);
		 * System.out.println(handleRequest); GenericAttachments[] button =
		 * handleRequest.getDialogAction().getResponseCard().
		 * getGenericAttachments(); lexRequest.put("inputTranscript",
		 * button[0].getButtons().get(0).getValue()); handleRequest =
		 * lexOrchestration.handleRequest(lexRequest, context);
		 * System.out.println(handleRequest); GenericAttachments[] button1=
		 * handleRequest.getDialogAction().getResponseCard().
		 * getGenericAttachments(); lexRequest.put("inputTranscript",
		 * button1[0].getButtons().get(0).getValue()); lexRequest.put("userId",
		 * "1234"); lexRequest.put("status","Intiated");
		 * System.out.println(lexRequest.get("inputTranscript")); handleRequest
		 * = lexOrchestration.handleRequest(lexRequest, context);
		 * System.out.println(handleRequest); GenericAttachments[] button2=
		 * handleRequest.getDialogAction().getResponseCard().
		 * getGenericAttachments(); lexRequest.put("inputTranscript",
		 * button2[0].getButtons().get(0).getValue());
		 * System.out.println(lexRequest.get("inputTranscript")); handleRequest
		 * = lexOrchestration.handleRequest(lexRequest, context);
		 * System.out.println(handleRequest);
		 */
		LexOrchestration lexOrchestration = new LexOrchestration();
		Bot bot = new Bot();
		bot.setName("sitaraashburn");
		ObjectMapper oMapper = new ObjectMapper();
		Map<String, Object> map1 = oMapper.convertValue(bot, Map.class);
		Map<String, Object> lexRequest = new HashMap<>();
		lexRequest.put("inputTranscript", "menuItemQuantity_f997e1a0-e527-4535-b5ea-071cefd0d87c_2");
		lexRequest.put("userId", "1319976254795203");
		lexRequest.put("bot", map1);
		System.out.println(lexRequest);
		Context context=null;
		LexResponse lexResponse = lexOrchestration.handleRequest(lexRequest, context);

	}

	public static void main(String[] args) {
		TestLexOrchestration testLexOrchestration = new TestLexOrchestration();
		testLexOrchestration.testGetStarted();

	}
}
