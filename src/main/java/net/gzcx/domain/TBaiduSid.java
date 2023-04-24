package net.gzcx.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chen xing
 * @description TODO
 * @date 2022-07-16 15:38
 */
@Data
public class TBaiduSid implements Serializable {
    private Integer id;
    private String url;
    private String sid;
    private String createTime;
    private String modifiedTime;
    private static final long serialVersionUID = 1L;
}
