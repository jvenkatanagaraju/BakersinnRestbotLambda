package com.moxieit.orderplatform.lambda.request;

public class LexInput {

	private String userId;
	private String inputTranscript;
	private String invocationSource;
	private String outputDialogMode;
	private String messageVersion;
	SessionAttributes sessionAttributes;
	Bot bot;
	CurrentIntent currentIntent;

	public LexInput(String userId, String inputTranscript, String invocationSource, String outputDialogMode,
			String messageVersion, SessionAttributes sessionAttributes, Bot bot, CurrentIntent currentIntent) {
		super();
		this.userId = userId;
		this.inputTranscript = inputTranscript;
		this.invocationSource = invocationSource;
		this.outputDialogMode = outputDialogMode;
		this.messageVersion = messageVersion;
		this.sessionAttributes = sessionAttributes;
		this.bot = bot;
		this.currentIntent = currentIntent;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInputTranscript() {
		return inputTranscript;
	}

	public void setInputTranscript(String inputTranscript) {
		this.inputTranscript = inputTranscript;
	}

	public String getInvocationSource() {
		return invocationSource;
	}

	public void setInvocationSource(String invocationSource) {
		this.invocationSource = invocationSource;
	}

	public String getOutputDialogMode() {
		return outputDialogMode;
	}

	public void setOutputDialogMode(String outputDialogMode) {
		this.outputDialogMode = outputDialogMode;
	}

	public String getMessageVersion() {
		return messageVersion;
	}

	public void setMessageVersion(String messageVersion) {
		this.messageVersion = messageVersion;
	}

	public SessionAttributes getSessionAttributes() {
		return sessionAttributes;
	}

	public void setSessionAttributes(SessionAttributes sessionAttributes) {
		this.sessionAttributes = sessionAttributes;
	}

	public Bot getBot() {
		return bot;
	}

	public void setBot(Bot bot) {
		this.bot = bot;
	}

	public CurrentIntent getCurrentIntent() {
		return currentIntent;
	}

	public void setCurrentIntent(CurrentIntent currentIntent) {
		this.currentIntent = currentIntent;
	}

}
