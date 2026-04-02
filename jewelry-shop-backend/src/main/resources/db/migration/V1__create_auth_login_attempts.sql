CREATE TABLE IF NOT EXISTS auth_login_attempts (
    principal VARCHAR(100) NOT NULL,
    failures INT NOT NULL,
    first_failure_at TIMESTAMP NULL,
    locked_until TIMESTAMP NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NULL,
    CONSTRAINT pk_auth_login_attempts PRIMARY KEY (principal)
);

CREATE INDEX IF NOT EXISTS idx_auth_login_attempts_locked_until
    ON auth_login_attempts (locked_until);
