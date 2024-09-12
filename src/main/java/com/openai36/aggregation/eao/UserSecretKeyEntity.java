package com.openai36.aggregation.eao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString(exclude={"secretKey"})
@Entity
public class UserSecretKeyEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private Integer userId;
    @NotNull
    private String name;
    @NotNull @Column(unique=true)
    private String secretKey;
    @NotNull
    private LocalDateTime createTime;
    private LocalDateTime lastUseTime;

    /**
     * 一个用户有一个默认SecretKey，默认SecretKey对用户不可见
     */
    @NotNull
    private Boolean defaultKey;
}
