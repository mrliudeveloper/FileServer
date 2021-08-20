package com.mrliu.file.service;

import com.mrliu.file.po.FileInfoEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Mr.Liu
 */
public interface FileService {
    /**
     * 文件上传接口
     * @param multipartFile 文件实体
     * @return true/false
     */
    FileInfoEntity uploadFile(MultipartFile multipartFile);

    /**
     * 文件删除接口
     * @param id 文件关联表id
     */
    void deleteFile(String id);

    /**
     * 单文件直接下载，多文件打包下载
     * @param response response
     * @param ids ids
     */
    void download(HttpServletResponse response, String[] ids);

}
