package com.himnario_service.himnario_service.Users.application;

import com.himnario_service.himnario_service.Users.domain.UsersService;
import com.himnario_service.himnario_service.Users.dto.UpdateUserRequestDto;
import com.himnario_service.himnario_service.Users.dto.UsersResponseDto;
import com.himnario_service.himnario_service.security.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UsersController {
    private final UsersService usersService;
    private final AuthenticationService authenticationService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsersResponseDto> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UsersResponseDto response = authenticationService.getUserResponseByEmail(email);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<UsersResponseDto> getUserById(@PathVariable Long id) {
        UsersResponseDto user = usersService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<UsersResponseDto>> getAllUsers() {
        List<UsersResponseDto> users = usersService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<UsersResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequestDto dto) {
        UsersResponseDto updatedUser = usersService.updateUser(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
