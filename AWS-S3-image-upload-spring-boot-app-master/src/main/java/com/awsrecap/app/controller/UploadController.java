package com.awsrecap.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request; // Import for listing objects
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response; // Import for listing objects
import software.amazon.awssdk.services.s3.model.S3Object; // Import for S3Object
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List; // Import for List
import java.util.stream.Collectors; // Import for Collectors
import java.util.Collections; // Import for Collections.emptyList()

@RestController
@RequestMapping("/home") // Controller ka base path
public class UploadController {

	@Autowired
	private S3Client s3Client;

	@Value("${bucketName}")
	private String bucketName;

	@Value("${endpointUrl}")
	private String endpointUrl;

	// Existing uploadFile method
	@PostMapping("/uploadFile")
	public ResponseEntity<String> uploadFile(@RequestPart(value = "file") MultipartFile multipartFile) {
		try {
			String fileName = URLEncoder.encode(multipartFile.getOriginalFilename(), StandardCharsets.UTF_8);

			// Create a PutObjectRequest
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
					.bucket(bucketName)
					.key(fileName)
					.contentType(multipartFile.getContentType())
					.build();

			// Upload the file
			s3Client.putObject(putObjectRequest, RequestBody.fromBytes(multipartFile.getBytes()));

			String fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;

			return ResponseEntity.ok("Uploaded Successfully: " + fileUrl);

		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Upload Failed: " + e.getMessage());
		}
	}

	// Naya endpoint images list karne ke liye
	@GetMapping("/images") // Is endpoint ko frontend call karega
	public ResponseEntity<List<String>> listImages() {
		try {
			ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
					.bucket(bucketName)
					.build();

			ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

			// S3 objects se public URLs generate karein
			List<String> imageUrls = listObjectsResponse.contents().stream()
					.map(S3Object::key) // Har object ki key (file name) lein
					.map(key -> endpointUrl + "/" + bucketName + "/" + key) // Public URL banayein
					.collect(Collectors.toList()); // List mein collect karein

			return ResponseEntity.ok(imageUrls); // URLs ki list return karein
		} catch (Exception e) {
			System.err.println("S3 se images list karne mein error: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Collections.emptyList()); // Error hone par empty list return karein
		}
	}
}
