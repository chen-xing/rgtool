package net.gzcx.biz.baiduseo;

import lombok.Data;

@Data
public class BaiduResourceBean {
    private String referer = "https://www.94rg.com";
    private String baiduId = "";
    private String hjs = "http://hm.baidu.com/h.js?";
    private String hGif = "http://hm.baidu.com/hm.gif?";
    private String targetPage = "/www.94rg.com";
    private String userAgent =
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)"; // IE9
}
