package com.moxieit.orderplatform.S3;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class S3Service {

	

	public static AmazonS3 getService() {
		String accessKey = "AKIAIDKQPYHF6A7JGL6Q";
		String secretKey = "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";
		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(
			accessKey, secretKey);
		AmazonS3 s3Client = new AmazonS3Client(basicAWSCredentials);
		return s3Client;
	}
}
