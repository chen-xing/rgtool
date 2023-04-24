package net.gzcx.biz.baiduseo;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.gzcx.utils.OkHttpUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Slf4j
public class BaiduSeoUtil {
    private BaiduStatisticsBean baiduStatisticsBean = BaiduStatisticsBean.init();
    private BaiduResourceBean baiduResourceBean = new BaiduResourceBean();
    private final Gson gson = new Gson();

    public BaiduSeoUtil(String baiduId, String referer, String targetPage)
            throws UnsupportedEncodingException {
        baiduResourceBean.setBaiduId(baiduId);
        baiduResourceBean.setReferer(referer);
        baiduResourceBean.setTargetPage(targetPage);
        baiduStatisticsBean.setSi(baiduId);
        baiduStatisticsBean.setSu(URLEncoder.encode(referer, "UTF-8"));
    }

    public void run() throws UnsupportedEncodingException {
        Map<String, String> mapHeader = Maps.newConcurrentMap();
        mapHeader.put("Referer", baiduResourceBean.getReferer());
        mapHeader.put("User-Agent", baiduResourceBean.getUserAgent());
        OkHttpUtil.getInstance()
                .getData(baiduResourceBean.getHjs() + baiduResourceBean.getHjs(), mapHeader);

        baiduStatisticsBean.setRnd(String.valueOf(new Random().nextInt(1) * 2147483647));
        baiduStatisticsBean.setLt(String.valueOf(System.currentTimeMillis()));

        String finalUrl = getRightUrl(baiduResourceBean.getHGif(), baiduStatisticsBean);
        OkHttpUtil.getInstance().getData(finalUrl, mapHeader);

        baiduStatisticsBean.setRnd(String.valueOf(new Random().nextInt(1) * 2147483647));
        baiduStatisticsBean.setEt("3");
        baiduStatisticsBean.setEp("2000,100");

        String finalUrl1 = getRightUrl(baiduResourceBean.getHGif(), baiduStatisticsBean);
        OkHttpUtil.getInstance().getData(finalUrl1, mapHeader);
    }

    private String getRightUrl(String url, BaiduStatisticsBean baiduStatisticsBean)
            throws UnsupportedEncodingException {
        Map<String, String> map = gson.fromJson(gson.toJson(baiduStatisticsBean), Map.class);
        String param = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (StrUtil.isBlank(entry.getKey()) || StrUtil.isBlank(entry.getValue())) {
                continue;
            }
            param += MessageFormat.format("&{0}={1}", entry.getKey(), entry.getValue());
        }
        return url + param.substring(1);
    }

    public static void main(String[] args)
            throws UnsupportedEncodingException, InterruptedException {
        Set<String> set = Sets.newHashSet();
        set.add("0c96e9f929c017b87f20eb15ec5cd11e");
        set.add("339b0c13a319e150383b56cef07e29c2");

        for (String sid : set) {
            BaiduSeoUtil baiduSeoUtil =
                    new BaiduSeoUtil(
                            "sid", "https://www.94rg.com/article/1821", "https://www.94rg.com");
            int i = 0;
            while (i < 3) {
                baiduSeoUtil.run();
                System.out.println("xxxx");
                i++;
                Thread.sleep(1000);
            }
        }
    }
}
