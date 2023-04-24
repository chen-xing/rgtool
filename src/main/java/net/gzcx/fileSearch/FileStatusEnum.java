package net.gzcx.fileSearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author chen xing
 * @description TODO
 * @date 2021-11-01 11:28
 */
@Getter
@AllArgsConstructor
public enum FileStatusEnum {
    NOT_EXISTS(0, "不存在"),
    EXISTS_UPDATE(1, "存在且更新"),
    EXISTS_NOT_UPDATE(2, "存在且不变");
    private int type;
    private String description;
}
