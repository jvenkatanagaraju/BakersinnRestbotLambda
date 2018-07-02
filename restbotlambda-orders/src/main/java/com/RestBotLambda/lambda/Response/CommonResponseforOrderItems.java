package com.RestBotLambda.lambda.Response;

public class CommonResponseforOrderItems {

	private String name;
	private String menuItemId;
	private Number quantity;
	private String spiceyLevel;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMenuItemId() {
		return menuItemId;
	}

	public void setMenuItemId(String menuItemId) {
		this.menuItemId = menuItemId;
	}

	public Number getQuantity() {
		return quantity;
	}

	public void setQuantity(Number quantity) {
		this.quantity = quantity;
	}

	public String getSpiceyLevel() {
		return spiceyLevel;
	}

	public void setSpiceyLevel(String spiceyLevel) {
		this.spiceyLevel = spiceyLevel;
	}

	@Override
	public String toString() {
		return "CommonResponseforOrderItems [name=" + name + ", menuItemId=" + menuItemId + ", quantity=" + quantity
				+ ", spiceyLevel=" + spiceyLevel + "]";
	}

}
