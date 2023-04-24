package net.gzcx.esign.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author chen xing
 * @description 环境相关的配置
 * @date 2023-04-24 11:12
 */
@AllArgsConstructor
@Getter
public enum EnvEnum {
    TEST("测试", "http://file-system.testk8s.tsign.cn/file-system"),
    SML("模拟", "http://file-system.smlk8s.esign.cn/file-system"),
    ;
    private String env;
    private String serviceUrl;

    public static String getServiceHostByEnv(String env) {
        for (EnvEnum envEnum : EnvEnum.values()) {
            if (envEnum.getEnv().equals(env)) {
                return envEnum.getServiceUrl();
            }
        }
        return null;
    }
}
