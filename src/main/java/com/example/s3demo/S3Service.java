package com.example.s3demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
public class S3Service {
	
	@Autowired
	S3Client s3client;
	
	
	/**
	 * 
	 * @param region
	 * @return
	 */
	@Bean("s3client")
	public S3Client getS3Client(Region region) {
		S3Client s3client = S3Client.builder()
				.credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
				.region(Region.US_WEST_2).build();
		
		return s3client;
	}
	
	/**
	 * 
	 * @param bucketName
	 * @return
	 */
	public List<S3Object> listS3ObjectsFromBucket(String bucketName){
        return s3client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build()).contents();
	}
	
	public String addFile(String bucketName, Path filepath, String absolutePath) {
		PutObjectResponse response = s3client.putObject(PutObjectRequest.builder()
				.bucket(bucketName)
				.key(filepath.getFileName().toString())
				.build(), 
				RequestBody.fromBytes(readBytesFromFile(absolutePath)));
				
		return response.eTag();
	}
	
	
	/**
	 * 
	 * @param filePath
	 * @return
	 */
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
