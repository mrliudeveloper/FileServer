package com.mrliu.file.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 删除文件条件封装
 * @author Mr.Liu
 */
@Data
@Builder
public class FileDeleteVo {

    private String group;

    private String fileName;

    private  String relativePath;

    private Boolean file;
}
