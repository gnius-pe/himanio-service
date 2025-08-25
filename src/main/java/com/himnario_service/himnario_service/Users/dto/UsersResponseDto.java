package com.himnario_service.himnario_service.Users.dto;

import com.himnario_service.himnario_service.Users.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersResponseDto {

    private Long id;

    private String email;

    private String name;

    private Role role;
}
