package com.jewelry.jewelryshopbackend.repository;

import com.jewelry.jewelryshopbackend.entity.Role;
import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    boolean existsByUserAndRole(User user, Role role);
    Optional<UserRole> findByUserAndRole(User user, Role role);
    List<UserRole> findByUser(User user);
}