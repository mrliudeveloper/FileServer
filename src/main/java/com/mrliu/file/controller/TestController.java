package com.mrliu.file.controller;

import com.mrliu.file.po.FileInfoEntity;
import com.mrliu.file.service.FileService;


import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * @author Mr.Liu
 */
@Slf4j
@RestController
public class TestController {

    @Resource
    private FileService fileService;

    /**
     * 测试接口
     *
     * @return 字符串
     */
    @RequestMapping("/hello")
    public String sayHello() {
        return "hello world";
    }

    /**
     * 上传测试接口
     *
     * @param multipartFile 文件实体
     * @return true/false
     */
    @PostMapping("/upload")
    public FileInfoEntity upload(MultipartFile multipartFile) {
        return fileService.uploadFile(multipartFile);
    }
    /**
     * 删除测试接口(真删除)
     *
     * @param id 文件id
     * @return ok/error
     */
    @PostMapping("/delete")
    public String delete(String id) {
        try {
            fileService.deleteFile(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return "error";
        }
        return "ok";
    }

    @GetMapping(value = "/download")
    public void download(@RequestParam("ids[]")String[] ids, HttpServletResponse response) {

        System.out.println(ids[0]);
        fileService.download(response, ids);
    }











}
