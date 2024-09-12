package com.openai36.aggregation.eao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString(exclude={"password"})
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    @NotNull @Column(unique=true)
    private String username;
    @NotNull
    private String password;
    @NotNull
    private Boolean enabled;
    @NotNull
    private LocalDateTime createTime;
}
