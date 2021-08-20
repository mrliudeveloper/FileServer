package com.mrliu.file.service;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

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

    /**
     * 单文件直接下载，多文件打包下载
     * @param response response
     * @param ids ids
     */
    HashMap<String,Object> download(HttpServletResponse response, String[] ids);

}
