package com.mrliu.file.controller;

import com.mrliu.file.po.FileInfoEntity;
import com.mrliu.file.service.FileService;
import com.mrliu.file.strategy.FileStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

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
    public String upload(MultipartFile multipartFile) {
        final boolean b = fileService.uploadFile(multipartFile);
        System.out.println(b);
        return String.valueOf(b);
    }

    /**
     * 下载测试接口
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
}
