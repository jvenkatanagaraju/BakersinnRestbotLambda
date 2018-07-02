package com.RestBotLambda.lambda.Response;

public class MenuCategoryResponse {

	private String categoryId;
	private String name;
	private String image;

	public MenuCategoryResponse() {
		// TODO Auto-generated constructor stub
	}

	public MenuCategoryResponse(String categoryId, String name, String image) {
		super();
		this.categoryId = categoryId;
		this.name = name;
		this.image = image;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
