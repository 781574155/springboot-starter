package com.openai36.aggregation.app.wechat.rep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WechatValidCreateState {
    private Boolean validKey;
    private Boolean login;
    private String tmpPassword;
}
