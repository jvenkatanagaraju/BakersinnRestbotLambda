package com.moxieit.orderplatform.function.service.api;

public class LexOrderCategory extends ILexDTO {

	private String orderuuid;

	private String categoryId;

	private String condition;

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getOrderuuid() {
		return orderuuid;
	}

	public void setOrderuuid(String orderuuid) {
		this.orderuuid = orderuuid;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	public String toString() {
		return "LexOrderCategory [orderuuid=" + orderuuid + ", categoryId=" + categoryId + ", getUserId()="
				+ getUserId() + "]";
	}

}
