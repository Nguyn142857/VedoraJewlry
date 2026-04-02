package com.jewelry.jewelryshopbackend.repository;

import com.jewelry.jewelryshopbackend.entity.AuthLoginAttempt;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthLoginAttemptRepository extends JpaRepository<AuthLoginAttempt, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AuthLoginAttempt a where a.principal = :principal")
    Optional<AuthLoginAttempt> findByPrincipalForUpdate(@Param("principal") String principal);
}
