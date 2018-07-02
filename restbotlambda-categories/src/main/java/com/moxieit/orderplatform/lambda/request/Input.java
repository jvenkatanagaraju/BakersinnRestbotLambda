package com.moxieit.orderplatform.lambda.request;

public class Input {

	private String messageVersion;
	private String invocationSource;
	private String userId;
	private String sessionAttributes;
	Bot bot;
	private String outputDialogMode;
	CurrentIntent currentIntent;
	private String inputTranscript;

	public String getMessageVersion() {
		return messageVersion;
	}

	public void setMessageVersion(String messageVersion) {
		this.messageVersion = messageVersion;
	}

	public String getInvocationSource() {
		return invocationSource;
	}

	public void setInvocationSource(String invocationSource) {
		this.invocationSource = invocationSource;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSessionAttributes() {
		return sessionAttributes;
	}

	public void setSessionAttributes(String sessionAttributes) {
		this.sessionAttributes = sessionAttributes;
	}

	public Bot getBot() {
		return bot;
	}

	public void setBot(Bot bot) {
		this.bot = bot;
	}

	public String getOutputDialogMode() {
		return outputDialogMode;
	}

	public void setOutputDialogMode(String outputDialogMode) {
		this.outputDialogMode = outputDialogMode;
	}

	public CurrentIntent getCurrentIntent() {
		return currentIntent;
	}

	public void setCurrentIntent(CurrentIntent currentIntent) {
		this.currentIntent = currentIntent;
	}

	public String getInputTranscript() {
		return inputTranscript;
	}

	public void setInputTranscript(String inputTranscript) {
		this.inputTranscript = inputTranscript;
	}

}
