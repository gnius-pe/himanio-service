package com.himnario_service.himnario_service.Users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SigninRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String name;
}
