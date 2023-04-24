package net.gzcx.seo.webmagic;

import cn.hutool.http.HttpUtil;
import com.timevale.mandarin.base.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import net.gzcx.seo.baiduSid.BaiduSidSupport;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

/**
 * @author chen xing
 * @description TODO
 * @date 2022-07-15 20:37
 */
@Slf4j
public class ChinazModelPipeline implements PageModelPipeline<ChinazUrlBean> {
    private static final Integer baiduSidLength = 32; // 百度sid的默认长度
    private static final String baiduTongjiJs = "hm.baidu.com/hm.js?"; // 用于匹配百度js地址

    @Override
    public void process(ChinazUrlBean chinazUrlBean, Task task) {
        try {
            String baiduUrl = chinazUrlBean.getUrl().replace("//seo.chinaz.com/", "https://");
            String baiduId = getBaiduId(baiduUrl);

            if (StringUtils.isBlank(baiduId)
                    || StringUtils.isBlank(baiduUrl)
                    || !isLetterOrDigits(baiduId)) {
                log.warn("crawl data error baiduSid {} baiduUrl {}", baiduId, baiduUrl);
            } else {
                BaiduSidSupport.save(baiduId, baiduUrl);
            }
        } catch (Exception ex) {
            log.error("爬虫异常", ex);
        }
    }

    private static String getBaiduId(String url) {
        String result = HttpUtil.get(url);
        int begin = baiduTongjiJs.length() + result.indexOf(baiduTongjiJs);
        String baiduId = result.substring(begin, begin + baiduSidLength);
        return baiduId;
    }

    /**
     * @author chen xing
     * @description 判断字符串是否仅仅包含数字和字符串
     * @param string
     * @return boolean
     * @date 2022-07-16 16:31
     */
    private boolean isLetterOrDigits(String string) {
        boolean flag = false;
        for (int i = 0; i < string.length(); i++) {
            if (Character.isLowerCase(string.charAt(i))
                    || Character.isUpperCase(string.charAt(i))
                    || Character.isDigit(string.charAt(i))) {
                flag = true;
            } else {
                flag = false;
                return flag;
            }
        }
        return flag;
    }

    public static void main(String[] args) {

        Site site =
                Site.me()
                        .addHeader(
                                "User-Agent",
                                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                        .setRetryTimes(1)
                        .setSleepTime(10000)
                        .setTimeOut(600);
        OOSpider.create(site, new ChinazModelPipeline(), ChinazUrlBean.class)
                .setIsExtractLinks(false)
                .addUrl("http://seo.chinaz.com/")
                .setExitWhenComplete(true)
                .thread(1)
                .run();
    }
}
