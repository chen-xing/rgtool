package net.gzcx.seo;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.gzcx.utils.OkHttpUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Random;

/**
 * @author chen xing
 * @description 百度seo工具类
 * @date 2022-02-09 10:07
 */
@Data
@Slf4j
public class BaiduSeo {
    private String Referer = "https://www.94rg.com";
    private String BaiduID = "";
    private String Hjs = "http://hm.baidu.com/h.js?";
    private String Hgif = "http://hm.baidu.com/hm.gif?";
    private String TargetPage = "https://www.94rg.com";
    private String UserAgent =
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)"; // IE9
    private String MyData =
            " {'cc': '1', 'ck': '1', 'cl': '32-bit', 'ds': '1024x768', 'et': '0', 'ep': '0', 'fl': '11.0', 'ja': '1',\n"
                    + "            'ln': 'zh-cn', 'lo': '0', 'nv': '1', 'st': '3', 'v': '1.0.17'}";
    private Map<String, String> mapData = Maps.newHashMap();

    public BaiduSeo(String baiduID, String referer, String targetPage) {
        this.BaiduID = baiduID;
        this.Referer = referer;
        this.TargetPage = targetPage;
        mapData = new Gson().fromJson(MyData, Map.class);
        mapData.put("si", baiduID);
        mapData.put("su", referer);
        mapData.put("u", targetPage);
    }

    public void run() throws UnsupportedEncodingException {
        Map<String, String> mapHeader = Maps.newConcurrentMap();
        mapHeader.put("Referer", this.getReferer());
        mapHeader.put("User-Agent", this.getUserAgent());
        mapHeader.put(
                "Cookie",
                "BIDUPSID=531E9A00E5A3277C422D2A8C254203BC; PSTM=1598938259; HMACCOUNT=355533C3FFD9926E; HMACCOUNT_BFESS=355533C3FFD9926E; BDUSS=xKemptWjV5dmNCZEwxaTVPMmo2YW9HWm9rZlBCeEN5cFZLcm5vTnZwd1h6bmxmRVFBQUFBJCQAAAAAAAAAAAEAAADN4TgMY3gzMDg2NzkyOTEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABdBUl8XQVJfSW; BDUSS_BFESS=xKemptWjV5dmNCZEwxaTVPMmo2YW9HWm9rZlBCeEN5cFZLcm5vTnZwd1h6bmxmRVFBQUFBJCQAAAAAAAAAAAEAAADN4TgMY3gzMDg2NzkyOTEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABdBUl8XQVJfSW; BAIDUID=0B5FA7843DD986480CDD976D9FD72983:FG=1; H_WISE_SIDS=152522_159670_161584_161286_160246_156289_161252_159547_158972_162915_161019_162371_159382_162045_160443_162434_157264_162943_161420_161969_127969_161771_159069_161958_160897_161731_161442_161922_131423_128699_161081_158055_160095_160800_161967_159954_160423_144966_161833_162187_154213_158640_155530_160980_163114_162478_162883_158792_162522_162815_162642_162263_162261_162156_110085_162024; __yjs_duid=1_27c752348479af9c587837fe47f06c2f1608812971753; MCITY=-179%3A; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; H_PS_PSSID=33425_33517_33241_33344_31253_33286_26350_33264; BAIDUID_BFESS=0B5FA7843DD986480CDD976D9FD72983:FG=1; delPer=0; PSINO=1; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; SIGNIN_UC=70a2711cf1d3d9b1a82d2f87d633bd8a03608791000CevUlQTZa7%2BRUMgzq3HQ%2FsUZoaPWCzVcubIE9neamsAF57KSaKlGIXR4hsQe2m7tnbRpRb2z%2Fo9VnD7RxVWk5M%2Fui3Jjl2ftJGzs5Gmu5ICgEBZWgxQVMdfLZePoCnebnxuAbj5uRXgYAcnI%2BIzCr2njWIYbpNGhhLbyntu8cmLe9l0WihuBfDtgXl4slNjbsHWjjnJUprC%2FakRRfCPwxerew9GFwqNx%2FqeEBtXIspqnIX9wDlrWwhdALJH5BSc0h3qjocjZ523XqFyOdlsbiw%3D%3D96542227889673098322281441800679; uc_login_unique=83fafdc3723f1db8130d9c2fa274080c; uc_recom_mark=cmVjb21tYXJrXzI3NTEzMDkw; HMVT=41fc030db57d5570dd22f78997dc4a7e|1610866663|");
        OkHttpUtil.getInstance().getData(this.Hjs + this.getBaiduID(), mapHeader);

        mapData.put("rnd", String.valueOf(new Random().nextInt(1) * 2147483647));
        mapData.put("lt", String.valueOf(System.currentTimeMillis()));

        String finalUrl = getRightUrl(this.getHgif(), mapData);
        OkHttpUtil.getInstance().getData(finalUrl, mapHeader);

        mapData.put("rnd", String.valueOf(new Random().nextInt(1) * 2147483647));
        mapData.put("et", "3");
        mapData.put("ep", "2000,100");

        String finalUrl1 = getRightUrl(this.getHgif(), mapData);
        OkHttpUtil.getInstance().getData(finalUrl1, mapHeader);
    }

    private String getRightUrl(String url, Map<String, String> map)
            throws UnsupportedEncodingException {
        String param = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (StrUtil.isBlank(entry.getKey()) || StrUtil.isBlank(entry.getValue())) {
                continue;
            }

            String value = URLEncoder.encode(entry.getValue(), "UTF-8");
            param += MessageFormat.format("&{0}={1}", entry.getKey(), value);
        }
        System.out.println(url + param.substring(1));
        return url + param.substring(1);
    }
}
