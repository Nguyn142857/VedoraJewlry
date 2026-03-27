package com.jewelry.jewelryshopbackend.repository;

import com.jewelry.jewelryshopbackend.entity.Permission;
import com.jewelry.jewelryshopbackend.entity.Role;
import com.jewelry.jewelryshopbackend.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    boolean existsByRoleAndPermission(Role role, Permission permission);
    Optional<RolePermission> findByRoleAndPermission(Role role, Permission permission);
    List<RolePermission> findByRole(Role role);
}