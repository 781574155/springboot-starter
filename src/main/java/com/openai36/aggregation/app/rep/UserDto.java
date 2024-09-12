package com.openai36.aggregation.app.rep;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {
    @NotNull @NotEmpty
    private String username;

    @NotNull @NotEmpty
    private String password;
    private String confirmPassword;
}
