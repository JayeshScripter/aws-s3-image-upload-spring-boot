package com.awsrecap.app.amazons3client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AmazonClient {

	@Value("${accessKey}")
	private String accessKey;

	@Value("${secretKey}")
	private String secretKey;

	@Value("${region}")
	private String region;

	@Bean
	public S3Client s3Client() {
		validateAwsProperties();

		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

		return S3Client.builder()
				.region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(awsCreds))
				.build();
	}

	private void validateAwsProperties() {
		if (!StringUtils.hasText(accessKey) || !StringUtils.hasText(secretKey) || !StringUtils.hasText(region)) {
			throw new IllegalArgumentException("AWS credentials or region are not properly set in application.properties");
		}
	}
}
