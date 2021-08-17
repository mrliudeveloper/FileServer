package com.mrliu.file.strategy;

import com.mrliu.file.po.FileInfoEntity;
import com.mrliu.file.vo.FileDeleteVo;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import static com.mrliu.file.constant.FileConstants.FILE_SPLIT;

/**
 * @author Mr.Liu
 * 文件策略的抽象处理类
 */
@Slf4j
public abstract class AbstractFileStrategy implements FileStrategy {

    @Override
    public FileInfoEntity upload(MultipartFile multipartFile) {

        try {
            /*
             * 获得上传文件的原始文件名称
             */
            String originalFilename = multipartFile.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(FILE_SPLIT)) {
                throw new RuntimeException("上传文件名称缺少后缀");
            }
            //封装到一个FileInfoEntity对象
            FileInfoEntity fileInfo = FileInfoEntity.builder()
                    .isDelete(false)
                    .fileSize(multipartFile.getSize())
                    .originalFileName(multipartFile.getOriginalFilename())
                    .fileExt(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))
                    .build();
            //设置时间参数
            fileInfo.setCrtTime(new Date());
            uploadFile(fileInfo, multipartFile);
            return fileInfo;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("文件上传失败");
        }
    }

    /**
     * 文件上传抽象方法，由子类实现
     *
     * @param fileInfoEntity 文件对象
     * @param multipartFile  文件实体
     * @return 文件实体
     * @Exception IOException
     */
    public abstract FileInfoEntity uploadFile(FileInfoEntity fileInfoEntity, MultipartFile multipartFile) throws Exception;

    @Override
    public boolean delete(List<FileDeleteVo> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        //删除操作是否成功的一个标志位
        boolean flag = false;
        for (FileDeleteVo fileDeleteVo : list) {
            try {
                deleteFile(fileDeleteVo);
                flag = true;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return flag;
    }

    /**
     * 文件删除抽象方法，由子类实现
     *
     * @param fileDeleteVo 删除条件
     */
    public abstract void deleteFile(FileDeleteVo fileDeleteVo);
}
