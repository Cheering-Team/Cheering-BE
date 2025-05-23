package com.cheering._core.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Util {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String upload(MultipartFile image) {
        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new CustomException(ExceptionCode.EMPTY_FILE);
        }

        return this.uploadImage(image);
    }

    private String uploadImage(MultipartFile image) {
        this.validateImageFileExtension(image.getOriginalFilename());

        try {
            return this.uploadImageToS3(image);
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.IMAGE_UPLOAD_FAILED);
        }
    }

    private void validateImageFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if(lastDotIndex == -1) {
            throw new CustomException(ExceptionCode.INVALID_FILE_EXTENSION);
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtensionList = Arrays.asList(
                "jpg", "jpeg", "png", "gif", "bmp", "tif", "tiff", "webp", "heic", "heif", "svg",
                "mp4", "mov", "avi", "mkv", "wmv", "flv", "webm", "mpeg", "mpg", "3gp", "m4v"
        );

        if(!allowedExtensionList.contains(extension)) {
            throw new CustomException(ExceptionCode.INVALID_FILE_EXTENSION);
        }
    }

    private String uploadImageToS3(MultipartFile image) throws IOException{
        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename;

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        ObjectMetadata metadata = new ObjectMetadata();
        if (extension.equals("mov") || extension.equals("mp4") || extension.equals("avi") || extension.equals("mkv") ||
                extension.equals("wmv") || extension.equals("flv") || extension.equals("webm") || extension.equals("mpeg") ||
                extension.equals("mpg") || extension.equals("3gp") || extension.equals("m4v")) {
            metadata.setContentType("video/" + getVideoMimeType(extension));
        } else {
            metadata.setContentType("image/" + getImageMimeType(extension));
        }
        metadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata).withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            System.err.println(e);
            throw new CustomException(ExceptionCode.IMAGE_UPLOAD_FAILED);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    public void deleteImageFromS3(String imageAddress) {
        String key = getKeyFromImageAddress(imageAddress);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.IMAGE_DELETE_FAILED);
        }
    }

    private String getKeyFromImageAddress(String imageAddress) {
        try {
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
            return decodingKey.substring(1);
        } catch(MalformedURLException e) {
            throw new CustomException(ExceptionCode.IMAGE_DELETE_FAILED);
        }
    }

    private String getVideoMimeType(String extension) {
        switch (extension) {
            case "mp4":
            case "m4v":
                return "mp4";
            case "mov":
                return "quicktime";
            case "avi":
                return "x-msvideo";
            case "mkv":
                return "x-matroska";
            case "wmv":
                return "x-ms-wmv";
            case "flv":
                return "x-flv";
            case "webm":
                return "webm";
            case "mpeg":
            case "mpg":
                return "mpeg";
            case "3gp":
                return "3gpp";
            default:
                return "mp4"; // 기본값
        }
    }

    private String getImageMimeType(String extension) {
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "jpeg";
            case "png":
                return "png";
            case "gif":
                return "gif";
            case "bmp":
                return "bmp";
            case "tif":
            case "tiff":
                return "tiff";
            case "webp":
                return "webp";
            case "heic":
            case "heif":
                return "heic";
            case "svg":
                return "svg+xml";
            default:
                return "jpeg"; // 기본값
        }
    }
}
