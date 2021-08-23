package com.mrliu.file.vo;

import lombok.Data;

/**
 * @author Mr.Liu
 */
@Data
public class FileChunkMergeVo {
    //文件唯一名
    private String name;

    //原始文件名
    private String originalFileName;

    //md5
    private String md5;
    //分片综述
    private Integer chunks;
    //文件后缀
    private String ext;
    //文件夹id
    private Long folderId;
    //文件大小
    private Long size;
    //类型
    private String contextType;
}
