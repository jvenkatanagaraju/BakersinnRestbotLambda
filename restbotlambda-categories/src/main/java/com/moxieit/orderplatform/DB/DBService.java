package com.moxieit.orderplatform.DB;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class DBService {

	private String regionName;

	public String getRegion() {
		return regionName;
	}

	public void setRegion(String regionName) {
		this.regionName = regionName;
	}

	public DBService() {
	}

	public static DynamoDB getDBConnection() {
		AmazonDynamoDBClient amazonDynamoDBClient = getDynamoDBClient();
		DynamoDB dynamoDB = new DynamoDB(amazonDynamoDBClient);
		return dynamoDB;
	}

	public static AmazonDynamoDBClient getDynamoDBClient() {
		BasicAWSCredentials basicAWSCredentials = getAWSCredentials();
		AmazonDynamoDBClient amazonDynamoDBClient = new AmazonDynamoDBClient();
		return amazonDynamoDBClient;
	}

	private static BasicAWSCredentials getAWSCredentials() {
		String accessKey = "AKIAI3SQEJTGL5AKUPRQ";
		String secretKey = "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";
		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
		return basicAWSCredentials;
	}

}
