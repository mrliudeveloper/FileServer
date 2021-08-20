package com.mrliu.file.biz;

import com.mrliu.file.po.FileInfoEntity;
import com.mrliu.file.utils.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Mr.Liu
 */
@Component
@Slf4j
public class FileBiz {
    @Resource
    private ZipUtils zipUtils;

    public HashMap<String, Object> filterFile(HttpServletResponse response, ArrayList<FileInfoEntity> fileInfoEntities) throws IOException {

        final int fileSize = fileInfoEntities.stream().filter((file) -> file != null && !StringUtils.isEmpty(file.getUrl())).mapToInt((file) -> Math.toIntExact(file.getFileSize())).sum();
        String extName = fileInfoEntities.get(0).getOriginalFileName();
        if (fileInfoEntities.size() > 1) {
            extName = extName.substring(0,
                    extName.lastIndexOf(".")) + "等.zip";
        }
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
                        originalFileName = buildNewFileName(originalFileName, duplicateFile.get(originalFileName));
                    }
                    map.put(originalFileName, file.getUrl());
                });
        //压缩文件并下载
        return getZipAndFileinfo(response, fileSize, extName, map);
    }

    private HashMap<String, Object> getZipAndFileinfo(HttpServletResponse response, int fileSize, String extName, Map<String, String> map) throws IOException {
        System.out.println(map);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition",
                "attachment;filename=" + URLEncoder.encode(extName, "UTF-8"));
        response.setContentType("application/x-msdownload");
        ArrayList<String> names = new ArrayList<>(map.keySet());
        // 压缩文件的压缩文件输出流
        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
        for (final String name : names) {
            final String url = map.get(name);
            try {
                byte[] resource = zipUtils.getResource(url);
                ZipEntry zipEntry = new ZipEntry(name);
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(resource);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        zipOutputStream.close();
        final HashMap<String, Object> fileInfo = new HashMap<>(4);
        fileInfo.put("fileName", extName);
        fileInfo.put("fileSize", fileSize);
        return fileInfo;
    }

    private static String buildNewFileName(String filename, Integer order) {
        return new StringBuffer().append(filename).
                insert(filename.lastIndexOf("."), "(" + order + ")").toString();
    }
}
