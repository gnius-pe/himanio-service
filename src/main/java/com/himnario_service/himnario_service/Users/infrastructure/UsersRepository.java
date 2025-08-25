package com.himnario_service.himnario_service.Users.infrastructure;

import com.himnario_service.himnario_service.Users.domain.Role;
import com.himnario_service.himnario_service.Users.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findUsersByEmail(String email);
    boolean existsByEmail(String email);
    List<Users> findByRole(Role role);
}
