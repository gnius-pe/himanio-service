package com.himnario_service.himnario_service.security;

import com.himnario_service.himnario_service.Users.infrastructure.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UsersRepository repository; // aqui nos dice dónde se va a guardar el usuario
    // ademas la entidad tiene que implementar UserDetails (Users.java)

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        return repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    public UserDetailsService userDetailsService() {
        return this;
    }
}
