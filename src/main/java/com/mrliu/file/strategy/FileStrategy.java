package com.mrliu.file.strategy;

import com.mrliu.file.po.FileInfoEntity;
import com.mrliu.file.vo.FileDeleteVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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

    /**
     * 单文件下载，多文件打包下载接口
     * @param response response
     * @param fileInfoEntities 文件详细信息
     */
    void download(HttpServletResponse response, List<FileInfoEntity> fileInfoEntities);
}
