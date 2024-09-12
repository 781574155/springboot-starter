package com.openai36.aggregation.eao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
public class WechatUserEntity {
    @Id
    private Integer userId;
    @NotNull @Column(unique=true)
    private String openId;

    private String nickname;
    private String sex;
    private String province;
    private String city;
    private String country;
    private String headimgurl;
    private String privilege;
    private String unionid;

    @NotNull
    private LocalDateTime createTime;
}
