package com.openai36.aggregation.app.wechat.rep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WechatLoginCreateState {
    private String state;
    private String appId;
}
