package com.moxieit.orderplatform.lambda.request;

public class Bot {

	private String name;
	private String alias;
	private String version;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Bot() {
		// TODO Auto-generated constructor stub
	}

	public Bot(String name, String alias, String version) {
		super();
		this.name = name;
		this.alias = alias;
		this.version = version;
	}

}
