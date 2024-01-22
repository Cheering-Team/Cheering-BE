package com.cheering.global.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Util {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 s3Client;

    public URL uploadFile(MultipartFile file, String category) throws IOException {
        if (file == null || file.isEmpty()) {
            log.error("file is Empty or file is null");
            throw new IOException();
        }

        String fileName = convertUUIDFileName(file, category);
        ObjectMetadata metadata = setObjectMetadata(file);
        s3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

        return s3Client.getUrl(bucket, fileName);
    }

    private static String convertUUIDFileName(MultipartFile file, String category) {
        String originalFilename = file.getOriginalFilename();
        String extension = null;

        if (originalFilename != null) {
            extension = getFileExtension(originalFilename);
        }

        return category + "/" + UUID.randomUUID() + "." + extension;
    }

    private static ObjectMetadata setObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        return metadata;
    }

    public List<URL> uploadFiles(List<MultipartFile> files, String category) throws IOException {
        // 다중 업로드 && 리스트 ","을 기준으로 하나의 문자열 반환
        // files 갯수 0 이면 반환 ""
        if (files == null || files.isEmpty()) {
            return null;
        }

        List<URL> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            URL url = uploadFile(file, category);
            fileUrls.add(url);
        }

        log.info("uploadFiles url: {}", fileUrls);

        return fileUrls;
    }

    public String getPath(String path) {
        return s3Client.getUrl(bucket, path).toString();
    }

    public byte[] downloadFile(String image) {

        String filename = image.substring(image.lastIndexOf('/') + 1);

        S3Object s3Object = s3Client.getObject(bucket, filename);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucket, fileName);
        return fileName + " removed ...";
    }

    private static String getFileExtension(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
    }

}
