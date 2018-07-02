package com.moxieit.orderplatform.lambda.response;

import java.util.List;
import java.util.Map;

public class WebsiteMenuCategoryWithItemsResponse {
	private String monToFri;
	private String satToSun;
	
	private String country;
	List<Map<String, List<MenuItemResponse>>> menuCategoryItems;

	public List<Map<String, List<MenuItemResponse>>> getMenuCategoryItems() {
		return menuCategoryItems;
	}

	public void setMenuCategoryItems(List<Map<String, List<MenuItemResponse>>> menuCategoryItems) {
		this.menuCategoryItems = menuCategoryItems;
	}

	public String getSatToSun() {
		return satToSun;
	}

	public void setSatToSun(String satToSun) {
		this.satToSun = satToSun;
	}

	public String getMonToFri() {
		return monToFri;
	}

	public void setMonToFri(String monToFri) {
		this.monToFri = monToFri;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	

}
