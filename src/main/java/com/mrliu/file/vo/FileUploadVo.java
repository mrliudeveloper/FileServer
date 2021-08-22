package com.mrliu.file.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FileUploadVo {
    //MD5
    private String md5;
    //大小
    private Long size;
    //文件唯一名
    private String name;
    //分片总数
    private Integer chunks;
    //当前分片
    private Integer chunk;
    //最后更新时间
    private String lastModifiedDate;
    //类型
    private String type;
    //文件后缀
    private String ext;
    //文件夹id
    private Long folderId;
}
