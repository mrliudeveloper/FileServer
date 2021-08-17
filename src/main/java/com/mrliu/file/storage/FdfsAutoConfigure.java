package com.mrliu.file.storage;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.mrliu.file.po.FileInfoEntity;
import com.mrliu.file.properties.FileServerProperties;
import com.mrliu.file.strategy.AbstractFileStrategy;
import com.mrliu.file.vo.FileDeleteVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author Mr.Liu
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(FileServerProperties.class)
@ConditionalOnProperty(name = "file.type", havingValue = "FAST_DFS")
public class FdfsAutoConfigure {
    @Resource
    private FileServerProperties fileServerProperties;

    private FileServerProperties.Properties properties;

    @Service
    public class FastDfsServiceImpl extends AbstractFileStrategy {
        @Resource
        private FastFileStorageClient client;

        @Override
        public FileInfoEntity uploadFile(FileInfoEntity fileInfoEntity, MultipartFile multipartFile) throws IOException {
            StorePath storePath = client.uploadFile(multipartFile.getInputStream(),
                    multipartFile.getSize(),
                    fileInfoEntity.getFileExt(),
                    null);
            fileInfoEntity.setGroup(storePath.getGroup());
            final String[] split = storePath.getPath().split("/");
            fileInfoEntity.setFileName(split[split.length - 1]);
            fileInfoEntity.setUrl(fileServerProperties.getUriPrefix() + storePath.getFullPath());
            fileInfoEntity.setRelativePath(storePath.getPath());
            return fileInfoEntity;
        }

        @Override
        public void deleteFile(FileDeleteVo fileDeleteVo) {
            client.deleteFile(fileDeleteVo.getGroup(), fileDeleteVo.getRelativePath());
        }
    }

}
