package com.moxieit.orderplatform.lambda.response;

public class TablesResponce {
	
	private String restaurantId;
	
	private String tableId;
	
	private String tableName;
	
	private String seats;
	private String minSeats;
	
	private String tableStatus;
	private String message;
	private String bookedDate;
	private String bookedTime;
	private String bookedSeats;	
	
	public TablesResponce() {
		// TODO Auto-generated constructor stub
	}

	public TablesResponce(String restaurantId, String tableId, String tableName,String seats,String minSeats,
			String tableStatus,String bookedDate,String bookedTime ) {
		this.restaurantId = restaurantId;
		this.tableId = tableId;
		this.tableName = tableName;
		this.seats = seats;
		this.minSeats = minSeats;
		this.tableStatus=tableStatus;
		this.bookedDate = bookedDate;
		this.bookedDate=bookedTime;
		
	}

	public TablesResponce(String message, String tableStatus) {
		super();
		this.setMessage(message);
		this.setTableStatus(tableStatus);
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getSeats() {
		return seats;
	}

	public void setSeats(String seats) {
		this.seats = seats;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTableStatus() {
		return tableStatus;
	}

	public void setTableStatus(String tableStatus) {
		this.tableStatus = tableStatus;
	}

	public String getBookedDate() {
		return bookedDate;
	}

	public void setBookedDate(String bookedDate) {
		this.bookedDate = bookedDate;
	}

	public String getBookedTime() {
		return bookedTime;
	}

	public void setBookedTime(String bookedTime) {
		this.bookedTime = bookedTime;
	}

	public String getBookedSeats() {
		return bookedSeats;
	}

	public void setBookedSeats(String bookedSeats) {
		this.bookedSeats = bookedSeats;
	}

	public String getMinSeats() {
		return minSeats;
	}

	public void setMinSeats(String minSeats) {
		this.minSeats = minSeats;
	}

}
