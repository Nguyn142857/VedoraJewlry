package com.jewelry.jewelryshopbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "auth_login_attempts")
public class AuthLoginAttempt {

    @Id
    @Column(name = "principal", nullable = false, length = 100)
    private String principal;

    @Column(name = "failures", nullable = false)
    private int failures;

    @Column(name = "first_failure_at")
    private Instant firstFailureAt;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version")
    private Long version;
}
