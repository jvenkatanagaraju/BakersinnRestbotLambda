
	package com.moxieit.orderplatform.lambda.function;

	import java.io.ByteArrayInputStream;
	import java.io.InputStream;
	import java.text.SimpleDateFormat;
	import java.util.ArrayList;
	import java.util.Date;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;

	import org.apache.commons.codec.binary.Base64;

	import com.amazonaws.services.lambda.runtime.Context;
	import com.amazonaws.services.s3.AmazonS3;
	import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
	import com.amazonaws.services.s3.model.CannedAccessControlList;
	import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
	import com.amazonaws.services.s3.model.ObjectMetadata;
	import com.amazonaws.services.s3.model.PartETag;
	import com.amazonaws.services.s3.model.UploadPartRequest;
	import com.moxieit.orderplatform.S3.S3Service;

	public class UploadResume
			implements com.amazonaws.services.lambda.runtime.RequestHandler<Map<String, Object>, Map<String, Object>> {

		private static String BUCKET_NAME = "jobportalresumes";

		public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {

			AmazonS3 s3Client = S3Service.getService();
			final Map<String, Object> output = new HashMap<String, Object>();
			context.getLogger().log(String.format("Upload S3 object"));

			// Create a list of UploadPartResponse objects. You get one of these for
			// each part upload.
			List<PartETag> partETags = new ArrayList<PartETag>();
			String dateString = getDateTimeForFileName(context);
			// check if the key is a device id or phoneNumber

			final String key = "resume";

			// Step 1: Initialize.
			String fileNameString = key + "/" + dateString;

			// Converting a Base64 String into Image byte array
			byte[] fileBytes = decoderesume((String) input.get("file"));
			InputStream in = new ByteArrayInputStream(fileBytes);
			// content lengh to determine the multi part upload
			Long contentLength = Long.valueOf((fileBytes).length);
			// object metadata
			final ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType("application/msword");
			objectMetadata.setContentLength(contentLength);
			InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(BUCKET_NAME, fileNameString)
					.withObjectMetadata(objectMetadata);
			com.amazonaws.services.s3.model.InitiateMultipartUploadResult initResponse = s3Client
					.initiateMultipartUpload(initRequest);

			long partSize = 10 * 1024 * 1024; // Set part size to 5 MB.

			try {
				// Step 2: Upload parts.
				long filePosition = 0;
				for (int i = 1; filePosition < contentLength; i++) {
					// Last part can be less than 5 MB. Adjust part size.
					partSize = Math.min(partSize, (contentLength - filePosition));
					// Create request to upload a part.
					UploadPartRequest uploadRequest = new UploadPartRequest().withObjectMetadata(objectMetadata)
							.withBucketName(BUCKET_NAME).withKey(fileNameString).withUploadId(initResponse.getUploadId())
							.withPartNumber(i).withFileOffset(filePosition).withInputStream(in).withPartSize(partSize);
					// Upload part and add response to our list.
					partETags.add(s3Client.uploadPart(uploadRequest).getPartETag());
					// append the size
					filePosition += partSize;
				}
				// Step 3: Complete.
				com.amazonaws.services.s3.model.CompleteMultipartUploadRequest compRequest = new com.amazonaws.services.s3.model.CompleteMultipartUploadRequest(
						BUCKET_NAME, fileNameString, initResponse.getUploadId(), partETags);

				s3Client.completeMultipartUpload(compRequest);
				context.getLogger().log("uploaded file URL to " + s3Client.getUrl(BUCKET_NAME, fileNameString));
				// prepare response

				s3Client.setObjectAcl(BUCKET_NAME, fileNameString, CannedAccessControlList.PublicRead);
				output.put("fileUrl", s3Client.getUrl(BUCKET_NAME, fileNameString));
			} catch (Exception e) {
				context.getLogger().log("image uplaod failed with exception:" + e);
				s3Client.abortMultipartUpload(
						new AbortMultipartUploadRequest(BUCKET_NAME, fileNameString, initResponse.getUploadId()));
			}
			return output;
		}

		/**
		 * Decodes the base64 string into byte array
		 *
		 * @param imageDataString
		 *            - a {@link java.lang.String}
		 * @return byte array
		 */
		public static byte[] decoderesume(String imageDataString) {
			return Base64.decodeBase64(imageDataString);
		}

		/**
		 * Generates file name from current date and time
		 * 
		 * @param context
		 *            context
		 * @return string
		 */
		private String getDateTimeForFileName(Context context) {
			// formatter
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			Date today = new Date();
			String dateString = formatter.format(today) + ".DOCX";
			// logs the date
			context.getLogger().log("date format:" + dateString);
			return dateString;
		}

	}

