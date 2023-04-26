package net.gzcx.esign.file;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import net.gzcx.domain.file.BaseResult;
import net.gzcx.domain.file.GetDownloadUrlBean;
import net.gzcx.domain.file.GetSignUrlResultBean;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/**
 * @author chen xing
 * @description 文件相关的操作
 * @date 2023-04-24 11:11
 */
@Slf4j
public class FileSupport {
    private static final String getSignUrlPath = "/fileService/getSignUrl";
    private static final String getDownloadUrlPath = "/fileService/getDownloadUrl";

    /*获取文件的上传地址*/
    public static GetSignUrlResultBean getUploadFileKeyAndUrl(String env, String fileName) {
        String host = EnvEnum.getServiceHostByEnv(env);
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("X-timevale-project-id", "1111563739");

        Map<String, String> bodyMap = Maps.newHashMap();

        bodyMap.put("contentType", "application/octet-stream");
        bodyMap.put("fileName", getNameByFilePath(fileName));

        String body =
                HttpRequest.post(host + getSignUrlPath)
                        .addHeaders(headerMap)
                        .body(JSON.toJSONString(bodyMap))
                        .timeout(5000)
                        .execute()
                        .body();
        log.info("getSignUrl result {}", body);

        GetSignUrlResultBean getSignUrlResultBean =
                JSON.parseObject(body, GetSignUrlResultBean.class);
        return getSignUrlResultBean;
    }

    public static boolean uploadFile(String uploadUrl, String fileName) {
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put("Content-Type", "application/octet-stream");
        headerMap.put(Header.USER_AGENT.getValue(), "Hutool http");
        boolean result = false;
        try {
            byte[] bytes = IoUtil.readBytes(new FileInputStream(fileName));
            String body =
                    HttpRequest.put(uploadUrl)
                            .addHeaders(headerMap)
                            .body(bytes)
                            .timeout(5000)
                            .execute()
                            .body();

            log.info("upload file result is {}", body);

            BaseResult baseResult = JSON.parseObject(body, BaseResult.class);

            if (null != baseResult && baseResult.getErrCode().equals("0")) {
                result = true;
            }
        } catch (Exception e) {
            log.error("uploadFile", e);
        }
        return result;
    }

    public static String getDownloadUrlByFileKey(String env, String fileKey) {
        String host = EnvEnum.getServiceHostByEnv(env);
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put("Content-Type", "application/json");

        Map<String, Object> bodyMap = Maps.newHashMap();

        bodyMap.put("fileKey", fileKey);

        String body =
                HttpRequest.post(host + getDownloadUrlPath)
                        .addHeaders(headerMap)
                        .body(JSON.toJSONString(bodyMap))
                        .timeout(5000)
                        .execute()
                        .body();
        log.info("getDownloadUrl {} result {}", fileKey, body);
        GetDownloadUrlBean getDownloadUrlBean = JSON.parseObject(body, GetDownloadUrlBean.class);
        if (null != getDownloadUrlBean && getDownloadUrlBean.getErrCode().equals("0")) {
            return getDownloadUrlBean.getUrl();
        }
        return null;
    }

    /*从路径中获取文件的上传名称*/
    private static String getNameByFilePath(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(File.separator);
        return fileName.substring(lastIndexOf + 1);
    }
}
