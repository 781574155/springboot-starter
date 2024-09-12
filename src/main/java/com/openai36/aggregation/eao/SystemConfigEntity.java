package com.openai36.aggregation.eao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class SystemConfigEntity {
    @Id
    private String name;
    @NotNull
    private Boolean enablePageRegister;
    @NotNull
    private Boolean enablePageLogin;    // 此开关只是控制前端是否显示登录输入框，并不是禁用页面登录

    @NotNull
    private Boolean enableWechatRegister;
    @NotNull
    private Boolean enableWechatLogin;
    @NotNull
    private Boolean enableWechatShare;
    private String wechatAppId;
    private String wechatAppSecret;
}
