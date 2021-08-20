package com.mrliu.file.utils;

import com.mrliu.file.strategy.FileStrategy;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
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
@Component
public class HttpUtils {
    @Resource
    private FileStrategy fileStrategy;

    /**
     * 获取文件资源
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
