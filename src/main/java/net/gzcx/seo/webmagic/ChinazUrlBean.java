package net.gzcx.seo.webmagic;

import lombok.Data;
import us.codecraft.webmagic.model.annotation.ExtractBy;

/**
 * @author chen xing
 * @description TODO
 * @date 2022-07-15 20:33
 */

@ExtractBy(value = "//*[@class=\"_chinaz-seo-latelyc\"]/a", multi = true)
@Data
public class ChinazUrlBean {
    @ExtractBy("//a/@href")
    private String url;
}
