package com.example.controller;

import com.example.helper.ApiResponse;
import com.example.service.S3ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class S3Controller {

    @Autowired
    private S3ServiceImpl service;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadFileToS3(@RequestParam("file") MultipartFile multipartFile) {
        String result = service.uploadFile(multipartFile);
        ApiResponse response = ApiResponse
                .builder()
                .message("Uploaded")
                .isSuccessful(true)
                .statusCode(200)
                .data(result)
                .build();
        return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFileFromS3(@PathVariable String fileName) {
        byte[] data = service.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<ApiResponse> deleteFileFromS3(@PathVariable String fileName) {
        String result = service.deleteFile(fileName);
        ApiResponse response = ApiResponse
                .builder()
                .message("Deleted")
                .isSuccessful(true)
                .statusCode(200)
                .data(result)
                .build();
        return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllFilesFromS3() {
        List<Object> allFiles = service.getAllFiles();
        ApiResponse response = ApiResponse
                .builder()
                .message("Fetched")
                .isSuccessful(true)
                .statusCode(200)
                .data(allFiles)
                .build();
        return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
    }
}
