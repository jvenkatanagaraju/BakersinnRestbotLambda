package com.moxieit.orderplatform.lambda.response;

import org.apache.http.Header;

public class ViewCartResponse implements CharSequence {

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
	public int length() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public char charAt(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setHeaders(Header[] array) {
		// TODO Auto-generated method stub

	}

}
