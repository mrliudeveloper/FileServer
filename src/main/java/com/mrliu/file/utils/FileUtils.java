package com.mrliu.file.utils;

import com.mrliu.file.po.FileInfoEntity;
import com.mrliu.file.properties.FileServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Mr.Liu
 */
@Slf4j
@Component
public class FileUtils {
    @Resource
    private HttpUtils httpUtils;
    @Resource
    private FileServerProperties properties;

    public void downloadFile(HttpServletResponse response, ArrayList<FileInfoEntity> fileInfoEntities) throws IOException {
        /*
         * 生成文件名
         */
        String extName = retreiveFileName(fileInfoEntities);
        /*
         * 获取文件url
         */
        Map<String, String> map = filterFile(fileInfoEntities);
        /*
         * 设置响应头
         */
        setResponseHeader(response, extName);
        /*
         * 下载文件逻辑
         */
        downloadFdfsFile(response, map);


    }

    private void setResponseHeader(HttpServletResponse response, String extName) throws UnsupportedEncodingException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition",
                "attachment;filename=" + URLEncoder.encode(extName, "UTF-8"));
        response.setContentType("application/x-msdownload");
    }

    private void zipFile(HttpServletResponse response, Map<String, String> map, ArrayList<String> names) throws IOException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
        for (final String name : names) {
            final String url = map.get(name);
            final byte[] resource = getFileResource(url);
            ZipEntry zipEntry = new ZipEntry(name);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(resource);
        }
        zipOutputStream.close();
    }

    public byte[] getFileResource(String url) {
        try {
            byte[] resource = new byte[0];
            if (properties.getFdfs() != null) {
                resource = httpUtils.getFdfsResource(url);
            } else if (properties.getMinio() != null) {
                resource = httpUtils.getMinioResource(url);
            }
            return resource;
        } catch (Exception e) {
            log.error(e.getMessage());
            return "null".getBytes();
        }
    }

    private void downloadFdfsFile(HttpServletResponse response, Map<String, String> map) throws IOException {
        ArrayList<String> names = new ArrayList<>(map.keySet());
        // 压缩文件的压缩文件输出流
        if (names.size() > 1) {
            zipFile(response, map, names);
        } else {
            //正常下载单个文件
            final ServletOutputStream outputStream = response.getOutputStream();
            byte[] resource = getFileResource(map.get(names.get(0)));
            IOUtils.write(resource, outputStream);
        }
    }

    /**
     * 过滤无效文件名
     *
     * @param fileInfoEntities 文件信息
     * @return 文件信息map
     */
    private Map<String, String> filterFile(ArrayList<FileInfoEntity> fileInfoEntities) {
        //key--fileName
        //value--url
        Map<String, String> map = new LinkedHashMap<>(fileInfoEntities.size());
        Map<String, Integer> duplicateFile = new HashMap<>(fileInfoEntities.size());

        fileInfoEntities.stream()
                .filter((file) -> file != null && !StringUtils.isEmpty(file.getUrl()))
                .forEach((file) -> {
                    String originalFileName = file.getOriginalFileName();
                    if (map.containsKey(originalFileName)) {
                        if (duplicateFile.containsKey(originalFileName)) {
                            duplicateFile.put(originalFileName, duplicateFile.get(originalFileName) + 1);
                        } else {
                            duplicateFile.put(originalFileName, 1);
                        }
                        //解决压缩包内文件名重复的问题
                        originalFileName = buildNewFileName(originalFileName, duplicateFile.get(originalFileName));
                    }
                    map.put(originalFileName, file.getUrl());
                });
        System.out.println(map);
        return map;
    }

    private String retreiveFileName(ArrayList<FileInfoEntity> fileInfoEntities) {
        String extName = fileInfoEntities.get(0).getOriginalFileName();
        if (fileInfoEntities.size() > 1) {
            extName = extName.substring(0,
                    extName.lastIndexOf(".")) + "等.zip";
        }
        return extName;
    }


    private static String buildNewFileName(String filename, Integer order) {
        return new StringBuffer().append(filename).
                insert(filename.lastIndexOf("."), "(" + order + ")").toString();
    }
}
