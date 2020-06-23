package com.example.s3demo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class S3DemoUtility {

	/**
	 * @return
	 * @throws IOException
	 */
	public Path generateTempFileWithRandomContent() throws IOException {
		Path filepath = Files.createTempFile(UUID.randomUUID().toString(), null);
		
		byte[] rnd = new byte[2048];
		new Random().nextBytes(rnd);
		
		Files.copy(new ByteArrayInputStream(rnd), filepath, StandardCopyOption.REPLACE_EXISTING);
		return filepath;
	}
	
}
