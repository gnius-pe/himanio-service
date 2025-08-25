package com.himnario_service.himnario_service.config;

import com.himnario_service.himnario_service.Users.domain.Role;
import com.himnario_service.himnario_service.Users.domain.Users;
import com.himnario_service.himnario_service.Users.infrastructure.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initDefaultUsers() {

        return args -> {
            if (usersRepository.findByEmail("admin@himnario.com").isEmpty()) {
                Users collab = new Users();
                collab.setEmail("admin@himnario.com");
                collab.setPassword(passwordEncoder.encode("himnario123"));
                collab.setName("Administrador");
                collab.setRole(Role.ADMIN);
                usersRepository.save(collab);
                System.out.println("✔ Usuario ADMIN creado por defecto.");
            }
        };
    }
}