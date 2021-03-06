package com.mrliu.file.strategy.impl;

import com.mrliu.file.po.FileInfoEntity;
import com.mrliu.file.properties.FileServerProperties;
import com.mrliu.file.service.FileService;
import com.mrliu.file.strategy.FileChunkStrategy;
import com.mrliu.file.utils.FileLock;
import com.mrliu.file.vo.FileChunkMergeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.Lock;

import static com.mrliu.file.constant.FileConstants.FILE_SPLIT;
import static com.mrliu.file.constant.FileConstants.FOLDER_SPLIT;

/**
 * 文件分片处理抽象类
 *
 * @author Mr.Liu
 */
@Slf4j
public abstract class AbstractFileChunkStrategy implements FileChunkStrategy {
    @Resource
    private FileService fileService;

    @Resource
    private FileServerProperties fileServerProperties;

    /**
     * 分片合并处理主要流程
     *
     * @param fileChunkMergeVo 文件合并参数vo
     * @return 文件详情
     */
    @Override
    public FileInfoEntity chunkMerge(FileChunkMergeVo fileChunkMergeVo) {
        //定义文件合并后的文件名称
        String fileName = fileChunkMergeVo.getName() + FILE_SPLIT + fileChunkMergeVo.getExt();
        //分片合并
        FileInfoEntity result = this.chunkMerge(fileChunkMergeVo, fileName);
        //合并后文件信息保存到数据库
        if (result != null) {
            //合并成功
            //文件信息保存到数据库
            //设置文件对象的属性，保存到数据库
            result.setId(UUID.randomUUID().toString());
            result.setOriginalFileName(fileChunkMergeVo.getOriginalFileName());
            result.setIsDelete(false);
            result.setFileSize(fileChunkMergeVo.getSize());
            result.setMd5(fileChunkMergeVo.getMd5());
            result.setFileName(fileName);
            result.setFileExt(fileChunkMergeVo.getExt());
            result.setCrtTime(new Date());
            result.setFileStorageType(fileServerProperties.getType());
            fileService.saveFileInfo(result);
            return result;
        }
        //合并失败
        return null;
    }

    /**
     * 分片合并
     *
     * @param fileChunkMergeVo 分配信息vo
     * @param fileName         文件名
     * @return 文件详情
     */
    public FileInfoEntity chunkMerge(FileChunkMergeVo fileChunkMergeVo, String fileName) {
        //获得分片文件的存储路径
        String storagePath = fileServerProperties.getStoragePath();
        String abstractFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("/yyyy/MM"));
        //写文件的临时目录
        String uploadFolder = storagePath + abstractFolder;

        Integer chunks = fileChunkMergeVo.getChunks();
        String folder = fileChunkMergeVo.getName();
        String relativePath = uploadFolder + FOLDER_SPLIT + folder;
        //根据指定目录获取文件数量
        int chunksNum = this.getChunksNum(relativePath);

        //检查分片数量是否足够
        if (chunks == chunksNum) {
            //数量足够,可以合并
            Lock lock = FileLock.getLock(folder);
            lock.lock();
            try {
                List<File> files = getChunks(relativePath);
                //合并前需要排序
                files.sort(Comparator.comparingInt(f -> Integer.parseInt(f.getName())));

                //调用子类分片合并方法实现分片合并
                FileInfoEntity result = this.merge(files, fileName, fileChunkMergeVo);
                //清理文件
                this.cleanSpace(folder, uploadFolder);
                return result;
            } catch (Exception e) {
                log.error("分片合并失败");
                return null;
            } finally {
                //释放锁
                lock.unlock();
                //清理锁对象
                FileLock.removeLock(folder);
            }
            //分片合并成功后需要删除分片的临时文件和目录
        }
        log.error("分片数量不足，无法进行数量合并");
        //暂时不做异常抛出
        return null;
    }

    /**
     * 清理本地文件
     * @param folder 文件夹
     * @param path 文件名
     */
    public void cleanSpace(String folder, String path) {
        System.out.println(folder);
        System.out.println(path);
        //删除存放分片文件的目录
        File chunkFolder = new File(path + folder);
        FileUtils.deleteQuietly(chunkFolder);
        //删除.tmp文件
        File tmpFile = new File(path + folder + FILE_SPLIT + "tmp");
        FileUtils.deleteQuietly(tmpFile);
    }

    /**
     * 获取指定文件夹下文件数量
     *
     * @param path 临时存储路径
     * @return 文件数量
     */
    public int getChunksNum(String path) {
        System.out.println(path);
        File folder = new File(path);
        File[] files = folder.listFiles((file) -> {
            return !file.isDirectory();
        });
        return files.length;
    }

    /**
     * 获取指定目录的文件
     *
     * @param path 路径
     * @return 文件列表
     */
    public List<File> getChunks(String path) {
        File folder = new File(path);
        File[] files = folder.listFiles((file) -> {
            if (file.isDirectory()) {
                return false;
            }
            return true;
        });
        return new ArrayList<>(Arrays.asList(files));
    }

    /**
     * 分片合并抽象方法
     *
     * @param files 文件分片列表
     * @param fileName 文件分片名称
     * @param fileChunkMergeVo 文件分片信息
     * @return 文件详情信息
     */
    protected abstract FileInfoEntity merge(List<File> files, String fileName, FileChunkMergeVo fileChunkMergeVo) throws IOException;
}
