package com.chlorine.minio.util;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MinioUtil {
    @Autowired
    MinioClient minioClient;

    public String generatePresignedUrl(String bucketName, String objectName) {
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry( 30, TimeUnit.SECONDS)
                            .build());
            return url;
        } catch (Exception e) {
            throw new RuntimeException("Error generating presigned URL", e);
        }
    }


    /**
     * 创建桶
     *
     * @param bucketName 桶名称
     */
    public Boolean createBucket(String bucketName) throws Exception {
        if (!StringUtils.hasLength(bucketName)) {
            throw new RuntimeException("创建桶的时候，桶名不能为空！");
        }
        if (checkBucketExist(bucketName)) {
            return true;
        }
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            return true;
        } catch (Exception e) {
            log.info("创建桶失败！", e);
            return false;
        }

    }

    /**
     * 检查桶是否存在
     *
     * @param bucketName 桶名称
     * @return boolean true-存在 false-不存在
     */
    public boolean checkBucketExist(String bucketName) throws Exception {
        if (!StringUtils.hasLength(bucketName)) {
            throw new RuntimeException("检测桶的时候，桶名不能为空！");
        }

        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 检测某个桶内是否存在某个文件
     *
     * @param objectName 文件名称
     * @param bucketName 桶名称
     */
    public boolean getBucketFileExist(String objectName, String bucketName) throws Exception {
        if (!StringUtils.hasLength(objectName) || !StringUtils.hasLength(bucketName)) {
            throw new RuntimeException("检测文件的时候，文件名和桶名不能为空！");
        }

        try {
            // 判断文件是否存在
            return (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()) &&
                    minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build()) != null);

        } catch (ErrorResponseException e) {
            log.info("文件不存在 ! Object does not exist");
            return false;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public GetObjectResponse getBucketFile(String bucketName, String objectName) throws Exception {
        if (!StringUtils.hasLength(objectName) || !StringUtils.hasLength(bucketName)) {
            throw new RuntimeException("文件名和桶名不能为空！");
        }

        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (ErrorResponseException e) {
            log.info("文件不存在 ! Object does not exist");
            throw new Exception(e);
        }
    }

    /**
     * 删除单个文件
     *
     * @param bucketName 桶名
     * @param objectName 文件名（包括路径，如果适用）
     * @return 是否删除成功
     */
    public Boolean deleteSingleFile(String bucketName, String objectName) {
        if (!StringUtils.hasLength(bucketName) || !StringUtils.hasLength(objectName)) {
            throw new IllegalArgumentException("删除文件时，桶名或文件名不能为空！");
        }

        try {
            // 构建删除对象的参数
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();

            // 执行删除操作
            minioClient.removeObject(removeObjectArgs);
            return true;
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 删除文件夹
     *
     * @param bucketName 桶名
     * @param objectName 文件夹名
     * @param isDeep     是否递归删除
     * @return
     */
    public Boolean deleteBucketFolder(String bucketName, String objectName, Boolean isDeep) {
        if (!StringUtils.hasLength(bucketName) || !StringUtils.hasLength(objectName)) {
            throw new RuntimeException("删除文件夹的时候，桶名或文件名不能为空！");
        }
        try {
            ListObjectsArgs args = ListObjectsArgs.builder().bucket(bucketName).prefix(objectName + "/").recursive(isDeep).build();
            Iterable<Result<Item>> listObjects = minioClient.listObjects(args);
            listObjects.forEach(objectResult -> {
                try {
                    Item item = objectResult.get();
                    System.out.println(item.objectName());
                    minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(item.objectName()).build());
                } catch (Exception e) {
                    log.info("删除文件夹中的文件异常", e);
                }
            });
            return true;
        } catch (Exception e) {
            log.info("删除文件夹失败");
            return false;
        }
    }

    public Boolean deleteBucket(String bucketName) {
        if (!StringUtils.hasLength(bucketName)) {
            throw new RuntimeException("桶名不能为空！");
        }
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            return true;
        } catch (Exception e) {
            log.info("删除文件夹失败");
            return false;
        }
    }

    /**
     * 文件上传文件
     *
     * @param file       文件
     * @param bucketName 桶名
     * @param objectName 文件名,如果有文件夹则格式为 "文件夹名/文件名"
     * @return
     */
    public Boolean uploadFile(MultipartFile file, String bucketName, String objectName) {

        if (Objects.isNull(file) || Objects.isNull(bucketName)) {
            throw new RuntimeException("文件或者桶名参数不全！");
        }

        try {
            //资源的媒体类型
            String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//默认未知二进制流
            InputStream inputStream = file.getInputStream();
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName).object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            ObjectWriteResponse response = minioClient.putObject(args);
            inputStream.close();
            return response.etag() != null;
        } catch (Exception e) {
            log.info("单文件上传失败！", e);
            return false;
        }
    }

    public String uploadFile(String bucketName, MultipartFile file, HttpServletRequest request) {

//        User user = userService.getUserFromRequest(request);
        String objectName = ""+ "/" + System.currentTimeMillis();

        try {
            createBucket(bucketName);
            InputStream inputStream = file.getInputStream();
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName).object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            ObjectWriteResponse response = minioClient.putObject(args);
            inputStream.close();
            return bucketName + "/" + objectName;
        } catch (Exception e) {
            log.info("单文件上传失败！", e);
            return null;

        }
    }

    /**
     * 分片合并
     *
     * @param bucketName
     * @param folderName
     * @param objectName
     * @param partNum
     * @return
     */
    public Boolean uploadFileComplete(String bucketName, String folderName, String objectName, Integer partNum) {

        try {
            //获取临时文件下的所有文件信息
            Iterable<Result<Item>> listObjects = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(folderName + "/").build());
            //计算minio中分片个数
            Integer num = 0;
            for (Result<Item> result : listObjects) {
                num++;
            }
            //在依次校验实际分片数和预计分片数是否一致
            if (!num.equals(partNum)) {
                log.info("文件 {} 分片合并的时候，检测到实际分片数 {} 和预计分片数 {} 不一致", folderName, num, partNum);
                return false;
            }

            InputStream inputStream = null;
            log.info("开始合并文件 {} 分片合并，实际分片数 {} 和预计分片数 {}", folderName, num, partNum);
            for (int i = 0; i < num; i++) {
                String tempName = folderName + "/" + objectName.substring(0, objectName.lastIndexOf(".")) + "_" + i + ".temp";
                try {
                    //获取分片文件流
                    InputStream response = minioClient.getObject(
                            GetObjectArgs.builder().bucket(bucketName).object(tempName).build());
                    //流合并
                    if (inputStream == null) {
                        inputStream = response;
                    } else {
                        inputStream = new SequenceInputStream(inputStream, response);
                    }
                } catch (Exception e) {
                    log.info("读取分片文件失败！", e);
                }
            }
            if (inputStream == null) {
                log.info("合并流数据为空！");
                return false;
            }
            //转换为文件格式
            MockMultipartFile file = new MockMultipartFile(objectName, inputStream);

            //将合并的文件流写入到minio中
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName).object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
//                    .contentType(file.getContentType())//这里可以不知道类型
                    .build();
            String etag = minioClient.putObject(args).etag();

            // 删除临时文件
            if (etag != null) {
                listObjects.forEach(objectResult -> {
                    try {
                        Item item = objectResult.get();
                        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(item.objectName()).build());
                    } catch (Exception e) {
                        log.info("删除文件夹中的文件异常", e);
                    }
                });
                log.info("{}:临时文件夹文件已删除！", folderName);
            }

            inputStream.close();
            return etag != null;
        } catch (Exception e) {
            log.info("合并 {} - {} 文件失败！", folderName, objectName, e);
            return false;
        }
    }

    public void getImage(String bucketName, String objectName, HttpServletResponse response) {
        try (InputStream inputStream = getBucketFile(bucketName, objectName)) {
            // 设置响应头，指定内容类型为图片类型
            response.setContentType(getContentTypeFromObjectName(objectName));
            // 将图片数据写入响应输出流
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getContentTypeFromObjectName(String objectName) {
        String extension = objectName.substring(objectName.lastIndexOf('.') + 1);
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG_VALUE;
            case "png":
                return MediaType.IMAGE_PNG_VALUE;
            case "gif":
                return MediaType.IMAGE_GIF_VALUE;
            default:
                return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }

    public static String generateUniqueId(int length) {
        UUID uuid = UUID.randomUUID();
        String hash = uuid.toString().replaceAll("-", "");
        return hash.substring(0, Math.min(length, hash.length()));
    }

}
