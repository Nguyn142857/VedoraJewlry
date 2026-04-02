package com.jewelry.jewelryshopbackend.service.user.impl;

import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.exception.UnauthorizedException;
import com.jewelry.jewelryshopbackend.repository.UserRepository;
import com.jewelry.jewelryshopbackend.security.CustomUserDetails;
import com.jewelry.jewelryshopbackend.service.user.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {

    private final UserRepository userRepository;

    @Override
    public User requireCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("Authentication is required");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }

        String username = authentication.getName();
        return userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));
    }
}
