package net.gzcx.mapper;

import net.gzcx.domain.TBaiduSid;

import java.util.List;

/**
 * @author chen xing
 * @description TODO
 * @date 2022-07-16 15:38
 */
public interface TBaiduSidMapper {
    TBaiduSid getTBaiduSidBySid(String sid);
    void save(TBaiduSid tBaiduSid);

    List<TBaiduSid> queryAll();
}
