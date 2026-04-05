package com.jewelry.jewelryshopbackend.security;

import com.jewelry.jewelryshopbackend.entity.RolePermission;
import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.entity.UserRole;
import com.jewelry.jewelryshopbackend.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;
        this.authorities = extractAuthorities(user);
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE;
    }

    private Set<GrantedAuthority> extractAuthorities(User user) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        for (UserRole userRole : user.getUserRoles()) {
          if (userRole.getRole() == null) {
              continue;
          }

          grantedAuthorities.add(new SimpleGrantedAuthority(userRole.getRole().getName()));

          for (RolePermission rolePermission : userRole.getRole().getRolePermissions()) {
              if (rolePermission.getPermission() != null) {
                  grantedAuthorities.add(new SimpleGrantedAuthority(rolePermission.getPermission().getName()));
              }
          }
        }

        return Set.copyOf(grantedAuthorities);
    }
}
