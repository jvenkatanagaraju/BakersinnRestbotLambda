package com.RestBotLambda.lambda.Response;

public class FoodItems {
	
	BiryaniResponse[] Biryani;

	MainCourseResponse[] MainCourse;

	SideOrdersResponse[] SideOrders;

	DessertsResponse[] Desserts;

	public BiryaniResponse[] getBiryani() {
		return Biryani;
	}

	public void setBiryani(BiryaniResponse[] biryani) {
		Biryani = biryani;
	}

	public MainCourseResponse[] getMainCourse() {
		return MainCourse;
	}

	public void setMainCourse(MainCourseResponse[] mainCourse) {
		MainCourse = mainCourse;
	}

	public SideOrdersResponse[] getSideOrders() {
		return SideOrders;
	}

	public void setSideOrders(SideOrdersResponse[] sideOrders) {
		SideOrders = sideOrders;
	}

	public DessertsResponse[] getDesserts() {
		return Desserts;
	}

	public void setDesserts(DessertsResponse[] desserts) {
		Desserts = desserts;
	}
	
	

}
