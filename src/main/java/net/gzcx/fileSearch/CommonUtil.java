package net.gzcx.fileSearch;

import cn.hutool.crypto.SecureUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author chen xing
 * @description 本地文件检索的工具类
 * @date 2021-10-28 11:00
 */
public class CommonUtil {
    /**
     * @author chen xing
     * @description 获取文件的内容
     * @param myFile
     * @return java.lang.String
     * @date 2021-10-29 10:58
     */
    public static String getContentFromFile(File myFile) {
        StringBuffer sb = new StringBuffer();
        if (!myFile.exists()) {
            return "";
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader(myFile));
            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
            in.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
        return sb.toString();
    }

    /**
     * @author chen xing
     * @description 计算文件对应的md5
     * @param content
     * @return java.lang.String
     * @date 2021-10-29 10:59
     */
    public static String getHash(String content) {
        return SecureUtil.md5(content);
    }
}
