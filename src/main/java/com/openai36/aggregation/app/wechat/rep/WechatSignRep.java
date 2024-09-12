package com.openai36.aggregation.app.wechat.rep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WechatSignRep {
    private String appId;
    private Long timestamp;
    private String nonceStr;
    private String signature;
}
