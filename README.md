
# AWS-S3-image-upload-spring-boot-app

A java spring-boot photo uploading app which saves all the photos uploaded from a simple UI to an AWS S3 Bucket. 

Below AWS services are used to achieve the functionality.
1. AWS EC2
2. AWS S3
3. AWS IAM
4. AWS CodeCommit
5. AWS SDK for Java

## Explanation

The photo uploading system is hosted in a t2.micro AWS EC2 instance. And this application runs on port 8080.

When the user uploads the images through the application UI, all the images are saved in an AWS S3 bucket

AWS IAM service is used in order to enable the web application to access AWS services via an IAM programmatic user. That user  is set to a ‘Group’ named ‘S3_App_User’ which has 'AmazonS3FullAccess'

AWS SDK for Java is used in order to upload the images to AWS S3. Below is the maven dependency for the aws java client


<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk</artifactId>
    <version>1.12.748</version>
</dependency>.

To upload a file, AmazonS3.putObject() method is used.
