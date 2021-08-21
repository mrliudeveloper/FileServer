package com.mrliu.file.strategy;

import com.mrliu.file.po.FileInfoEntity;
import com.mrliu.file.vo.FileDeleteVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Mr.Liu
 * 最高层文件处理策略接口
 */
public interface FileStrategy {
    /**
     * 上传文件
     * @param multipartFile 文件实体
     * @return FileInfoEntity
     */
    FileInfoEntity upload(MultipartFile multipartFile);

    /**
     * 删除文件
     * @param fileDeleteVos 删除条件
     */
    void delete(List<FileDeleteVo> fileDeleteVos);
}
