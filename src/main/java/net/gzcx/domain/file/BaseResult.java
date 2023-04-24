package net.gzcx.domain.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author chen xing
 * @description TODO
 * @date 2023-04-24 11:38
 */
@Data
public class BaseResult {
    @JsonProperty("errCode")
    private String errCode;

    @JsonProperty("msg")
    private String msg;
}
