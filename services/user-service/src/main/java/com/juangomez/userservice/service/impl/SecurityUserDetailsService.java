
package com.juangomez.userservice.service.impl;

import com.juangomez.userservice.model.entity.User;
import com.juangomez.userservice.model.enums.UserAccountStatus;
import com.juangomez.userservice.repository.UserRepository;
import com.juangomez.userservice.util.SecurityUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

    // Service which fetch user credentials from db when validating the jwt
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new SecurityUser(user);
    }
}
