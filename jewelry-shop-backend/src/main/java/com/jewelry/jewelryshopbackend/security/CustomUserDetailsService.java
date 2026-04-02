package com.jewelry.jewelryshopbackend.security;

import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));

        return new CustomUserDetails(user);
    }
}
