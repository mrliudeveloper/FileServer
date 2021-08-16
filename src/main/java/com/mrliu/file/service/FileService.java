package com.mrliu.file.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Mr.Liu
 */
public interface FileService {
    /**
     * 文件上传接口
     * @param multipartFile 文件实体
     * @return true/false
     */
    boolean uploadFile(MultipartFile multipartFile);
}
