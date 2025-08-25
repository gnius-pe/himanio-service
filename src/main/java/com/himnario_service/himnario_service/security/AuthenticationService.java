package com.himnario_service.himnario_service.security;

import com.himnario_service.himnario_service.Users.domain.Role;
import com.himnario_service.himnario_service.Users.domain.Users;
import com.himnario_service.himnario_service.Users.dto.SigninRequest;
import com.himnario_service.himnario_service.Users.dto.UsersRequestDto;
import com.himnario_service.himnario_service.Users.dto.UsersResponseDto;
import com.himnario_service.himnario_service.Users.infrastructure.UsersRepository;
import com.himnario_service.himnario_service.exception.ResourceAlreadyExistsException;
import com.himnario_service.himnario_service.security.dto.JwtAuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
public class AuthenticationService {

    @Autowired
    UsersRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private ModelMapper modelMapper;

    public JwtAuthenticationResponse signup(UsersRequestDto userSignUpRequest) {
        // Mapear SigninRequest a Users
        if (userRepository.existsByEmail(userSignUpRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("El correo ya está registrado: " + userSignUpRequest.getEmail());
        }

        Users user = modelMapper.map(userSignUpRequest, Users.class);
        user.setRole(Role.USER);
        // Encriptar la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Guardar el usuario
        userRepository.save(user);

        // Generar el token JWT
        String jwt = jwtService.generateToken(user);

        // Crear respuesta
        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        response.setToken(jwt);
        return response;
    }

    public JwtAuthenticationResponse login(SigninRequest request) throws IllegalArgumentException {
        // Autenticar credenciales del usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Obtener el usuario por email y validar que exista
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Generar token JWT
        String jwt = jwtService.generateToken(user);

        // Crear y devolver la respuesta
        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        response.setToken(jwt);

        return response;
    }


    @Autowired
    private UsersRepository userAccountRepository;

    public UsersResponseDto getUserResponseByEmail(String email) {
        Users user = userAccountRepository.findUsersByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return modelMapper.map(user, UsersResponseDto.class);
    }



}
