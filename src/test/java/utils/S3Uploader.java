package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.file.Files;
import java.nio.file.Path;
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

        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            logger.error("File does not exist: {}", filePath);
            throw new IllegalArgumentException("File not found: " + filePath);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String key = "screenshots/google-homepage-" + timestamp + ".png";

        logger.info("Uploading screenshot to S3");

        //uploading to s3 bucket
        try (S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .build()) {

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.putObject(request, path);

            logger.info("Successfully uploaded file '{}' to s3://{}/{}",
                    filePath, bucketName, key);

        } catch (S3Exception e) {

            logger.error(
                    "AWS S3 error while uploading file '{}'. Bucket='{}', Key='{}', StatusCode='{}', RequestId='{}', Error='{}'",
                    filePath,
                    bucketName,
                    key,
                    e.statusCode(),
                    e.requestId(),
                    e.awsErrorDetails() != null
                            ? e.awsErrorDetails().errorMessage()
                            : e.getMessage(),
                    e);

            throw e;

        } catch (SdkException e) {

            logger.error(
                    "AWS SDK client error while uploading '{}' to bucket '{}': {}",
                    filePath,
                    bucketName,
                    e.getMessage(),
                    e);

            throw e;

        } catch (Exception e) {

            logger.error(
                    "Unexpected error while uploading '{}' to S3",
                    filePath,
                    e);

            throw new RuntimeException(
                    "Failed to upload file to S3", e);
        }
    }
}
