package com.chlorine.minio.controller;

import com.chlorine.minio.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/minio")
public class MinioController {

    @Autowired
    private MinioUtil minioUtil;

    //    @GetMapping("/proxy/image/{bucketName}/{objectName}")
//    public void getImage(@PathVariable String bucketName, @PathVariable String objectName, HttpServletResponse response) {
//        minioUtil.getImage(objectName, bucketName, response);
//    }

    @GetMapping("/generatePresignedUrl")
    public String generatePresignedUrl(@RequestParam("bucketName") String bucketName, @RequestParam("objectName") String objectName) {
        return minioUtil.generatePresignedUrl(bucketName, objectName);
    }

    @GetMapping("/proxy/image/{bucketName}/{objectPath}/{objectName}")
    public void getImage(@PathVariable String bucketName, @PathVariable String objectPath, @PathVariable String objectName, HttpServletResponse response) {
        minioUtil.getImage(bucketName, objectPath + "/" + objectName, response);
    }


    @PostMapping("/createBucket")
    public ResponseEntity<String> createBucket(@RequestParam("bucketName") String bucketName) {
        try {
            boolean isSuccess = minioUtil.createBucket(bucketName);
            return isSuccess ? ResponseEntity.ok("Bucket created successfully!") : ResponseEntity.badRequest().body("Failed to create bucket.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating bucket: " + e.getMessage());
        }
    }

    @GetMapping("/checkBucketExist")
    public ResponseEntity<Boolean> checkBucketExist(@RequestParam("bucketName") String bucketName) {
        try {
            boolean exists = minioUtil.checkBucketExist(bucketName);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

//    @PostMapping("/uploadFile/{bucketName}/{objectName}")
//    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
//                                             @PathVariable("bucketName") String bucketName,
//                                             @PathVariable("objectName") String objectName) {
//        try {
//            boolean isSuccess = minioUtil.uploadFile(file, bucketName, objectName);
//            return isSuccess ? ResponseEntity.ok("File uploaded successfully!") : ResponseEntity.badRequest().body("Failed to upload file.");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
//        }
//    }

    @PostMapping("/uploadFile/{bucketName}")
    public ResponseEntity<String> uploadFile(@PathVariable String bucketName, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        return ResponseEntity.ok(minioUtil.uploadFile(bucketName, file, request));
    }

    @PostMapping("/uploadFileComplete")
    public ResponseEntity<String> uploadFileComplete(@RequestParam("bucketName") String bucketName,
                                                     @RequestParam("folderName") String folderName,
                                                     @RequestParam("objectName") String objectName,
                                                     @RequestParam("partNum") Integer partNum) {
        try {
            boolean isSuccess = minioUtil.uploadFileComplete(bucketName, folderName, objectName, partNum);
            return isSuccess ? ResponseEntity.ok("File parts merged and uploaded successfully!") : ResponseEntity.badRequest().body("Failed to merge and upload file parts.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error merging and uploading file parts: " + e.getMessage());
        }
    }

    @GetMapping("/deleteBucket")
    public ResponseEntity<String> deleteBucket(@RequestParam("bucketName") String bucketName) {
        try {
            boolean isSuccess = minioUtil.deleteBucket(bucketName);
            return isSuccess ? ResponseEntity.ok("Folder deleted successfully!") : ResponseEntity.badRequest().body("Failed to delete folder.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting folder: " + e.getMessage());
        }
    }

    @GetMapping("/deleteFolder")
    public ResponseEntity<String> deleteFolder(@RequestParam("bucketName") String bucketName,
                                               @RequestParam("objectName") String objectName,
                                               @RequestParam(value = "isDeep", defaultValue = "true") boolean isDeep) {
        try {
            boolean isSuccess = minioUtil.deleteBucketFolder(bucketName, objectName, isDeep);
            return isSuccess ? ResponseEntity.ok("Folder deleted successfully!") : ResponseEntity.badRequest().body("Failed to delete folder.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting folder: " + e.getMessage());
        }
    }

    @GetMapping("/deleteSingleFile")
    public ResponseEntity<String> deleteSingleFile(@RequestParam("bucketName") String bucketName,
                                                   @RequestParam("objectName") String objectName) {
        try {
            boolean isSuccess = minioUtil.deleteSingleFile(bucketName, objectName);
            return isSuccess ? ResponseEntity.ok("File deleted successfully!") : ResponseEntity.badRequest().body("Failed to delete file.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting file: " + e.getMessage());
        }
    }
}
