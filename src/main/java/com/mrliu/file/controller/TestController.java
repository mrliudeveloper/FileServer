package com.mrliu.file.controller;

import com.mrliu.file.po.FileInfoEntity;
import com.mrliu.file.service.FileService;
import com.mrliu.file.strategy.FileStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author Mr.Liu
 */
@RestController
public class TestController {

    @Resource
    private FileService fileService;
    /**
     * 测试接口
     * @return 字符串
     */
    @RequestMapping("/hello")
    public String sayHello(){
        return "hello world";
    }

    @PostMapping("/upload")
    public String upload(MultipartFile multipartFile){
        final boolean b = fileService.uploadFile(multipartFile);
        System.out.println(b);
        return String.valueOf(b);
    }
}
