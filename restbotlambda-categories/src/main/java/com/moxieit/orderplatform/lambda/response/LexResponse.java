package com.moxieit.orderplatform.lambda.response;

import java.util.List;

import com.amazonaws.services.lexruntime.model.Button;

public class LexResponse {

	DialogActionResponseForElicitIntent dialogAction;
	List<Button> button;
	Button fbbutton;
	SessionAttributes sessionAttributes;
	FbResponse fbResponse;

	public FbResponse getFbResponse() {
		return fbResponse;
	}

	public void setFbResponse(FbResponse fbResponse) {
		this.fbResponse = fbResponse;
	}

	public Button getFbbutton() {
		return fbbutton;
	}

	public void setFbbutton(Button fbbutton) {
		this.fbbutton = fbbutton;
	}

	public List<Button> getButton() {
		return button;
	}

	public void setButton(List<Button> button) {
		this.button = button;
	}

	public SessionAttributes getSessionAttributes() {
		return sessionAttributes;
	}

	public void setSessionAttributes(SessionAttributes sessionAttributes) {
		this.sessionAttributes = sessionAttributes;
	}

	public DialogActionResponseForElicitIntent getDialogAction() {
		return dialogAction;
	}

	public void setDialogAction(DialogActionResponseForElicitIntent dialogAction) {
		this.dialogAction = dialogAction;
	}

	@Override
	public String toString() {
		return "LexResponse [dialogAction=" + dialogAction + ", button=" + button + ", fbbutton=" + fbbutton
				+ ", sessionAttributes=" + sessionAttributes + ", fbResponse=" + fbResponse + "]";
	}

}
