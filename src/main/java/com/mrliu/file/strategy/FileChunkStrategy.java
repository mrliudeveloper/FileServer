package com.mrliu.file.strategy;

import com.mrliu.file.po.FileInfoEntity;
import com.mrliu.file.vo.FileChunkMergeVo;

/**
 * 最高层的文件分片处理策略接口
 * @author Mr.Liu
 */
public interface FileChunkStrategy {
    /**
     * 分片上传文件合并方法
     * @param fileChunkMergeVo 文件合并参数vo
     * @return 文件详细信息
     */
    public FileInfoEntity chunkMerge(FileChunkMergeVo fileChunkMergeVo);
}
