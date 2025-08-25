package com.himnario_service.himnario_service.Users.domain;

import com.himnario_service.himnario_service.Users.dto.UpdateUserRequestDto;
import com.himnario_service.himnario_service.Users.dto.UsersResponseDto;
import com.himnario_service.himnario_service.Users.infrastructure.UsersRepository;
import com.himnario_service.himnario_service.exception.AccessDeniedException;
import com.himnario_service.himnario_service.exception.ResourceNotFoundException;
import com.himnario_service.himnario_service.security.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersService {
    private final AuthUtils authUtils;
    private final UsersRepository usersRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UsersResponseDto updateUser(Long id, UpdateUserRequestDto dto) {
        Users actualUser = authUtils.getAuthenticatedUser();

        if (!actualUser.getId().equals(id)) {
            throw new AccessDeniedException("No tienes permisos para actualizar este usuario.");
        }
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            user.setName(dto.getName().trim());
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            Optional<Users> existingUser = usersRepository.findByEmail(dto.getEmail().trim());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new RuntimeException("El email ya está en uso por otro usuario");
            }
            user.setEmail(dto.getEmail().trim());
        }
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user = usersRepository.save(user);
        return modelMapper.map(user, UsersResponseDto.class);
    }

    public void deleteUser(Long id) {
        Users actualUser = authUtils.getAuthenticatedUser();

        if (!actualUser.getId().equals(id)) {
            throw new AccessDeniedException("No tienes permisos para eliminar este usuario.");
        }
        if (!usersRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }
        usersRepository.deleteById(id);
    }

    public UsersResponseDto getUserById(Long id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return modelMapper.map(user, UsersResponseDto.class);
    }

    public List<UsersResponseDto> getAllUsers() {
        return usersRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UsersResponseDto.class))
                .collect(Collectors.toList());
    }

    public List<Users> list() {
        return usersRepository.findAll();
    }

    public void save(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersRepository.save(user);
    }
}
