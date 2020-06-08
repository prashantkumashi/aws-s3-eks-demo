package com.example.s3demo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
public class S3DemoUtility {

	public String runS3demo(String bucketName) throws Exception{
		
		StringBuffer returndata = new StringBuffer("");
		
		S3Client s3client = S3Client.builder()
				.credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
				.region(Region.US_WEST_1).build();
		
		Path filepath = Files.createTempFile(UUID.randomUUID().toString(), null);
		
		byte[] rnd = new byte[2048];
		new Random().nextBytes(rnd);
		
		Files.copy(new ByteArrayInputStream(rnd), filepath, StandardCopyOption.REPLACE_EXISTING);
		
		
		PutObjectResponse response = s3client.putObject(PutObjectRequest.builder()
				.bucket(bucketName)
				.key(filepath.getFileName().toString())
				.build(), 
				RequestBody.fromBytes(readBytesFromFile("/tmp/"+filepath.getFileName().toString() )));

		returndata.append(response.eTag());
		
		
		ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(bucketName)
                .build();

        ListObjectsResponse res = s3client.listObjects(listObjects);
        List<S3Object> objects = res.contents();

        for (ListIterator iterVals = objects.listIterator(); iterVals.hasNext(); ) {
            S3Object myValue = (S3Object) iterVals.next();
            System.out.print("\n The name of the key is " + myValue.key());
            System.out.print("\n The object is " + (myValue.size()/1024) + " KBs");
            System.out.print("\n The owner is " + myValue.owner());
        }		
		
		return returndata.toString();
	}
	
	private static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }
	
}
