package com.mrliu.file.enumeration;

/**
 * 文件 存储类型 枚举
 *
 * @author Mr.Liu
 */
public enum FileStorageType {
    /**
     * 本地
     */
    LOCAL,
    /**
     * FastDFS
     */
    FAST_DFS,
    ALI,
    MINIO,
    QINIU,
    TENCENT,
    ;

    public boolean eq(FileStorageType type) {
        for (FileStorageType t : FileStorageType.values()) {
            return t.equals(type);
        }
        return false;
    }
}
