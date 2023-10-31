package com.example.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3;

    public S3ServiceImpl(AmazonS3 s3) {
        this.s3 = s3;
    }

    @Value("${amazon.s3.bucket-name}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        File convertedFile = convertMultipartToFile(multipartFile);
        s3.putObject(bucketName, fileName, convertedFile);
        convertedFile.delete();
        return fileName + " is uploaded to S3 bucket " + bucketName;
    }

    @Override
    public byte[] downloadFile(String fileName) {
        S3Object object = s3.getObject(bucketName, fileName);
        S3ObjectInputStream objectContent = object.getObjectContent();
        byte[] content = new byte[0];
        try {
            content = IOUtils.toByteArray(objectContent);
        } catch (IOException e) {
            e.printStackTrace();;
        }
        return content;
    }

    @Override
    public String deleteFile(String fileName) {
        s3.deleteObject(bucketName, fileName);
        return fileName + " is deleted";
    }

    @Override
    public List<Object> getAllFiles() {
        List<Object> allObjects = new ArrayList<>();
        ListObjectsV2Result listObjectsV2Result = s3.listObjectsV2(bucketName);
        for(S3ObjectSummary summary: listObjectsV2Result.getObjectSummaries()) {
            String objectName = summary.getKey();
            allObjects.add(objectName);
        }
        return allObjects;
    }

    private File convertMultipartToFile(MultipartFile multipartFile) {
        File convertedFile = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertedFile;
    }
}
