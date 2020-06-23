package com.example.s3demo;

import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.services.s3.model.S3Object;

@RestController
public class S3Controller {
	
	@Autowired
	S3DemoUtility s3DemoUtility;

	@Autowired
	S3Service s3Service;
	
	@GetMapping("/s3rundemo")
	public ResponseEntity<String> getS3Files(@RequestParam("bucketName") String bucketName) throws Exception{
		StringBuffer returndata = new StringBuffer("");
		
		Path filepath = s3DemoUtility.generateTempFileWithRandomContent();
		String absolutePath = "/tmp/"+filepath.getFileName().toString() ;
		
		returndata.append(s3Service.addFile(bucketName, filepath, absolutePath));
		
		List<S3Object> objects = s3Service.listS3ObjectsFromBucket(bucketName);

		for (S3Object s3Object : objects) {
			returndata.append(s3Object.toString());
		}	

		return ResponseEntity.ok().body(returndata.toString());
	}
}
