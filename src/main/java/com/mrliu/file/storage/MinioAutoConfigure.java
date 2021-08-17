package com.mrliu.file.storage;

import com.mrliu.file.po.FileInfoEntity;
import com.mrliu.file.properties.FileServerProperties;
import com.mrliu.file.strategy.AbstractFileStrategy;
import com.mrliu.file.vo.FileDeleteVo;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static com.mrliu.file.constant.FileConstants.FILE_SPLIT;

/**
 * @author Mr.Liu
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(FileServerProperties.class)
@ConditionalOnProperty(name = "file.type", havingValue = "MINIO")
public class MinioAutoConfigure {
    @Resource
    private FileServerProperties properties;

    private FileServerProperties.Properties minio;

    @Service
    public class MinioServiceImpl extends AbstractFileStrategy {

        private MinioClient buildClient() {
            minio = properties.getMinio();
            return MinioClient.builder()
                    .endpoint(minio.getEndpoint())
                    .credentials(minio.getAccessKeyId(), minio.getAccessKeySecret())
                    .build();
        }

        @SuppressWarnings("All")
        @Override
        public FileInfoEntity uploadFile(FileInfoEntity fileInfoEntity, MultipartFile multipartFile) {
            try {
                final MinioClient minioClient = buildClient();
                final String fileName = UUID.randomUUID() + FILE_SPLIT + fileInfoEntity.getFileExt();
                final String bucketName = minio.getBucketName();
                if (!minioClient.bucketExists(bucketName)) {
                    minioClient.makeBucket(bucketName);
                }
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(multipartFile.getInputStream(), multipartFile.getSize(), 1024 * 1024 * 100L)
                        .build());
                String relativePath = "";
                fileInfoEntity.setFileName(fileName);
                fileInfoEntity.setBucketName(bucketName);
                fileInfoEntity.setRelativePath(relativePath);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            return fileInfoEntity;
        }

        @Override
        public void deleteFile(FileDeleteVo fileDeleteVo) {

            MinioClient minioClient = buildClient();
            final RemoveObjectArgs build = RemoveObjectArgs.builder()
                    .bucket(minio.getBucketName())
                    .object(fileDeleteVo.getFileName())
                    .build();
            try {
                minioClient.removeObject(build);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
