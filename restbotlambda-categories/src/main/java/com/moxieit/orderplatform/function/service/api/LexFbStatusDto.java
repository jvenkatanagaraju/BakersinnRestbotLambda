package com.moxieit.orderplatform.function.service.api;

import com.moxieit.orderplatform.lambda.response.Message;
import com.moxieit.orderplatform.lambda.response.Recipient;

public class LexFbStatusDto {

	Recipient recipient;

	Message message;

	public Recipient getRecipient() {
		return recipient;
	}

	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "LexFbStatusDto [recipient=" + recipient + ", message=" + message + "]";
	}

}
