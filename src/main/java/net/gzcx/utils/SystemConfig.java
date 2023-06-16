package net.gzcx.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @author chen xing
 * @description TODO
 * @date 2021-10-29 13:55
 */
@Slf4j
public class SystemConfig {
    // 读取配置文件
    private static Setting setting =
            new Setting(
                    FileUtil.touch(SystemUtil.configHome + File.separator + "rgSearch.setting"),
                    CharsetUtil.CHARSET_UTF_8,
                    true);
    /**
     * @author chen xing
     * @description 获取搜索的目录
     * @param
     * @return java.lang.String
     * @date 2021-10-29 11:07
     */
    public static String getLuceneSearchDir() {
        return setting.get("searchDir");
    }

    /**
     * @author chen xing
     * @description 获取索引的目录
     * @param
     * @return java.lang.String
     * @date 2021-10-29 11:07
     */
    public static String getIndexDir() {
        return setting.getStr("indexDir");
    }

    /**
    * @author chen xing
    * @description 获取创建索引任务的cron
     * @param
    * @return java.lang.String
    * @date 2021-10-29 16:57
    */
    public static String getCreateIndexCron() {
        return setting.getStr("createIndexCron");
    }

    /**
     * 更新索引的位置
     * @param newPath
     */
    public static void setSearchDir(String newPath) {
         setting.set("searchDir",newPath);
         setting.store();
    }
}
