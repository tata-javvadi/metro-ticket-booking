package com.metro.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3FileService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public void uploadFile(String keyName, MultipartFile file) throws IOException {
        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .contentType(file.getContentType())
                .build();

        byte[] fileBytes = file.getBytes();
        s3Client.putObject(putOb, RequestBody.fromBytes(fileBytes));
    }

    public byte[] downloadFileAsBytes(String keyName) {
        GetObjectRequest getOb = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        // Directly reads all content into memory
        ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getOb);
        return responseBytes.asByteArray();
    }

}