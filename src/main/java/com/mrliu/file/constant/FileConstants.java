package com.mrliu.file.constant;


import java.io.Serializable;

/**
 * <p>
 * 数据库常量
 * 文件表
 * </p>
 *
 * @author Mr.Liu
 */
public class FileConstants implements Serializable {

    /**
     * 字段常量
     */
    public static final String FILE_SPLIT = ".";

    public static final String FOLDER_SPLIT = "/";

    private FileConstants() {
        super();
    }
}
