package com.openai36.aggregation.service.dto;

import lombok.Data;

@Data
public class WechatLogin {
    private Boolean success;
    private Integer userId;
    private String tmpPassword;
}
