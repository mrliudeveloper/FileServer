package com.mrliu.file.service.impl;

import com.mrliu.file.mapper.FileinfoMapper;
import com.mrliu.file.po.FileInfoEntity;
import com.mrliu.file.service.FileService;
import com.mrliu.file.strategy.FileStrategy;
import com.mrliu.file.vo.FileDeleteVo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Mr.Liu
 */
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FileStrategy fileStrategy;
    @Resource
    private FileinfoMapper fileinfoMapper;
    @Override
    public boolean uploadFile(MultipartFile multipartFile) {
        final FileInfoEntity infoEntity = fileStrategy.upload(multipartFile);
        infoEntity.setId(UUID.randomUUID().toString());
        System.out.println(infoEntity);
        final int i = fileinfoMapper.insertSelective(infoEntity);
        return i > 0;
    }

    @Override
    public boolean deleteFile(String id) {
        //删除fdfs文件服务器中的文件资源
        final ArrayList<FileDeleteVo> fileDeleteVos = new ArrayList<>();
        final FileInfoEntity infoEntity = fileinfoMapper.selectByPrimaryKey(id);
        final FileDeleteVo fileDeleteVo = new FileDeleteVo();
        fileDeleteVo.setId(id);
        fileDeleteVo.setFileName(infoEntity.getFileName());
        fileDeleteVo.setRelativePath(infoEntity.getRelativePath());
        fileDeleteVo.setGroup(infoEntity.getGroup());
        fileDeleteVos.add(fileDeleteVo);
        fileStrategy.delete(fileDeleteVos);
        //删除文件关联表
        fileinfoMapper.deleteByPrimaryKey(id);
        return false;
    }
}
