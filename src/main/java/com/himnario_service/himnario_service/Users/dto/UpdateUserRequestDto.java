package com.himnario_service.himnario_service.Users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDto {
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
}
