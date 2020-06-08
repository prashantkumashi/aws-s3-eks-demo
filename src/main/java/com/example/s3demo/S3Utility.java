package com.example.s3demo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;

import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;

/**
 *
 * @author isuftin
 */
public class S3Utility {

	private static AmazonS3 s3;
	private final static String ROLE_ARN = "arn:aws:iam::1234567890:role/YOUR-ROLE-HERE";
	private final static String SESSION_NAME = "test";

	@SuppressWarnings("deprecation")
	private static void init() throws Exception {

		File configFile = new File(System.getProperty("user.home"), ".aws/credentials");

		AWSCredentialsProvider credentialsProvider = new ProfileCredentialsProvider(
				new ProfilesConfigFile(configFile), "default");

		if (credentialsProvider.getCredentials() == null) {
			throw new RuntimeException("No AWS security credentials found");
		}
		
		
		AWSSecurityTokenServiceClient stsClient = new AWSSecurityTokenServiceClient(credentialsProvider.getCredentials());
		AssumeRoleRequest assumeRequest = new AssumeRoleRequest()
				.withRoleArn(ROLE_ARN)
				.withDurationSeconds(3600)
				.withRoleSessionName(SESSION_NAME);
		AssumeRoleResult assumeResult = stsClient.assumeRole(assumeRequest);

		BasicSessionCredentials temporaryCredentials
				= new BasicSessionCredentials(
						assumeResult.getCredentials().getAccessKeyId(),
						assumeResult.getCredentials().getSecretAccessKey(),
						assumeResult.getCredentials().getSessionToken());

		s3 = new AmazonS3Client(temporaryCredentials);
		S3Client s3 = S3Client.builder()
                .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .region(Region.AP_SOUTHEAST_2)
                .build();
	}

	private static void listBuckets() {
		List<Bucket> buckets = s3.listBuckets();
		System.out.println("You have " + buckets.size() + " Amazon S3 bucket(s).");
		if (buckets.size() > 0) {
			for (Bucket bucket : buckets) {
				System.out.println(bucket.getName());
			}
		}
	}

	private static void writeToBucket() throws IOException {
		Path tempFile = null;
		try {
			tempFile = Files.createTempFile(UUID.randomUUID().toString(), null);

			byte[] rnd = new byte[2048];
			new Random().nextBytes(rnd);
			Files.copy(new ByteArrayInputStream(rnd), tempFile, StandardCopyOption.REPLACE_EXISTING);
			s3.putObject("dev-owi", "test/" + tempFile.getFileName().toString(), tempFile.toFile());
		} finally {
			tempFile.toFile().delete();
		}
	}

	public static void main(String[] args) throws Exception {
		init();
		listBuckets();
		writeToBucket();
		System.exit(0);
	}
 
}
