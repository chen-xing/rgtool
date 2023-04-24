package net.gzcx.domain.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chen xing
 * @description TODO
 * @date 2023-04-24 13:54
 */
@NoArgsConstructor
@Data
public class GetDownloadUrlBean {

    @JsonProperty("errCode")
    private String errCode;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("url")
    private String url;
}
