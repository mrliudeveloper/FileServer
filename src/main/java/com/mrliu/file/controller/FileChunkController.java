package com.mrliu.file.controller;

import com.mrliu.file.po.FileInfoEntity;
import com.mrliu.file.properties.FileServerProperties;
import com.mrliu.file.service.FileService;
import com.mrliu.file.strategy.FileChunkStrategy;
import com.mrliu.file.strategy.FileStrategy;
import com.mrliu.file.utils.WebUploader;
import com.mrliu.file.vo.FileChunkMergeVo;
import com.mrliu.file.vo.FileUploadVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * fastDFS分片上传
 * @author Mr.Liu
 */
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/chunk")
public class FileChunkController {
    @Resource
    private FileStrategy fileStrategy;
    @Resource
    private FileService fileService;
    @Resource
    private FileChunkStrategy fileChunkStrategy;
    @Resource
    private FileServerProperties fileServerProperties;
    @Resource
    private WebUploader webUploader;

    /**
     * 分片上传文件
     *
     * @param file         文件实体
     * @param fileUploadVo 上传vo
     * @return 文件合并vo
     */
    @PostMapping("/upload")
    public FileChunkMergeVo uploadFile(
            @RequestParam(value = "file") MultipartFile file,
            FileUploadVo fileUploadVo) throws IOException {

        if (file == null || file.isEmpty()) {
            log.error("分片上传的分片文件为空！");
            return null;
        }
        if (fileUploadVo.getChunks() == null || fileUploadVo.getChunks() <= 0) {

            FileInfoEntity upload = fileStrategy.upload(file);
            upload.setMd5(fileUploadVo.getMd5());
            upload.setId(UUID.randomUUID().toString());
            fileService.saveFileInfo(upload);
            return null;

        } else {
            //当前上传属于分片上传
            String storagePath = fileServerProperties.getStoragePath();
            String abstractFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("/yyyy/MM"));
            //写文件的临时目录
            String uploadFolder = storagePath + abstractFolder;
            //为需要上传的分片文件准备对应的存储位置
            File targetFile = webUploader.getReadySpace(fileUploadVo, uploadFolder);
            //保存文件
            file.transferTo(targetFile);
            //封装上传合并信息vo
            FileChunkMergeVo fileChunkMergeVo = new FileChunkMergeVo();
            fileChunkMergeVo.setOriginalFileName(file.getOriginalFilename());
            BeanUtils.copyProperties(fileUploadVo, fileChunkMergeVo);
            log.info("接收到分片：" + file + "info:" + fileChunkMergeVo);
            return fileChunkMergeVo;
        }
    }
    @PostMapping("/merge")
    public FileInfoEntity mergeFile(FileChunkMergeVo fileChunkMergeVo)
    {
        System.out.println(fileChunkMergeVo);
        return fileChunkStrategy.chunkMerge(fileChunkMergeVo);
    }
}
