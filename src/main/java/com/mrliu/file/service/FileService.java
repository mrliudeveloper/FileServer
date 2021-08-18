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

    /**
     * 文件删除接口
     * @param id 文件关联表id
     * @return true/false
     */
    boolean deleteFile(String id);

}
