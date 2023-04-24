package net.gzcx.seo.baiduSid;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import net.gzcx.mapper.TBaiduSidMapper;
import net.gzcx.domain.TBaiduSid;
import net.gzcx.utils.MybatisUtil;

/**
 * @author chen xing
 * @description baiduSid工具类
 * @date 2022-07-16 15:52
 */
@Slf4j
public class BaiduSidSupport {
    public static void save(String sid, String url) {
        TBaiduSidMapper mapper = MybatisUtil.getSqlSession().getMapper(TBaiduSidMapper.class);
        TBaiduSid tBaiduSidBySid = mapper.getTBaiduSidBySid(sid);
        if (null != tBaiduSidBySid) {
            log.warn("sid { url {} is exists}", sid, url);
        } else {
            TBaiduSid tBaiduSid = new TBaiduSid();
            tBaiduSid.setSid(sid);
            tBaiduSid.setUrl(url);

            String currentTime = LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now());
            tBaiduSid.setCreateTime(currentTime);
            tBaiduSid.setModifiedTime(currentTime);
            mapper.save(tBaiduSid);
        }
    }

    public static void main(String[] args) {
        save("99f259e8c1db2b5724781c9ef9612de2","https://www.chenzhuofan.top/");
    }
}
