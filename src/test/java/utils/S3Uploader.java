package utils;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class S3Uploader {

    public static void uploadFile(String filePath) {
        String bucketName = System.getenv("S3_BUCKET_NAME");
        String region = System.getenv().getOrDefault("AWS_REGION", "ap-south-1");

        if (bucketName == null || bucketName.isEmpty()) {
            throw new IllegalStateException("Environment variable S3_BUCKET_NAME is not set");
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String key = "screenshots/google-homepage-" + timestamp + ".png";

        try (S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .build()) {

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.putObject(putObjectRequest, Paths.get(filePath));

            System.out.println("Screenshot uploaded to s3://" + bucketName + "/" + key);
        }
    }
}
