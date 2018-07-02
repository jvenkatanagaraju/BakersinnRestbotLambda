package com.moxieit.orderplatform.lambda.request;

import java.util.List;

public class MainMenuRequest {

	List<MenuCategoriesRequest> menuCategoriesRequest;

	List<MenuItemsRequest> menuItemsRequest;
	
	List<TablesRequest> tablesRequest;
	
	List<WaitersRequest> WaitersRequest;

	List<String> categoryIds;
	
	List<String> tableIds;
	
	List<String> waiterIds;

	private String type;

	private String botName;

	private String restaurantId;
	
	private String waiterId;

	private String waiterName;

	private String SecurePin;
	
	public List<String> getWaiterIds() {
		return waiterIds;
	}

	public void setWaiterIds(List<String> waiterIds) {
		this.waiterIds = waiterIds;
	}

	public List<String> getTableIds() {
		return tableIds;
	}

	public void setTableIds(List<String> tableIds) {
		this.tableIds = tableIds;
	}
	
	public List<String> getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(List<String> categoryIds) {
		this.categoryIds = categoryIds;
	}

	public String getBotName() {
		return botName;
	}

	public void setBotName(String botName) {
		this.botName = botName;
	}

	public List<MenuItemsRequest> getMenuItemsRequest() {
		return menuItemsRequest;
	}

	public void setMenuItemsRequest(List<MenuItemsRequest> menuItemsRequest) {
		this.menuItemsRequest = menuItemsRequest;
	}

	public List<MenuCategoriesRequest> getMenuCategoriesRequest() {
		return menuCategoriesRequest;
	}

	public void setMenuCategoriesRequest(List<MenuCategoriesRequest> menuCategoriesRequest) {
		this.menuCategoriesRequest = menuCategoriesRequest;
	}
	
	public List<TablesRequest> getTablesRequest() {
		return tablesRequest;
	}
	
	public void setTablesRequest(List<TablesRequest> tablesRequest) {
		this.tablesRequest = tablesRequest;
	}

	public List<WaitersRequest> getWaitersRequest() {
		return WaitersRequest;
	}
	public void setWaitersRequest(List<WaitersRequest> waitersRequest) {
		this.WaitersRequest = waitersRequest;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

	

	public String getWaiterId() {
		return waiterId;
	}

	public void setWaiterId(String waiterId) {
		this.waiterId = waiterId;
	}

	public String getWaiterName() {
		return waiterName;
	}

	public void setWaiterName(String waiterName) {
		this.waiterName = waiterName;
	}

	public String getSecurePin() {
		return SecurePin;
	}

	public void setSecurePin(String securePin) {
		SecurePin = securePin;
	}

	

	



}
