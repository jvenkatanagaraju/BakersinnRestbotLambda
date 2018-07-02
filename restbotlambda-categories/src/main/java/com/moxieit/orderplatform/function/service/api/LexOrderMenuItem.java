package com.moxieit.orderplatform.function.service.api;

public class LexOrderMenuItem extends ILexDTO {

	private String orderUuid;

	private String menuItemId;

	private String menuCategoryId;

	private String restaurantId;

	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrderUuid() {
		return orderUuid;
	}

	public void setOrderUuid(String orderUuid) {
		this.orderUuid = orderUuid;
	}

	public String getMenuItemId() {
		return menuItemId;
	}

	public void setMenuItemId(String menuItemId) {
		this.menuItemId = menuItemId;
	}

	public String getMenuCategoryId() {
		return menuCategoryId;
	}

	public void setMenuCategoryId(String menuCategoryId) {
		this.menuCategoryId = menuCategoryId;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

}
