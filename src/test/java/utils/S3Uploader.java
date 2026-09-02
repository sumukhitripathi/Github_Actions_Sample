package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class S3Uploader {

    private static final Logger logger = LoggerFactory.getLogger(S3Uploader.class);

    public static void uploadFile(String filePath) {
        //env variables from github secrets and variables
        String bucketName = System.getenv("S3_BUCKET_NAME");
        String region = System.getenv().getOrDefault("AWS_REGION", "ap-south-1");

        if (bucketName == null || bucketName.isEmpty()) {
            logger.error("S3 bucket name is null or empty");
            throw new IllegalStateException("Environment variable S3_BUCKET_NAME is not set");
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String key = "screenshots/google-homepage-" + timestamp + ".png";

        logger.info("Uploading screenshot to S3");

        //uploading to s3 bucket
        try (S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .build()) {

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.putObject(putObjectRequest, Paths.get(filePath));

            logger.info("Screenshot uploaded to s3://" + bucketName + "/" + key);
        } catch (S3Exception e) {
            logger.error("S3 upload failed for bucket '{}', key '{}': {}",
                    bucketName, key, e.awsErrorDetails() != null ? e.awsErrorDetails().errorMessage() : e.getMessage(), e);
            throw e;
        }
    }
}
