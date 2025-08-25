package com.himnario_service.himnario_service.Users.application;

import com.himnario_service.himnario_service.Users.dto.SigninRequest;
import com.himnario_service.himnario_service.Users.dto.UsersRequestDto;
import com.himnario_service.himnario_service.security.AuthenticationService;
import com.himnario_service.himnario_service.security.dto.JwtAuthenticationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    // Crear usuario (signup)
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> createUser(@Valid @RequestBody UsersRequestDto dto) {
        JwtAuthenticationResponse response = authenticationService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody SigninRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
}
