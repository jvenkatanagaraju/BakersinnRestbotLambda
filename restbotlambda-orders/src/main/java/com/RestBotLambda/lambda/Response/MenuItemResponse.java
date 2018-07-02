package com.RestBotLambda.lambda.Response;

public class MenuItemResponse {

	private String itemName;
	private String image;
	private String itemId;
	private Number price;
	private String itemType;

	public MenuItemResponse() {
		// TODO Auto-generated constructor stub
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Number getPrice() {
		return price;
	}

	public void setPrice(Number price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "MenuItemResponse [itemName=" + itemName + ", image=" + image + ", itemId=" + itemId + ", price=" + price
				+ ", itemType=" + itemType + "]";
	}

}
