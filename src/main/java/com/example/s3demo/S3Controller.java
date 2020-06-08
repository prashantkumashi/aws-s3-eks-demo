package com.example.s3demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class S3Controller {
	
	@Autowired
	S3DemoUtility s3DemoUtility;
	
	@GetMapping("/s3rundemo")
	public ResponseEntity<String> getS3Files(@RequestParam("bucketName") String bucketName) throws Exception{
		return ResponseEntity.ok().body(s3DemoUtility.runS3demo(bucketName));
	}
}
