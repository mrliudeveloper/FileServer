package com.mrliu.file.utils;

import com.mrliu.file.properties.FileServerProperties;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Mr.Liu
 */
@Slf4j
@Component
public class HttpUtils {
    @Resource
    private FileServerProperties properties;

    private FileServerProperties.Properties minio;
    /**
     * 构建文件客户端对象
     * @return minio客户端对象
     */
    private MinioClient buildClient() {
        minio = properties.getMinio();
        return MinioClient.builder()
                .endpoint(minio.getEndpoint())
                .credentials(minio.getAccessKeyId(), minio.getAccessKeySecret())
                .build();
    }
    /**
     * 获取fdfs服务器的文件资源
     *
     * @param path 文件资源的路径
     * @return 文件资源的字节数组
     * @throws IOException 异常
     */
    public byte[] getFdfsResource(String path) throws IOException {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(1000 * 5);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Charset", "UTF-8");
        final InputStream in = conn.getInputStream();
        return toByteArray(in);
    }

    /**
     * 获取minio服务器的输入流
     * @param path bucketName/ObjectName
     * @return 文件字节数组
     */
    public byte[] getMinioResource(String path){

        int i = path.indexOf("/");
        String bucketName = path.substring(0, i);
        String onjectName = path.substring(i + 1);
        GetObjectArgs args = GetObjectArgs.builder().bucket(bucketName).object(onjectName).build();
        try {
            InputStream inputStream = buildClient().getObject(args);
            return toByteArray(inputStream);
        }  catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }

    }


    /**
     * 输入流转字节码工具类
     *
     * @param input 资源输入流
     * @return 字节数组
     * @throws IOException 异常
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}
