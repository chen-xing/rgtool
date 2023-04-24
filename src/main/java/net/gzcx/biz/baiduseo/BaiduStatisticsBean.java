package net.gzcx.biz.baiduseo;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class BaiduStatisticsBean {
    private static String sampleData =
            " {'cc': '1', 'ck': '1', 'cl': '32-bit', 'ds': '1024x768', 'et': '0', 'ep': '0', 'fl': '11.0', 'ja': '1',\n"
                    + "            'ln': 'zh-cn', 'lo': '0', 'nv': '1', 'st': '3', 'v': '1.0.17'}";

    /**
     * cc : 1 ck : 1 cl : 32-bit ds : 1024x768 et : 3 ep : 2000,100 fl : 11.0 ja : 1 ln : zh-cn lo :
     * 0 nv : 1 st : 3 v : 1.0.17 si : a3e7b564007ea8a1787cad287cdbbbd3 su :
     * https%3A%2F%2Fwww.ha0123.com%2F rnd : 0 lt : 1610973582806
     */
    private String cc;

    private String ck;
    private String cl;
    private String ds;
    private String et;
    private String ep;
    private String fl;
    private String ja;
    private String ln;
    private String lo;
    private String nv;
    private String st;
    private String v;
    private String si;
    private String su;
    private String rnd;
    private String lt;

    public static BaiduStatisticsBean  init() {
        return new Gson().fromJson(sampleData, BaiduStatisticsBean.class);
    }
}
