package com.example.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface S3Service {

    String uploadFile(MultipartFile multipartFile) throws IOException;

    byte[] downloadFile(String fileName) throws IOException;

    String deleteFile(String fileName);

    List<Object> getAllFiles();
}
